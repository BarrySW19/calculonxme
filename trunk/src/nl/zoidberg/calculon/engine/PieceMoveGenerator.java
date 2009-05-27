package nl.zoidberg.calculon.engine;

import java.util.Vector;


public abstract class PieceMoveGenerator {

	public abstract void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv);
	
	/**
	 * Generate threatening moves to use in quiescence searching.
	 */
	public abstract void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv);
	
	public static final boolean isEmpty(BitBoard bitBoard, int file, int rank) {
		if( ! Board.isOnBoard(file, rank)) {
			return false;
		}
		return ((bitBoard.getAllPieces()&(1L<<(rank<<3)<<file)) == 0);
	}
}
