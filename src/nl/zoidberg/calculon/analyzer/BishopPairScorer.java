package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.LongUtil;
import nl.zoidberg.calculon.model.Piece;

public class BishopPairScorer implements PositionScorer {

	public int scorePosition(Board board) {
		int score = 0;
		score += getScore(board, Piece.WHITE);
		score -= getScore(board, Piece.BLACK);
		return score;
	}

	private int getScore(Board board, byte color) {
		BitBoard bitBoard = board.getBitBoard();
		long colorMap = bitBoard.getBitmapColor(color);
		if(LongUtil.bitCount(colorMap & bitBoard.getBitmapBishops()) >= 2) {
			return (colorMap & bitBoard.getBitmapQueens()) == 0 ? 150 : 300;
		}
		return 0;
	}
}
