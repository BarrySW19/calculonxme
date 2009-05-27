package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.KingMoveGenerator;
import nl.zoidberg.calculon.engine.LongUtil;
import nl.zoidberg.calculon.model.Piece;

public class KingSafetyScorer implements PositionScorer {

	public int scorePosition(BitBoard bitBoard) {
		int score = 0;
		score += scoreSafety(bitBoard, Piece.WHITE);
		score -= scoreSafety(bitBoard, Piece.BLACK);
		return score;
	}

	private int scoreSafety(BitBoard bitBoard, byte color) {

		if((bitBoard.getBitmapOppColor(color)|bitBoard.getBitmapQueens()) == 0) {
			// Fairly crude endgame test...
			return 0;
		}
		
		int score = 0;
		long king = bitBoard.getBitmapColor(color) & bitBoard.getBitmapKings();
		int mapIdx = LongUtil.numberOfTrailingZeros(king);
		long inFront = KingMoveGenerator.KING_MOVES[mapIdx]
		           & BitBoard.getRankMap((mapIdx>>>3) + (color == Piece.WHITE ? 1 : -1)) & bitBoard.getBitmapColor(color);
		score += 70 * (LongUtil.bitCount(inFront & bitBoard.getBitmapPawns()));
		score += 40 * (LongUtil.bitCount(inFront & ~bitBoard.getBitmapPawns()));
		
		int[] kingPos = BitBoard.toCoords(king);
		
		if(kingPos[0] == 3 || kingPos[0] == 4) {
			score -= 250;
		}
		
		return score;
	}

}
