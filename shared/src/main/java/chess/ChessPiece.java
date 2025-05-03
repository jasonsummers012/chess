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
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
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
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        switch (type) {
            case KING:
                possibleMoves = getKingMoves(board, myPosition);
                break;

            case ROOK:
                possibleMoves = getRookMoves(board, myPosition);
                break;
        }
        return possibleMoves;
    }

    public Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition currentPosition) {
        int currentRow = currentPosition.getRow();
        int currentCol = currentPosition.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        for (int row = currentRow - 1; row <= currentRow + 1; row ++) {
            for (int col = currentCol - 1; col <= currentCol + 1; col ++) {
                if (row == currentRow && col == currentCol) {
                    continue;
                } if (OutOfBounds(row, col)) {
                    continue;
                }
                ChessPosition endPosition = new ChessPosition(row, col);
                ChessPiece blockingPiece = board.getPiece(endPosition);
                if (pieceBlocking(this, blockingPiece)) {
                    continue;
                }
                ChessMove move = new ChessMove(currentPosition, endPosition, null);
                possibleMoves.add(move);
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition currentPosition) {
        int currentRow = currentPosition.getRow();
        int currentCol = currentPosition.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        for (int row = currentRow; row <= 8; row ++) {
            if (row == currentRow) {
                continue;
            } if (OutOfBounds(row, currentCol)) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(row, currentCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            if (pieceCaptured(this, otherPiece)) {
                ChessMove move = new ChessMove(currentPosition, endPosition, null);
                possibleMoves.add(move);
                break;
            }
            ChessMove move = new ChessMove(currentPosition, endPosition, null);
            possibleMoves.add(move);
        }
        for (int row = currentRow; row >= 1; row --) {
            if (row == currentRow) {
                continue;
            } if (OutOfBounds(row, currentCol)) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(row, currentCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            if (pieceCaptured(this, otherPiece)) {
                ChessMove move = new ChessMove(currentPosition, endPosition, null);
                possibleMoves.add(move);
                break;
            }
            ChessMove move = new ChessMove(currentPosition, endPosition, null);
            possibleMoves.add(move);
        }
        for (int col = currentCol; col <= 8; col ++) {
            if (col == currentCol) {
                continue;
            } if (OutOfBounds(currentRow, col)) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(currentRow, col);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            if (pieceCaptured(this, otherPiece)) {
                ChessMove move = new ChessMove(currentPosition, endPosition, null);
                possibleMoves.add(move);
                break;
            }
            ChessMove move = new ChessMove(currentPosition, endPosition, null);
            possibleMoves.add(move);
        }
        for (int col = currentCol; col >= 1; col --) {
            if (col == currentCol) {
                continue;
            } if (OutOfBounds(currentRow, col)) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(currentRow, col);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            if (pieceCaptured(this, otherPiece)) {
                ChessMove move = new ChessMove(currentPosition, endPosition, null);
                possibleMoves.add(move);
                break;
            }
            ChessMove move = new ChessMove(currentPosition, endPosition, null);
            possibleMoves.add(move);
        }
        return possibleMoves;
    }

    public boolean OutOfBounds(int row, int col) {
        return row < 1 || row > 8 || col < 1 || col > 8;
    }

    public boolean pieceBlocking(ChessPiece currentPiece, ChessPiece blockingPiece) {
        return blockingPiece != null && currentPiece.getTeamColor() == blockingPiece.getTeamColor();
    }

    public boolean pieceCaptured(ChessPiece currentPiece, ChessPiece capturedPiece) {
        return capturedPiece != null && currentPiece.getTeamColor() != capturedPiece.getTeamColor();
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
