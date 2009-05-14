package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.model.Piece;


public class BishopMoveGenerator extends StraightMoveGenerator {

	public static final int[][] DIRECTIONS = new int[][] {
		{ 1, 1 },
		{ -1, 1 },
		{ -1, -1 },
		{ 1, -1 },
	};
	
	public int[][] getDirections() {
		return DIRECTIONS;
	}

	public Vector generateMoves(Board board) {
		Vector rv = new Vector();
		BitBoard bitBoard = board.getBitBoard();
		
		byte player = board.getPlayer();
		
		long pieces = bitBoard.getBitmapColor(player) & bitBoard.getBitmapBishops();
		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			int[] coords = BitBoard.toCoords(nextPiece);
			
			int mapIdx = coords[0]<<3|coords[1];
			makeUpBoardMoves(board, player, nextPiece, Bitmaps.maps[Bitmaps.BM_UR][mapIdx], 9, rv);
			makeUpBoardMoves(board, player, nextPiece, Bitmaps.maps[Bitmaps.BM_UL][mapIdx], 7, rv);
			makeDownBoardMoves(board, player, nextPiece, Bitmaps.maps[Bitmaps.BM_DR][mapIdx], 7, rv);
			makeDownBoardMoves(board, player, nextPiece, Bitmaps.maps[Bitmaps.BM_DL][mapIdx], 9, rv);
		}
		
		return rv;
	}

	protected int getPieceType() {
		return Piece.BISHOP;
	}
}