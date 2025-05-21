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
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            int rowStep = direction[0];
            int colStep = direction[1];
            moves.addAll(getMovesOneDirection(board, startPosition, rowStep, colStep));
        }
        return moves;
    }

    public Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] direction : directions) {
            int rowStep = direction[0];
            int colStep = direction[1];
            moves.addAll(getMovesOneDirection(board, startPosition, rowStep, colStep));
        }
        return moves;
    }

    private Collection<ChessMove> getMovesOneDirection(ChessBoard board, ChessPosition startPosition, int rowStep, int colStep) {
        int startRow = startPosition.getRow() + rowStep;
        int startCol = startPosition.getColumn() + colStep;
        Collection<ChessMove> moves = new ArrayList<>();
        while (!outOfBounds(startRow, startCol)) {
            ChessPosition endPosition = new ChessPosition(startRow, startCol);
            ChessPiece otherPiece = board.getPiece(endPosition);
            if (pieceBlocking(this, otherPiece)) {
                break;
            }
            ChessMove move = new ChessMove(startPosition, endPosition, null);
            moves.add(move);
            if (pieceCaptured(this, otherPiece)) {
                break;
            }
            startRow += rowStep;
            startCol += colStep;
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
        int [][] directions = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};

        for (int[] direction : directions) {
            int rowStep = direction[0];
            int colStep = direction[1];
            ChessMove move = getSingleKnightMove(board, startPosition, rowStep, colStep);
            if (move != null) {
                moves.add(move);
            }
        }
        return moves;
    }

    private ChessMove getSingleKnightMove(ChessBoard board, ChessPosition startPosition, int rowStep, int colStep) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        if (outOfBounds(startRow + rowStep, startCol + colStep)) {
            return null;
        }
        ChessPosition endPosition = new ChessPosition(startRow + rowStep, startCol + colStep);
        ChessPiece otherPiece = board.getPiece(endPosition);
        if (pieceBlocking(this, otherPiece)) {
            return null;
        }
        return new ChessMove(startPosition, endPosition, null);
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
