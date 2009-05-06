package nl.zoidberg.calculon.engine;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import nl.zoidberg.calculon.analyzer.GameScorer;
import nl.zoidberg.calculon.model.Board;

public class SearchNode {
        private static final Random random = new Random();
	private static final int DEPTH = 3;
	
	private Board board;
	private Hashtable childNodes;
	
	public float getAlphaBetaScore(int depth, GameScorer scorer) {
		return alphaBetaMax(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, depth, scorer);
	}
	
	private float alphaBetaMax(float alpha, float beta, int depthLeft, GameScorer scorer) {
		if(depthLeft == 0 || getChildNodes().size() == 0) {
			float rv = scorer.score(board);
			return rv;
		}

        for(Enumeration e = getChildNodes().keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            SearchNode childNode = (SearchNode) getChildNodes().get(key);
            float score = childNode.alphaBetaMin(alpha, beta, depthLeft - 1, scorer);
            childNode.reset();
            if(score >= beta) {
                    return beta;
            }
            if(score > alpha) {
                    alpha = score;
            }
		}
		
		return alpha;
	}

	private float alphaBetaMin(float alpha, float beta, int depthLeft, GameScorer scorer) {
		if(depthLeft == 0 || getChildNodes().size() == 0) {
			float rv = scorer.score(board);
			return rv * -1;
		}

                for(Enumeration e = getChildNodes().keys(); e.hasMoreElements(); ) {
                    String key = (String) e.nextElement();
                    SearchNode childNode = (SearchNode) getChildNodes().get(key);
                    float score = childNode.alphaBetaMax(alpha, beta, depthLeft - 1, scorer);
                    childNode.reset();
                    if(score <= alpha) {
                            return alpha;
                    }
                    if(score < beta) {
                            beta = score;
                    }
		}
		
		return beta;
	}
	
	public void reset() {
		childNodes = null;
	}
	
	public SearchNode(Board board) {
		this.board = board;
	}

	public String toString() {
		return "SearchNode[" + board + "]";
	}

	public Hashtable getChildNodes() {
		if(childNodes == null) {
				childNodes = MoveGenerator.get().generateMoves(board);
		}
		return childNodes;
	}

	public Board getBoard() {
		return board;
	}
	
	public Hashtable getScoredMoves(GameScorer scorer) {
		Hashtable rv = new Hashtable();
                for(Enumeration e = this.getChildNodes().keys(); e.hasMoreElements(); ) {
                    String move = (String) e.nextElement();
                    float score = ((SearchNode)getChildNodes().get(move)).getAlphaBetaScore(DEPTH, scorer);
                    rv.put(move, new Float(score));
		}
		return rv;
	}
	
	public String getBestMove(GameScorer scorer) {
		return selectBestMove(getScoredMoves(scorer));
	}

	public static String selectBestMove(Hashtable allMoves) {
		Hashtable bestMoves = selectBestMoves(allMoves);
		if(bestMoves.size() == 0) {
			return null;
		}
		Vector moves = new Vector();
                for(Enumeration e = bestMoves.keys(); e.hasMoreElements(); ) {
                    moves.addElement(e.nextElement());;
                }
		return (String) moves.elementAt((int) (random.nextFloat() * moves.size()));
	}
	
	public String getPreferredMove(GameScorer scorer) {
		Hashtable allMoves = SearchNode.selectBestMoves(getScoredMoves(scorer));
		if(allMoves.size() > 1) {
			allMoves = rescoreImmediate(allMoves, scorer);
		}
		String rv = SearchNode.selectBestMove(allMoves);
		return rv;
	}
	
	public String getPreferredMove() {
		return getPreferredMove(GameScorer.getDefaultScorer());
	}
	
	public static Hashtable selectBestMoves(Hashtable allMoves) {
		float bestScore = Float.POSITIVE_INFINITY;
		Hashtable bestMoves = new Hashtable();
                for(Enumeration e = allMoves.keys(); e.hasMoreElements(); ) {
                    String move = (String) e.nextElement();
                    float score = ((Float)allMoves.get(move)).floatValue();
                    if (score < bestScore) {
                            bestMoves.clear();
                            bestMoves.put(move, new Float(score));
                            bestScore = score;
                    } else if (score == bestScore) {
                            bestMoves.put(move, new Float(score));
                    }
		}
		
		return bestMoves;
	}
	
	public Hashtable rescoreImmediate(Hashtable allMoves, GameScorer scorer) {
		Hashtable rv = new Hashtable();
                for(Enumeration e = allMoves.keys(); e.hasMoreElements(); ) {
                    String move = (String) e.nextElement();
			float score = ((SearchNode)getChildNodes().get(move)).getAlphaBetaScore(1, scorer);
			rv.put(move, new Float(score));
		}
		return rv;
	}
}
