package nl.zoidberg.calculon.model;

import nl.zoidberg.calculon.analyzer.BishopMobilityScorer;
import nl.zoidberg.calculon.analyzer.BishopPairScorer;
import nl.zoidberg.calculon.analyzer.GameScorer;
import nl.zoidberg.calculon.analyzer.KnightScorer;
import nl.zoidberg.calculon.analyzer.MaterialScorer;
import nl.zoidberg.calculon.analyzer.PawnStructureScorer;
import nl.zoidberg.calculon.analyzer.RookScorer;
import nl.zoidberg.calculon.engine.SearchNode;

public class Game {
	
	public static final String RES_NO_RESULT = "*";
	public static final String RES_WHITE_WIN = "1-0";
	public static final String RES_BLACK_WIN = "0-1";
	public static final String RES_DRAW = "1/2-1/2";

	private Board board;
	private Player whitePlayer, blackPlayer;

	public Game() {
		board = new Board().initialise();
		whitePlayer = new Player();
		blackPlayer = new Player();

//		GameScorer whiteScorer = GameScorer.getDefaultScorer();

		GameScorer scorer1 = GameScorer.getDefaultScorer();
		GameScorer scorer2 = new GameScorer();
		scorer2.addScorer(new MaterialScorer(), 1.0f);
		scorer2.addScorer(new BishopPairScorer(), 1.0f);
		scorer2.addScorer(new PawnStructureScorer(), 1.0f);
		scorer2.addScorer(new KnightScorer(), 1.0f);
		scorer2.addScorer(new BishopMobilityScorer(), 1.0f);
		scorer2.addScorer(new RookScorer(), 1.0f);

		whitePlayer.setGameScorer(scorer1);
		
		// GameScorer blackScorer = new GameScorer();
		// blackScorer.addScorer(new MaterialScorer());
		// blackScorer.addScorer(new BishopPairScorer());
		// blackScorer.addScorer(new PawnStructureScorer());
		// blackScorer.addScorer(new KnightActivityScorer());
		blackPlayer.setGameScorer(scorer2);
	}

	public Board getBoard() {
		return board;
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}

	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public static void main(String[] args) throws Exception {
		
		try {
			run();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public Player getCurrentPlayer() {
		return board.getPlayer() == Piece.WHITE ? whitePlayer : blackPlayer;
	}

	public static void run() throws Exception {
		Game game = new Game();
		
		for (;;) {
			SearchNode node = new SearchNode(game.getBoard());
			System.out.println(game.getBoard().getStateInfo());
			GameScorer useScorer = game.getBoard().getPlayer() == Piece.WHITE ?
					game.getWhitePlayer().getGameScorer() : game.getBlackPlayer().getGameScorer();
			String bestMove = node.getPreferredMove(useScorer);
			
			if (bestMove == null) {
				break;
			}
			if (game.getBoard().getHalfMoveCount() >= 100) {
				break;
			}
			
			game.getBoard().applyMove(bestMove);
		}
	}
}
