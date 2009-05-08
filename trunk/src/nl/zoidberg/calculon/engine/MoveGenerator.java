package nl.zoidberg.calculon.engine;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Game;
import nl.zoidberg.calculon.model.Piece;

public class MoveGenerator {
	
	private static MoveGenerator instance = new MoveGenerator();
	private static Hashtable generatorMap = new Hashtable();
	static {
		generatorMap.put(new Byte(Piece.PAWN),      new PawnMoveGenerator());
		generatorMap.put(new Byte(Piece.KNIGHT),    new KnightMoveGenerator());
		generatorMap.put(new Byte(Piece.BISHOP),    new BishopMoveGenerator());
		generatorMap.put(new Byte(Piece.ROOK),      new RookMoveGenerator());
		generatorMap.put(new Byte(Piece.QUEEN),     new QueenMoveGenerator());
		generatorMap.put(new Byte(Piece.KING),      new KingMoveGenerator());
	}
	
	public static MoveGenerator get() {
		return instance;
	}

	private MoveGenerator() { }
	
	public boolean isMovePossible(Board board) {
		for(int file = 0; file < 8; file++) {
			for(int rank = 0; rank < 8; rank++) {
				byte piece = board.getPiece(file, rank);
				if((piece & Piece.MASK_TYPE) != Piece.EMPTY && (piece & Piece.MASK_COLOR) == board.getPlayer()) {
					byte type = (byte) (board.getPiece(file, rank) & Piece.MASK_TYPE);
					Hashtable moves = ((PieceMoveGenerator)generatorMap.get(new Byte(type))).generateMoves(board, file, rank, true);
					if(moves.size() > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
    public Hashtable generateMoves(Board board) {
        Hashtable rv = new Hashtable();

		if(board == null || board.getResult() != Game.RES_NO_RESULT) {
			return rv;
		}

        for(int file = 0; file < 8; file++) {
            for(int rank = 0; rank < 8; rank++) {
                byte piece = board.getPiece(file, rank);
                if((piece & Piece.MASK_TYPE) != Piece.EMPTY && (piece & Piece.MASK_COLOR) == board.getPlayer()) {
                    byte type = (byte) (board.getPiece(file, rank) & Piece.MASK_TYPE);
                    Hashtable genMoves = ((PieceMoveGenerator)generatorMap.get(new Byte(type))).generateMoves(board, file, rank);
                    for(Enumeration e = genMoves.keys(); e.hasMoreElements(); ) {
                        String key = (String) e.nextElement();
                        rv.put(key, genMoves.get(key));
                    }
                }
            }
        }

        return rv;
    }

	public Hashtable getPossibleMoves(Board board) {
		Hashtable moves = new Hashtable();
                Hashtable genMoves = MoveGenerator.get().generateMoves(board);
		for(Enumeration e = genMoves.keys(); e.hasMoreElements(); ) {
                        String move = (String) e.nextElement();
			if("O-O".equals(move)) {
				move = board.getPlayer() == Piece.WHITE ? "E1G1" : "E8G8";
			}
			if("O-O-O".equals(move)) {
				move = board.getPlayer() == Piece.WHITE ? "E1C1" : "E8C8";
			}
			String from = move.substring(0, 2);
			if(moves.get(from) == null) {
				moves.put(from, new Vector());
			}
			Vector toList = (Vector) moves.get(from);
			toList.addElement(move.substring(2));
		}
		return moves;
	}
}
