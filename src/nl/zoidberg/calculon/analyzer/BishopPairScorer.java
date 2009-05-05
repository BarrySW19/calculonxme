package nl.zoidberg.calculon.analyzer;

import java.util.Hashtable;

import java.util.Vector;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class BishopPairScorer implements PositionScorer {

	public float scorePosition(Board board, Hashtable pieceMap) {
		float score = 0;
		score += getScore(board, Piece.WHITE, pieceMap);
		score -= getScore(board, Piece.BLACK, pieceMap);
		return score;
	}

	private float getScore(Board board, byte color, Hashtable pieceMap) {
		Vector bishops = (Vector) pieceMap.get(new Byte((byte)(Piece.BISHOP|color)));

		if(bishops != null && bishops.size() >= 2) {
			return pieceMap.get(new Byte((byte) (Piece.QUEEN|color))) != null ? 0.3f : 0.15f;
		}
		
		return 0;
	}
}
