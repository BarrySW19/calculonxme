package nl.zoidberg.calculon.analyzer;

import java.util.Enumeration;
import java.util.Vector;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.model.Game;
import nl.zoidberg.calculon.model.Piece;

public class GameScorer {
	public static final int MATE_SCORE = -100000;
	
	private static GameScorer instance = getUnweightedScorer();
	
	private static GameScorer getUnweightedScorer() {
		GameScorer rv = new GameScorer();
		
		rv.addScorer(new MaterialScorer());
		rv.addScorer(new BishopPairScorer());
		rv.addScorer(new BishopMobilityScorer());
		rv.addScorer(new PawnStructureScorer());
		rv.addScorer(new KnightScorer());
		rv.addScorer(new RookScorer());
		rv.addScorer(new KingSafetyScorer());
		
		return rv;
	}
	
	private Vector scorers = new Vector();
	
	public static GameScorer getDefaultScorer() {
		return instance;
	}
	
	public void addScorer(PositionScorer scorer) {
		scorers.addElement(scorer);
	}
	
	/**
	 * Generate a score - positive is good for the current player. Position scorers however can stick with the 
	 * convention of having white as positive.
	 * 
	 * @param board
	 * @param quiesce Indicates whether quiescence searching should be applied.
	 * @return
	 */
	public int score(BitBoard bitBoard, boolean quiesce, int alpha, int beta) {

//		System.out.println("score(" + alpha + ","+beta+")");
		String result = bitBoard.getResult();
		
		if(result == Game.RES_DRAW) {
			return 0;
		}
		
		if(result == Game.RES_BLACK_WIN || result == Game.RES_WHITE_WIN) {
			return MATE_SCORE;
		}
		
		int score = 0;
		
		for(Enumeration e = scorers.elements(); e.hasMoreElements(); ) {
			PositionScorer scorer = (PositionScorer) e.nextElement();
			score += scorer.scorePosition(bitBoard);
		}

		return score * (bitBoard.getPlayer() == Piece.WHITE ? 1 : -1);
	}
}
