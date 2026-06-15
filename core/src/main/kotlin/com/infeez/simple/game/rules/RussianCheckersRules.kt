package com.infeez.simple.game.rules

import com.infeez.simple.game.engine.GameEndEvaluator
import com.infeez.simple.game.model.AppliedMove
import com.infeez.simple.game.model.BoardPosition
import com.infeez.simple.game.model.BoardState
import com.infeez.simple.game.model.GameState
import com.infeez.simple.game.model.GameStatus
import com.infeez.simple.game.model.InvalidMoveReason
import com.infeez.simple.game.model.Move
import com.infeez.simple.game.model.MoveResult
import com.infeez.simple.game.model.MoveType
import com.infeez.simple.game.model.Piece
import com.infeez.simple.game.model.PieceKind
import com.infeez.simple.game.model.PlayerColor
import kotlin.math.abs
import kotlin.math.sign

class RussianCheckersRules : CheckersRules {
    private val endEvaluator = GameEndEvaluator(this)

    override fun createInitialState(): GameState {
        return GameState(
            board = InitialBoardFactory.russianCheckers(),
            currentTurn = PlayerColor.WHITE,
        )
    }

    override fun legalMoves(state: GameState): List<Move> {
        if (state.status != GameStatus.InProgress) {
            return emptyList()
        }

        val forcedPiece = state.forcedPiece
        if (forcedPiece != null) {
            return captureMovesForPiece(state.board, forcedPiece, state.currentTurn)
        }

        val captures = state.board
            .positionsOf(state.currentTurn)
            .flatMap { position -> captureMovesForPiece(state.board, position, state.currentTurn) }

        if (captures.isNotEmpty()) {
            return captures
        }

        return state.board
            .positionsOf(state.currentTurn)
            .flatMap { position -> simpleMovesForPiece(state.board, position, state.currentTurn) }
    }

    override fun legalMovesForPiece(state: GameState, from: BoardPosition): List<Move> {
        return legalMoves(state).filter { move -> move.from == from }
    }

    override fun applyMove(state: GameState, move: Move): MoveResult {
        if (state.status != GameStatus.InProgress) {
            return MoveResult.Invalid(InvalidMoveReason.GAME_ALREADY_FINISHED)
        }
        if (!move.from.isInsideBoard()) {
            return MoveResult.Invalid(InvalidMoveReason.SOURCE_OUT_OF_BOARD)
        }
        if (!move.to.isInsideBoard()) {
            return MoveResult.Invalid(InvalidMoveReason.TARGET_OUT_OF_BOARD)
        }

        val movedPieceBefore = state.board.pieceAt(move.from)
            ?: return MoveResult.Invalid(InvalidMoveReason.SOURCE_EMPTY)

        if (movedPieceBefore.color != state.currentTurn) {
            return MoveResult.Invalid(InvalidMoveReason.NOT_CURRENT_PLAYER_PIECE)
        }
        if (state.board.isOccupied(move.to)) {
            return MoveResult.Invalid(InvalidMoveReason.TARGET_NOT_EMPTY)
        }
        if (!move.to.isDarkSquare()) {
            return MoveResult.Invalid(InvalidMoveReason.TARGET_NOT_DARK_SQUARE)
        }

        val legalMoves = legalMoves(state)
        if (move !in legalMoves) {
            return MoveResult.Invalid(invalidReasonForRejectedMove(state, move, movedPieceBefore, legalMoves))
        }

        var nextBoard = state.board.move(move.from, move.to)
        if (move.type == MoveType.CAPTURE) {
            nextBoard = nextBoard.remove(move.captured ?: return MoveResult.Invalid(InvalidMoveReason.UNKNOWN))
        }

        var movedPieceAfter = movedPieceBefore
        if (shouldPromote(movedPieceAfter, move.to)) {
            nextBoard = nextBoard.promote(move.to)
            movedPieceAfter = movedPieceAfter.copy(kind = PieceKind.KING)
        }

        var turnAfter = state.currentTurn
        var forcedPiece: BoardPosition? = null

        if (move.type == MoveType.CAPTURE) {
            val continuationCaptures = captureMovesForPiece(nextBoard, move.to, state.currentTurn)
            if (continuationCaptures.isNotEmpty()) {
                forcedPiece = move.to
            } else {
                turnAfter = state.currentTurn.opponent()
            }
        } else {
            turnAfter = state.currentTurn.opponent()
        }

        val stateBeforeEndEvaluation = state.copy(
            board = nextBoard,
            currentTurn = turnAfter,
            status = GameStatus.InProgress,
            forcedPiece = forcedPiece,
        )
        val statusAfter = if (forcedPiece == null) {
            endEvaluator.evaluate(stateBeforeEndEvaluation)
        } else {
            GameStatus.InProgress
        }
        val appliedMove = AppliedMove(
            move = move,
            movedPieceBefore = movedPieceBefore,
            movedPieceAfter = movedPieceAfter,
            turnBefore = state.currentTurn,
            turnAfter = turnAfter,
            statusAfter = statusAfter,
        )

        return MoveResult.Success(
            state = stateBeforeEndEvaluation.copy(
                status = statusAfter,
                moveHistory = state.moveHistory + appliedMove,
            ),
            appliedMove = appliedMove,
        )
    }

    fun shouldPromote(piece: Piece, position: BoardPosition): Boolean {
        return piece.kind == PieceKind.MAN &&
            when (piece.color) {
                PlayerColor.WHITE -> position.row == 0
                PlayerColor.BLACK -> position.row == BoardPosition.BOARD_SIZE - 1
            }
    }

    private fun invalidReasonForRejectedMove(
        state: GameState,
        move: Move,
        piece: Piece,
        legalMoves: List<Move>,
    ): InvalidMoveReason {
        if (state.forcedPiece != null && move.from != state.forcedPiece) {
            return InvalidMoveReason.MUST_CONTINUE_CAPTURE_WITH_SAME_PIECE
        }
        if (legalMoves.any { legalMove -> legalMove.type == MoveType.CAPTURE } && move.type == MoveType.SIMPLE) {
            return InvalidMoveReason.FORCED_CAPTURE_AVAILABLE
        }
        if (move.type == MoveType.CAPTURE) {
            return invalidCaptureReason(state.board, move, piece)
        }

        return when (piece.kind) {
            PieceKind.MAN -> InvalidMoveReason.ILLEGAL_MAN_MOVE
            PieceKind.KING -> InvalidMoveReason.ILLEGAL_KING_MOVE
        }
    }

    private fun invalidCaptureReason(board: BoardState, move: Move, piece: Piece): InvalidMoveReason {
        return when (piece.kind) {
            PieceKind.MAN -> invalidManCaptureReason(board, move, piece)
            PieceKind.KING -> invalidKingCaptureReason(board, move, piece)
        }
    }

    private fun invalidManCaptureReason(board: BoardState, move: Move, piece: Piece): InvalidMoveReason {
        val dc = move.to.col - move.from.col
        val dr = move.to.row - move.from.row
        if (abs(dc) != MAN_CAPTURE_DISTANCE || abs(dr) != MAN_CAPTURE_DISTANCE) {
            return InvalidMoveReason.ILLEGAL_MAN_MOVE
        }

        val captured = BoardPosition(
            col = move.from.col + dc / MAN_CAPTURE_DISTANCE,
            row = move.from.row + dr / MAN_CAPTURE_DISTANCE,
        )
        val capturedPiece = board.pieceAt(captured) ?: return InvalidMoveReason.NO_OPPONENT_TO_CAPTURE
        if (capturedPiece.color == piece.color) {
            return InvalidMoveReason.OWN_PIECE_ON_CAPTURE_PATH
        }

        return InvalidMoveReason.UNKNOWN
    }

    private fun invalidKingCaptureReason(board: BoardState, move: Move, piece: Piece): InvalidMoveReason {
        val dc = move.to.col - move.from.col
        val dr = move.to.row - move.from.row
        if (abs(dc) != abs(dr) || dc == 0) {
            return InvalidMoveReason.ILLEGAL_KING_MOVE
        }

        val stepCol = dc.sign
        val stepRow = dr.sign
        var current = move.from.offset(stepCol, stepRow)
        var opponentSeen = false

        while (current != move.to) {
            val pathPiece = board.pieceAt(current)
            if (pathPiece != null) {
                if (pathPiece.color == piece.color) {
                    return InvalidMoveReason.OWN_PIECE_ON_CAPTURE_PATH
                }
                if (opponentSeen) {
                    return InvalidMoveReason.MULTIPLE_PIECES_ON_CAPTURE_PATH
                }
                opponentSeen = true
            }
            current = current.offset(stepCol, stepRow)
        }

        return if (opponentSeen) {
            InvalidMoveReason.UNKNOWN
        } else {
            InvalidMoveReason.NO_OPPONENT_TO_CAPTURE
        }
    }

    private fun simpleMovesForPiece(board: BoardState, from: BoardPosition, color: PlayerColor): List<Move> {
        val piece = board.pieceAt(from) ?: return emptyList()
        if (piece.color != color) {
            return emptyList()
        }

        return when (piece.kind) {
            PieceKind.MAN -> simpleManMoves(board, from, color)
            PieceKind.KING -> simpleKingMoves(board, from)
        }
    }

    private fun captureMovesForPiece(board: BoardState, from: BoardPosition, color: PlayerColor): List<Move> {
        val piece = board.pieceAt(from) ?: return emptyList()
        if (piece.color != color) {
            return emptyList()
        }

        return when (piece.kind) {
            PieceKind.MAN -> captureManMoves(board, from, color)
            PieceKind.KING -> captureKingMoves(board, from, color)
        }
    }

    private fun simpleManMoves(board: BoardState, from: BoardPosition, color: PlayerColor): List<Move> {
        val rowDelta = when (color) {
            PlayerColor.WHITE -> -1
            PlayerColor.BLACK -> 1
        }

        return SIMPLE_MAN_COL_DELTAS
            .map { colDelta -> from.offset(colDelta, rowDelta) }
            .filter { target -> target.isInsideBoard() && target.isDarkSquare() && board.isEmpty(target) }
            .map { target -> Move(from = from, to = target, type = MoveType.SIMPLE) }
    }

    private fun captureManMoves(board: BoardState, from: BoardPosition, color: PlayerColor): List<Move> {
        return DIAGONAL_DIRECTIONS.mapNotNull { (colDelta, rowDelta) ->
            val captured = from.offset(colDelta, rowDelta)
            val target = from.offset(colDelta * MAN_CAPTURE_DISTANCE, rowDelta * MAN_CAPTURE_DISTANCE)
            val capturedPiece = board.pieceAt(captured)

            if (
                target.isInsideBoard() &&
                target.isDarkSquare() &&
                board.isEmpty(target) &&
                capturedPiece != null &&
                capturedPiece.color != color
            ) {
                Move(
                    from = from,
                    to = target,
                    type = MoveType.CAPTURE,
                    captured = captured,
                )
            } else {
                null
            }
        }
    }

    private fun simpleKingMoves(board: BoardState, from: BoardPosition): List<Move> {
        val moves = mutableListOf<Move>()

        for ((colDelta, rowDelta) in DIAGONAL_DIRECTIONS) {
            var target = from.offset(colDelta, rowDelta)
            while (target.isInsideBoard() && board.isEmpty(target)) {
                moves += Move(from = from, to = target, type = MoveType.SIMPLE)
                target = target.offset(colDelta, rowDelta)
            }
        }

        return moves
    }

    private fun captureKingMoves(board: BoardState, from: BoardPosition, color: PlayerColor): List<Move> {
        val moves = mutableListOf<Move>()

        for ((colDelta, rowDelta) in DIAGONAL_DIRECTIONS) {
            var current = from.offset(colDelta, rowDelta)
            var captured: BoardPosition? = null

            while (current.isInsideBoard()) {
                val piece = board.pieceAt(current)
                if (piece == null) {
                    val capturedPosition = captured
                    if (capturedPosition != null) {
                        moves += Move(
                            from = from,
                            to = current,
                            type = MoveType.CAPTURE,
                            captured = capturedPosition,
                        )
                    }
                } else {
                    if (piece.color == color || captured != null) {
                        break
                    }
                    captured = current
                }
                current = current.offset(colDelta, rowDelta)
            }
        }

        return moves
    }

    private companion object {
        const val MAN_CAPTURE_DISTANCE = 2
        val SIMPLE_MAN_COL_DELTAS = listOf(-1, 1)
        val DIAGONAL_DIRECTIONS = listOf(
            -1 to -1,
            1 to -1,
            -1 to 1,
            1 to 1,
        )
    }
}
