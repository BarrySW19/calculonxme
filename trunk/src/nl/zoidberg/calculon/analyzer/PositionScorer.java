package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;

public interface PositionScorer {
	
	public int scorePosition(BitBoard bitBoard);
}
