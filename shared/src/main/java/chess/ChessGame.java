package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor color;
    private ChessBoard board;

    public ChessGame() {
        color = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return color;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        color = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : allMoves) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece capturedPiece = board.getPiece(endPosition);
            board.movePiece(move);
            boolean inCheck = isInCheck(piece.getTeamColor());
            reverseMove(move, capturedPiece);
            if (!inCheck) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    public boolean isValidMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        Collection<ChessMove> validMoves = validMoves(startPosition);
        return validMoves.contains(move);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        Collection<ChessMove> validMoves = validMoves(startPosition);
        if (piece == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Wrong color moved");
        }
        ChessPiece capturedPiece = board.getPiece(endPosition);
        ChessPiece.PieceType promote = move.getPromotionPiece();
        if (promote == null) {
            board.addPiece(endPosition, piece);
        } else {
            ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), promote);
            board.addPiece(endPosition, newPiece);
        }
        board.addPiece(startPosition, null);
        if (isInCheck(color)) {
            reverseMove(move, capturedPiece);
            throw new InvalidMoveException("Invalid move: King is in check");
        }
        nextTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() != opponentColor) {
                    continue;
                }
                for (ChessMove move : piece.pieceMoves(board, position)) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return noValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return noValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ChessPosition getKingPosition(TeamColor color) {
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == color) {
                    kingPosition = currentPosition;
                    return kingPosition;
                }

            }
        }
        return null;
    }

    public Collection<ChessMove> getAllMoves(TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == color) {
                    Collection<ChessMove> moves = validMoves(position);
                    allMoves.addAll(moves);
                }
            }
        }
        return allMoves;
    }

    public void reverseMove(ChessMove move, ChessPiece capturedPiece) {
        ChessPosition startPosition = move.getEndPosition();
        ChessPosition endPosition = move.getStartPosition();
        ChessMove reverse = new ChessMove(startPosition, endPosition, null);

        board.movePiece(reverse);
        board.addPiece(startPosition, capturedPiece);
    }

    public boolean noValidMoves(TeamColor color) {
        Collection<ChessMove> allMoves = getAllMoves(color);
        for (ChessMove move : allMoves) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece capturedPiece = board.getPiece(endPosition);
            board.movePiece(move);
            if (!isInCheck(color)) {
                reverseMove(move, capturedPiece);
                return false;
            }
            reverseMove(move, capturedPiece);
        }
        return true;
    }

    public void nextTurn() {
        if (color == TeamColor.WHITE) {
            color = TeamColor.BLACK;
        } else {
            color = TeamColor.WHITE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return color == chessGame.color && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, board);
    }
}