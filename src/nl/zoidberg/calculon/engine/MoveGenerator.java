package nl.zoidberg.calculon.engine;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

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
	
	private Board board;
	private Vector queuedMoves = new Vector();
	private int genIndex = 0;
	
	public MoveGenerator(Board board) {
		this.board = board.clone();
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
		Move rv = (Move) queuedMoves.elementAt(0);
		queuedMoves.removeElementAt(0);
		return rv;
	}

	public void remove() {
		throw new RuntimeException("Unsupported Method");
	}
	
	private void populateMoves() {
		if(genIndex >= generators.size()) {
			return;
		}
		
		if(board.isDrawnByRule()) {
			return;
		}
		
		PieceMoveGenerator nextGen = (PieceMoveGenerator) generators.elementAt(genIndex++);
		EngineUtils.addAll(queuedMoves, nextGen.generateMoves(board));
		
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
	
	public static Hashtable getPossibleMoves(Board board) {
		Hashtable moves = new Hashtable();
		
		Vector v = new MoveGenerator(board).getAllRemainingMoves();
		for(Enumeration e = v.elements(); e.hasMoreElements(); ) {
			Move moveObj = (Move) e.nextElement();
			String move = moveObj.getMove();
			if("O-O".equals(move)) {
				move = board.getPlayer() == Piece.WHITE ? "E1G1" : "E8G8";
			}
			if("O-O-O".equals(move)) {
				move = board.getPlayer() == Piece.WHITE ? "E1C1" : "E8C8";
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
