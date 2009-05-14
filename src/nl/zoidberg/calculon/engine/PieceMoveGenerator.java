package nl.zoidberg.calculon.engine;

import java.util.Vector;

public abstract class PieceMoveGenerator {

	public abstract Vector generateMoves(Board board);
	
	public static final boolean isEmpty(BitBoard bitBoard, int file, int rank) {
		if( ! Board.isOnBoard(file, rank)) {
			return false;
		}
		return ((bitBoard.getAllPieces()&(1L<<(rank<<3)<<file)) == 0);
	}
}
