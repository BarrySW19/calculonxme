package nl.zoidberg.calculon.engine;

import java.util.Enumeration;
import java.util.Vector;

import nl.zoidberg.calculon.analyzer.GameScorer;
import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;

public class ChessEngine {
	private static final int DEPTH = 3;
	
//	private static final Logger log = Logger.getLogger(ChessEngine.class.getName());
	
	private GameScorer gameScorer;
	private int searchDepth = DEPTH;
	private int scoreMargin = 1000;
	private int maxDeepMoves = 5;
	
	public ChessEngine() {
		this.gameScorer = GameScorer.getDefaultScorer();
	}
	
	public ChessEngine(GameScorer gameScorer) {
		this.gameScorer = gameScorer;
	}
	
	public String getPreferredMove(BitBoard bitBoard) {
//		String bookMove = OpeningBook.getDefaultBook() == null ? null : OpeningBook.getDefaultBook().getBookMove(board); 
//		if(bookMove != null) {
//			log.fine("Using book move: " + bookMove);
//			return PGNUtils.toPgnMoveMap(board).get(bookMove);
//		}

		Vector allMoves = getScoredMoves(bitBoard);
//		System.out.println(allMoves);
//		ChessEngine.selectBestMoves(allMoves, scoreMargin, maxDeepMoves);
//		searchDepth++;
//		for(ScoredMove move: allMoves) {
//			List<ScoredMove> nextDepth = getScoredMoves(board.clone().applyMove(move.getMove()));
//			System.out.println(nextDepth);
//		}
		String rv = ChessEngine.selectBestMove(allMoves);
		
		return rv;
	}
	
	public Vector getScoredMoves(BitBoard bitBoard) {
		Vector rv = new Vector();

//		BitBoardMove bbm = bitBoard.getMove("G1G2");
//		bitBoard.makeMove(bbm);
//		int score = alphaBetaMax(Integer.MIN_VALUE, Integer.MAX_VALUE, searchDepth, bitBoard);
//		rv.add(new ScoredMove("G1G2", score));
//		bitBoard.unmakeMove();
		
		for(Enumeration moveItr = new MoveGenerator(bitBoard); moveItr.hasMoreElements(); ) {
			BitBoardMove move = (BitBoardMove) moveItr.nextElement();

			bitBoard.makeMove(move);
			int score = alphaBetaMax(Integer.MIN_VALUE, Integer.MAX_VALUE, searchDepth, bitBoard);
			
			bitBoard.unmakeMove();

			rv.addElement(new ScoredMove(move.getAlgebraic(), score));
		}
		
		return rv;
	}
	
	public int getScoreMargin() {
		return scoreMargin;
	}

	public void setScoreMargin(int scoreMargin) {
		this.scoreMargin = scoreMargin;
	}

	public int getMaxDeepMoves() {
		return maxDeepMoves;
	}

	public void setMaxDeepMoves(int maxDeepMoves) {
		this.maxDeepMoves = maxDeepMoves;
	}

	public final int getDepth() {
		return searchDepth;
	}

	public final void setDepth(int depth) {
		this.searchDepth = depth;
	}
	
	private int alphaBetaMax(int alpha, int beta, int depthLeft, BitBoard bitBoard) {
		Enumeration moveItr = new MoveGenerator(bitBoard);
		if(depthLeft == 0 || ! moveItr.hasMoreElements()) {
			int rv;
			rv = gameScorer.score(bitBoard, false, alpha, beta);
//			if(! moveItr.hasNext()) {
//				rv = gameScorer.score(bitBoard, false, alpha, beta);
//			} else {
//				rv = alphaBetaMinQ(alpha, beta, 0, bitBoard);
//			}
			if(rv == GameScorer.MATE_SCORE) {
				rv *= (depthLeft+1);
			}
			return rv;
		}
		
		while(moveItr.hasMoreElements()) {
			BitBoardMove move = (BitBoardMove) moveItr.nextElement();
			
			bitBoard.makeMove(move);
			int score = alphaBetaMin(alpha, beta, depthLeft - 1, bitBoard);
			bitBoard.unmakeMove();

			if(score >= beta) {
				return beta;
			}
			if(score > alpha) {
				alpha = score;
			}
		}
		
		return alpha;
	}

	private int alphaBetaMin(int alpha, int beta, int depthLeft, BitBoard bitBoard) {
		Enumeration moveItr = new MoveGenerator(bitBoard);
		if(depthLeft == 0 || ! moveItr.hasMoreElements()) {
			int rv;
			rv = gameScorer.score(bitBoard, false, alpha, beta);
//			if(! moveItr.hasNext()) {
//				rv = gameScorer.score(bitBoard, false, alpha, beta);
//			} else {
//				rv = alphaBetaMaxQ(alpha, beta, 0, bitBoard);
//			}
			if(rv == GameScorer.MATE_SCORE) {
				rv *= (depthLeft+1);
			}
			return -rv;
		}

		while(moveItr.hasMoreElements()) {
			BitBoardMove move = (BitBoardMove) moveItr.nextElement();
			
			bitBoard.makeMove(move);
			int score = alphaBetaMax(alpha, beta, depthLeft - 1, bitBoard);
			bitBoard.unmakeMove();
			
			if(score <= alpha) {
				return alpha;
			}
			if(score < beta) {
				beta = score;
			}
		}
		
		return beta;
	}
	
	private static String selectBestMove(Vector allMoves) {
		ScoredMove bestMove = null;
		for(Enumeration e = allMoves.elements(); e.hasMoreElements(); ) {
			ScoredMove move = (ScoredMove) e.nextElement();
			if(bestMove == null || move.getScore() < bestMove.getScore()) {
				bestMove = move;
			}
		}
		return (bestMove == null ? null : bestMove.getMove());
	}
	
	private static class ScoredMove {
		private String move;
		private int score;
		
		public ScoredMove(String move, int score) {
			super();
			this.move = move;
			this.score = score;
		}
		public String getMove() {
			return move;
		}
		public void setMove(String move) {
			this.move = move;
		}
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		
		public String toString() {
			return move + "=" + score;
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((move == null) ? 0 : move.hashCode());
			result = prime * result + score;
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ScoredMove other = (ScoredMove) obj;
			if (move == null) {
				if (other.move != null)
					return false;
			} else if (!move.equals(other.move))
				return false;
			if (score != other.score)
				return false;
			return true;
		}
		
		public int compareTo(Object o) {
			return score - ((ScoredMove)o).getScore();
		}
	}
}
