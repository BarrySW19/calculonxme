package nl.zoidberg.calculon.analyzer;

import nl.zoidberg.calculon.engine.BitBoard;
import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.model.Piece;

public class KingSafetyScorer implements PositionScorer {

	public int scorePosition(Board board) {
		int score = 0;
		score += scoreSafety(board, Piece.WHITE);
		score -= scoreSafety(board, Piece.BLACK);
		return score;
	}

	private int scoreSafety(Board board, byte color) {
		BitBoard bitBoard = board.getBitBoard();

		if((bitBoard.getBitmapOppColor(color)|bitBoard.getBitmapQueens()) == 0) {
			// Fairly crude endgame test...
			return 0;
		}
		
		int score = 0;
		int[] kingPos = BitBoard.toCoords(bitBoard.getBitmapColor(color) & bitBoard.getBitmapKings());
		
		if(kingPos[0] == 3 || kingPos[0] == 4) {
			score -= 250;
		}
		
		for(int i = -1; i <= 1; i++) {
			byte rPiece = board.getPieceIfOnBoard(kingPos[0]+i, kingPos[1]+(color == Piece.WHITE ? 1 : -1)); 
			if(rPiece == (Piece.PAWN|color)) {
				score += 70;
			} else if(rPiece != 0 && (rPiece&Piece.MASK_COLOR) == color) {
				score += 40;
			}
		}
		
		return score;
	}

}
