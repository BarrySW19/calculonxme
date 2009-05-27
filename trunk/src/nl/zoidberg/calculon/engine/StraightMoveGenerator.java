package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;

public abstract class StraightMoveGenerator extends PieceMoveGenerator {

	private ShiftStrategy shiftUp = new ShiftUpStrategy();
	private ShiftStrategy shiftDown = new ShiftDownStrategy();
	
	protected abstract byte getPieceType();
	
	private void makeBoardThreats(BitBoard bitBoard, long source, long destinations, int distance,
			boolean alreadyInCheck, boolean safeFromCheck, Vector rv, ShiftStrategy ss) {

		byte player = bitBoard.getPlayer();
		int shift = distance;
		boolean isCapture = false;

		while( !isCapture && (ss.shift1(destinations, shift) & source) != 0) {
			long moveTo = ss.shift2(source, shift);
			shift += distance;
			
			if((moveTo & bitBoard.getBitmapColor(player)) != 0) {
				return;
			}
			
			isCapture = (moveTo & bitBoard.getBitmapOppColor(player)) != 0; 
			BitBoardMove bbMove;
			if(isCapture) {
				// This is a capturing move.
				bbMove = BitBoard.generateCapture(
						source, moveTo, player, getPieceType(), bitBoard.getPiece(moveTo));
			} else {
				bbMove = BitBoard.generateMove(source, moveTo, player, getPieceType());
			}
			
			bitBoard.makeMove(bbMove);
			if(safeFromCheck || ! CheckDetector.isPlayerJustMovedInCheck(bitBoard)) {
//				if(isCapture || CheckDetector.isPlayerToMoveInCheck(bitBoard)) {
				if(isCapture) {
					rv.addElement(bbMove);
				}
			}
			bitBoard.unmakeMove();
		}
	}
	
	private void makeBoardMoves(BitBoard bitBoard, long source, long destinations, int distance,
			boolean alreadyInCheck, boolean safeFromCheck, Vector rv, ShiftStrategy ss) {

		byte player = bitBoard.getPlayer();
		int shift = distance;
		boolean isCapture = false;

		while( !isCapture && (ss.shift1(destinations, shift) & source) != 0) {
			long moveTo = ss.shift2(source, shift);
			if((moveTo & bitBoard.getBitmapColor(player)) != 0) {
				return;
			}
			
			isCapture = (moveTo & bitBoard.getBitmapOppColor(player)) != 0; 
			BitBoardMove bbMove;
			if(isCapture) {
				// This is a capturing move.
				bbMove = BitBoard.generateCapture(
						source, moveTo, player, getPieceType(), bitBoard.getPiece(moveTo));
			} else {
				bbMove = BitBoard.generateMove(source, moveTo, player, getPieceType());
			}
			
			if(safeFromCheck) {
				rv.addElement(bbMove);
			} else {
				bitBoard.makeMove(bbMove);
            	if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard, ! alreadyInCheck)) {
	                rv.addElement(bbMove);
            	}
				bitBoard.unmakeMove();
			}
			
			shift += distance;
		}
	}
	
	protected void makeUpBoardMoves(BitBoard bitBoard, long source,
			long destinations, int distance, boolean alreadyInCheck, boolean safeFromCheck, Vector rv) {
		this.makeBoardMoves(bitBoard, source, destinations, distance, alreadyInCheck, safeFromCheck, rv, shiftUp);
	}
	
	protected void makeDownBoardMoves(BitBoard bitBoard, long source,
			long destinations, int distance, boolean alreadyInCheck, boolean safeFromCheck, Vector rv) {
		this.makeBoardMoves(bitBoard, source, destinations, distance, alreadyInCheck, safeFromCheck, rv, shiftDown);
	}
	
	protected void makeUpBoardThreats(BitBoard bitBoard, long source,
			long destinations, int distance, boolean alreadyInCheck, boolean safeFromCheck, Vector rv) {
		this.makeBoardThreats(bitBoard, source, destinations, distance, alreadyInCheck, safeFromCheck, rv, shiftUp);
	}
	
	protected void makeDownBoardThreats(BitBoard bitBoard, long source,
			long destinations, int distance, boolean alreadyInCheck, boolean safeFromCheck, Vector rv) {
		this.makeBoardThreats(bitBoard, source, destinations, distance, alreadyInCheck, safeFromCheck, rv, shiftDown);
	}
	
	private interface ShiftStrategy {
		public long shift1(long val, int dist);
		public long shift2(long val, int dist);
	}
	
	private static class ShiftUpStrategy implements ShiftStrategy {
		public long shift1(long val, int dist) {
			return (val>>>dist);
			
		}
		public long shift2(long val, int dist) {
			return (val<<dist);
		}
	}
	
	private static class ShiftDownStrategy implements ShiftStrategy {
		public long shift1(long val, int dist) {
			return (val<<dist);
			
		}
		public long shift2(long val, int dist) {
			return (val>>>dist);
		}
	}
}
