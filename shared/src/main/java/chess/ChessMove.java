package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType promote;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promote = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promote;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessMove chessMove)) {
            return false;
        }
        return Objects.equals(start, chessMove.start) && Objects.equals(end, chessMove.end) && promote == chessMove.promote;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promote);
    }

    @Override
    public String toString() {
        String startPos = positionToChessNotation(start);
        String endPos = positionToChessNotation(end);

        if (promote != null) {
            return startPos + " to " + endPos + " (promote to " + promote + ")";
        } else {
            return startPos + " to " + endPos;
        }
    }

    private String positionToChessNotation(ChessPosition position) {

        int row = position.getRow();
        int col = position.getColumn();

        char colLetter = (char) ('a' + col - 1);

        return "" + colLetter + row;
    }
}
