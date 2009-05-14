package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.model.Piece;

public class PawnMoveGenerator extends PieceMoveGenerator {

	public Vector generateMoves(Board board) {
		Vector rv = new Vector();
		
		BitBoard bitBoard = board.getBitBoard();
		BitBoard scratchPad = bitBoard.clone();
		long myColor = bitBoard.getBitmapColor(board.getPlayer());
		long toPlayMap = myColor & bitBoard.getAllPieces();
		
		int playerIdx = board.getPlayer() == Piece.WHITE ? BitBoard.MAP_WHITE : BitBoard.MAP_BLACK;
		long freePawns = toPlayMap & bitBoard.getBitmapPawns();
		freePawns &= ~(board.getPlayer() == Piece.WHITE ? bitBoard.getAllPieces()>>>8 : bitBoard.getAllPieces()<<8);
		for(long pawns = freePawns; pawns != 0; ) {
			long nextPawn = LongUtil.highestOneBit(pawns);
			pawns ^= nextPawn;
			long bothSquares = nextPawn | (playerIdx == BitBoard.MAP_WHITE ? nextPawn<<8 : nextPawn>>>8);
			scratchPad.bitmaps[BitBoard.MAP_PAWNS] ^= bothSquares;
			scratchPad.bitmaps[playerIdx] ^= bothSquares;
			if( ! CheckDetector.inCheck(scratchPad, board.getPlayer(), ! board.isInCheck())) {
				String move = EngineUtils.toCoord(nextPawn) + EngineUtils.toCoord(bothSquares^nextPawn);
				if((bothSquares & (BitBoard.getRankMap(0)|BitBoard.getRankMap(7))) != 0) {
					rv.addElement(new Move(move + "=Q", board.clone().applyMove(move + "=Q")));
					rv.addElement(new Move(move + "=R", board.clone().applyMove(move + "=R")));
					rv.addElement(new Move(move + "=N", board.clone().applyMove(move + "=N")));
					rv.addElement(new Move(move + "=B", board.clone().applyMove(move + "=B")));
				} else {
					rv.addElement(new Move(move, board.clone().applyMove(move)));
				}
			}
			// Unmake move
			scratchPad.bitmaps[BitBoard.MAP_PAWNS] ^= bothSquares;
			scratchPad.bitmaps[playerIdx] ^= bothSquares;
		}
		
		freePawns &= ~(board.getPlayer() == Piece.WHITE ? bitBoard.getAllPieces()>>>16 : bitBoard.getAllPieces()<<16);
		freePawns &= (BitBoard.getRankMap(board.getPlayer() == Piece.WHITE ? 1 : 6));
		for(long pawns = freePawns; pawns != 0; ) {
			long nextPawn = LongUtil.highestOneBit(pawns);
			pawns ^= nextPawn;
			long bothSquares = nextPawn | (playerIdx == BitBoard.MAP_WHITE ? nextPawn<<16 : nextPawn>>>16);
			scratchPad.bitmaps[BitBoard.MAP_PAWNS] ^= bothSquares;
			scratchPad.bitmaps[playerIdx] ^= bothSquares;
			if( ! CheckDetector.inCheck(scratchPad, board.getPlayer(), ! board.isInCheck())) {
				String move = EngineUtils.toCoord(nextPawn) + EngineUtils.toCoord(bothSquares^nextPawn);
				rv.addElement(new Move(move, board.clone().applyMove(move)));
			}
			// Unmake move
			scratchPad.bitmaps[BitBoard.MAP_PAWNS] ^= bothSquares;
			scratchPad.bitmaps[playerIdx] ^= bothSquares;
		}
		
		return rv;
	}
}
