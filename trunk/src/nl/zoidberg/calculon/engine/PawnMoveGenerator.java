package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.model.Piece;

public class PawnMoveGenerator extends PieceMoveGenerator {

	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		
		long myColor = bitBoard.getBitmapColor(bitBoard.getPlayer());
		long toPlayMap = myColor & bitBoard.getAllPieces();
		
		byte playerIdx = bitBoard.getPlayer();
		long freePawns = toPlayMap & bitBoard.getBitmapPawns();
		freePawns &= ~(playerIdx == Piece.WHITE ? bitBoard.getAllPieces()>>>8 : bitBoard.getAllPieces()<<8);
		for(long pawns = freePawns; pawns != 0; ) {
			long nextPawn = LongUtil.highestOneBit(pawns);
			pawns ^= nextPawn;
			boolean safeFromCheck = ((nextPawn & potentialPins) == 0) & !alreadyInCheck;

			long toSquare = (playerIdx == BitBoard.MAP_WHITE ? nextPawn<<8 : nextPawn>>>8);
			if(safeFromCheck) {
				if((toSquare & BitBoard.FINAL_RANKS) != 0) {
					rv.addElement(BitBoard.generatePromote(nextPawn, toSquare, playerIdx, Piece.QUEEN));
					rv.addElement(BitBoard.generatePromote(nextPawn, toSquare, playerIdx, Piece.ROOK));
					rv.addElement(BitBoard.generatePromote(nextPawn, toSquare, playerIdx, Piece.BISHOP));
					rv.addElement(BitBoard.generatePromote(nextPawn, toSquare, playerIdx, Piece.KNIGHT));
				} else {
					rv.addElement(BitBoard.generateMove(nextPawn, toSquare, playerIdx, Piece.PAWN));
				}
			} else {
				BitBoardMove bbMove = BitBoard.generateMove(nextPawn, toSquare, playerIdx, Piece.PAWN);
				bitBoard.makeMove(bbMove);
				if( !CheckDetector.isPlayerJustMovedInCheck(bitBoard, !alreadyInCheck)) {
					if((toSquare & BitBoard.FINAL_RANKS) != 0) {
						rv.addElement(BitBoard.generatePromote(nextPawn, toSquare, playerIdx, Piece.QUEEN));
						rv.addElement(BitBoard.generatePromote(nextPawn, toSquare, playerIdx, Piece.ROOK));
						rv.addElement(BitBoard.generatePromote(nextPawn, toSquare, playerIdx, Piece.BISHOP));
						rv.addElement(BitBoard.generatePromote(nextPawn, toSquare, playerIdx, Piece.KNIGHT));
					} else {
						rv.addElement(bbMove);
					}
				}
				bitBoard.unmakeMove();
			}
		}
		
		freePawns &= ~(playerIdx == Piece.WHITE ? bitBoard.getAllPieces()>>>16 : bitBoard.getAllPieces()<<16);
		freePawns &= (BitBoard.getRankMap(playerIdx == Piece.WHITE ? 1 : 6));
		for(long pawns = freePawns; pawns != 0; ) {
			long nextPawn = LongUtil.highestOneBit(pawns);
			pawns ^= nextPawn;
			boolean safeFromCheck = ((nextPawn & potentialPins) == 0) & !alreadyInCheck;
			
			long toSquare = (playerIdx == BitBoard.MAP_WHITE ? nextPawn<<16 : nextPawn>>>16);
			BitBoardMove bbMove = BitBoard.generateDoubleAdvanceMove(nextPawn, toSquare, playerIdx);
			if(safeFromCheck) {
				rv.addElement(bbMove);
			} else {
				bitBoard.makeMove(bbMove);
				if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard, ! alreadyInCheck)) {
					rv.addElement(bbMove);
				}
				bitBoard.unmakeMove();
			}
		}
	}

	public void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck,
			long potentialPins, Vector rv) {
		// TODO Auto-generated method stub
		
	}
}
