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
            moves.add(new ChessMove(startPosition, endPosition, null));
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
        Collection<ChessMove> moves = new ArrayList<>();

        int firstRow = (color == ChessGame.TeamColor.WHITE)? 2 : 7;
        int step = (color == ChessGame.TeamColor.WHITE)? 1 : -1;
        int promotionRow = (color == ChessGame.TeamColor.WHITE)? 8 : 1;

        moves.addAll(getPawnForwardMoves(board, startPosition, firstRow, step, promotionRow));
        moves.addAll(getPawnCaptureMoves(board, startPosition, step, promotionRow));
        return moves;
    }

    private Collection<ChessMove> getPawnForwardMoves(ChessBoard board, ChessPosition startPosition, int firstRow, int step, int promotionRow) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPosition ahead1 = new ChessPosition(startRow + step, startCol);
        if (!outOfBounds(startRow + step, startCol) && board.getPiece(ahead1) == null) {
            if (ahead1.getRow() == promotionRow) {
                moves.addAll(getPawnPromotionMoves(startPosition, ahead1));
            } else {
                moves.add(new ChessMove(startPosition, ahead1, null));
            }
        }

        if (startRow == firstRow && board.getPiece(ahead1) == null) {
            ChessPosition ahead2 = new ChessPosition(startRow + step * 2, startCol);
            if (board.getPiece(ahead2) == null) {
                moves.add(new ChessMove(startPosition, ahead2, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> getPawnPromotionMoves(ChessPosition startPosition, ChessPosition endPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        PieceType[] promotionTypes = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};

        for (PieceType promotionType : promotionTypes) {
            moves.add(new ChessMove(startPosition, endPosition, promotionType));
        }
        return moves;
    }

    private Collection<ChessMove> getPawnCaptureMoves(ChessBoard board, ChessPosition startPosition, int step, int promotionRow) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        int[] directions = {-1, 1};

        for (int direction : directions) {
            ChessPosition endPosition = new ChessPosition(startRow + step, startCol + direction);
            if (!outOfBounds(endPosition.getRow(), endPosition.getColumn())) {
                ChessPiece otherPiece = board.getPiece(endPosition);
                if (pieceCaptured(this, otherPiece)) {
                    if (endPosition.getRow() == promotionRow) {
                        moves.addAll(getPawnPromotionMoves(startPosition, endPosition));
                    } else {
                        moves.add(new ChessMove(startPosition, endPosition, null));
                    }
                }
            }
        }
        return moves;
    }

    public boolean outOfBounds(int row, int col) {
        return row < 1 || row > 8 || col < 1 || col > 8;
    }

    public boolean pieceBlocking(ChessPiece currentPiece, ChessPiece blockingPiece) {
        return blockingPiece != null && currentPiece.getTeamColor() == blockingPiece.getTeamColor();
    }

    public boolean pieceCaptured(ChessPiece currentPiece, ChessPiece blockingPiece) {
        return blockingPiece != null && currentPiece.getTeamColor() != blockingPiece.getTeamColor();
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
