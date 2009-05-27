package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.model.Piece;


public class BishopMoveGenerator extends StraightMoveGenerator {

	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		long pieces = bitBoard.getBitmapColor() & bitBoard.getBitmapBishops();
		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			
			int mapIdx = LongUtil.numberOfTrailingZeros(nextPiece);
			makeUpBoardMoves(bitBoard, nextPiece,
					Bitmaps.maps2[Bitmaps.BM_UR][mapIdx], 9, alreadyInCheck, safeFromCheck, rv);
			makeUpBoardMoves(bitBoard, nextPiece,
					Bitmaps.maps2[Bitmaps.BM_UL][mapIdx], 7, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardMoves(bitBoard, nextPiece,
					Bitmaps.maps2[Bitmaps.BM_DR][mapIdx], 7, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardMoves(bitBoard, nextPiece,
					Bitmaps.maps2[Bitmaps.BM_DL][mapIdx], 9, alreadyInCheck, safeFromCheck, rv);
		}
	}

	protected byte getPieceType() {
		return Piece.BISHOP;
	}

	public void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		long pieces = bitBoard.getBitmapColor() & bitBoard.getBitmapBishops();
		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			
			int mapIdx = LongUtil.numberOfTrailingZeros(nextPiece);
			makeUpBoardThreats(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_UR][mapIdx], 9, alreadyInCheck, safeFromCheck, rv);
			makeUpBoardThreats(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_UL][mapIdx], 7, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardThreats(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_DR][mapIdx], 7, alreadyInCheck, safeFromCheck, rv);
			makeDownBoardThreats(bitBoard, nextPiece, Bitmaps.maps2[Bitmaps.BM_DL][mapIdx], 9, alreadyInCheck, safeFromCheck, rv);
		}
	}
}