package nl.zoidberg.calculon.analyzer;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import nl.zoidberg.calculon.engine.BishopMoveGenerator;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class BishopMobilityScorer implements PositionScorer {

	public float scorePosition(Board board, Hashtable pieceMap) {
		float score = 0;
		score += getScore(board, Piece.WHITE, pieceMap);
		score -= getScore(board, Piece.BLACK, pieceMap);
		return score;
	}

	private float getScore(Board board, byte color, Hashtable pieceMap) {
		Vector bishops = (Vector) pieceMap.get(new Byte((byte)(Piece.BISHOP|color)));
		if(bishops == null) {
			return 0f;
		}
		
		byte oppColor = (color == Piece.WHITE ? Piece.BLACK : Piece.WHITE);
		
		float score = 0f;
                for(Enumeration e = bishops.elements(); e.hasMoreElements(); ) {
                    int[] pos = (int[]) e.nextElement();
                    for(int i = 0; i < BishopMoveGenerator.DIRECTIONS.length; i++) {
                        int[] dirs = BishopMoveGenerator.DIRECTIONS[i];
                        if( ! Board.isOnBoard(pos[0]+dirs[0], pos[1]+dirs[1])) {
                                score -= 0.05f;
                                continue;
                        }
                        byte rPiece = board.getPiece(pos[0]+dirs[0], pos[1]+dirs[1]);
                        if(rPiece == 0 || (rPiece&Piece.MASK_COLOR) == oppColor) {
                                score += 0.1f;
                        } else {
                                score -= 0.1f;
                        }
                    }
		}
		return score;
	}
}
