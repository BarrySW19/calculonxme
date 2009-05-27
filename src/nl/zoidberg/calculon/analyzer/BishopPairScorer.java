package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.LongUtil;
import nl.zoidberg.calculon.model.Piece;

public class BishopPairScorer implements PositionScorer {

	public int scorePosition(BitBoard bitBoard) {
		int score = 0;
		score += getScore(bitBoard, Piece.WHITE);
		score -= getScore(bitBoard, Piece.BLACK);
		return score;
	}

	private int getScore(BitBoard bitBoard, byte color) {
		long colorMap = bitBoard.getBitmapColor(color);
		if(LongUtil.bitCount(colorMap & bitBoard.getBitmapBishops()) >= 2) {
			return (colorMap & bitBoard.getBitmapQueens()) == 0 ? 150 : 300;
		}
		return 0;
	}
}
