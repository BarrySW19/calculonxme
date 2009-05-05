package nl.zoidberg.calculon.engine;

import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class PawnMoveGenerator extends PieceMoveGenerator {

	public Hashtable generateMoves(Board board, int file, int rank) {
		return this.generateMoves(board, file, rank, false);
	}
	
	public Hashtable generateMoves(Board board, int file, int rank, boolean oneOnly) {
		Hashtable rv = new Hashtable();

		int nextRank = (board.getPlayer() & Piece.MASK_COLOR) == Piece.WHITE ? 1 : -1;
		int homeRank = (board.getPlayer() & Piece.MASK_COLOR) == Piece.WHITE ? 1 : 6;
		
		if(isEmpty(board, file, rank + nextRank)) {
			String move = EngineUtils.toSimpleAlgebraic(file, rank, file, rank + nextRank);
			addMove(move, board, rv);
			if(oneOnly && rv.size() > 0) {
				return rv;
			}
		}
		
		if(rank == homeRank && isEmpty(board, file, rank + nextRank)
				&& isEmpty(board, file, rank + (2*nextRank))) {
			String move = EngineUtils.toSimpleAlgebraic(file, rank, file, rank + (2*nextRank));
			addMove(move, board, rv);
			if(oneOnly && rv.size() > 0) {
				return rv;
			}
		}
		
		if(isCaptureTargetForPawn(board, file-1, rank+nextRank)) {
			String move = EngineUtils.toSimpleAlgebraic(file, rank, file-1, rank + nextRank);
			addMove(move, board, rv);
			if(oneOnly && rv.size() > 0) {
				return rv;
			}
		}
		
		if(isCaptureTargetForPawn(board, file+1, rank+nextRank)) {
			String move = EngineUtils.toSimpleAlgebraic(file, rank, file+1, rank + nextRank);
			addMove(move, board, rv);
			if(oneOnly && rv.size() > 0) {
				return rv;
			}
		}
		
		return rv;
	}
	
	private void addMove(String move, Board board, Hashtable rv) {
		Board nextBoard = board.clone().applyMove(move);
		if(CheckDetector.inCheck(nextBoard)) {
			return;
		}
		
		if(move.charAt(3) == '1' || move.charAt(3) == '8') {
			rv.put(move + "=N", new SearchNode(board.clone().applyMove(move + "=N")));
			rv.put(move + "=B", new SearchNode(board.clone().applyMove(move + "=B")));
			rv.put(move + "=R", new SearchNode(board.clone().applyMove(move + "=R")));
			rv.put(move + "=Q", new SearchNode(board.clone().applyMove(move + "=Q")));
		} else {
			rv.put(move, new SearchNode(nextBoard));
		}
	}
}
