package nl.zoidberg.calculon.analyzer;

import java.util.Hashtable;

import java.util.Vector;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class BishopPairScorer implements PositionScorer {

	public int scorePosition(Board board, Hashtable pieceMap) {
		int score = 0;
		score += getScore(board, Piece.WHITE, pieceMap);
		score -= getScore(board, Piece.BLACK, pieceMap);
		return score;
	}

	private int getScore(Board board, byte color, Hashtable pieceMap) {
		Vector bishops = (Vector) pieceMap.get(new Byte((byte)(Piece.BISHOP|color)));

		if(bishops != null && bishops.size() >= 2) {
			return pieceMap.get(new Byte((byte) (Piece.QUEEN|color))) != null ? 300 : 150;
		}
		
		return 0;
	}
}
