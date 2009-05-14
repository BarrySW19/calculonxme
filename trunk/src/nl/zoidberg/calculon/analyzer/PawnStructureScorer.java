package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.LongUtil;

public class PawnStructureScorer implements PositionScorer {
	
	public static int S_ISLAND 		= 100;
	public static int S_ISOLATED 	= 100;
	public static int S_DOUBLED 	= 100;
	
	private static int[] S_ADVANCE = { 20, 100, 200, 400, }; 

	public int scorePosition(Board board) {
		
		BitBoard bitBoard = board.getBitBoard();
		long whitePawns = bitBoard.getBitmapWhite()&bitBoard.getBitmapPawns();
		long blackPawns = bitBoard.getBitmapBlack()&bitBoard.getBitmapPawns();

		int score = 0;
		score += countIslands(blackPawns) * S_ISLAND;
		score -= countIslands(whitePawns) * S_ISLAND;
		
		score += getIsolatedCount(blackPawns) * S_ISOLATED;
		score -= getIsolatedCount(whitePawns) * S_ISOLATED;
		
		score += getDoubledScore(whitePawns, blackPawns);
		score += getAdvanceScore(board, whitePawns, blackPawns);
		
		return score;
	}
	
	private int getAdvanceScore(Board board, long whitePawns, long blackPawns) {
		
		int score = 0;
		for(int rank = 3; rank < 7; rank++) {
			score += LongUtil.bitCount(whitePawns&BitBoard.getRankMap(rank)) * S_ADVANCE[rank-3]; 
			score -= LongUtil.bitCount(blackPawns&BitBoard.getRankMap(7-rank)) * S_ADVANCE[rank-3]; 
		}
		
		return score;
	}
	
	private int countIslands(long pawns) {
		boolean inSea = true;
		int count = 0;
		for(int file = 0; file < 8; file++) {
			long pawnsOnFile = pawns & BitBoard.getFileMap(file);
			if(pawnsOnFile != 0 && inSea) {
				count++;
				inSea = false;
			} else if(pawnsOnFile == 0 && !inSea) {
				inSea = true;
			}
		}
		return count;
	}
	
	private int getDoubledScore(long whitePawns, long blackPawns) {
		int score = 0;
		
		for(int file = 0; file < 8; file++) {
			int wCount = LongUtil.bitCount(whitePawns & BitBoard.getFileMap(file));
			int bCount = LongUtil.bitCount(blackPawns & BitBoard.getFileMap(file));
			score -= (wCount > 1 ? (wCount-1) * S_DOUBLED : 0);
			score += (bCount > 1 ? (bCount-1) * S_DOUBLED : 0);
		}
		return score;
	}
	
	private int getIsolatedCount(long pawns) {
		int count = 0;
		long prevFile = 0;
		long thisFile = 0;
		for(int file = 0; file < 8; file++) {
			if(file == 0) {
				thisFile = pawns & BitBoard.getFileMap(file);
			}
			long nextFile = (file == 7 ? 0 : pawns & BitBoard.getFileMap(file+1));
			
			if(thisFile != 0 && prevFile == 0 && nextFile == 0) {
				count += LongUtil.bitCount(thisFile);
			}
			prevFile = thisFile;
			thisFile = nextFile;
		}
		return count;
	}
}
