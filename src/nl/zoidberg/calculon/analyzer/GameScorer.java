package nl.zoidberg.calculon.analyzer;

import java.util.Enumeration;
import java.util.Hashtable;

import java.util.Vector;
import nl.zoidberg.calculon.engine.CheckDetector;
import nl.zoidberg.calculon.engine.MoveGenerator;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class GameScorer {
	private static GameScorer instance = getUnweightedScorer();
	
	private static GameScorer getUnweightedScorer() {
		GameScorer rv = new GameScorer();
		rv.addScorer(new MaterialScorer(), 1.0f);
		rv.addScorer(new BishopPairScorer(), 1.0f);
		rv.addScorer(new BishopMobilityScorer(), 1.0f);
		rv.addScorer(new PawnStructureScorer(), 1.0f);
		rv.addScorer(new KnightScorer(), 1.0f);
		rv.addScorer(new RookScorer(), 1.0f);
		rv.addScorer(new KingSafetyScorer(), 1.0f);
		return rv;
	}
	
	private Hashtable scorers = new Hashtable();
	
	public static GameScorer getDefaultScorer() {
		return instance;
	}
	
	public void addScorer(PositionScorer scorer, float weighting) {
		scorers.put(scorer, new Float(weighting));
	}
	
	/**
	 * Generate a score - positive is good for the current player. Position scorers however can stick with the 
	 * convention of having white as positive.
	 * 
	 * @param board
	 * @return
	 */
	public float score(Board board) {
		if(board.getHalfMoveCount() >= 100) {
			return 0; // Draw by 50 move rule
		}
		
		if( ! MoveGenerator.get().isMovePossible(board)) {
			if(CheckDetector.alreadyInCheck(board)) {
				//return board.getPlayer() == Piece.WHITE ? -100 : 100;  // Checkmate
				return -100;
			} else {
				return 0;  // Stalemate
			}
		}
		
		if(board.getRepeatedCount() >= 3) {
			return 0;
		}
		
		float score = 0;
		Hashtable pieceMap = generatePieceMap(board);
		for(Enumeration e = scorers.keys(); e.hasMoreElements(); ) {
                    PositionScorer scorer = (PositionScorer) e.nextElement();
                    score += (scorer.scorePosition(board, pieceMap) * ((Float) scorers.get(scorer)).floatValue());
		}

		return score * (board.getPlayer() == Piece.WHITE ? 1 : -1);
	}
	
	public static Hashtable generatePieceMap(Board board) {
		Hashtable pieceMap = new Hashtable();
		for(int file = 0; file < 8; file++) {
			for(int rank = 0; rank < 8; rank++) {
				byte rPiece = board.getPiece(file, rank);
				if(rPiece == 0) {
					continue;
				}
				Vector locations = (Vector) pieceMap.get(new Byte(rPiece));
				if(locations == null) {
					locations = new Vector();
					pieceMap.put(new Byte(rPiece), locations);
				}
				locations.addElement(new int[] { file, rank });
			}
		}
		return pieceMap;
	}
}
