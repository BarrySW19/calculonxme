package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BishopMoveGenerator;
import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.LongUtil;
import nl.zoidberg.calculon.model.Piece;

public class BishopMobilityScorer implements PositionScorer {

	public int scorePosition(Board board) {
		int score = 0;
		score += getScore(board, Piece.WHITE);
		score -= getScore(board, Piece.BLACK);
		return score;
	}

	private int getScore(Board board, byte color) {
		BitBoard bitBoard = board.getBitBoard();
		long bishopMap = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapBishops());
		int score = 0;
		byte oppColor = (color == Piece.WHITE ? Piece.BLACK : Piece.WHITE);
		
		while(bishopMap != 0) {
			long nextBishop = LongUtil.highestOneBit(bishopMap);
			bishopMap ^= nextBishop;

			int[] pos = BitBoard.toCoords(nextBishop);
			for(int x = 0; x < BishopMoveGenerator.DIRECTIONS.length; x++) {
				int[] dirs = BishopMoveGenerator.DIRECTIONS[x];
				if( ! Board.isOnBoard(pos[0]+dirs[0], pos[1]+dirs[1])) {
					score -= 50;
					continue;
				}
				byte rPiece = board.getPiece(pos[0]+dirs[0], pos[1]+dirs[1]);
				if(rPiece == 0 || (rPiece&Piece.MASK_COLOR) == oppColor) {
					score += 100;
				} else {
					score -= 100;
				}
			}
		}

		return score;
	}
}
