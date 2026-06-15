package com.infeez.simple.game.rules

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
import com.infeez.simple.game.model.WinReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RussianCheckersRulesTest {
    private val rules = RussianCheckersRules()

    @Test
    fun createInitialState_startsWhiteTurnInProgress() {
        val state = rules.createInitialState()

        assertEquals(PlayerColor.WHITE, state.currentTurn)
        assertEquals(GameStatus.InProgress, state.status)
        assertEquals(24, state.board.pieces.size)
    }

    @Test
    fun legalMoves_whiteMan_movesForwardOnly() {
        val from = BoardPosition(2, 5)
        val moves = rules.legalMovesForPiece(state(from to whiteMan()), from)

        assertTrue(moves.contains(simple(from, BoardPosition(1, 4))))
        assertTrue(moves.contains(simple(from, BoardPosition(3, 4))))
        assertFalse(moves.contains(simple(from, BoardPosition(1, 6))))
        assertFalse(moves.contains(simple(from, BoardPosition(3, 6))))
    }

    @Test
    fun legalMoves_blackMan_movesForwardInOppositeDirection() {
        val from = BoardPosition(3, 2)
        val moves = rules.legalMovesForPiece(
            state(from to blackMan(), currentTurn = PlayerColor.BLACK),
            from,
        )

        assertTrue(moves.contains(simple(from, BoardPosition(2, 3))))
        assertTrue(moves.contains(simple(from, BoardPosition(4, 3))))
    }

    @Test
    fun legalMoves_manDoesNotMoveToOccupiedTarget() {
        val from = BoardPosition(2, 5)
        val occupied = BoardPosition(1, 4)
        val moves = rules.legalMovesForPiece(
            state(from to whiteMan(), occupied to blackMan()),
            from,
        )

        assertFalse(moves.contains(simple(from, occupied)))
    }

    @Test
    fun applyMove_manCannotMoveToLightSquareOrAcrossBoard() {
        val from = BoardPosition(2, 5)
        val lightSquareMove = simple(from, BoardPosition(2, 4))
        val longMove = simple(from, BoardPosition(7, 0))

        assertInvalid(InvalidMoveReason.TARGET_NOT_DARK_SQUARE, rules.applyMove(state(from to whiteMan()), lightSquareMove))
        assertInvalid(InvalidMoveReason.ILLEGAL_MAN_MOVE, rules.applyMove(state(from to whiteMan()), longMove))
    }

    @Test
    fun legalMoves_whenCaptureExists_returnsOnlyCaptures() {
        val from = BoardPosition(2, 5)
        val captured = BoardPosition(3, 4)
        val target = BoardPosition(4, 3)
        val moves = rules.legalMoves(state(from to whiteMan(), captured to blackMan()))

        assertEquals(listOf(capture(from, target, captured)), moves)
    }

    @Test
    fun applyMove_simpleMoveWhenCaptureExists_returnsForcedCaptureAvailable() {
        val from = BoardPosition(2, 5)
        val captured = BoardPosition(3, 4)
        val result = rules.applyMove(
            state(from to whiteMan(), captured to blackMan()),
            simple(from, BoardPosition(1, 4)),
        )

        assertInvalid(InvalidMoveReason.FORCED_CAPTURE_AVAILABLE, result)
    }

    @Test
    fun legalMoves_manCanCaptureForwardAndBackward() {
        val forwardFrom = BoardPosition(2, 5)
        val forwardCaptured = BoardPosition(3, 4)
        val backwardFrom = BoardPosition(4, 3)
        val backwardCaptured = BoardPosition(3, 4)

        assertTrue(
            rules.legalMovesForPiece(state(forwardFrom to whiteMan(), forwardCaptured to blackMan()), forwardFrom)
                .contains(capture(forwardFrom, BoardPosition(4, 3), forwardCaptured)),
        )
        assertTrue(
            rules.legalMovesForPiece(state(backwardFrom to whiteMan(), backwardCaptured to blackMan()), backwardFrom)
                .contains(capture(backwardFrom, BoardPosition(2, 5), backwardCaptured)),
        )
    }

    @Test
    fun applyMove_captureRemovesOpponentPiece() {
        val from = BoardPosition(2, 5)
        val captured = BoardPosition(3, 4)
        val target = BoardPosition(4, 3)

        val result = success(rules.applyMove(state(from to whiteMan(), captured to blackMan()), capture(from, target, captured)))

        assertNull(result.state.board.pieceAt(from))
        assertNull(result.state.board.pieceAt(captured))
        assertEquals(whiteMan(), result.state.board.pieceAt(target))
    }

    @Test
    fun applyMove_manCannotCaptureOwnPieceOrLandOnOccupiedSquare() {
        val from = BoardPosition(2, 5)
        val captured = BoardPosition(3, 4)
        val target = BoardPosition(4, 3)

        assertInvalid(
            InvalidMoveReason.OWN_PIECE_ON_CAPTURE_PATH,
            rules.applyMove(state(from to whiteMan(), captured to whiteMan()), capture(from, target, captured)),
        )
        assertInvalid(
            InvalidMoveReason.TARGET_NOT_EMPTY,
            rules.applyMove(
                state(from to whiteMan(), captured to blackMan(), target to blackMan()),
                capture(from, target, captured),
            ),
        )
    }

    @Test
    fun applyMove_multiCaptureKeepsTurnAndForcesSamePiece() {
        val firstFrom = BoardPosition(2, 5)
        val firstCaptured = BoardPosition(3, 4)
        val firstTarget = BoardPosition(4, 3)
        val secondCaptured = BoardPosition(5, 2)
        val secondTarget = BoardPosition(6, 1)
        val otherWhite = BoardPosition(0, 5)

        val firstResult = success(
            rules.applyMove(
                state(firstFrom to whiteMan(), firstCaptured to blackMan(), secondCaptured to blackMan(), otherWhite to whiteMan()),
                capture(firstFrom, firstTarget, firstCaptured),
            ),
        )

        assertEquals(PlayerColor.WHITE, firstResult.state.currentTurn)
        assertEquals(firstTarget, firstResult.state.forcedPiece)
        assertInvalid(
            InvalidMoveReason.MUST_CONTINUE_CAPTURE_WITH_SAME_PIECE,
            rules.applyMove(firstResult.state, simple(otherWhite, BoardPosition(1, 4))),
        )

        val secondResult = success(
            rules.applyMove(firstResult.state, capture(firstTarget, secondTarget, secondCaptured)),
        )

        assertEquals(PlayerColor.BLACK, secondResult.state.currentTurn)
        assertNull(secondResult.state.forcedPiece)
    }

    @Test
    fun applyMove_promotesWhiteAndBlackMenOnLastRow() {
        val whiteFrom = BoardPosition(2, 1)
        val whiteTarget = BoardPosition(1, 0)
        val blackFrom = BoardPosition(1, 6)
        val blackTarget = BoardPosition(0, 7)

        val whiteResult = success(rules.applyMove(state(whiteFrom to whiteMan()), simple(whiteFrom, whiteTarget)))
        val blackResult = success(
            rules.applyMove(
                state(blackFrom to blackMan(), currentTurn = PlayerColor.BLACK),
                simple(blackFrom, blackTarget),
            ),
        )

        assertEquals(Piece(PlayerColor.WHITE, PieceKind.KING), whiteResult.state.board.pieceAt(whiteTarget))
        assertEquals(Piece(PlayerColor.BLACK, PieceKind.KING), blackResult.state.board.pieceAt(blackTarget))
    }

    @Test
    fun applyMove_promotesDuringCaptureBeforeCheckingContinuation() {
        val from = BoardPosition(3, 2)
        val firstCaptured = BoardPosition(4, 1)
        val promotedAt = BoardPosition(5, 0)
        val secondCaptured = BoardPosition(2, 3)
        val kingLanding = BoardPosition(1, 4)

        val result = success(
            rules.applyMove(
                state(from to whiteMan(), firstCaptured to blackMan(), secondCaptured to blackMan()),
                capture(from, promotedAt, firstCaptured),
            ),
        )

        assertEquals(Piece(PlayerColor.WHITE, PieceKind.KING), result.state.board.pieceAt(promotedAt))
        assertEquals(promotedAt, result.state.forcedPiece)
        assertTrue(
            rules.legalMovesForPiece(result.state, promotedAt)
                .contains(capture(promotedAt, kingLanding, secondCaptured)),
        )
    }

    @Test
    fun legalMoves_kingMovesAcrossEmptyDiagonalButNotThroughPieces() {
        val king = BoardPosition(2, 5)
        val blocker = BoardPosition(4, 3)
        val emptyStateMoves = rules.legalMovesForPiece(state(king to whiteKing()), king)
        val blockedStateMoves = rules.legalMovesForPiece(state(king to whiteKing(), blocker to whiteMan()), king)

        assertTrue(emptyStateMoves.contains(simple(king, BoardPosition(7, 0))))
        assertTrue(emptyStateMoves.contains(simple(king, BoardPosition(0, 7))))
        assertFalse(emptyStateMoves.contains(simple(king, BoardPosition(4, 5))))
        assertFalse(blockedStateMoves.contains(simple(king, BoardPosition(5, 2))))
    }

    @Test
    fun legalMoves_kingCapturesOpponentAtDistanceToAnyFreeSquareBehindIt() {
        val king = BoardPosition(2, 5)
        val captured = BoardPosition(4, 3)
        val moves = rules.legalMovesForPiece(state(king to whiteKing(), captured to blackMan()), king)

        assertTrue(moves.contains(capture(king, BoardPosition(5, 2), captured)))
        assertTrue(moves.contains(capture(king, BoardPosition(6, 1), captured)))
        assertTrue(moves.contains(capture(king, BoardPosition(7, 0), captured)))
        assertTrue(moves.all { move -> move.type == MoveType.CAPTURE })
    }

    @Test
    fun legalMoves_kingDoesNotCaptureThroughOwnPieceOrTwoAdjacentPieces() {
        val king = BoardPosition(2, 5)
        val first = BoardPosition(3, 4)
        val second = BoardPosition(4, 3)

        assertTrue(
            rules.legalMovesForPiece(state(king to whiteKing(), first to whiteMan()), king)
                .none { move -> move.type == MoveType.CAPTURE },
        )
        assertTrue(
            rules.legalMovesForPiece(state(king to whiteKing(), first to blackMan(), second to blackMan()), king)
                .none { move -> move.type == MoveType.CAPTURE },
        )
    }

    @Test
    fun applyMove_kingCanContinueCaptureSeries() {
        val king = BoardPosition(2, 5)
        val firstCaptured = BoardPosition(4, 3)
        val firstTarget = BoardPosition(5, 2)
        val secondCaptured = BoardPosition(4, 1)
        val secondTarget = BoardPosition(3, 0)

        val firstResult = success(
            rules.applyMove(
                state(king to whiteKing(), firstCaptured to blackMan(), secondCaptured to blackMan()),
                capture(king, firstTarget, firstCaptured),
            ),
        )

        assertEquals(firstTarget, firstResult.state.forcedPiece)
        assertTrue(
            rules.legalMovesForPiece(firstResult.state, firstTarget)
                .contains(capture(firstTarget, secondTarget, secondCaptured)),
        )
    }

    @Test
    fun applyMove_whenOpponentHasNoPieces_winsGame() {
        val from = BoardPosition(2, 5)
        val captured = BoardPosition(3, 4)
        val target = BoardPosition(4, 3)

        val result = success(rules.applyMove(state(from to whiteMan(), captured to blackMan()), capture(from, target, captured)))

        assertEquals(
            GameStatus.Winner(PlayerColor.WHITE, WinReason.OPPONENT_HAS_NO_PIECES),
            result.state.status,
        )
    }

    @Test
    fun applyMove_whenOpponentHasNoLegalMoves_winsGame() {
        val from = BoardPosition(2, 5)
        val target = BoardPosition(1, 4)
        val blockedBlack = BoardPosition(0, 7)

        val result = success(rules.applyMove(state(from to whiteMan(), blockedBlack to blackMan()), simple(from, target)))

        assertEquals(
            GameStatus.Winner(PlayerColor.WHITE, WinReason.OPPONENT_HAS_NO_LEGAL_MOVES),
            result.state.status,
        )
    }

    private fun state(
        vararg pieces: Pair<BoardPosition, Piece>,
        currentTurn: PlayerColor = PlayerColor.WHITE,
        forcedPiece: BoardPosition? = null,
        status: GameStatus = GameStatus.InProgress,
    ): GameState {
        return GameState(
            board = BoardState(mapOf(*pieces)),
            currentTurn = currentTurn,
            status = status,
            forcedPiece = forcedPiece,
        )
    }

    private fun simple(from: BoardPosition, to: BoardPosition): Move {
        return Move(from = from, to = to, type = MoveType.SIMPLE)
    }

    private fun capture(from: BoardPosition, to: BoardPosition, captured: BoardPosition): Move {
        return Move(from = from, to = to, type = MoveType.CAPTURE, captured = captured)
    }

    private fun whiteMan(): Piece = Piece(PlayerColor.WHITE, PieceKind.MAN)

    private fun blackMan(): Piece = Piece(PlayerColor.BLACK, PieceKind.MAN)

    private fun whiteKing(): Piece = Piece(PlayerColor.WHITE, PieceKind.KING)

    private fun success(result: MoveResult): MoveResult.Success {
        return result as MoveResult.Success
    }

    private fun assertInvalid(expected: InvalidMoveReason, result: MoveResult) {
        assertEquals(MoveResult.Invalid(expected), result)
    }
}
