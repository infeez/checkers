package com.infeez.simple.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.infeez.simple.Cells
import com.infeez.simple.ResourceSingleton
import com.infeez.simple.base.GameSpriteBatch
import com.infeez.simple.game.ai.AiMoveResult
import com.infeez.simple.game.ai.CheckersAiFactory
import com.infeez.simple.game.ai.RandomAi
import com.infeez.simple.game.controller.DefaultGameConfig
import com.infeez.simple.game.controller.GameController
import com.infeez.simple.game.controller.GameMode
import com.infeez.simple.game.controller.TurnState
import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult
import com.infeez.simple.game.model.MoveType
import com.infeez.simple.game.model.Piece
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor
import com.infeez.simple.game.rules.CheckersRules
import com.infeez.simple.game.rules.RussianCheckersRules
import com.infeez.simple.input.PCInputProcessor
import com.infeez.simple.render.BoardRenderMapper
import com.infeez.simple.state.CheckerColor
import com.infeez.simple.state.CheckerState
import com.infeez.simple.utils.BoardArrayPosition
import com.infeez.simple.utils.BoardCommandUtil
import com.infeez.simple.utils.BoardConfig
import com.infeez.simple.utils.Constants.GameEnvTypes
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.infeez.simple.game.model.BoardState as DomainBoardState
import com.infeez.simple.game.model.GameState as DomainGameState
import com.infeez.simple.state.GameState as SerializableGameState

class Board(spriteBatch: GameSpriteBatch? = null) : GameObject(
    ResourceSingleton.getUniqueId(),
    0f,
    0f,
    graphicsWidth(),
    graphicsHeight(),
    spriteBatch,
), PCInputProcessor {
    internal val cells = Cells()
    private val rules: CheckersRules = RussianCheckersRules()
    private val renderMapper = BoardRenderMapper()
    private val gameConfig = DefaultGameConfig.value
    private val aiFactory = CheckersAiFactory(rules)
    private val ai = aiFactory.create(gameConfig.aiDifficulty)
    private val fallbackAi = RandomAi(rules)
    private val gameController = GameController(rules, ai, gameConfig)
    private val aiExecutor: ExecutorService = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "checkers-ai").apply {
            isDaemon = true
        }
    }
    private var dragged = false
    private var cellForDrag: Cell? = null
    private var activePointerId: Int? = null
    private var legacyMoveNumberOffset = 0
    private var selectedPosition: BoardPosition? = null
    private var availableMovesForSelectedPiece: List<Move> = emptyList()
    private var lastDebugMessage: String? = null
    private var hudFont: BitmapFont? = null
    private var resetButtonPressed = false
    private var lastAiMove: Move? = null
    @Volatile
    private var aiCalculationRunning = false
    @Volatile
    private var aiTaskVersion = 0

    private val gameState: DomainGameState
        get() = gameController.state

    fun create() {
        cells.createBoard(batch)
    }

    override fun draw() {
        for (cell in cells) {
            cell.draw()
        }
        val highlightedTargets = availableMovesForSelectedPiece.map { move -> move.to }.toSet()
        for (cell in cells) {
            if (cell.domainPosition() in highlightedTargets) {
                cell.drawHighlight()
            }
        }
        for (cell in cells) {
            cell.drawChecker()
        }
        for (cell in cells) {
            cell.drawCapturedChecker()
        }
        drawKingMarkers()
        drawHud()
        drawResetButton()
    }

    override fun update() {
        for (cell in cells) {
            cell.update()
        }
    }

    override fun dispose() {
        for (cell in cells) {
            cell.dispose()
        }
        hudFont?.dispose()
        hudFont = null
        aiExecutor.shutdownNow()
    }

    override fun mouseDown(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
        if (activePointerId != null) {
            return false
        }
        if (isResetButtonHit(x, y)) {
            activePointerId = pointer
            resetButtonPressed = true
            return true
        }
        if (gameState.status != GameStatus.InProgress) {
            logDebug("Game is already finished.")
            return false
        }
        if (!gameController.isHumanTurn()) {
            logDebug(if (gameController.isAiTurn()) "AI is thinking." else "Input is locked.")
            return false
        }

        val selectedCell = cells.findCellByCoordinatesAndHaveChecker(x, y) ?: return false
        val position = selectedCell.domainPosition()
        val piece = gameState.board.pieceAt(position) ?: return false
        if (piece.color != gameState.currentTurn) {
            logDebug("${piece.color.displayName()} piece cannot move on ${gameState.currentTurn.displayName()} turn.")
            return false
        }

        val forcedPiece = gameState.forcedPiece
        if (forcedPiece != null && position != forcedPiece) {
            logDebug("Continue capture with ${forcedPiece.toDisplayString()}.")
            return false
        }

        val legalMovesForPiece = rules.legalMovesForPiece(gameState, position)
        val captureAvailable = rules.legalMoves(gameState).any { move -> move.type == MoveType.CAPTURE }
        if (captureAvailable && legalMovesForPiece.isEmpty()) {
            logDebug("Capture is mandatory.")
            return false
        }

        activePointerId = pointer
        cellForDrag = selectedCell
        selectedPosition = position
        availableMovesForSelectedPiece = legalMovesForPiece
        dragged = false
        return true
    }

    override fun mouseDrag(x: Float, y: Float, pointer: Int): Boolean {
        if (activePointerId != pointer) {
            return false
        }
        if (resetButtonPressed) {
            return true
        }

        val dragCell = cellForDrag
        if (dragCell != null) {
            dragCell.captureChecker(x, y)
            dragged = true
            return true
        } else {
            cellForDrag = cells.findCellByCoordinatesAndHaveChecker(x, y)
        }

        return cellForDrag != null
    }

    override fun mouseUp(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
        if (activePointerId != pointer) {
            return false
        }
        if (resetButtonPressed) {
            val shouldReset = isResetButtonHit(x, y)
            resetDrag()
            if (shouldReset) {
                startNewGame()
                logDebug("New game started.")
            }
            return shouldReset
        }

        val sourceCell = cellForDrag
        if (!dragged || sourceCell == null) {
            cancelActiveDrag()
            return false
        }

        val targetCell = cells.findCellByCoordinates(x, y)
        val targetPosition = targetCell?.domainPosition()
        if (targetPosition == null) {
            cancelActiveDrag()
            logDebug("Move target is outside the board.")
            return false
        }

        val selectedMove = availableMovesForSelectedPiece.firstOrNull { move -> move.to == targetPosition }
        if (selectedMove == null) {
            cancelActiveDrag()
            logDebug("Illegal move to ${targetPosition.toDisplayString()}.")
            return false
        }

        return when (val result = gameController.makeHumanMove(selectedMove)) {
            is MoveResult.Success -> {
                markGameStateChanged()
                syncCellsFromGameState()
                resetDrag()
                logAppliedMove(result.appliedMove.move)
                scheduleAiMoveIfNeeded()
                true
            }
            is MoveResult.Invalid -> {
                cancelActiveDrag()
                logDebug("Invalid move: ${result.reason}.")
                false
            }
        }
    }

    override fun touchCancelled(x: Float, y: Float, pointer: Int, mouseButton: Int): Boolean {
        if (activePointerId != pointer) {
            return false
        }

        cancelActiveDrag()
        return true
    }

    fun moveChecker(from: String, to: String) {
        val chPosFrom = BoardCommandUtil.parseCommand(from)
        val chPosTo = BoardCommandUtil.parseCommand(to)
        val source = chPosFrom.toDomainPosition()
        val target = chPosTo.toDomainPosition()

        val move = rules.legalMovesForPiece(gameState, source)
            .firstOrNull { legalMove -> legalMove.to == target }
            ?: return

        when (val result = gameController.makeHumanMove(move)) {
            is MoveResult.Success -> {
                markGameStateChanged()
                syncCellsFromGameState()
                logAppliedMove(move)
            }
            is MoveResult.Invalid -> logDebug("Invalid move: ${result.reason}.")
        }
    }

    fun animateMoveChecker(from: String, to: String) {
        animateMoveChecker(BoardCommandUtil.parseCommand(from), BoardCommandUtil.parseCommand(to))
    }

    fun animateMoveChecker(from: BoardArrayPosition, to: BoardArrayPosition) {
    }

    fun startNewGame() {
        gameController.reset()
        legacyMoveNumberOffset = 0
        markGameStateChanged()
        lastAiMove = null
        resetDrag()
        syncCellsFromGameState()
        scheduleAiMoveIfNeeded()
    }

    fun toGameState(): SerializableGameState {
        val checkerStates = gameState.board.pieces.entries
            .sortedWith(compareBy({ entry -> entry.key.row }, { entry -> entry.key.col }))
            .map { (position, piece) ->
                val command = position.toDisplayString()
                CheckerState(
                    id = "${piece.color.name.lowercase()}-$command",
                    color = piece.color.toCheckerColor(),
                    position = BoardArrayPosition(
                        position.col,
                        position.row,
                    ),
                    isKing = piece.kind == PieceKind.KING,
                )
            }

        return SerializableGameState(
            board = checkerStates,
            currentTurn = gameState.currentTurn.toCheckerColor(),
            moveNumber = legacyMoveNumberOffset + gameState.moveHistory.size,
        )
    }

    fun tryRestoreGameState(state: SerializableGameState): Boolean {
        return runCatching {
            restoreGameState(state)
        }.isSuccess
    }

    fun cancelActiveDrag() {
        cellForDrag?.cancelCapture()
        resetDrag()
    }

    fun checkWinner(): Winner {
        val whiteCount = gameState.board.positionsOf(PlayerColor.WHITE).size
        val blackCount = gameState.board.positionsOf(PlayerColor.BLACK).size

        return when {
            whiteCount == 0 && blackCount > 0 -> Winner.BLACK
            blackCount == 0 && whiteCount > 0 -> Winner.WHITE
            gameState.status is GameStatus.Winner -> (gameState.status as GameStatus.Winner).color.toWinner()
            else -> Winner.NONE
        }
    }

    internal fun getCell(command: String): Cell = cells.getCell(BoardCommandUtil.parseCommand(command))

    private fun resetDrag() {
        activePointerId = null
        cellForDrag = null
        dragged = false
        selectedPosition = null
        availableMovesForSelectedPiece = emptyList()
        resetButtonPressed = false
    }

    private fun restoreGameState(state: SerializableGameState) {
        require(state.moveNumber >= 0) {
            "Move number must not be negative."
        }

        val occupiedPositions = HashSet<BoardPosition>()
        val pieces = mutableMapOf<BoardPosition, Piece>()
        for (checkerState in state.board) {
            val position = checkerState.position.toDomainPosition()
            require(occupiedPositions.add(position)) {
                "Duplicate checker position ${checkerState.position}."
            }
            require(position.isDarkSquare()) {
                "Checker must be placed on a black cell."
            }
            pieces[position] = Piece(
                color = checkerState.color.toPlayerColor(),
                kind = if (checkerState.isKing) PieceKind.KING else PieceKind.MAN,
            )
        }

        val restoredState = DomainGameState(
            board = DomainBoardState(pieces),
            currentTurn = state.currentTurn?.toPlayerColor() ?: PlayerColor.WHITE,
        )

        gameController.replaceState(restoredState)
        legacyMoveNumberOffset = state.moveNumber
        markGameStateChanged()
        lastAiMove = null
        resetDrag()
        syncCellsFromGameState()
        scheduleAiMoveIfNeeded()
    }

    private fun syncCellsFromGameState() {
        cells.clearCheckers()
        for (piece in renderMapper.toRenderablePieces(gameState)) {
            val position = BoardArrayPosition(piece.position.col, piece.position.row)
            cells.getCell(position).setChecker(piece.color.toGameEnvType(), piece.kind)
        }
    }

    private fun scheduleAiMoveIfNeeded() {
        if (Gdx.app == null || !gameController.isAiTurn() || aiCalculationRunning) {
            return
        }

        val request = gameController.createAiMoveRequest() ?: return
        val taskVersion = aiTaskVersion
        aiCalculationRunning = true
        logDebug("AI thinking...")

        aiExecutor.execute {
            val startedAt = System.currentTimeMillis()
            val result = runCatching {
                ai.chooseMove(request)
            }.getOrElse { error ->
                println("AI failed: ${error.message}. Falling back to random move.")
                runCatching {
                    fallbackAi.chooseMove(request)
                }.getOrElse {
                    AiMoveResult(move = null)
                }
            }
            val elapsedMillis = System.currentTimeMillis() - startedAt

            postToRenderThread {
                applyScheduledAiMove(result, elapsedMillis, taskVersion)
            }
        }
    }

    private fun applyScheduledAiMove(result: AiMoveResult, elapsedMillis: Long, taskVersion: Int) {
        if (taskVersion != aiTaskVersion) {
            return
        }

        aiCalculationRunning = false
        val applied = applyAiMoveResult(result, allowFallback = true)
        if (applied is MoveResult.Success) {
            val move = applied.appliedMove.move
            lastAiMove = move
            logDebug(
                "AI ${gameConfig.aiDifficulty}: ${move.from.toDisplayString()}${
                    if (move.type == MoveType.CAPTURE) ":" else "-"
                }${move.to.toDisplayString()}, score=${result.score}, nodes=${result.searchedNodes}, time=${elapsedMillis}ms",
            )
            if (gameController.isAiTurn()) {
                scheduleAiMoveIfNeeded()
            }
        }
    }

    private fun applyAiMoveResult(result: AiMoveResult, allowFallback: Boolean): MoveResult {
        val applied = gameController.applyAiMoveResult(result)
        if (applied is MoveResult.Success) {
            markGameStateChanged()
            syncCellsFromGameState()
            return applied
        }

        if (allowFallback) {
            val fallbackRequest = gameController.createAiMoveRequest()
            val fallbackResult = fallbackRequest?.let(fallbackAi::chooseMove)
            if (fallbackResult != null && fallbackResult.move != null && fallbackResult.move != result.move) {
                return applyAiMoveResult(fallbackResult, allowFallback = false)
            }
        }

        if (applied is MoveResult.Invalid) {
            logDebug("AI move rejected: ${applied.reason}.")
        }
        return applied
    }

    private fun postToRenderThread(action: () -> Unit) {
        if (Gdx.app != null) {
            Gdx.app.postRunnable(action)
        } else {
            action()
        }
    }

    private fun markGameStateChanged() {
        aiTaskVersion++
        aiCalculationRunning = false
    }

    private fun drawKingMarkers() {
        val currentBatch = batch ?: return
        val font = getHudFont() ?: return
        font.setColor(1f, 0.84f, 0f, 1f)
        for (cell in cells) {
            if (cell.checker?.kind == PieceKind.KING) {
                font.draw(currentBatch, "K", cell.x + 19f, cell.y + 16f)
            }
        }
    }

    private fun drawHud() {
        val currentBatch = batch ?: return
        val font = getHudFont() ?: return
        font.setColor(1f, 1f, 1f, 1f)
        font.draw(currentBatch, hudText(), 6f, 16f)
    }

    private fun drawResetButton() {
        val currentBatch = batch ?: return
        val font = getHudFont() ?: return
        if (Gdx.files == null) {
            return
        }

        val oldColor = Color(currentBatch.color)
        currentBatch.setColor(0.08f, 0.09f, 0.10f, 0.88f)
        currentBatch.draw(
            ResourceSingleton.getWhiteCell(),
            RESET_BUTTON_X,
            RESET_BUTTON_Y,
            RESET_BUTTON_WIDTH,
            RESET_BUTTON_HEIGHT,
        )
        currentBatch.setColor(oldColor)

        font.setColor(1f, 1f, 1f, 1f)
        font.draw(currentBatch, RESET_BUTTON_LABEL, RESET_BUTTON_X + 16f, RESET_BUTTON_Y + 8f)
    }

    private fun getHudFont(): BitmapFont? {
        if (Gdx.files == null) {
            return null
        }
        return hudFont ?: BitmapFont(true).also { font ->
            font.data.setScale(0.85f)
            hudFont = font
        }
    }

    private fun hudText(): String {
        val status = gameState.status
        val messageParts = mutableListOf<String>()
        if (status is GameStatus.Winner) {
            messageParts += "${status.color.displayName()} wins"
            messageParts += status.reason.name.lowercase().replace('_', ' ')
        } else {
            when (gameController.turnState) {
                TurnState.AI_THINKING -> messageParts += "AI thinking..."
                TurnState.HUMAN_TURN -> {
                    messageParts += when (gameConfig.gameMode) {
                        GameMode.HUMAN_VS_AI -> "${gameConfig.humanSide.displayName()} to move"
                        GameMode.HUMAN_VS_HUMAN -> "${gameState.currentTurn.displayName()} to move"
                    }
                    when {
                        gameState.forcedPiece != null -> messageParts += "Continue capture"
                        rules.legalMoves(gameState).any { move -> move.type == MoveType.CAPTURE } -> {
                            messageParts += "Capture is mandatory"
                        }
                    }
                }
                TurnState.ANIMATING_MOVE -> messageParts += "Animating move"
                TurnState.GAME_OVER -> messageParts += "Game over"
            }
        }
        lastDebugMessage?.let { messageParts += it }
        return messageParts.joinToString(" | ")
    }

    private fun logAppliedMove(move: Move) {
        val separator = if (move.type == MoveType.CAPTURE) ":" else "-"
        logDebug("${move.from.toDisplayString()}$separator${move.to.toDisplayString()}")
    }

    private fun logDebug(message: String) {
        lastDebugMessage = message
        if (Gdx.app != null) {
            Gdx.app.log("Checkers", message)
        } else {
            println(message)
        }
    }

    private fun isResetButtonHit(x: Float, y: Float): Boolean {
        return x >= RESET_BUTTON_X &&
            x <= RESET_BUTTON_X + RESET_BUTTON_WIDTH &&
            y >= RESET_BUTTON_Y &&
            y <= RESET_BUTTON_Y + RESET_BUTTON_HEIGHT
    }

    private fun BoardArrayPosition.toDomainPosition(): BoardPosition {
        return BoardPosition(indexFirst, indexSecond)
    }

    private fun Cell.domainPosition(): BoardPosition {
        return boardPosition.toDomainPosition()
    }

    private fun BoardPosition.toDisplayString(): String {
        return BoardCommandUtil.checkerPositionToCommand(BoardArrayPosition(col, row))
    }

    private fun PlayerColor.toCheckerColor(): CheckerColor {
        return when (this) {
            PlayerColor.WHITE -> CheckerColor.WHITE
            PlayerColor.BLACK -> CheckerColor.BLACK
        }
    }

    private fun CheckerColor.toPlayerColor(): PlayerColor {
        return when (this) {
            CheckerColor.WHITE -> PlayerColor.WHITE
            CheckerColor.BLACK -> PlayerColor.BLACK
        }
    }

    private fun PlayerColor.toGameEnvType(): GameEnvTypes {
        return when (this) {
            PlayerColor.WHITE -> GameEnvTypes.WHITE
            PlayerColor.BLACK -> GameEnvTypes.BLACK
        }
    }

    private fun PlayerColor.toWinner(): Winner {
        return when (this) {
            PlayerColor.WHITE -> Winner.WHITE
            PlayerColor.BLACK -> Winner.BLACK
        }
    }

    private fun PlayerColor.displayName(): String {
        return name.lowercase().replaceFirstChar { char -> char.uppercase() }
    }

    companion object {
        private const val RESET_BUTTON_LABEL = "Reset"
        private const val RESET_BUTTON_WIDTH = 76f
        private const val RESET_BUTTON_HEIGHT = 28f
        private const val RESET_BUTTON_X = 6f
        private const val RESET_BUTTON_Y = 22f

        private fun graphicsWidth(): Float {
            return (Gdx.graphics?.width ?: BoardConfig.BOARD_PIXEL_SIZE).toFloat()
        }

        private fun graphicsHeight(): Float {
            return (Gdx.graphics?.height ?: BoardConfig.BOARD_PIXEL_SIZE).toFloat()
        }
    }
}
