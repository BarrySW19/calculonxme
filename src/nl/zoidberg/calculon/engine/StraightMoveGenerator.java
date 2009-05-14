package nl.zoidberg.calculon.engine;

import java.util.Vector;


public abstract class StraightMoveGenerator extends PieceMoveGenerator {

	protected abstract int getPieceType();
	
	public abstract int[][] getDirections();

	protected void makeUpBoardMoves(Board board, byte player, long source, long destinations, int distance, Vector rv) {
		BitBoard bitBoard = board.getBitBoard();
		
		int shift = distance;
		while((destinations>>>shift & source) != 0) {
			long moveTo = source<<shift;
			if((moveTo & bitBoard.getBitmapColor(player)) != 0) {
				return;
			}
			
			if((moveTo & bitBoard.getBitmapOppColor(player)) != 0) {
            	String move = EngineUtils.toCoord(source) + EngineUtils.toCoord(moveTo);
            	Board captureBoard = board.clone().applyMove(move);
            	if( ! CheckDetector.inCheck(captureBoard, ! board.isInCheck())) {
	                rv.addElement(new Move(move, null, board));
            	}
				break;
			}
			
			long moveMap = source | source<<shift;
			bitBoard.bitmaps[getPieceType()] ^= moveMap;
			bitBoard.bitmaps[player] ^= moveMap;
			if( ! CheckDetector.inCheck(bitBoard, player, ! board.isInCheck())) {
            	String move = EngineUtils.toCoord(source) + EngineUtils.toCoord(moveTo);
                rv.addElement(new Move(move, null, board));
			}
			bitBoard.bitmaps[getPieceType()] ^= moveMap;
			bitBoard.bitmaps[player] ^= moveMap;
			shift += distance;;
		}
	}
	
	protected void makeDownBoardMoves(Board board, byte player, long source, long destinations, int distance, Vector rv) {
		BitBoard bitBoard = board.getBitBoard();
		
		int shift = distance;
		while((destinations<<shift & source) != 0) {
			long moveTo = source>>>shift;
			if((moveTo & bitBoard.getBitmapColor(player)) != 0) {
				return;
			}
			
			if((moveTo & bitBoard.getBitmapOppColor(player)) != 0) {
            	String move = EngineUtils.toCoord(source) + EngineUtils.toCoord(moveTo);
            	Board captureBoard = board.clone().applyMove(move);
            	if( ! CheckDetector.inCheck(captureBoard, ! board.isInCheck())) {
	                rv.addElement(new Move(move, null, board));
            	}
				break;
			}
			
			long moveMap = source | source>>>shift;
			bitBoard.bitmaps[getPieceType()] ^= moveMap;
			bitBoard.bitmaps[player] ^= moveMap;
			if( ! CheckDetector.inCheck(bitBoard, player, ! board.isInCheck())) {
            	String move = EngineUtils.toCoord(source) + EngineUtils.toCoord(moveTo);
                rv.addElement(new Move(move, null, board));
			}
			bitBoard.bitmaps[getPieceType()] ^= moveMap;
			bitBoard.bitmaps[player] ^= moveMap;
			shift += distance;;
		}
	}
}
