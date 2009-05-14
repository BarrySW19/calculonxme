package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.KnightMoveGenerator;
import nl.zoidberg.calculon.engine.LongUtil;
import nl.zoidberg.calculon.model.Piece;

public class KnightScorer implements PositionScorer {

	static final int SECURE_BONUS = 200;
	static final int[] rankScores = new int[] { 0, 0, 20, 60, 100, 100, 40, 0 };
	static final int[] targetScores = new int[] { -500, -450, -400, -200, -200, 0, 0, 100, 150 };
	
	public int scorePosition(Board board) {
		int score = 0;
		score += scoreKnights(board, Piece.WHITE);
		score -= scoreKnights(board, Piece.BLACK);
		return score;
	}

	private int scoreKnights(Board board, byte color) {
		BitBoard bitBoard = board.getBitBoard();
		int score = 0;
		
		int pawnAttackDir = (color == Piece.WHITE ? 1 : -1);
		byte oppColor = (color == Piece.WHITE ? Piece.BLACK : Piece.WHITE);
		long knightMap = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapKnights());
		while(knightMap != 0) {
			long nextKnight = LongUtil.highestOneBit(knightMap);
			knightMap ^= nextKnight;
			int[] position = BitBoard.toCoords(nextKnight);
			long knightMoves = KnightMoveGenerator.kmBitmap[position[0]<<3|position[1]];

			int targetCount = 0;
			int onRank = color == Piece.WHITE ? position[1] : 7-position[1];
			score += rankScores[onRank];
			while(knightMoves != 0) {
				long nextSq = LongUtil.highestOneBit(knightMoves);
				knightMoves ^= nextSq;
				int[] newPos = BitBoard.toCoords(nextSq);

				if(board.getPieceIfOnBoard(newPos[0]-1, newPos[1]+pawnAttackDir) == (oppColor|Piece.PAWN)) {
					continue;
				}
				if(board.getPieceIfOnBoard(newPos[0]+1, newPos[1]+pawnAttackDir) == (oppColor|Piece.PAWN)) {
					continue;
				}
				targetCount++;
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
