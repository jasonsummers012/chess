package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    public boolean OutOfBounds(int row, int col) {
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return true;
        }
        return false;
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        switch (type) {
            case KING:
                for (int row = currentRow - 1; row <= currentRow + 1; row ++) {
                    for (int col = currentCol - 1; col <= currentCol + 1; col ++) {
                        if (row == currentRow && col == currentCol) {
                            continue;
                        } if (OutOfBounds(row, col)) {
                            continue;
                        }
                        ChessPosition endPosition = new ChessPosition(row, col);
                        ChessMove move = new ChessMove(myPosition, endPosition, null);
                        possibleMoves.add(move);
                    }
                }
                break;
        }
        return possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
