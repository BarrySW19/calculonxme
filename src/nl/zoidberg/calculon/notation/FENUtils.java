package nl.zoidberg.calculon.notation;


import nl.zoidberg.calculon.engine.EngineUtils;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class FENUtils {
	private static final String FILES = "abcdefgh";
	private static final String RANKS = "12345678";

	public static String generate(Board board) {
		
		StringBuffer fen = new StringBuffer();
		fen.append(generatePosition(board));
		
		fen.append(" ").append(board.getPlayer() == Piece.WHITE ? "w" : "b");
		fen.append(" ").append(generateCastling(board));
		fen.append(" ").append(generateEnPassant(board));
		fen.append(" ").append(board.getHalfMoveCount());
		fen.append(" ").append(board.getMoveNumber());
		
		return fen.toString();
	}
	
	public static String generateEnPassant(Board board) {

		if( ! board.isEnPassant()) { 
			return "-";
		}
		
		StringBuffer fen = new StringBuffer();
		fen.append(Character.toLowerCase(EngineUtils.FILES.charAt(board.getEnPassantSquare()>>3)));
		fen.append(EngineUtils.RANKS.charAt(board.getEnPassantSquare()&0x07));
		
		return fen.toString();
	}
	
	public static String generateCastling(Board board) {
		StringBuffer fen = new StringBuffer();
		
		if((board.getCastlingOptions() & Board.CASTLE_WKS) != 0) {
			fen.append("K");
		}
		if((board.getCastlingOptions() & Board.CASTLE_WQS) != 0) {
			fen.append("Q");
		}
		if((board.getCastlingOptions() & Board.CASTLE_BKS) != 0) {
			fen.append("k");
		}
		if((board.getCastlingOptions() & Board.CASTLE_BQS) != 0) {
			fen.append("q");
		}
		if(fen.length() == 0) {
			fen.append("-");
		}
		
		return fen.toString();
	}
	
	public static String generatePosition(Board board) {
		StringBuffer fen = new StringBuffer();
		
		for(int rank = 7; rank >= 0; rank--) {
			int emptyCount = 0;
			for(int file = 0; file < 8; file++) {
				String symbol = getSymbol(board.getPiece(file, rank));
				if(symbol == null) {
					emptyCount++;
				} else {
					if(emptyCount > 0) {
						fen.append(emptyCount);
						emptyCount = 0;
					}
					fen.append(symbol);
				}
			}
			if(emptyCount > 0) {
				fen.append(emptyCount);
			}
			if(rank > 0) {
				fen.append("/");
			}
		}
		
		return fen.toString();
	}
	
	static String getSymbol(byte piece) {
		
		byte pieceType = EngineUtils.getType(piece);
		
		if(pieceType == Piece.EMPTY) {
			return null;
		}
		
		String symbol = null;
		if(pieceType == Piece.PAWN) {
			symbol = "p";
		} else if(pieceType == Piece.KNIGHT) {
			symbol = "n";
		} else if(pieceType == Piece.BISHOP) {
			symbol = "b";
		} else if(pieceType == Piece.ROOK) {
			symbol = "r";
		} else if(pieceType == Piece.QUEEN) {
			symbol = "q";
		} else if(pieceType == Piece.KING) {
			symbol = "k";
		}
		
		if(EngineUtils.getColor(piece) == Piece.BLACK) {
			return symbol;
		} else {
			return symbol.toUpperCase();
		}
	}

	public static Board loadPosition(String string, Board board) {
		board.clear();
		String[] fields = StringUtils.split(string);
		String[] ranks = StringUtils.split(fields[0], '/');
		
		for(int rank = 7; rank >= 0; rank--) {
			int file = 0;
			for(int j = 0; j < ranks[7-rank].length(); j++) {
				char c = ranks[7-rank].charAt(j);
				switch(c) {
				case 'P':
					board.setPiece(file, rank, (byte) (Piece.PAWN | Piece.WHITE));
					break;
				case 'p':
					board.setPiece(file, rank, (byte) (Piece.PAWN | Piece.BLACK));
					break;
				case 'R':
					board.setPiece(file, rank, (byte) (Piece.ROOK | Piece.WHITE));
					break;
				case 'r':
					board.setPiece(file, rank, (byte) (Piece.ROOK | Piece.BLACK));
					break;
				case 'N':
					board.setPiece(file, rank, (byte) (Piece.KNIGHT | Piece.WHITE));
					break;
				case 'n':
					board.setPiece(file, rank, (byte) (Piece.KNIGHT | Piece.BLACK));
					break;
				case 'B':
					board.setPiece(file, rank, (byte) (Piece.BISHOP | Piece.WHITE));
					break;
				case 'b':
					board.setPiece(file, rank, (byte) (Piece.BISHOP | Piece.BLACK));
					break;
				case 'Q':
					board.setPiece(file, rank, (byte) (Piece.QUEEN | Piece.WHITE));
					break;
				case 'q':
					board.setPiece(file, rank, (byte) (Piece.QUEEN | Piece.BLACK));
					break;
				case 'K':
					board.setPiece(file, rank, (byte) (Piece.KING | Piece.WHITE));
					break;
				case 'k':
					board.setPiece(file, rank, (byte) (Piece.KING | Piece.BLACK));
					break;
				}
				if(c >= '1' && c <= '8') {
					file += c - '1';
				}
				file++;
			}
		}
		
		board.setPlayer("b".equals(fields[1]) ? Piece.BLACK : Piece.WHITE);
		board.setCastlingOptions((short) 
				((fields[2].indexOf("K") >= 0 ? Board.CASTLE_WKS : 0) |
				(fields[2].indexOf("Q") >= 0 ? Board.CASTLE_WQS : 0) |
				(fields[2].indexOf("k") >= 0 ? Board.CASTLE_BKS : 0) |
				(fields[2].indexOf("q") >= 0 ? Board.CASTLE_BQS : 0)));
		
		if(fields[3].length() == 2) {
			board.setEnPassantSquare(FILES.indexOf(fields[3].charAt(0)), RANKS.indexOf(fields[3].charAt(1)));
		} else {
			board.setEnPassantSquare(-1, -1);
		}
		board.setHalfMoveCount(Short.parseShort(fields[4]));
		board.setMoveNumber(Short.parseShort(fields[5]));
		
		return board;
	}
	
	public static Board getBoard(String fen) {
		Board board = new Board();
		loadPosition(fen, board);
		return board;
	}
}
