package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.Bitmaps;
import nl.zoidberg.calculon.engine.LongUtil;
import nl.zoidberg.calculon.model.Piece;

public class BishopMobilityScorer implements PositionScorer {

	public int scorePosition(BitBoard bitBoard) {
		int score = 0;
		score += getScore(bitBoard, Piece.WHITE);
		score -= getScore(bitBoard, Piece.BLACK);
		return score;
	}

	private int getScore(BitBoard bitBoard, byte color) {
		long bishopMap = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapBishops());
		int score = 0;
		
		while(bishopMap != 0) {
			long nextBishop = LongUtil.highestOneBit(bishopMap);
			bishopMap ^= nextBishop;
			int mapIdx = LongUtil.numberOfTrailingZeros(nextBishop);
			long dirs = Bitmaps.diag2Map[mapIdx] & BitBoard.getRankMap((mapIdx>>>3) + (color == Piece.WHITE ? 1 : -1));
			long freeAdvance = dirs & ~bitBoard.getBitmapColor(color);
			score += 75 * LongUtil.bitCount(freeAdvance);
		}

		return score;
	}
}
