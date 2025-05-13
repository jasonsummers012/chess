package chess;

import java.util.ArrayList;
import java.util.Collection;

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
    public Collection<ChessMove> validMoves(ChessPosition startPosition) throws InvalidMoveException{
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        try {
            Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
            Collection<ChessMove> validMoves = new ArrayList<>();

            for (ChessMove move : allMoves) {
                ChessPosition endPosition = move.getEndPosition();
                ChessPiece capturedPiece = board.getPiece(endPosition);

                board.addPiece(endPosition, piece);
                board.addPiece(startPosition, null);

                boolean inCheck = false;
                try {
                    inCheck = isInCheck(piece.getTeamColor());
                } catch (InvalidMoveException e) {
                    inCheck = true;
                }

                reverseMove(move, capturedPiece);

                if (!inCheck) {
                    validMoves.add(move);
                }
            }

            return validMoves;
        } catch (InvalidMoveException e) {
            return new ArrayList<>();
        }
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

        if (piece == null) {
            throw new InvalidMoveException("Null piece");
        }
        if (piece.getTeamColor() != color) {
            throw new InvalidMoveException("Wrong color's turn");
        }
        Collection<ChessMove> moves = validMoves(startPosition);
        if (moves == null || !moves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, null);

        if (move.getPromotionPiece() != null) {
            ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(endPosition, promotionPiece);
        }

        if (color == TeamColor.WHITE) {
            color = TeamColor.BLACK;
        } else {
            color = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) throws InvalidMoveException {
        TeamColor color;
        if (teamColor == TeamColor.WHITE) {
            color = TeamColor.BLACK;
        } else {
            color = TeamColor.WHITE;
        }
        ChessPosition kingPosition = getKingPosition(color);
        if (kingPosition == null) {
            throw new InvalidMoveException("No king");
        }
        Collection<ChessMove> allMoves = getAllMoves(color);

        for (ChessMove move : allMoves) {
            ChessPosition endPosition = move.getEndPosition();
            if (endPosition == kingPosition) {
                return true;
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
    public boolean isInCheckmate(TeamColor teamColor) throws InvalidMoveException {
        try {
            if (!isInCheck(teamColor)) {
                return false;
            }
            return noValidMoves(teamColor);
        } catch (InvalidMoveException e) {
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) throws InvalidMoveException{
        try {
            if (isInCheck(teamColor)) {
                return false;
            }
            return noValidMoves(teamColor);
        } catch (InvalidMoveException e) {
            return false;
        }
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

    public ChessPosition getKingPosition(TeamColor color) throws InvalidMoveException{
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == color) {
                    kingPosition = currentPosition;
                }

            }
        }
        return kingPosition;
    }

    public Collection<ChessMove> getAllMoves(TeamColor color) throws InvalidMoveException {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int row = 1; row <= 8; row ++) {
            for (int col = 1; col <= 8; col ++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == color) {
                    try {
                        Collection<ChessMove> moves = validMoves(position);
                        allMoves.addAll(moves);
                    } catch (InvalidMoveException e) {
                        continue;
                    }
                }
            }
        }
        return allMoves;
    }

    public void reverseMove(ChessMove move, ChessPiece capturedPiece) throws InvalidMoveException {
        ChessPosition startPosition = move.getEndPosition();
        ChessPosition endPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(endPosition);

        board.addPiece(startPosition, piece);
        board.addPiece(endPosition, capturedPiece);
    }

    public boolean noValidMoves(TeamColor color) throws InvalidMoveException {
        Collection<ChessMove> allMoves = getAllMoves(color);
        for (ChessMove move : allMoves) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece capturedPiece = board.getPiece(endPosition);
            makeMove(move);
            if (!isInCheck(color)) {
                return false;
            }
            reverseMove(move, capturedPiece);
        }
        return true;
    }
}
