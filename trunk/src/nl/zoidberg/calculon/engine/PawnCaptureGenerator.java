package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.model.Piece;

public class PawnCaptureGenerator extends PieceMoveGenerator {

	private void addMove(String move, Board board, Vector rv) {
		Board nextBoard = board.clone();
		if(move.charAt(3) == '1' || move.charAt(3) == '8') {
			nextBoard.applyMove(move + "=Q");
		} else {
			nextBoard.applyMove(move);
		}
		
		if(CheckDetector.inCheck(nextBoard, ! board.isInCheck())) {
			return;
		}
		
		if(move.charAt(3) == '1' || move.charAt(3) == '8') {
			rv.addElement(new Move(move + "=N", board.clone().applyMove(move + "=N")));
			rv.addElement(new Move(move + "=B", board.clone().applyMove(move + "=B")));
			rv.addElement(new Move(move + "=R", board.clone().applyMove(move + "=R")));
			rv.addElement(new Move(move + "=Q", board.clone().applyMove(move + "=Q")));
		} else {
			rv.addElement(new Move(move, nextBoard));
		}
	}

	public Vector generateMoves(Board board) {
		Vector rv = new Vector();
		byte player = board.getPlayer();
		
		BitBoard bitBoard = board.getBitBoard();
		long myPawns = bitBoard.getBitmapColor(player) & bitBoard.getBitmapPawns();
		long enemyPieces = bitBoard.getBitmapOppColor(player);
		if(board.isEnPassant()) {
			enemyPieces |= 1L<<(board.getEnPassantRank()<<3)<<board.getEnPassantFile();
		}
		
		long captureRight = player == Piece.WHITE
				? (enemyPieces & ~BitBoard.getFileMap(0))>>>9 : (enemyPieces & ~BitBoard.getFileMap(0))<<7;
		long captureLeft = player == Piece.WHITE
				? (enemyPieces & ~BitBoard.getFileMap(7))>>>7 : (enemyPieces & ~BitBoard.getFileMap(7))<<9;
		
		myPawns &= (captureLeft | captureRight);
		
		while(myPawns != 0) {
			long nextPiece = LongUtil.highestOneBit(myPawns);
			myPawns ^= nextPiece;
			if((nextPiece & captureLeft) != 0) {
				String move = EngineUtils.toCoord(nextPiece)
					+ EngineUtils.toCoord(player == Piece.WHITE ? nextPiece<<7 : nextPiece>>>9);
				addMove(move, board, rv);
			}
			if((nextPiece & captureRight) != 0) {
				String move = EngineUtils.toCoord(nextPiece)
					+ EngineUtils.toCoord(player == Piece.WHITE ? nextPiece<<9 : nextPiece>>>7);
				addMove(move, board, rv);
			}
		}
		
		return rv;
	}
}
