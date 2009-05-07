package nl.zoidberg.calculon.analyzer;

import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class PawnStructureScorer implements PositionScorer {

	public static int S_ISLAND = 100;
	public static int S_ISOLATED = 100;
	public static int S_DOUBLED = 100;

	private static int[] S_ADVANCE = { 20, 100, 200, 400, };

	public int scorePosition(Board board, Hashtable pieceMap) {

		byte[] whitePawns = new byte[8], blackPawns = new byte[8];
		populate(board, whitePawns, Piece.WHITE);
		populate(board, blackPawns, Piece.BLACK);

		int score = 0;
		score += countIslands(blackPawns) * S_ISLAND;
		score -= countIslands(whitePawns) * S_ISLAND;

		score -= getIsolatedCount(whitePawns) * S_ISOLATED;
		score += getIsolatedCount(blackPawns) * S_ISOLATED;

		score -= getDoubledCount(whitePawns) * S_DOUBLED;
		score += getDoubledCount(blackPawns) * S_DOUBLED;

		score += getAdvanceScore(board, Piece.WHITE);
		score -= getAdvanceScore(board, Piece.BLACK);

		return score;
	}

	private int getAdvanceScore(Board board, byte color) {
		int score = 0;
		byte findPiece = (byte) (color | Piece.PAWN);
		for (int rank = 3; rank < 7; rank++) {
			int sRank = color == Piece.WHITE ? rank : 7 - rank;
			int bonus = S_ADVANCE[rank - 3];
			for (int file = 0; file < 8; file++) {
				if (board.getPiece(file, sRank) == findPiece) {
					score += bonus;
				}
			}
		}
		return score;
	}

	private void populate(Board board, byte[] counts, byte color) {
		byte findPiece = (byte) (color | Piece.PAWN);
		for (int file = 0; file < 8; file++) {
			for (int rank = 0; rank < 8; rank++) {
				if (board.getPiece(file, rank) == findPiece) {
					counts[file]++;
				}
			}
		}
	}

	private int countIslands(byte[] pawns) {
		boolean inSea = true;
		int count = 0;
		for (int i = 0; i < 8; i++) {
			if (pawns[i] != 0 && inSea) {
				count++;
				inSea = false;
			} else if (pawns[i] == 0 && !inSea) {
				inSea = true;
			}
		}
		return count;
	}

	private int getDoubledCount(byte[] pawns) {
		int count = 0;
		for (int i = 0; i < 8; i++) {
			if (pawns[i] > 1) {
				count += pawns[i] - 1;
			}
		}
		return count;
	}

	private int getIsolatedCount(byte[] pawns) {
		int count = 0;
		for (int i = 0; i < 8; i++) {
			if (pawns[i] > 0) {
				if ((i == 0 || pawns[i - 1] == 0)
						&& (i == 7 || pawns[i + 1] == 0)) {
					count += pawns[i];
				}
			}
		}
		return count;
	}
}
