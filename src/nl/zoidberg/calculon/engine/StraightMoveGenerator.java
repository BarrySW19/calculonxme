package nl.zoidberg.calculon.engine;

import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;

public abstract class StraightMoveGenerator extends PieceMoveGenerator {

	public Hashtable generateMoves(Board board, int file, int rank) {
		return this.generateMoves(board, file, rank, false);
	}
	
	public Hashtable generateMoves(Board board, int file, int rank, boolean oneOnly) {
		Hashtable rv = new Hashtable();

                int[][] directions = getDirections();
                for(int x = 0; x < directions.length; x++)  {
                    int[] i = directions[x];
			generateMoves(board, file, rank, i[0], i[1], rv, oneOnly);
		}
		
		return rv;
	}
	
	public abstract int[][] getDirections();

	private void generateMoves(Board board, int file, int rank, int finc, int rinc, Hashtable rv, boolean oneOnly) {
		int newFile = file + finc;
		int newRank = rank + rinc;
		while(Board.isOnBoard(newFile, newRank)) {
			if(isOwnPiece(board, newFile, newRank)) {
				return;
			}
			
			String move = EngineUtils.toSimpleAlgebraic(file, rank, newFile, newRank);
			Board nextBoard = board.clone().applyMove(move);
			if( ! CheckDetector.inCheck(nextBoard)) {
				rv.put(move, new SearchNode(nextBoard));
                if(oneOnly) {
                	return;
                }
			}
			
			if(isCaptureTarget(board, newFile, newRank)) {
				return;
			}
			newFile += finc;
			newRank += rinc;
		}
	}
}
