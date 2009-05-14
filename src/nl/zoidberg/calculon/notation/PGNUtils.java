package nl.zoidberg.calculon.notation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.CheckDetector;
import nl.zoidberg.calculon.engine.EngineUtils;
import nl.zoidberg.calculon.engine.Move;
import nl.zoidberg.calculon.engine.MoveGenerator;
import nl.zoidberg.calculon.model.Piece;

public class PGNUtils {

	private PGNUtils() {
	}

	public static Hashtable toPgnMoveMap(Board board) {
		Vector allMoves = new MoveGenerator(board).getAllRemainingMoves();
		Hashtable rv = new Hashtable();
		for(Enumeration e = allMoves.elements(); e.hasMoreElements(); ) {
			String intMove = ((Move) e.nextElement()).getMove();
			rv.put(translateMove(board, intMove), intMove);
		}
		Hashtable duplicate = new Hashtable();
		for(Enumeration e = rv.keys(); e.hasMoreElements(); ) {
			String pgnMove = (String) e.nextElement();
			duplicate.put(pgnMove, rv.get(pgnMove));
			if(pgnMove.endsWith("+")) {
				duplicate.put(pgnMove.substring(0, pgnMove.length()-1), rv.get(pgnMove));
			}
		}
		return duplicate;
	}

	/**
	 * Translate a simple algebraic move (e.g. G1F3) into its PGN equivalent (e.g. Nf3).
	 * 
	 * @param board
	 * @param simpleAlgebraic
	 * @return
	 */
	public static String translateMove(Board board, String simpleAlgebraic) {
		if (simpleAlgebraic.startsWith("O-")) {
			Board b = board.clone().applyMove(simpleAlgebraic);
			return simpleAlgebraic
					+ (CheckDetector.alreadyInCheck(b) ? "+" : "");
		}

		int fromFile = EngineUtils.FILES.indexOf(simpleAlgebraic.charAt(0));
		int fromRank = EngineUtils.RANKS.indexOf(simpleAlgebraic.charAt(1));
		int toFile = EngineUtils.FILES.indexOf(simpleAlgebraic.charAt(2));
		int toRank = EngineUtils.RANKS.indexOf(simpleAlgebraic.charAt(3));

		byte movePiece = board.getPiece(fromFile, fromRank);
		if (movePiece == Piece.EMPTY) {
			throw new RuntimeException("Move from empty square: "
					+ simpleAlgebraic);
		}

		StringBuffer move = new StringBuffer();
		boolean testClash = false;
		switch (movePiece & Piece.MASK_TYPE) {
		case Piece.KNIGHT:
			move.append("N");
			testClash = true;
			break;
		case Piece.BISHOP:
			move.append("B");
			testClash = true;
			break;
		case Piece.ROOK:
			move.append("R");
			testClash = true;
			break;
		case Piece.QUEEN:
			move.append("Q");
			testClash = true;
			break;
		case Piece.KING:
			if(fromFile == Board.F_E && toFile == Board.F_G) {
				Board b = board.clone().applyMove(simpleAlgebraic);
				return "O-O" + (CheckDetector.alreadyInCheck(b) ? "+" : "");
			} else if(fromFile == Board.F_E && toFile == Board.F_C) {
				Board b = board.clone().applyMove(simpleAlgebraic);
				return "O-O-O" + (CheckDetector.alreadyInCheck(b) ? "+" : "");
			} else {
				move.append("K");
			}
			break;
		}

		if (testClash) {
			String fromSquare = simpleAlgebraic.substring(0, 2);
			Vector m = new MoveGenerator(board).getAllRemainingMoves();
			Vector clashingPieces = new Vector();
			for(Enumeration e = m.elements(); e.hasMoreElements(); ) {
				String key = ((Move) e.nextElement()).getMove();
				if (key.startsWith("O-")) {
					continue;
				}
				if (!simpleAlgebraic.substring(2, 4)
						.equals(key.substring(2, 4))) {
					continue;
				}
				String pieceSquare = key.substring(0, 2);
				if (fromSquare.equals(pieceSquare)
						|| board.getPiece(pieceSquare) != movePiece) {
					continue;
				}
				clashingPieces.addElement(key);
			}

			if (clashingPieces.size() != 0) {
				boolean sameFile = false;
				boolean sameRank = false;
				for(Enumeration e = clashingPieces.elements(); e.hasMoreElements(); ) {
					String clash = (String) e.nextElement();
					if (clash.charAt(0) == simpleAlgebraic.charAt(0)) {
						sameFile = true;
					}
					if (clash.charAt(1) == simpleAlgebraic.charAt(1)) {
						sameRank = true;
					}
				}
				if (!sameFile) {
					move.append(Character
							.toLowerCase(simpleAlgebraic.charAt(0)));
				} else if (!sameRank) {
					move.append(simpleAlgebraic.charAt(1));
				} else {
					move.append(simpleAlgebraic.substring(0, 2).toLowerCase());
				}
			}
		}

		byte targetPiece = board.getPiece(toFile, toRank);
		if (!((targetPiece & Piece.MASK_TYPE) == 0)) {
			if ((movePiece & Piece.MASK_TYPE) == Piece.PAWN) {
				move.append(Character.toLowerCase(simpleAlgebraic.charAt(0)));
			}
			move.append("x");
		} else if ((movePiece & Piece.MASK_TYPE) == Piece.PAWN
				&& toFile != fromFile) {
			// En passant
			move.append(Character.toLowerCase(simpleAlgebraic.charAt(0)))
					.append("x");
		}

		move.append(Character.toLowerCase(simpleAlgebraic.charAt(2))).append(
				simpleAlgebraic.charAt(3));
		Board newBoard = board.clone().applyMove(simpleAlgebraic);

		if (simpleAlgebraic.indexOf('=') >= 0) {
			move
					.append(simpleAlgebraic.substring(simpleAlgebraic
							.indexOf('=')));
		}

		if (CheckDetector.alreadyInCheck(newBoard)) {
			move.append("+");
		}

		return move.toString();
	}
}
