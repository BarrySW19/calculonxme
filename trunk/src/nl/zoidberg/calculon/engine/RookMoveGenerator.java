package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.model.Piece;


public class RookMoveGenerator extends StraightMoveGenerator {

	public static int[][] DIRECTIONS = new int[][] {
		{ 1, 0 },
		{ 0, 1 },
		{ -1, 0 },
		{ 0, -1 },
	};

	public int[][] getDirections() {
		return DIRECTIONS;
	}

	public Vector generateMoves(Board board) {
		Vector rv = new Vector();
		BitBoard bitBoard = board.getBitBoard();
		
		byte player = board.getPlayer();
		
		long pieces = bitBoard.getBitmapColor(player) & bitBoard.getBitmapRooks();
		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			int[] coords = BitBoard.toCoords(nextPiece);
			
			int mapIdx = coords[0]<<3|coords[1];
			makeUpBoardMoves(board, player, nextPiece, Bitmaps.maps[Bitmaps.BM_U][mapIdx], 8, rv);
			makeUpBoardMoves(board, player, nextPiece, Bitmaps.maps[Bitmaps.BM_R][mapIdx], 1, rv);
			makeDownBoardMoves(board, player, nextPiece, Bitmaps.maps[Bitmaps.BM_L][mapIdx], 1, rv);
			makeDownBoardMoves(board, player, nextPiece, Bitmaps.maps[Bitmaps.BM_D][mapIdx], 8, rv);
		}
		
		return rv;
	}

	protected int getPieceType() {
		return Piece.ROOK;
	}
}