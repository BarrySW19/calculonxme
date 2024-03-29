package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.model.Piece;

public class PawnCaptureGenerator extends PieceMoveGenerator {

	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		byte player = bitBoard.getPlayer();
		
		long myPawns = bitBoard.getBitmapColor(player) & bitBoard.getBitmapPawns();
		long enemyPieces = bitBoard.getBitmapOppColor(player);
		long epLocation = -1;
		if(bitBoard.isEnPassant()) {
			// Just treat the enpassant square as another enemy piece.
			epLocation = 1L<<(bitBoard.getEnPassantRank()<<3)<<bitBoard.getEnPassantFile();
			enemyPieces |= epLocation;
		}
		
		long captureRight = player == Piece.WHITE
				? (enemyPieces & ~BitBoard.getFileMap(0))>>>9 : (enemyPieces & ~BitBoard.getFileMap(0))<<7;
		long captureLeft = player == Piece.WHITE
				? (enemyPieces & ~BitBoard.getFileMap(7))>>>7 : (enemyPieces & ~BitBoard.getFileMap(7))<<9;
		
		myPawns &= (captureLeft | captureRight);
		
		while(myPawns != 0) {
			long nextPiece = LongUtil.highestOneBit(myPawns);
			myPawns ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			
			if((nextPiece & captureLeft) != 0) {
				long captured = (player == Piece.WHITE ? nextPiece<<7 : nextPiece>>>9);
				tryCaptures(bitBoard, player, nextPiece, captured, epLocation, alreadyInCheck, safeFromCheck, rv);
			}
			if((nextPiece & captureRight) != 0) {
				long captured = (player == Piece.WHITE ? nextPiece<<9 : nextPiece>>>7);
				tryCaptures(bitBoard, player, nextPiece, captured, epLocation, alreadyInCheck, safeFromCheck, rv);
			}
		}
	}
	
	private void tryCaptures(BitBoard bitBoard, byte player, long nextPiece,
			long captured, long epLocation, boolean alreadyInCheck, boolean safeFromCheck, Vector rv) {
		
		BitBoardMove bbMove;
		if(captured == epLocation) {
			bbMove = BitBoard.generateEnPassantCapture(nextPiece, captured, player);
		} else {
			bbMove = BitBoard.generateCapture(
					nextPiece, captured, player, Piece.PAWN, bitBoard.getPiece(captured));
		}
		
		if(safeFromCheck) {
			if((captured & BitBoard.FINAL_RANKS) == 0) {
				rv.addElement(bbMove);
			} else {
				rv.addElement(BitBoard.generateCaptureAndPromote(
						nextPiece, captured, player, bitBoard.getPiece(captured), Piece.QUEEN));
				rv.addElement(BitBoard.generateCaptureAndPromote(
						nextPiece, captured, player, bitBoard.getPiece(captured), Piece.ROOK));
				rv.addElement(BitBoard.generateCaptureAndPromote(
						nextPiece, captured, player, bitBoard.getPiece(captured), Piece.BISHOP));
				rv.addElement(BitBoard.generateCaptureAndPromote(
						nextPiece, captured, player, bitBoard.getPiece(captured), Piece.KNIGHT));
			}
		} else {
			bitBoard.makeMove(bbMove);
			if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard, ! alreadyInCheck)) {
				bitBoard.unmakeMove();
				if((captured & BitBoard.FINAL_RANKS) == 0) {
					rv.addElement(bbMove);
				} else {
					rv.addElement(BitBoard.generateCaptureAndPromote(
							nextPiece, captured, player, bitBoard.getPiece(captured), Piece.QUEEN));
					rv.addElement(BitBoard.generateCaptureAndPromote(
							nextPiece, captured, player, bitBoard.getPiece(captured), Piece.ROOK));
					rv.addElement(BitBoard.generateCaptureAndPromote(
							nextPiece, captured, player, bitBoard.getPiece(captured), Piece.BISHOP));
					rv.addElement(BitBoard.generateCaptureAndPromote(
							nextPiece, captured, player, bitBoard.getPiece(captured), Piece.KNIGHT));
				}
			} else {
				bitBoard.unmakeMove();
			}
		}
	}

	public void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck,
			long potentialPins, Vector rv) {
		// Simple - I only generate captures anyway!
		generateMoves(bitBoard, alreadyInCheck, potentialPins, rv);
	}
}
