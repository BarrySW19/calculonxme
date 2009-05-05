package nl.zoidberg.calculon.engine;

import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;



public class KnightMoveGenerator extends PieceMoveGenerator {
	
	public static int[][] MOVES = new int[][] {
		{ 1, 2 }, 	{ 1, -2 },
		{ -1, 2 }, 	{ -1, -2 },
		{ 2, 1 },	{ 2, -1 },
		{ -2, 1 },	{ -2, -1 },
	};

	public Hashtable generateMoves(Board board, int file, int rank) {
		return this.generateMoves(board, file, rank, false);
	}
	
	public Hashtable generateMoves(Board board, int file, int rank, boolean oneOnly) {
		Hashtable rv = new Hashtable();
                for(int i = 0; i < MOVES.length; i++) {
                    int[] m = MOVES[i];
			if(isEmptyOrCaptureTarget(board, file+m[0], rank+m[1])) {
				String move = EngineUtils.toSimpleAlgebraic(file, rank, file+m[0], rank+m[1]);
				Board nextBoard = board.clone().applyMove(move);
				if( ! CheckDetector.inCheck(nextBoard)) {
					rv.put(move, new SearchNode(nextBoard));
                    if(oneOnly) {
                    	return rv;
                    }
				}
			}
		}
		return rv;
	}
}
