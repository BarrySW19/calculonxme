package nl.zoidberg.calculon.notation;

import java.util.ArrayList;
import java.util.List;

import nl.zoidberg.calculon.engine.EngineUtils;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class TextUtils {
	
	public static List<String> getMiniTextBoard(Board board) {
		List<String> rv = new ArrayList<String>();
		rv.add("+--------+");
		for(int rank = 7; rank >= 0; rank--) {
			StringBuffer cRank = new StringBuffer("|"); 
			for(int file = 0; file < 8; file++) {
				byte piece = board.getPiece(file, rank);
				if(piece == Piece.EMPTY) {
					cRank.append(Board.getSquareColor(file, rank) == Piece.WHITE ? " " : ".");
				} else {
					cRank.append(FENUtils.getSymbol(piece));
				}
			}
			rv.add(cRank.append("|").toString());
		}
		rv.add("+--------+");
		return rv;
	}
	
	public static String textBoardToString(List<String> l) {
		StringBuffer buf = new StringBuffer();
		for(String s: l) {
			buf.append(s).append("\n");
		}
		return buf.toString();
	}
	
	public static String getHtmlBoard(Board board) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>\n");
		buf.append("<head><link rel='stylesheet' type='text/css' href='/chess.css' /></head>\n");
		buf.append("<body>\n");
		buf.append("<table cellpadding='0' cellspacing='0' border='0'>\n");
		
		for(int rank = 7; rank >= 0; rank--) {
			buf.append("<tr>\n");
			for(int file = 0; file < 8; file++) {
				if(Board.getSquareColor(file, rank) == Piece.WHITE) {
					buf.append("<td class='square_l'>");
				} else {
					buf.append("<td class='square_d'>");
				}
				byte piece = board.getPiece(file, rank);
				if(piece != Piece.EMPTY) {
					buf.append("<img src='img/25px-Chess_tile_").append(getImageName(piece)).append(".png' />");
				}
				buf.append("</td>\n");
			}
			buf.append("</tr>\n");
		}
		
		buf.append("</table>\n");
		buf.append("</body>\n");
		buf.append("</html>\n");

		return buf.toString();
	}

	private static String getImageName(byte piece) {
		byte pieceType = EngineUtils.getType(piece);
		
		if(pieceType == Piece.EMPTY) {
			return "";
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
			return symbol + "d";
		} else {
			return symbol + "l";
		}
	}
}
