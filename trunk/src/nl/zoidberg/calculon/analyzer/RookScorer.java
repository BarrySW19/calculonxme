package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.LongUtil;
import nl.zoidberg.calculon.model.Piece;

public class RookScorer implements PositionScorer {

	public static final int OPEN_FILE_SCORE = 150;
	public static final int CONNECTED_BONUS = 150;

	public int scorePosition(Board board) {
		int score = 0;
		score += scoreRooks(board, Piece.WHITE);
		score -= scoreRooks(board, Piece.BLACK);
		return score;
	}
	
	private int scoreRooks(Board board, byte color) {
		BitBoard bitBoard = board.getBitBoard();
		int score = 0;
		
		long rookMap = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapRooks());
		while(rookMap != 0) {
			long nextRook = LongUtil.highestOneBit(rookMap);
			rookMap ^= nextRook;
			int file = (int) (LongUtil.numberOfTrailingZeros(nextRook) % 8);
			if((bitBoard.getBitmapColor(color) & bitBoard.getBitmapPawns() & BitBoard.getFileMap(file)) == 0) {
				score += OPEN_FILE_SCORE;
			}
		}
		
		rookMap = (bitBoard.getBitmapColor(color) & bitBoard.getBitmapRooks());
		if(LongUtil.bitCount(rookMap) == 2) {
			int[] rook1 = BitBoard.toCoords(LongUtil.highestOneBit(rookMap));
			int[] rook2 = BitBoard.toCoords(LongUtil.lowestOneBit(rookMap));
			if(rook1[0] == rook2[0]) {
				long connMask = 0;
				for(int i = Math.min(rook1[1], rook2[1]) + 1; i < Math.max(rook1[1], rook2[1]); i++) {
					connMask |= 1L<<(i<<3)<<rook1[0];
				}
				if((connMask & bitBoard.getAllPieces()) == 0) {
					score += CONNECTED_BONUS;
				}
			} else if(rook1[1] == rook2[1]) {
				long connMask = 0;
				for(int i = Math.min(rook1[0], rook2[0]) + 1; i < Math.max(rook1[0], rook2[0]); i++) {
					connMask |= 1L<<(rook1[1]<<3)<<i;
				}
				if((connMask & bitBoard.getAllPieces()) == 0) {
					score += CONNECTED_BONUS;
				}
			}
		}
				
		return score;
	}
}
