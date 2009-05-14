package nl.zoidberg.calculon.engine;

import nl.zoidberg.calculon.model.Piece;


public class BitBoard {
	
	public final static int MAP_WHITE 	= 0;
	public final static int MAP_BLACK 	= 8;
	
	public final static int MAP_PAWNS 	= Piece.PAWN;
	public final static int MAP_KNIGHTS = Piece.KNIGHT;
	public final static int MAP_BISHOPS = Piece.BISHOP;
	public final static int MAP_ROOKS 	= Piece.ROOK;
	public final static int MAP_QUEENS 	= Piece.QUEEN;
	public final static int MAP_KINGS 	= Piece.KING;
	
	long bitmaps[] = new long[9];
	
	public static void main(String[] args) {
		BitBoard b = new BitBoard(new Board().initialise());
		b.clearSquare(1, 7, (byte) (Piece.BLACK|Piece.KNIGHT));
		b.populateSquare(2, 5, (byte) (Piece.BLACK|Piece.KNIGHT));
		System.out.println(b);
	}
	
	public static long getRankMap(int rank) {
		return 255L<<(rank*8);
	}
	
	public static long getFileMap(int file) {
		return 0x0101010101010101L << file;
	}
	
	public BitBoard() {
	}
	
	public BitBoard(Board board) {
		this.setBoard(board);
	}
	
	public void castle(short dir) {
		switch(dir) {
		case Board.CASTLE_WKS:
			bitmaps[MAP_WHITE] ^= 0xF0L;
			bitmaps[MAP_KINGS] ^= 0x50L;
			bitmaps[MAP_ROOKS] ^= 0xA0L;
			break;
		case Board.CASTLE_WQS:
			bitmaps[MAP_WHITE] ^= 0x1DL;
			bitmaps[MAP_KINGS] ^= 0x14L;
			bitmaps[MAP_ROOKS] ^= 0x09L;
			break;
		case Board.CASTLE_BKS:
			bitmaps[MAP_BLACK] ^= 0xF0L<<56;
			bitmaps[MAP_KINGS] ^= 0x50L<<56;
			bitmaps[MAP_ROOKS] ^= 0xA0L<<56;
			break;
		case Board.CASTLE_BQS:
			bitmaps[MAP_BLACK] ^= 0x1DL<<56;
			bitmaps[MAP_KINGS] ^= 0x14L<<56;
			bitmaps[MAP_ROOKS] ^= 0x09L<<56;
			break;
		}
	}
	
	public void setBoard(Board board) {
		bitmaps[MAP_WHITE] 		= 0;
		bitmaps[MAP_BLACK] 		= 0;
		bitmaps[MAP_PAWNS] 		= 0;
		bitmaps[MAP_ROOKS] 		= 0;
		bitmaps[MAP_KNIGHTS] 	= 0;
		bitmaps[MAP_BISHOPS] 	= 0;
		bitmaps[MAP_QUEENS] 	= 0;
		bitmaps[MAP_KINGS] 		= 0;
	
		long currentBit = 1;
		for(long rank = 0; rank < 8; rank++) {
			for(long file = 0; file < 8; file++ ) {
				byte piece = board.getPiece((int)file, (int)rank);
				if(piece != 0) {
					if((piece&Piece.MASK_COLOR) == Piece.WHITE) {
						bitmaps[MAP_WHITE] |= currentBit;
					} else {
						bitmaps[MAP_BLACK] |= currentBit;
					}
					bitmaps[piece&Piece.MASK_TYPE] |= currentBit;
				}
				currentBit <<= 1;
			}
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("white   ").append(toPrettyString(bitmaps[MAP_WHITE])).append("\n");
		buf.append("black   ").append(toPrettyString(bitmaps[MAP_BLACK])).append("\n");

		buf.append("pawns   ").append(toPrettyString(bitmaps[MAP_PAWNS])).append("\n");
		buf.append("rooks   ").append(toPrettyString(bitmaps[MAP_ROOKS])).append("\n");
		buf.append("knights ").append(toPrettyString(bitmaps[MAP_KNIGHTS])).append("\n");
		buf.append("bishops ").append(toPrettyString(bitmaps[MAP_BISHOPS])).append("\n");
		buf.append("queens  ").append(toPrettyString(bitmaps[MAP_QUEENS])).append("\n");
		buf.append("kings   ").append(toPrettyString(bitmaps[MAP_KINGS])).append("\n");
		
		return buf.toString();
	}
	
	public final long getBitmapColor(byte color) {
		return color == Piece.WHITE ? getBitmapWhite() : getBitmapBlack();
	}
	
	public final long getBitmapOppColor(byte color) {
		return color == Piece.WHITE ? getBitmapBlack() : getBitmapWhite();
	}
	
	public final long getBitmapWhite() {
		return bitmaps[MAP_WHITE];
	}

	public final long getBitmapBlack() {
		return bitmaps[MAP_BLACK];
	}

	public final long getBitmapPawns() {
		return bitmaps[MAP_PAWNS];
	}

	public final long getBitmapRooks() {
		return bitmaps[MAP_ROOKS];
	}

	public final long getBitmapKnights() {
		return bitmaps[MAP_KNIGHTS];
	}

	public final long getBitmapBishops() {
		return bitmaps[MAP_BISHOPS];
	}

	public final long getBitmapQueens() {
		return bitmaps[MAP_QUEENS];
	}

	public final long getBitmapKings() {
		return bitmaps[MAP_KINGS];
	}

	public static String toPrettyString(long val) {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < 64; i++) {
			buf.insert(0, (val & 1L<<i) == 0 ? "0" : "1");
			if(i%8 == 7) {
				buf.insert(0, " ");
			}
		}
		buf.delete(0, 1);
		return buf.toString();
	}
	
	public static String toBlockString(long val) {
		StringBuffer buf = new StringBuffer();
		StringBuffer buf2 = new StringBuffer();
		for(int i = 0; i < 64; i++) {
			buf.append((val & 1L<<i) == 0 ? "0" : "1");
			if(i%8 == 7) {
				buf2.insert(0, buf.toString() + "\n");
				buf.setLength(0);
			}
		}
		return buf2.toString();
	}
	
	/** 
	 * Returns the file/rank of the lowest 1 in the bitmap
	 * @param bitmap
	 * @return
	 */
	public final static int[] toCoords(long bitmap) {
		int zeros = LongUtil.numberOfTrailingZeros(bitmap);
		int[] coords = new int[] { zeros&0x07, zeros>>>3};
		return coords;
	}

	public long getAllPieces() {
		return bitmaps[MAP_BLACK] | bitmaps[MAP_WHITE];
	}

	public long getAllEmpty() {
		return ~(bitmaps[MAP_BLACK] | bitmaps[MAP_WHITE]);
	}

	public BitBoard clone() {
		BitBoard bb = new BitBoard();
		System.arraycopy(bitmaps, 0, bb.bitmaps, 0, bitmaps.length);
		
//		for(int i = 0; i < bitmaps.length; i++) {
//			bb.bitmaps[i] = bitmaps[i];
//		}
		
		return bb;
	}

	void clearSquare(int file, int rank, byte piece) {
		if(piece == 0) {
			return;
		}
		long bitMask = ~(1L<<(rank<<3|file));
		bitmaps[piece&Piece.MASK_TYPE] &= bitMask;
		bitmaps[MAP_BLACK] &= bitMask;
		bitmaps[MAP_WHITE] &= bitMask;
	}

	void populateSquare(int file, int rank, byte piece) {
		long bitMask = (1L<<(rank<<3|file));
		if((piece & Piece.MASK_COLOR) == Piece.BLACK) {
			bitmaps[MAP_BLACK] |= bitMask;
		} else {
			bitmaps[MAP_WHITE] |= bitMask;
		}
		bitmaps[piece&Piece.MASK_TYPE] |= bitMask;
	}

	public final boolean hasMatingMaterial() {
		if((bitmaps[MAP_QUEENS] | bitmaps[MAP_ROOKS] | bitmaps[MAP_PAWNS]) != 0) {
			return true;
		}
		long minorMap = bitmaps[MAP_BISHOPS] | bitmaps[MAP_KNIGHTS];
		if(LongUtil.bitCount(minorMap & bitmaps[MAP_BLACK]) > 1 || LongUtil.bitCount(minorMap & bitmaps[MAP_WHITE]) > 1) {
			return true;
		}
		return false;
	}
}
