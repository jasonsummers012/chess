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
    private ChessPiece.PieceType type;

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
                possibleMoves = getQueenMoves(board, myPosition);
                break;
            case KNIGHT:
                possibleMoves = getKnightMoves(board, myPosition);
                break;
            case PAWN:
                possibleMoves = getPawnMoves(board, myPosition);
        }
        return possibleMoves;
    }

    public Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition startPosition) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        for (int row = startRow - 1; row <= startRow + 1; row ++) {
            for (int col = startCol - 1; col <= startCol + 1; col ++) {
                if (outOfBounds(row, col)) {
                    continue;
                }
                if (row == startRow && col == startCol) {
                    continue;
                }
                ChessPosition endPosition = new ChessPosition(row, col);
                ChessPiece otherPiece = board.getPiece(endPosition);
                if (pieceBlocking(this, otherPiece)) {
                    continue;
                }
                ChessMove move = new ChessMove(startPosition, endPosition, null);
                moves.add(move);
            }
        }
        return moves;
    }

    public Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition startPosition) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        for (int row = startRow; row >= 1; row --) {
            if (outOfBounds(row, startCol)) {
                continue;
            }
            if (row == startRow) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(row, startCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            if (pieceCaptured(this, otherPiece)) {
                moves.add(move);
                break;
            }
            moves.add(move);
        }

        for (int row = startRow; row <= 8; row ++) {
            if (outOfBounds(row, startCol)) {
                continue;
            }
            if (row == startRow) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(row, startCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            if (pieceCaptured(this, otherPiece)) {
                moves.add(move);
                break;
            }
            moves.add(move);
        }

        for (int col = startCol; col <= 8; col ++) {
            if (outOfBounds(startRow, col)) {
                continue;
            }
            if (col == startCol) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(startRow, col);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            if (pieceCaptured(this, otherPiece)) {
                moves.add(move);
                break;
            }
            moves.add(move);
        }

        for (int col = startCol; col >= 1; col --) {
            if (outOfBounds(startRow, col)) {
                continue;
            }
            if (col == startCol) {
                continue;
            }
            ChessPosition endPosition = new ChessPosition(startRow, col);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            if (pieceCaptured(this, otherPiece)) {
                moves.add(move);
                break;
            }
            moves.add(move);
        }
        return moves;
    }

    public Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition startPosition) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        for (int steps = 1; steps <= 8; steps ++) {
            int newRow = startRow + steps;
            int newCol = startCol + steps;
            if (outOfBounds(newRow, newCol)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(newRow, newCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            if (pieceCaptured(this, otherPiece)) {
                moves.add(move);
                break;
            }
            moves.add(move);
        }

        for (int steps = 1; steps <= 8; steps ++) {
            int newRow = startRow - steps;
            int newCol = startCol + steps;
            if (outOfBounds(newRow, newCol)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(newRow, newCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            if (pieceCaptured(this, otherPiece)) {
                moves.add(move);
                break;
            }
            moves.add(move);
        }

        for (int steps = 1; steps <= 8; steps ++) {
            int newRow = startRow - steps;
            int newCol = startCol - steps;
            if (outOfBounds(newRow, newCol)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(newRow, newCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            if (pieceCaptured(this, otherPiece)) {
                moves.add(move);
                break;
            }
            moves.add(move);
        }

        for (int steps = 1; steps <= 8; steps ++) {
            int newRow = startRow + steps;
            int newCol = startCol - steps;
            if (outOfBounds(newRow, newCol)) {
                break;
            }
            ChessPosition endPosition = new ChessPosition(newRow, newCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            if (pieceCaptured(this, otherPiece)) {
                moves.add(move);
                break;
            }
            moves.add(move);
        }
        return moves;
    }

    public Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves1 = getRookMoves(board, startPosition);
        Collection<ChessMove> moves2 = getBishopMoves(board, startPosition);
        moves1.addAll(moves2);
        return moves1;
    }

    public Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition startPosition) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        if (!outOfBounds(startRow + 2, startCol + 1)) {
            ChessPosition upRight = new ChessPosition(startRow + 2, startCol + 1);
            ChessPiece otherPiece1 = board.getPiece(upRight);
            if (!pieceBlocking(this, otherPiece1)) {
                ChessMove move1 = new ChessMove(startPosition, upRight, null);
                moves.add(move1);
            }
        }
        if (!outOfBounds(startRow + 1, startCol + 2)) {
            ChessPosition rightUp = new ChessPosition(startRow + 1, startCol + 2);
            ChessPiece otherPiece2 = board.getPiece(rightUp);
            if (!pieceBlocking(this, otherPiece2)) {
                ChessMove move2 = new ChessMove(startPosition, rightUp, null);
                moves.add(move2);
            }
        }
        if (!outOfBounds(startRow - 1, startCol + 2)) {
            ChessPosition rightDown = new ChessPosition(startRow - 1, startCol + 2);
            ChessPiece otherPiece3 = board.getPiece(rightDown);
            if (!pieceBlocking(this, otherPiece3)) {
                ChessMove move3 = new ChessMove(startPosition, rightDown, null);
                moves.add(move3);
            }
        }
        if (!outOfBounds(startRow - 2, startCol + 1)) {
            ChessPosition downRight = new ChessPosition(startRow - 2, startCol + 1);
            ChessPiece otherPiece4 = board.getPiece(downRight);
            if (!pieceBlocking(this, otherPiece4)) {
                ChessMove move4 = new ChessMove(startPosition, downRight, null);
                moves.add(move4);
            }
        }
        if (!outOfBounds(startRow - 2, startCol - 1)) {
            ChessPosition downLeft = new ChessPosition(startRow - 2, startCol - 1);
            ChessPiece otherPiece5 = board.getPiece(downLeft);
            if (!pieceBlocking(this, otherPiece5)) {
                ChessMove move5 = new ChessMove(startPosition, downLeft, null);
                moves.add(move5);
            }
        }
        if (!outOfBounds(startRow - 1, startCol - 2)) {
            ChessPosition leftDown = new ChessPosition(startRow - 1, startCol - 2);
            ChessPiece otherPiece6 = board.getPiece(leftDown);
            if (!pieceBlocking(this, otherPiece6)) {
                ChessMove move6 = new ChessMove(startPosition, leftDown, null);
                moves.add(move6);
            }
        }
        if (!outOfBounds(startRow + 1, startCol - 2)) {
            ChessPosition leftUp = new ChessPosition(startRow + 1, startCol - 2);
            ChessPiece otherPiece7 = board.getPiece(leftUp);
            if (!pieceBlocking(this, otherPiece7)) {
                ChessMove move7 = new ChessMove(startPosition, leftUp, null);
                moves.add(move7);
            }
        }
        if (!outOfBounds(startRow + 2, startCol - 1)) {
            ChessPosition upLeft = new ChessPosition(startRow + 2, startCol - 1);
            ChessPiece otherPiece8 = board.getPiece(upLeft);
            if (!pieceBlocking(this, otherPiece8)) {
                ChessMove move8 = new ChessMove(startPosition, upLeft, null);
                moves.add(move8);
            }
        }
        return moves;
    }

    public Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition startPosition) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        if (color == ChessGame.TeamColor.WHITE) {
            if (startRow < 7) {
                ChessPosition ahead1 = new ChessPosition(startRow + 1, startCol);
                ChessPiece otherPiece1 = board.getPiece(ahead1);
                if (!pieceBlocking(this, otherPiece1) && !pieceCaptured(this, otherPiece1)) {
                    ChessMove move1 = new ChessMove(startPosition, ahead1, null);
                    moves.add(move1);
                    if (startRow == 2) {
                        ChessPosition ahead2 = new ChessPosition(startRow + 2, startCol);
                        ChessPiece otherPiece2 = board.getPiece(ahead2);
                        if (!pieceBlocking(this, otherPiece2) && !pieceCaptured(this, otherPiece2)) {
                            ChessMove move2 = new ChessMove(startPosition, ahead2, null);
                            moves.add(move2);
                        }
                    }
                }
                if (!outOfBounds(startRow + 1, startCol - 1)) {
                    ChessPosition diagonal1 = new ChessPosition(startRow + 1, startCol - 1);
                    ChessPiece capturedPiece1 = board.getPiece(diagonal1);
                    if (pieceCaptured(this, capturedPiece1)) {
                        ChessMove capture1 = new ChessMove(startPosition, diagonal1, null);
                        moves.add(capture1);
                    }
                }
                if (!outOfBounds(startRow + 1, startCol + 1)) {
                    ChessPosition diagonal2 = new ChessPosition(startRow + 1, startCol + 1);
                    ChessPiece capturedPiece2 = board.getPiece(diagonal2);
                    if (pieceCaptured(this, capturedPiece2)) {
                        ChessMove capture2 = new ChessMove(startPosition, diagonal2, null);
                        moves.add(capture2);
                    }
                }
            } else {
                ChessPosition ahead1 = new ChessPosition(8, startCol);
                ChessPiece otherPiece = board.getPiece(ahead1);
                if (!pieceBlocking(this, otherPiece) && !pieceCaptured(this, otherPiece)) {
                    ChessMove moveQ = new ChessMove(startPosition, ahead1, PieceType.QUEEN);
                    ChessMove moveR = new ChessMove(startPosition, ahead1, PieceType.ROOK);
                    ChessMove moveB = new ChessMove(startPosition, ahead1, PieceType.BISHOP);
                    ChessMove moveN = new ChessMove(startPosition, ahead1, PieceType.KNIGHT);
                    moves.add(moveQ);
                    moves.add(moveR);
                    moves.add(moveB);
                    moves.add(moveN);
                }
                if (!outOfBounds(startRow + 1, startCol - 1)) {
                    ChessPosition diagonal1 = new ChessPosition(startRow + 1, startCol - 1);
                    ChessPiece capturedPiece1 = board.getPiece(diagonal1);
                    if (pieceCaptured(this, capturedPiece1)) {
                        ChessMove moveQ = new ChessMove(startPosition, diagonal1, PieceType.QUEEN);
                        ChessMove moveR = new ChessMove(startPosition, diagonal1, PieceType.ROOK);
                        ChessMove moveB = new ChessMove(startPosition, diagonal1, PieceType.BISHOP);
                        ChessMove moveN = new ChessMove(startPosition, diagonal1, PieceType.KNIGHT);
                        moves.add(moveQ);
                        moves.add(moveR);
                        moves.add(moveB);
                        moves.add(moveN);
                    }
                }
                if (!outOfBounds(startRow + 1, startCol + 1)) {
                    ChessPosition diagonal2 = new ChessPosition(startRow + 1, startCol + 1);
                    ChessPiece capturedPiece2 = board.getPiece(diagonal2);
                    if (pieceCaptured(this, capturedPiece2)) {
                        ChessMove moveQ = new ChessMove(startPosition, diagonal2, PieceType.QUEEN);
                        ChessMove moveR = new ChessMove(startPosition, diagonal2, PieceType.ROOK);
                        ChessMove moveB = new ChessMove(startPosition, diagonal2, PieceType.BISHOP);
                        ChessMove moveN = new ChessMove(startPosition, diagonal2, PieceType.KNIGHT);
                        moves.add(moveQ);
                        moves.add(moveR);
                        moves.add(moveB);
                        moves.add(moveN);
                    }
                }
            }
        } else {
            if (startRow > 2) {
                ChessPosition ahead1 = new ChessPosition(startRow - 1, startCol);
                ChessPiece otherPiece1 = board.getPiece(ahead1);
                if (!pieceBlocking(this, otherPiece1) && !pieceCaptured(this, otherPiece1)) {
                    ChessMove move1 = new ChessMove(startPosition, ahead1, null);
                    moves.add(move1);
                    if (startRow == 7) {
                        ChessPosition ahead2 = new ChessPosition(startRow - 2, startCol);
                        ChessPiece otherPiece2 = board.getPiece(ahead2);
                        if (!pieceBlocking(this, otherPiece2) && !pieceCaptured(this, otherPiece2)) {
                            ChessMove move2 = new ChessMove(startPosition, ahead2, null);
                            moves.add(move2);
                        }
                    }
                }
                if (!outOfBounds(startRow - 1, startCol - 1)) {
                    ChessPosition diagonal1 = new ChessPosition(startRow - 1, startCol - 1);
                    ChessPiece capturedPiece1 = board.getPiece(diagonal1);
                    if (pieceCaptured(this, capturedPiece1)) {
                        ChessMove capture1 = new ChessMove(startPosition, diagonal1, null);
                        moves.add(capture1);
                    }
                }
                if (!outOfBounds(startRow - 1, startCol + 1)) {
                    ChessPosition diagonal2 = new ChessPosition(startRow - 1, startCol + 1);
                    ChessPiece capturedPiece2 = board.getPiece(diagonal2);
                    if (pieceCaptured(this, capturedPiece2)) {
                        ChessMove capture2 = new ChessMove(startPosition, diagonal2, null);
                        moves.add(capture2);
                    }
                }
            } else {
                ChessPosition ahead1 = new ChessPosition(1, startCol);
                ChessPiece otherPiece = board.getPiece(ahead1);
                if (!pieceBlocking(this, otherPiece) && !pieceCaptured(this, otherPiece)) {
                    ChessMove moveQ = new ChessMove(startPosition, ahead1, PieceType.QUEEN);
                    ChessMove moveR = new ChessMove(startPosition, ahead1, PieceType.ROOK);
                    ChessMove moveB = new ChessMove(startPosition, ahead1, PieceType.BISHOP);
                    ChessMove moveN = new ChessMove(startPosition, ahead1, PieceType.KNIGHT);
                    moves.add(moveQ);
                    moves.add(moveR);
                    moves.add(moveB);
                    moves.add(moveN);
                }
                if (!outOfBounds(startRow - 1, startCol - 1)) {
                    ChessPosition diagonal1 = new ChessPosition(startRow - 1, startCol - 1);
                    ChessPiece capturedPiece1 = board.getPiece(diagonal1);
                    if (pieceCaptured(this, capturedPiece1)) {
                        ChessMove moveQ = new ChessMove(startPosition, diagonal1, PieceType.QUEEN);
                        ChessMove moveR = new ChessMove(startPosition, diagonal1, PieceType.ROOK);
                        ChessMove moveB = new ChessMove(startPosition, diagonal1, PieceType.BISHOP);
                        ChessMove moveN = new ChessMove(startPosition, diagonal1, PieceType.KNIGHT);
                        moves.add(moveQ);
                        moves.add(moveR);
                        moves.add(moveB);
                        moves.add(moveN);
                    }
                }
                if (!outOfBounds(startRow - 1, startCol + 1)) {
                    ChessPosition diagonal2 = new ChessPosition(startRow - 1, startCol + 1);
                    ChessPiece capturedPiece2 = board.getPiece(diagonal2);
                    if (pieceCaptured(this, capturedPiece2)) {
                        ChessMove moveQ = new ChessMove(startPosition, diagonal2, PieceType.QUEEN);
                        ChessMove moveR = new ChessMove(startPosition, diagonal2, PieceType.ROOK);
                        ChessMove moveB = new ChessMove(startPosition, diagonal2, PieceType.BISHOP);
                        ChessMove moveN = new ChessMove(startPosition, diagonal2, PieceType.KNIGHT);
                        moves.add(moveQ);
                        moves.add(moveR);
                        moves.add(moveB);
                        moves.add(moveN);
                    }
                }
            }
        }
        return moves;
    }

    public boolean outOfBounds(int row, int col) {
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return true;
        }
        return false;
    }

    public boolean pieceBlocking(ChessPiece currentPiece, ChessPiece blockingPiece) {
        if (blockingPiece != null && currentPiece.getTeamColor() == blockingPiece.getTeamColor()) {
            return true;
        }
        return false;
    }

    public boolean pieceCaptured(ChessPiece currentPiece, ChessPiece blockingPiece) {
        if (blockingPiece != null && currentPiece.getTeamColor() != blockingPiece.getTeamColor()) {
            return true;
        }
        return false;
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

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }
}
