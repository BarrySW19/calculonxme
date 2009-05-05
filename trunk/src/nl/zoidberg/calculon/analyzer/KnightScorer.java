package nl.zoidberg.calculon.analyzer;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import nl.zoidberg.calculon.engine.KnightMoveGenerator;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class KnightScorer implements PositionScorer {

	static final float SECURE_BONUS = 0.2f;
	static final float[] rankScores = new float[] { 0f, 0f, 0.02f, 0.06f, 0.1f, 0.1f, 0.04f, 0f };
	static final float[] targetScores = new float[] { -0.5f, -0.45f, -0.4f, -0.2f, -0.2f, 0, 0, 0.1f, 0.15f };
	
	public float scorePosition(Board board, Hashtable pieceMap) {
		float score = 0;
		score += scoreKnights(board, Piece.WHITE, pieceMap);
		score -= scoreKnights(board, Piece.BLACK, pieceMap);
		return score;
	}

	private float scoreKnights(Board board, byte color, Hashtable pieceMap) {
		float score = 0;
		
		int pawnAttackDir = (color == Piece.WHITE ? 1 : -1);
		byte oppColor = (color == Piece.WHITE ? Piece.BLACK : Piece.WHITE);
		
		Vector knights = (Vector) pieceMap.get(new Byte((byte) (Piece.KNIGHT|color)));
		if(knights == null) {
			return 0f;
		}
		for(Enumeration e = knights.elements(); e.hasMoreElements(); ) {
                        int[] position = (int[]) e.nextElement();
			int targetCount = 0;
			int onRank = color == Piece.WHITE ? position[1] : 7-position[1];
			score += rankScores[onRank];
                        for(int i = 0; i < KnightMoveGenerator.MOVES.length; i++) {
                            int[] m = KnightMoveGenerator.MOVES[i];
                            if(Board.isOnBoard(position[0]+m[0], position[1]+m[1])) {
                                    if(board.getPieceIfOnBoard(position[0]+m[0]-1, position[1]+m[1]+pawnAttackDir) == (oppColor|Piece.PAWN)) {
                                            continue;
                                    }
                                    if(board.getPieceIfOnBoard(position[0]+m[0]+1, position[1]+m[1]+pawnAttackDir) == (oppColor|Piece.PAWN)) {
                                            continue;
                                    }
                                    targetCount++;
                            }
			}
			// A knight on the rim is dim - penalise it.
			score += targetScores[targetCount];
			
			/**
			 * Strategically, the best place for a knight is supported by a pawn on the 4th - 6th rank
			 * where it cannot be driven off by an enemy pawn. Give a decent bonus to knights in this 
			 * position.
			 */
			if(onRank >=3 && onRank <= 5) {
				boolean isSupported = false;
				boolean isAttackable = false;
				if(board.getPieceIfOnBoard(position[0]-1, position[1]-pawnAttackDir) == (color|Piece.PAWN)
						|| board.getPieceIfOnBoard(position[0]+1, position[1]-pawnAttackDir) == (color|Piece.PAWN)) {
					isSupported = true;
					for(int r = onRank+pawnAttackDir; r > 0 && r < 7; r += pawnAttackDir) {
						if(board.getPieceIfOnBoard(position[0]-1, r) == (oppColor|Piece.PAWN)) {
							isAttackable = true;
							break;
						}
						if(board.getPieceIfOnBoard(position[0]+1, r) == (oppColor|Piece.PAWN)) {
							isAttackable = true;
							break;
						}
					}
				}
				if(isSupported && ! isAttackable) {
					score += SECURE_BONUS; // With ranking bonus - quite high.
				}
			}
		}
		
		return score;
	}
}
