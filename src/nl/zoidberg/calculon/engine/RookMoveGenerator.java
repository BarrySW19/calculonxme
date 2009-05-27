package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.model.Piece;


public class RookMoveGenerator extends StraightMoveGenerator {

	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		long pieces = bitBoard.getBitmapColor() & bitBoard.getBitmapRooks();
		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			
			int mapIdx = LongUtil.numberOfTrailingZeros(nextPiece);
			makeUpBoardMoves(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_U][mapIdx], 8, alreadyInCheck, safeFromCheck, rv);
			makeUpBoardMoves(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_R][mapIdx], 1, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardMoves(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_L][mapIdx], 1, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardMoves(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_D][mapIdx], 8, alreadyInCheck, safeFromCheck, rv);
		}
	}

	protected byte getPieceType() {
		return Piece.ROOK;
	}

	public void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		long pieces = bitBoard.getBitmapColor() & bitBoard.getBitmapRooks();
		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			
			int mapIdx = LongUtil.numberOfTrailingZeros(nextPiece);
			makeUpBoardThreats(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_U][mapIdx], 8, alreadyInCheck, safeFromCheck, rv);
			makeUpBoardThreats(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_R][mapIdx], 1, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardThreats(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_L][mapIdx], 1, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardThreats(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_D][mapIdx], 8, alreadyInCheck, safeFromCheck, rv);
		}
	}
}