package nl.zoidberg.calculon.analyzer;

import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;

public interface PositionScorer {
	
	public int scorePosition(Board board, Hashtable pieceMap);
}
