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
		
		long enemyPawns = bitBoard.getBitmapOppColor(color) & bitBoard.getBitmapPawns();
		long enemyPawnsRight = color == Piece.WHITE
			? (enemyPawns & ~BitBoard.getFileMap(0))>>>9 : (enemyPawns & ~BitBoard.getFileMap(0))<<7;
		long enemyPawnsLeft = color == Piece.WHITE
			? (enemyPawns & ~BitBoard.getFileMap(7))>>>7 : (enemyPawns & ~BitBoard.getFileMap(7))<<9;
			
		long myPawns = bitBoard.getBitmapColor(color) & bitBoard.getBitmapPawns();
		long myPawnsRight = color == Piece.WHITE
			? (myPawns & ~BitBoard.getFileMap(0))<<7 : (myPawns & ~BitBoard.getFileMap(0))>>>9;
		long myPawnsLeft = color == Piece.WHITE
			? (myPawns & ~BitBoard.getFileMap(7))<<9 : (myPawns & ~BitBoard.getFileMap(7))>>>7;

		int pawnAttackDir = (color == Piece.WHITE ? 1 : -1);
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

				if((nextSq & enemyPawnsLeft) != 0 || (nextSq & enemyPawnsRight) != 0) {
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
				if((nextKnight & myPawnsLeft) != 0 || (nextKnight & myPawnsRight) != 0) {
					isSupported = true;
					long scaryPawns = 0;
					for(int r = onRank+pawnAttackDir; r > 0 && r < 7; r += pawnAttackDir) {
						if(position[0] > 0) {
							scaryPawns |= 1L<<(r<<3)<<(position[0]-1);
						}
						if(position[0] < 7) {
							scaryPawns |= 1L<<(r<<3)<<(position[0]+1);
						}
					}
					if((enemyPawns & scaryPawns) != 0) {
						isAttackable = true;
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
