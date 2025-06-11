package ui;

import chess.*;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;


public class BoardDrawer {
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();

        if (color == ChessGame.TeamColor.WHITE) {
            return switch (type) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case PAWN -> WHITE_PAWN;
            };
        } else {
            return switch (type) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case ROOK -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN -> BLACK_PAWN;
            };
        }
    }

    public static void drawBoardWhitePerspective(PrintStream out, ChessBoard board, ChessPosition position) {
        drawHeaders(out, false);
        drawChessBoard(out, board, false, position);
        resetColors(out);
    }

    public static void drawBoardBlackPerspective(PrintStream out, ChessBoard board, ChessPosition position) {
        drawHeaders(out, true);
        drawChessBoard(out, board, true, position);
        resetColors(out);
    }

    private static void resetColors(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void drawHeaders(PrintStream out, boolean flipped) {
        setBlack(out);

        out.print("   ");
        if (flipped) {
            for (char col = 'h'; col >= 'a'; col--) {
                drawColumnHeader(out, String.valueOf(col));
            }
        } else {
            for (char col = 'a'; col <= 'h'; col++) {
                drawColumnHeader(out, String.valueOf(col));
            }
        }
        out.println();
    }

    public static void drawColumnHeader(PrintStream out, String headerText) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        if (!headerText.equals("b") && !headerText.equals("d") && !headerText.equals("f")) {
            out.print("  ");
            out.print(headerText);
            out.print(" ");
        } else {
            out.print(" ");
            out.print(headerText);
            out.print(" ");
        }

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard board, boolean flipped, ChessPosition position) {
        if (flipped) {
            for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0; --boardRow) {
                drawRowOfSquares(out, boardRow, board, flipped, position);
            }
        } else {
            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
                drawRowOfSquares(out, boardRow, board, flipped, position);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow, ChessBoard board, boolean flipped, ChessPosition selectedPosition) {
        int rowNumber = flipped ? (8 - boardRow) : (BOARD_SIZE_IN_SQUARES - boardRow);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);
        out.print(" " + rowNumber + " ");

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            int actualCol = flipped ? (BOARD_SIZE_IN_SQUARES - 1 - boardCol) : boardCol;

            boolean isLightSquare = (boardRow + actualCol) % 2 == 0;

            ChessPosition position = new ChessPosition(BOARD_SIZE_IN_SQUARES - boardRow, actualCol + 1);
            ChessPiece piece = board.getPiece(position);
            String pieceSymbol = getPieceSymbol(piece);

            Collection<ChessPosition> legalMoves = getLegalMoves(selectedPosition, board);
            boolean isStartingPosition = position.equals(selectedPosition);
            boolean isLegalMove = legalMoves != null && legalMoves.contains(position);

            printPiece(out, pieceSymbol, isLightSquare, isStartingPosition, isLegalMove);
        }

        out.print(SET_BG_COLOR_BLACK);
        out.println();
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void printPiece(PrintStream out, String piece, boolean isLightSquare, boolean isStartingPosition, boolean isLegalMove) {
        if (isStartingPosition){
            out.print(SET_BG_COLOR_YELLOW);
        } else if (isLegalMove) {
            out.print(SET_BG_COLOR_GREEN);
        } else if (isLightSquare) {
            out.print(SET_BG_COLOR_WHITE);
        } else {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        }

        if (piece.equals(EMPTY)) {
            out.print(piece);
        } else if (piece.contains("♔") || piece.contains("♕") || piece.contains("♖") ||
                piece.contains("♗") || piece.contains("♘") || piece.contains("♙")) {
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(piece);
        } else {
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(piece);
        }
    }

    private static Collection<ChessPosition> getLegalMoves(ChessPosition startPosition, ChessBoard board) {
        Collection<ChessPosition> legalMoves = new ArrayList<>();
        if (startPosition == null) {
            return null;
        }
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            legalMoves.add(move.getEndPosition());
        }
        return legalMoves;
    }
}