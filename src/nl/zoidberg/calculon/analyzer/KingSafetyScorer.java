package nl.zoidberg.calculon.analyzer;

import java.util.Hashtable;
import java.util.Vector;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class KingSafetyScorer implements PositionScorer {

	public float scorePosition(Board board, Hashtable pieceMap) {
		int score = 0;
		score += scoreSafety(board, Piece.WHITE, pieceMap);
		score -= scoreSafety(board, Piece.BLACK, pieceMap);
		return (((float)score)/1000f);
	}

	private int scoreSafety(Board board, byte color, Hashtable pieceMap) {
		byte oppColor = color == Piece.WHITE ? Piece.BLACK : Piece.WHITE;

		if(pieceMap.get(new Byte((byte) (Piece.QUEEN|oppColor))) == null) {
			// Fairly crude endgame test...
			return 0;
		}
		
		int score = 0;
		int[] kingPos = (int[]) ((Vector)pieceMap.get(new Byte((byte) (Piece.KING|color)))).elementAt(0);
		if(kingPos[0] == 3 || kingPos[0] == 4) {
			score -= 250;
		}
		
		for(int i = -1; i <= 1; i++) {
			byte rPiece = board.getPieceIfOnBoard(kingPos[0]+i, kingPos[1]+(color == Piece.WHITE ? 1 : -1)); 
			if(rPiece == (Piece.PAWN|color)) {
				score += 70;
			} else if(rPiece != 0 && (rPiece&Piece.MASK_COLOR) == color) {
				score += 40;
			}
		}
		
		return score;
	}
}
