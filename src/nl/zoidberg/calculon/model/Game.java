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
//		FENUtils.loadPosition("1rbq2r1/3pkpp1/2n1p2p/1N1n4/1p1P3N/3Q2P1/1PP2PBP/R3R1K1 b - - 1 16", game.getBoard());
//		FENUtils.loadPosition("r1bqk2r/pppp1ppp/2n1p3/b7/2nPQ3/P1N2N2/1PPBPPPP/R3KB1R b KQkq - 4 9", game.getBoard());
//		FENUtils.loadPosition(
//				FENUtils.convertStyle12("<12> -------- -------- -k------ p------- -ppr---- ----p--- ----K--- ------q- B -1 0 0 0 0 1 176 DREZIO CalculonX 2 20 20 0 18 1797 2082 73 K/f3-e2 (0:01) Ke2 0 0 0"),
//				game.getBoard());
		
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
//			String pgnMove = PGNUtils.translateMove(game.getBoard(), bestMove);
			
			SearchNode.mSearch = 0;
			SearchNode.mHit = 0;
			
			game.getBoard().applyMove(bestMove);
//			for(String s: TextUtils.getMiniTextBoard(game.getBoard())) {
//				log.debug(s);
//			}
		}
	}
}
