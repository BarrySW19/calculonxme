package nl.zoidberg.calculon.analyzer;

import java.util.Enumeration;
import java.util.Hashtable;

import java.util.Vector;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class MaterialScorer implements PositionScorer {
    
    private static float[] scores = new float[16];
    
    static {
        scores[Piece.WHITE | Piece.PAWN]    = 1;
        scores[Piece.WHITE | Piece.KNIGHT]  = 3;
        scores[Piece.WHITE | Piece.BISHOP]  = 3;
        scores[Piece.WHITE | Piece.ROOK]    = 5;
        scores[Piece.WHITE | Piece.QUEEN]   = 9;
        scores[Piece.WHITE | Piece.KING]    = 50;
        scores[Piece.BLACK | Piece.PAWN]    = -1;
        scores[Piece.BLACK | Piece.KNIGHT]  = -3;
        scores[Piece.BLACK | Piece.BISHOP]  = -3;
        scores[Piece.BLACK | Piece.ROOK]    = -5;
        scores[Piece.BLACK | Piece.QUEEN]   = -9;
        scores[Piece.BLACK | Piece.KING]    = -50;
    }
    
	public float scorePosition(Board board, Hashtable pieceMap) {
		float score = 0;
                for(Enumeration e = pieceMap.keys(); e.hasMoreElements(); ) {
                    Byte piece = (Byte) e.nextElement();
                    score += scores[piece.byteValue()] * ((Vector)pieceMap.get(piece)).size();
		}
		return score;
	}
}
