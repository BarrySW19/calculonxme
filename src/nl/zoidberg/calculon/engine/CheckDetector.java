package nl.zoidberg.calculon.engine;

import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class CheckDetector {

	/**
	 * Tests whether the player with the move is in check - i.e. whether the last move played was a checking move.
	 */
	public static boolean alreadyInCheck(Board board) {
        return inCheck(board, board.getPlayer());
    }

	/**
	 * Tests whether the player who just moved has left themselves in check - i.e. it was an illegal move.
	 * 
	 * @param board
	 * @return
	 */
    public static boolean inCheck(Board board) {
        byte color = board.getPlayer() == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
        return inCheck(board, color);
    }

    private static boolean inCheck(Board board, byte color) {
        int kingFile = -1, kingRank = -1;

        for(int file = 0; file < 8 && kingFile == -1; file++) {
            for(int rank = 0; rank < 8; rank++) {
                byte piece = board.getPiece(file, rank);
                if((piece&Piece.MASK_TYPE) == Piece.KING && (piece&Piece.MASK_COLOR) == color) {
                    kingFile = file;
                    kingRank = rank;
                    break;
                }
            }
        }

        for(int i = 0; i < QueenMoveGenerator.DIRECTIONS.length; i++) {
            int[] dir = QueenMoveGenerator.DIRECTIONS[i];
            if(detectPiece(board, kingFile, kingRank, dir)) {
                return true;
            }
        }
        
        for(int i = 0; i < KnightMoveGenerator.MOVES.length; i++) {
            int[] move = KnightMoveGenerator.MOVES[i];
            int nFile = kingFile + move[0];
            int nRank = kingRank + move[1];
            if( ! Board.isOnBoard(nFile, nRank)) {
                continue;
            }
            byte piece = board.getPiece(nFile, nRank);
            if((piece & Piece.MASK_COLOR) != color && (piece & Piece.MASK_TYPE) == Piece.KNIGHT) {
                return true;
            }
        }

        return false;
    }

    private static boolean detectPiece(Board board, int kingFile, int kingRank, int[] dir) {
        byte king = board.getPiece(kingFile, kingRank);
        int nFile = kingFile + dir[0];
        int nRank = kingRank + dir[1];
        int distance = 1;
        while(Board.isOnBoard(nFile, nRank)) {
            byte piece = board.getPiece(nFile, nRank);
            if(piece == Piece.EMPTY) {
                nFile += dir[0];
                nRank += dir[1];
                distance++;
                continue;
            }
            if((piece&Piece.MASK_COLOR) == (king&Piece.MASK_COLOR)) {
                return false;
            }

            byte pieceType = (byte) (piece&Piece.MASK_TYPE);
            if(pieceType == Piece.QUEEN) {
                return true;
            }
            if((dir[0] == 0 || dir[1] == 0) && (pieceType == Piece.ROOK)) {
                return true;
            }
            if((dir[0] != 0 && dir[1] != 0) && (pieceType == Piece.BISHOP)) {
                return true;
            }
            if(distance == 1 && pieceType == Piece.KING) {
            	return true;
            }
            if((distance == 1 && dir[0] != 0 && dir[1] != 0) && (pieceType == Piece.PAWN)) {
                return nRank == (kingRank + ((piece&Piece.MASK_COLOR) == Piece.WHITE ? -1 : 1));
            }
            return false;
        }
        return false;
    }
}
