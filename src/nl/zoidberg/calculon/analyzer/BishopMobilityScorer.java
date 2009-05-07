package nl.zoidberg.calculon.analyzer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import nl.zoidberg.calculon.engine.BishopMoveGenerator;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class BishopMobilityScorer implements PositionScorer {

	public int scorePosition(Board board, Hashtable pieceMap) {
		int score = 0;
		score += getScore(board, Piece.WHITE, pieceMap);
		score -= getScore(board, Piece.BLACK, pieceMap);
		return score;
	}

	private int getScore(Board board, byte color, Hashtable pieceMap) {
		Vector bishops = (Vector) pieceMap.get(new Byte(
				(byte) (Piece.BISHOP | color)));
		if (bishops == null) {
			return 0;
		}

		byte oppColor = (color == Piece.WHITE ? Piece.BLACK : Piece.WHITE);

		int score = 0;
		for (Enumeration e = bishops.elements(); e.hasMoreElements();) {
			int[] pos = (int[]) e.nextElement();
			for (int i = 0; i < BishopMoveGenerator.DIRECTIONS.length; i++) {
				int[] dirs = BishopMoveGenerator.DIRECTIONS[i];
				if (!Board.isOnBoard(pos[0] + dirs[0], pos[1] + dirs[1])) {
					score -= 50;
					continue;
				}
				byte rPiece = board.getPiece(pos[0] + dirs[0], pos[1] + dirs[1]);
				if (rPiece == 0 || (rPiece & Piece.MASK_COLOR) == oppColor) {
					score += 100;
				} else {
					score -= 100;
				}
			}
		}
		return score;
	}
}
