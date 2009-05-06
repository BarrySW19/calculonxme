package nl.zoidberg.calculon.analyzer;

import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class RookScorer implements PositionScorer {

	public static final int OPEN_FILE_SCORE = 150;

	public float scorePosition(Board board, Hashtable pieceMap) {
		int score = 0;
		score += scoreRooks(board, Piece.WHITE);
		score -= scoreRooks(board, Piece.BLACK);
		return (((float) score) / 1000f);
	}
	
	private int scoreRooks(Board board, byte color) {
		int score = 0;
		
		for(int file = 0; file < 8; file++) {
			for(int rank = 0; rank < 8; rank++) {
				byte piece = board.getPiece(file, rank);
				if((piece & Piece.MASK_TYPE) == Piece.ROOK && (piece&Piece.MASK_COLOR) == color) {
					boolean isOpen = true;
					for(int x = 0; x < 8; x++) {
						byte altPiece = board.getPiece(file, x);
						if(altPiece == (Piece.PAWN|color)) {
							isOpen = false;
						}
					}
					if(isOpen) {
						score += OPEN_FILE_SCORE;
					}
				}
			}
		}
		
		return score;
	}
}
