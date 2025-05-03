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

            case BISHOP:
                possibleMoves = getBishopMoves(board, myPosition);
                break;

            case QUEEN:
                possibleMoves = getRookMoves(board, myPosition);
                Collection<ChessMove> possibleMoves2 = new ArrayList<>();
                possibleMoves2 = getBishopMoves(board, myPosition);
                possibleMoves.addAll(possibleMoves2);
                break;

            case KNIGHT:
                possibleMoves = getKnightMoves(board, myPosition);
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

    public Collection<ChessMove> getBishopMoves (ChessBoard board, ChessPosition currentPosition) {
        int currentRow = currentPosition.getRow();
        int currentCol = currentPosition.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        for (int steps = 1; steps <= 8; steps ++) {
            if (OutOfBounds(currentRow + steps, currentCol + steps)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(currentRow + steps, currentCol + steps);
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

        for (int steps = 1; steps <= 8; steps ++) {
            if (OutOfBounds(currentRow + steps, currentCol - steps)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(currentRow + steps, currentCol - steps);
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

        for (int steps = 1; steps <= 8; steps ++) {
            if (OutOfBounds(currentRow - steps, currentCol - steps)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(currentRow - steps, currentCol - steps);
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

        for (int steps = 1; steps <= 8; steps ++) {
            if (OutOfBounds(currentRow - steps, currentCol + steps)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(currentRow - steps, currentCol + steps);
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

    public Collection<ChessMove> getKnightMoves (ChessBoard board, ChessPosition currentPosition) {
        int currentRow = currentPosition.getRow();
        int currentCol = currentPosition.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        ChessPosition endPosition1 = new ChessPosition(currentRow + 2, currentCol + 1);
        if (!OutOfBounds(currentRow + 2, currentCol + 1)) {
            ChessPiece otherPiece1 = board.getPiece(endPosition1);
            if (!pieceBlocking(this, otherPiece1)) {
                ChessMove move = new ChessMove(currentPosition, endPosition1, null);
                possibleMoves.add(move);
            }
        }
        ChessPosition endPosition2 = new ChessPosition(currentRow + 1, currentCol + 2);
        if (!OutOfBounds(currentRow + 1, currentCol + 2)) {
            ChessPiece otherPiece2 = board.getPiece(endPosition2);
            if (!pieceBlocking(this, otherPiece2)) {
                ChessMove move = new ChessMove(currentPosition, endPosition2, null);
                possibleMoves.add(move);
            }
        }
        ChessPosition endPosition3 = new ChessPosition(currentRow - 1, currentCol + 2);
        if (!OutOfBounds(currentRow - 1, currentCol + 2)) {
            ChessPiece otherPiece3 = board.getPiece(endPosition3);
            if (!pieceBlocking(this, otherPiece3)) {
                ChessMove move = new ChessMove(currentPosition, endPosition3, null);
                possibleMoves.add(move);
            }
        }
        ChessPosition endPosition4 = new ChessPosition(currentRow - 2, currentCol + 1);
        if (!OutOfBounds(currentRow - 2, currentCol + 1)) {
            ChessPiece otherPiece4 = board.getPiece(endPosition4);
            if (!pieceBlocking(this, otherPiece4)) {
                ChessMove move = new ChessMove(currentPosition, endPosition4, null);
                possibleMoves.add(move);
            }
        }
        ChessPosition endPosition5 = new ChessPosition(currentRow - 2, currentCol - 1);
        if (!OutOfBounds(currentRow - 2, currentCol - 1)) {
            ChessPiece otherPiece5 = board.getPiece(endPosition5);
             if (!pieceBlocking(this, otherPiece5)) {
                 ChessMove move = new ChessMove(currentPosition, endPosition5, null);
                 possibleMoves.add(move);
             }
        }
        ChessPosition endPosition6 = new ChessPosition(currentRow - 1, currentCol - 2);
        if (!OutOfBounds(currentRow - 1, currentCol - 2)) {
            ChessPiece otherPiece6 = board.getPiece(endPosition6);
            if (!pieceBlocking(this, otherPiece6)) {
                ChessMove move = new ChessMove(currentPosition, endPosition6, null);
                possibleMoves.add(move);
            }
        }
        ChessPosition endPosition7 = new ChessPosition(currentRow + 1, currentCol - 2);
        if (!OutOfBounds(currentRow + 1, currentCol - 2)) {
            ChessPiece otherPiece7 = board.getPiece(endPosition7);
            if (!pieceBlocking(this, otherPiece7)) {
                ChessMove move = new ChessMove(currentPosition, endPosition7, null);
                possibleMoves.add(move);
            }
        }
        ChessPosition endPosition8 = new ChessPosition(currentRow + 2, currentCol - 1);
        if (!OutOfBounds(currentRow + 2, currentCol - 1 )) {
            ChessPiece otherPiece8 = board.getPiece(endPosition8);
            if (!pieceBlocking(this, otherPiece8)) {
                ChessMove move = new ChessMove(currentPosition, endPosition8, null);
                possibleMoves.add(move);
            }
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
