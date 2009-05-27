package nl.zoidberg.calculon.model;

import nl.zoidberg.calculon.analyzer.GameScorer;

public class Player {
	
	private GameScorer gameScorer;

	public GameScorer getGameScorer() {
		return gameScorer;
	}

	public void setGameScorer(GameScorer gameScorer) {
		this.gameScorer = gameScorer;
	}
}
