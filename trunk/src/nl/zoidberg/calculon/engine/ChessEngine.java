package nl.zoidberg.calculon.engine;

import java.util.Enumeration;
import java.util.Vector;

import nl.zoidberg.calculon.analyzer.GameScorer;

public class ChessEngine {
	private static final int DEPTH = 2;
	
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
	
	public String getPreferredMove(Board board) {
		Vector allMoves = getScoredMoves(board);
		String rv = ChessEngine.selectBestMove(allMoves);
		
		return rv;
	}
	
	public Vector getScoredMoves(Board board) {
		Vector rv = new Vector();

		for(Enumeration moveItr = new MoveGenerator(board); moveItr.hasMoreElements(); ) {
			Move move = (Move) moveItr.nextElement();
			int score = getAlphaBetaScore(searchDepth, move.getBoard());
			rv.addElement(new ScoredMove(move.getMove(), score));
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
	
	private int getAlphaBetaScore(int depth, Board board) {
		int score = alphaBetaMax(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, board);
		return score;
	}
	
	private int alphaBetaMax(int alpha, int beta, int depthLeft, Board board) {
		Enumeration moveItr = new MoveGenerator(board);
		if(depthLeft == 0 || ! moveItr.hasMoreElements()) {
			int rv = gameScorer.score(board);
			if(rv == GameScorer.MATE_SCORE) {
				rv *= (depthLeft+1);
			}
			return rv;
		}
		
		while(moveItr.hasMoreElements()) {
			int score = alphaBetaMin(alpha, beta, depthLeft - 1, ((Move)moveItr.nextElement()).getBoard());
			if(score >= beta) {
				return beta;
			}
			if(score > alpha) {
				alpha = score;
			}
		}
		
		return alpha;
	}

	private int alphaBetaMin(int alpha, int beta, int depthLeft, Board board) {
		Enumeration moveItr = new MoveGenerator(board);
		if(depthLeft == 0 || ! moveItr.hasMoreElements()) {
			int rv = gameScorer.score(board);
			if(rv == GameScorer.MATE_SCORE) {
				rv *= (depthLeft+1);
			}
			return -rv;
		}

		while(moveItr.hasMoreElements()) {
			int score = alphaBetaMax(alpha, beta, depthLeft - 1, ((Move)moveItr.nextElement()).getBoard());
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
	
	private static class ScoredMove implements Comparable {
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
