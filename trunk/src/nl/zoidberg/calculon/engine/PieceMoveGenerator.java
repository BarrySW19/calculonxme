package nl.zoidberg.calculon.engine;

import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public abstract class PieceMoveGenerator {
	
	public abstract Hashtable generateMoves(Board board, int file, int rank);

	public abstract Hashtable generateMoves(Board board, int file, int rank, boolean oneOnly);
	
	public static boolean isEmptyOrCaptureTarget(Board board, int file, int rank) {
		return isEmpty(board, file, rank) || isCaptureTarget(board, file, rank);
	}
	
	public static boolean isOwnPiece(Board board, int file, int rank) {
		if( ! Board.isOnBoard(file, rank)) {
			return false;
		}
		byte piece = board.getPiece(file, rank);
		return ((piece & Piece.MASK_TYPE) != Piece.EMPTY && (piece & Piece.MASK_COLOR) == board.getPlayer());
	}

	public static boolean isCaptureTarget(Board board, int file, int rank) {
		if( ! Board.isOnBoard(file, rank)) {
			return false;
		}
		byte piece = board.getPiece(file, rank);
		return ((piece & Piece.MASK_TYPE) != Piece.EMPTY && (piece & Piece.MASK_COLOR) != board.getPlayer());
	}

	public static boolean isCaptureTargetForPawn(Board board, int file, int rank) {
		if( ! Board.isOnBoard(file, rank)) {
			return false;
		}
		byte piece = board.getPiece(file, rank);
		if((piece & Piece.MASK_TYPE) != Piece.EMPTY && (piece & Piece.MASK_COLOR) != board.getPlayer()) {
			return true;
		}
	
		if(board.isEnPassant() && file == board.getEnPassantFile() && rank == board.getEnPassantRank()) {
			return true;
		}
		
		return false;
	}

	public static boolean isEmpty(Board board, int file, int rank) {
		if( ! Board.isOnBoard(file, rank)) {
			return false;
		}
		byte piece = board.getPiece(file, rank);
		return ((piece & Piece.MASK_TYPE) == Piece.EMPTY);
	}
}
