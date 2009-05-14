package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.LongUtil;

public class MaterialScorer implements PositionScorer {
    
	public int scorePosition(Board board) {
		BitBoard bitBoard = board.getBitBoard(); 
		int score = 0;
		
		score += 9000 * (LongUtil.bitCount(bitBoard.getBitmapWhite()&bitBoard.getBitmapQueens())
				- LongUtil.bitCount(bitBoard.getBitmapBlack()&bitBoard.getBitmapQueens()));
		score += 5000 * (LongUtil.bitCount(bitBoard.getBitmapWhite()&bitBoard.getBitmapRooks())
				- LongUtil.bitCount(bitBoard.getBitmapBlack()&bitBoard.getBitmapRooks()));
		score += 3000 * (LongUtil.bitCount(bitBoard.getBitmapWhite()&bitBoard.getBitmapBishops())
				- LongUtil.bitCount(bitBoard.getBitmapBlack()&bitBoard.getBitmapBishops()));
		score += 3000 * (LongUtil.bitCount(bitBoard.getBitmapWhite()&bitBoard.getBitmapKnights())
				- LongUtil.bitCount(bitBoard.getBitmapBlack()&bitBoard.getBitmapKnights()));
		score += 1000 * (LongUtil.bitCount(bitBoard.getBitmapWhite()&bitBoard.getBitmapPawns())
				- LongUtil.bitCount(bitBoard.getBitmapBlack()&bitBoard.getBitmapPawns()));
		
		return score;
	}
}
