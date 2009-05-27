package nl.zoidberg.calculon.engine;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.model.Piece;

public class MoveGenerator implements Enumeration {
	
	private static Vector generators = new Vector();
	
	static {
		generators.addElement(new PawnCaptureGenerator());
		generators.addElement(new KnightMoveGenerator());
		generators.addElement(new BishopMoveGenerator());
		generators.addElement(new RookMoveGenerator());
		generators.addElement(new QueenMoveGenerator());
		generators.addElement(new PawnMoveGenerator());
		generators.addElement(new KingMoveGenerator());
	}
	
	private static int[] DIR_LINE = new int[] { Bitmaps.BM_U, Bitmaps.BM_D, Bitmaps.BM_L, Bitmaps.BM_R, };
	private static int[] DIR_DIAG = new int[] { Bitmaps.BM_UR, Bitmaps.BM_DR, Bitmaps.BM_UL, Bitmaps.BM_DL, };
	
	private BitBoard bitBoard;
	private Vector queuedMoves = new Vector();
	private int genIndex = 0;
	private boolean inCheck;
	private long potentialPins = 0;
	
	public MoveGenerator(BitBoard bitBoard) {
		this.bitBoard = bitBoard;
		this.inCheck = CheckDetector.isPlayerToMoveInCheck(bitBoard);
		
		long enemyDiagAttackers = bitBoard.getBitmapOppColor() & (bitBoard.getBitmapBishops() | bitBoard.getBitmapQueens());
		long enemyLineAttackers = bitBoard.getBitmapOppColor() & (bitBoard.getBitmapRooks() | bitBoard.getBitmapQueens());
		int myKingIdx = LongUtil.numberOfTrailingZeros(bitBoard.getBitmapColor() & bitBoard.getBitmapKings());

		for(int x = 0; x < DIR_LINE.length; x++) {
			if((Bitmaps.maps2[DIR_LINE[x]][myKingIdx] & enemyLineAttackers) != 0) {
				potentialPins |= Bitmaps.maps2[DIR_LINE[x]][myKingIdx];
			}
		}
		for(int x = 0; x < DIR_DIAG.length; x++) {
			if((Bitmaps.maps2[DIR_DIAG[x]][myKingIdx] & enemyDiagAttackers) != 0) {
				potentialPins |= Bitmaps.maps2[DIR_DIAG[x]][myKingIdx];
			}
		}
	}

	public boolean hasMoreElements() {
		if(queuedMoves.size() == 0) {
			populateMoves();
		}
		return (queuedMoves.size() > 0);
	}

	public Object nextElement() {
		if(queuedMoves.size() == 0) {
			populateMoves();
		}
		if(queuedMoves.size() == 0) {
			throw new NoSuchElementException();
		}
		BitBoardMove rv = (BitBoardMove) queuedMoves.elementAt(0);
		queuedMoves.removeElementAt(0);
		return rv;
	}

	private void populateMoves() {
		if(genIndex >= generators.size()) {
			return;
		}

		if(bitBoard.isDrawnByRule()) {
			return;
		}
		
		PieceMoveGenerator nextGen = (PieceMoveGenerator) generators.elementAt(genIndex++);
		nextGen.generateMoves(bitBoard, inCheck, potentialPins, queuedMoves);
		
		if(queuedMoves.size() == 0 && genIndex < generators.size()) {
			populateMoves();
		}
	}
	
	/**
	 * An easy way to generate all moves - this will be useful for testing and legal move generation, but not for
	 * calculation as it's too slow.
	 * 
	 * @return
	 */
	public Vector getAllRemainingMoves() {
		Vector moves = new Vector();
		while(this.hasMoreElements()) {
			moves.addElement(this.nextElement());
		}
		return moves;
	}
	
	public Vector getThreateningMoves() {
		Vector moves = new Vector();
		
//		for(PieceMoveGenerator generator: generators) {
//			generator.generateThreatMoves(bitBoard, inCheck, potentialPins, moves);
//		}
		
		return moves;
	}
	
	public static Hashtable getPossibleMoves(BitBoard bitBoard) {
		Hashtable moves = new Hashtable();
		for(Enumeration e = new MoveGenerator(bitBoard).getAllRemainingMoves().elements(); e.hasMoreElements(); ) {
			BitBoardMove moveObj = (BitBoardMove) e.nextElement();
			String move = moveObj.getAlgebraic();
			if("O-O".equals(move)) {
				move = bitBoard.getPlayer() == Piece.WHITE ? "E1G1" : "E8G8";
			}
			if("O-O-O".equals(move)) {
				move = bitBoard.getPlayer() == Piece.WHITE ? "E1C1" : "E8C8";
			}
			String from = move.substring(0, 2);
			if(moves.get(from) == null) {
				moves.put(from, new Vector());
			}
			Vector toList = (Vector) moves.get(from);
			toList.addElement(move.substring(2));
		}
		return moves;
	}
}
