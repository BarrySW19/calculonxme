package nl.zoidberg.calculon.engine;

import nl.zoidberg.calculon.model.Game;
import nl.zoidberg.calculon.model.Piece;


public class BitBoard {
	public static final long FINAL_RANKS = 255L<<56 | 255L;
	
	public final static int MAP_WHITE 	= Piece.WHITE;
	public final static int MAP_BLACK 	= Piece.BLACK;
	
	public final static int MAP_PAWNS 	= Piece.PAWN;
	public final static int MAP_KNIGHTS = Piece.KNIGHT;
	public final static int MAP_BISHOPS = Piece.BISHOP;
	public final static int MAP_ROOKS 	= Piece.ROOK;
	public final static int MAP_QUEENS 	= Piece.QUEEN;
	public final static int MAP_KINGS 	= Piece.KING;
	
	public final static int IS_EN_PASSANT = 0x10;
	public final static int EN_PASSANT_MASK = 0xF0;
	public final static byte CASTLE_MASK = 0x0F;
	
	private long bitmaps[] = new long[9];
	private byte player;
	private byte flags;
	
	private short moveCount, halfMoveCount;
	private BitBoardMove lastMove;
	
	public byte getCastlingOptions() {
        return (byte) (flags & CASTLE_MASK);
    }
	
	public static long getRankMap(int rank) {
		if(rank >=0 && rank < 8) {
			return 255L<<(rank*8);
		}
		return 0;
	}
	
	public boolean isEnPassant() {
		return (flags & IS_EN_PASSANT) != 0;
	}
	
	public byte getFlags() {
		return flags;
	}
	
	public short getMoveCount() {
		return moveCount;
	}

	public short getHalfMoveCount() {
		return halfMoveCount;
	}

	public int getEnPassantFile() {
		return (flags & EN_PASSANT_MASK)>>>5;
	}
	
	public int getEnPassantRank() {
		return getPlayer() == Piece.WHITE ? 5 : 2;
	}
	
	public static long getFileMap(int file) {
		return 0x0101010101010101L << file;
	}
	
	public BitBoard() {
	}
	
	public BitBoard(Board board) {
		this.setBoard(board);
	}
	
	public byte getPlayer() {
		return (byte) (player & Piece.BLACK);
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
		if(board.getPlayer() == Piece.BLACK) {
			player |= Piece.BLACK;
		} else {
			player &= ~Piece.BLACK;
		}
		flags = 0;
		if(board.isEnPassant()) {
			flags |= IS_EN_PASSANT;
			flags |= board.getEnPassantFile()<<5;
		}
		flags |= board.getCastlingOptions();
		moveCount = board.getMoveCount();
//		halfMoveCount = board.getHalfMoveCount();
	}
	
	public static long toMap(int file, int rank) {
		return 1L<<(rank<<3)<<file;
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
		buf.append("flags   " + Integer.toBinaryString(flags&0xFF) + ", player = " + player);
		
		return buf.toString();
	}
	
	public final long getBitmapColor() {
		return bitmaps[player];
	}
	
	public final long getBitmapColor(byte color) {
		return color == Piece.WHITE ? getBitmapWhite() : getBitmapBlack();
	}
	
	public final long getBitmapOppColor() {
		return bitmaps[player^Piece.BLACK];
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
		bb.player = player;
		bb.flags = flags;
		bb.halfMoveCount = halfMoveCount;
		bb.moveCount = moveCount;
		return bb;
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
	
	public byte getPiece(long pos) {
		long val = LongUtil.rotateLeft(bitmaps[MAP_PAWNS] & pos, MAP_PAWNS)
			| LongUtil.rotateLeft(bitmaps[MAP_KNIGHTS] & pos, MAP_KNIGHTS)
			| LongUtil.rotateLeft(bitmaps[MAP_BISHOPS] & pos, MAP_BISHOPS)
			| LongUtil.rotateLeft(bitmaps[MAP_ROOKS] & pos, MAP_ROOKS)
			| LongUtil.rotateLeft(bitmaps[MAP_QUEENS] & pos, MAP_QUEENS)
			| LongUtil.rotateLeft(bitmaps[MAP_KINGS] & pos, MAP_KINGS);
		if(val == 0) {
			return Piece.EMPTY;
		}
		return (byte) ((LongUtil.numberOfTrailingZeros(val) - LongUtil.numberOfTrailingZeros(pos)) & 0x07);
	}
	
	public byte getFullPiece(long pos) {
		byte piece = getPiece(pos);
		if((bitmaps[MAP_BLACK] & pos) != 0) {
			piece |= Piece.BLACK;
		}
		return piece;
	}
	
	private void castle(BitBoardMove move) {
		switch(move.castleDir) {
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
			bitmaps[MAP_BLACK] ^= -1152921504606846976L;
			bitmaps[MAP_KINGS] ^= 5764607523034234880L;
			bitmaps[MAP_ROOKS] ^= -6917529027641081856L;
			break;
		case Board.CASTLE_BQS:
			bitmaps[MAP_BLACK] ^= 2089670227099910144L;
			bitmaps[MAP_KINGS] ^= 1441151880758558720L;
			bitmaps[MAP_ROOKS] ^= 648518346341351424L;
			break;
		}
		flags &= ~move.castleOff;
	}
	
	public void makeMove(BitBoardMove move) {
		move.previousMove = this.lastMove;
		this.lastMove = move;
		
		move.flags = this.flags;
		move.halfMoveCount = halfMoveCount;

		halfMoveCount++;
		moveCount++;
		
		player ^= Piece.BLACK;
		flags &= ~EN_PASSANT_MASK;

		if(move.castle) {
			castle(move);
			return;
		}

		if(move.capture) {
			halfMoveCount = 0;
			bitmaps[move.captureType] ^= move.captureSquare;
			bitmaps[move.colorIndex^0x08] ^= move.captureSquare;
		} else if(move.pieceIndex == Piece.PAWN) {
			halfMoveCount = 0;
		}
		bitmaps[move.pieceIndex] ^= move.xorPattern;
		bitmaps[move.colorIndex] ^= move.xorPattern;
		if(move.promote) {
			bitmaps[move.pieceIndex] ^= move.toSquare;
			bitmaps[move.promoteTo] ^= move.toSquare;
		}
		if(move.enpassant) {
			flags &= ~EN_PASSANT_MASK;
			flags |= move.epFile;
		} else {
			flags &= ~EN_PASSANT_MASK;
		}
		flags &= ~move.castleOff;
	}
	
	public void unmakeMove() {
		moveCount--;
		player ^= Piece.BLACK;

		if(lastMove.castle) {
			castle(lastMove);
			this.flags = lastMove.flags;
			this.halfMoveCount = lastMove.halfMoveCount;
			this.lastMove = lastMove.previousMove;
			return;
		}
		
		if(lastMove.promote) {
			bitmaps[lastMove.pieceIndex] ^= lastMove.toSquare;
			bitmaps[lastMove.promoteTo] ^= lastMove.toSquare;
		}
		bitmaps[lastMove.pieceIndex] ^= lastMove.xorPattern;
		bitmaps[lastMove.colorIndex] ^= lastMove.xorPattern;
		if(lastMove.capture) {
			bitmaps[lastMove.captureType] ^= lastMove.captureSquare;
			bitmaps[lastMove.colorIndex^0x08] ^= lastMove.captureSquare;
		}
		this.flags = lastMove.flags;
		this.halfMoveCount = lastMove.halfMoveCount;
		this.lastMove = lastMove.previousMove;
	}

	public String getCacheId() {
		char[] c = new char[17];
		for(int i = 0; i < 64; i+=4) {
			c[i/4] |= (getPiece(1L<<i)<<12|getPiece(1L<<(i+1))<<8|getPiece(1L<<(i+2))<<4|getPiece(1L<<(i+3)));
		}
		c[16] |= (flags|(player<<8));
		return new String(c);
	}
	
	public long getChecksum() {
		long rv = 0;
		for(int i = 0; i < bitmaps.length; i++) {
			rv ^= LongUtil.rotateLeft(bitmaps[i], i*8);
		}
		return rv;
	}
	
	public String getResult() {

		if(isDrawnByRule()) {
			return Game.RES_DRAW;
		}

		// Do a fast dirty test to rule out stalemates. We look for any pieces which couldn't possibly
        // be pinned and see if they have obvious moves.
		boolean inCheck = CheckDetector.isPlayerToMoveInCheck(this); 
        if( ! inCheck) {
            long myKing = bitmaps[MAP_KINGS] & bitmaps[player];
            int kingIdx = LongUtil.numberOfTrailingZeros(myKing);
            long possiblePins = Bitmaps.star2Map[kingIdx]
                       & ~BitBoard.getFileMap(kingIdx&0x07) & ~Bitmaps.BORDER & bitmaps[player];
            possiblePins ^= bitmaps[getPlayer()];

            long freePawns = possiblePins & bitmaps[MAP_PAWNS];
            freePawns &= ~(getPlayer() == Piece.WHITE ? getAllPieces()>>>8 : getAllPieces()<<8);
            if(freePawns != 0) {
                // Any pawn move means we're not stalemated...
                return Game.RES_NO_RESULT;
            }
        }

        if ( ! new MoveGenerator(this).hasMoreElements()) {
			if (inCheck) {
		   		return (player == Piece.BLACK ? Game.RES_WHITE_WIN : Game.RES_BLACK_WIN);
			} else {
				return Game.RES_DRAW; // Stalemate
			}
		}
        
		return Game.RES_NO_RESULT;
	}

	public int getRepeatedCount() {
		if(halfMoveCount < 8) {
			// No point in checking until at least 8 half moves made...
			return 0;
		}
		
		int repeatCount = 0;
		BitBoard clone = this.clone();
		BitBoardMove[] moves = new BitBoardMove[halfMoveCount];
		
		while(halfMoveCount > 1) {
			moves[halfMoveCount-1] = lastMove;
			unmakeMove();
			moves[halfMoveCount-1] = lastMove;
			unmakeMove();
			if(this.equalPosition(clone)) {
				repeatCount++;
			}
		}
		for(int i = halfMoveCount; i < moves.length; i++) {
			makeMove(moves[i]);
		}
    	return repeatCount + 1;
    }
	
	public boolean equalPosition(BitBoard bb) {
    	 return bb.bitmaps[0] == bitmaps[0] && bb.bitmaps[1] == bitmaps[1]
              	       && bb.bitmaps[2] == bitmaps[2] && bb.bitmaps[3] == bitmaps[3]
              	       && bb.bitmaps[4] == bitmaps[4] && bb.bitmaps[5] == bitmaps[5]
              	       && bb.bitmaps[6] == bitmaps[6] && bb.bitmaps[8] == bitmaps[8]
              	       && bb.flags == flags && bb.player == player;
	}

	public boolean isDrawnByRule() {

		// Draw by 50 move rule
		if(halfMoveCount >= 100) {
			return true;
		}

		if(this.getRepeatedCount() >= 3) {
			return true;
		}
		
		if( ! hasMatingMaterial()) {
			return true;
		}
		
		return false;
    }
    
	public static BitBoardMove generateMove(
			long fromSquare, long toSquare, int colorIndex, int pieceIndex) {
		return new BitBoardMove(fromSquare, toSquare, colorIndex, pieceIndex);
	}

	public static BitBoardMove generateDoubleAdvanceMove(
			long fromSquare, long toSquare, int colorIndex) {
		BitBoardMove move = new BitBoardMove(fromSquare, toSquare, colorIndex, Piece.PAWN);
		move.enpassant = true;
		move.epFile = (short) (((LongUtil.numberOfTrailingZeros(fromSquare)&0x07)<<5) | IS_EN_PASSANT);
		return move;
	}
	
	public static BitBoardMove generateCapture(
			long fromSquare, long toSquare, byte colorIndex, byte pieceIndex, byte captureType) {
		return new BitBoardMove(fromSquare, toSquare, colorIndex, pieceIndex, captureType);
	}

	public static BitBoardMove generatePromote(
			long fromSquare, long toSquare, int colorIndex, byte promoteTo) {
		BitBoardMove move = new BitBoardMove(fromSquare, toSquare, colorIndex, Piece.PAWN);
		move.promote = true;
		move.promoteTo = promoteTo;
		return move;
	}
	
	public static BitBoardMove generateCaptureAndPromote(
			long fromSquare, long toSquare, int colorIndex, byte captureType, byte promoteTo) {
		BitBoardMove move = new BitBoardMove(fromSquare, toSquare, colorIndex, Piece.PAWN, captureType);
		move.promote = true;
		move.promoteTo = promoteTo;
		return move;
	}

	public static BitBoardMove generateEnPassantCapture(
			long fromSquare, long toSquare, int colorIndex) {
		BitBoardMove move = new BitBoardMove(fromSquare, toSquare, colorIndex, Piece.PAWN, Piece.PAWN);
		move.captureSquare = (colorIndex == Piece.WHITE ? toSquare>>>8 : toSquare<<8);
		return move;
	}
	
	public static BitBoardMove generateCastling(short castleDir) {
		return new BitBoardMove(castleDir);
	}
	
	public static long coordToPosition(String coord) {
		return 1L<<(EngineUtils.FILES.indexOf(coord.charAt(0)) | EngineUtils.RANKS.indexOf(coord.charAt(1))<<3); 
	}
	
	/**
	 * Translates an algebraic move into a BitBoardMove - slow but effective... definitely not for use in
	 * move generation routines, but fine for use with interfaces, etc.
	 * 
	 * @param move - A simple algebraic, e.g. E2E4, F7F8=Q, O-O-O.
	 * @return
	 */
	public BitBoardMove getMove(String move) {
		if(move.equals("O-O-O")) {
			return BitBoard.generateCastling(player == Piece.WHITE ? Board.CASTLE_WQS : Board.CASTLE_BQS);
		}
		if(move.equals("O-O")) {
			return BitBoard.generateCastling(player == Piece.WHITE ? Board.CASTLE_WKS : Board.CASTLE_BKS);
		}
		
		long from = coordToPosition(move.substring(0, 2));
		long to = coordToPosition(move.substring(2, 4));
		
		byte piece = getPiece(from);
		byte pieceCap = getPiece(to);
		if(piece == Piece.PAWN && Math.abs(LongUtil.numberOfTrailingZeros(from) - LongUtil.numberOfTrailingZeros(to)) == 16) {
			return BitBoard.generateDoubleAdvanceMove(from, to, player);
		}
		
		if(piece == Piece.PAWN && (to & FINAL_RANKS) != 0) {
			byte promoTo = 0;
			switch(move.charAt(move.length()-1)) {
				case 'Q': promoTo = Piece.QUEEN; break;
				case 'R': promoTo = Piece.ROOK; break;
				case 'N': promoTo = Piece.KNIGHT; break;
				case 'B': promoTo = Piece.BISHOP; break;
			}
			if(pieceCap != 0) {
				return BitBoard.generateCaptureAndPromote(from, to,player, pieceCap, promoTo);
			} else {
				return BitBoard.generatePromote(from, to, player, promoTo);
			}
		}
		
		if(piece == Piece.PAWN && Math.abs(LongUtil.numberOfTrailingZeros(from) - LongUtil.numberOfTrailingZeros(to)) != 8 && pieceCap == 0) {
			return BitBoard.generateEnPassantCapture(from, to, player);
		}
		
		if(pieceCap != 0) {
			return BitBoard.generateCapture(from, to, player, piece, getPiece(to));
		} else {
			return BitBoard.generateMove(from, to, player, piece);
		}
	}
	
	public static void compare(BitBoard bb1, BitBoard bb2) {
		for(int i = 0; i < bb1.bitmaps.length; i++) {
			if(bb1.bitmaps[i] != bb2.bitmaps[i]) {
				System.out.println("Index " + i);
				System.out.println(toPrettyString(bb1.bitmaps[i]));
				System.out.println(toPrettyString(bb2.bitmaps[i]));
			}
		}
		if(bb1.player != bb2.player) {
			System.out.println("Flags: " + bb1.player + " " + bb2.player);
		}
	}
	
	/**
	 * Represents all the information needed to make/unmake a move on a bitboard.
	 */
	public static class BitBoardMove {
		private int colorIndex;
		private int pieceIndex;
		private byte captureType;
		private byte promoteTo;
		private long captureSquare;
		private long toSquare;
		private long fromSquare;
		private short castleDir;
		private short epFile;
		
		private byte flags = 0;

		private long xorPattern;
		private boolean capture = false;
		private boolean promote = false;
		private boolean castle = false;
		private boolean enpassant = false;
		private byte castleOff;
		private short halfMoveCount;
		private BitBoardMove previousMove;
		
		private BitBoardMove(short castleDir) {
			this.castle = true;
			this.castleDir = castleDir;
			if(castleDir == Board.CASTLE_WKS || castleDir == Board.CASTLE_WQS) {
				castleOff = Board.CASTLE_WKS | Board.CASTLE_WQS;
			} else {
				castleOff = Board.CASTLE_BKS | Board.CASTLE_BQS;
			}
		}
		
		private BitBoardMove(long fromSquare, long toSquare, int colorIndex, int pieceIndex) {
			this.fromSquare = fromSquare;
			this.toSquare = toSquare;
			this.colorIndex = colorIndex;
			this.pieceIndex = pieceIndex;
			this.xorPattern = (fromSquare|toSquare);
			
			if((xorPattern & 0x90L) != 0) {
				castleOff |= Board.CASTLE_WKS;
			}
			if((xorPattern & 0x11L) != 0) {
				castleOff |= Board.CASTLE_WQS;
			}
			if((xorPattern & 0x90L<<56) != 0) {
				castleOff |= Board.CASTLE_BKS;
			}
			if((xorPattern & 0x11L<<56) != 0) {
				castleOff |= Board.CASTLE_BQS;
			}
		}

		private BitBoardMove(long fromSquare, long toSquare, int colorIndex, int pieceIndex, byte captureType) {
			this(fromSquare, toSquare, colorIndex, pieceIndex);
			this.captureType = captureType;
			this.capture = true;
			this.captureSquare = toSquare;
		}
		
		public String getAlgebraic() {
			if(castle) {
				switch(castleDir) {
				case Board.CASTLE_WKS:
				case Board.CASTLE_BKS:
					return "O-O";
				case Board.CASTLE_WQS:
				case Board.CASTLE_BQS:
					return "O-O-O";
				}
			}
			String move = EngineUtils.toCoord(fromSquare) + EngineUtils.toCoord(toSquare);
			if(promote) {
				switch(promoteTo) {
					case Piece.QUEEN: 	move = move + "=Q"; break;
					case Piece.ROOK: 	move = move + "=R"; break;
					case Piece.BISHOP: 	move = move + "=B"; break;
					case Piece.KNIGHT: 	move = move + "=N"; break;
				}
			}
			return move;
		}

		/**
		 * Constructs a <code>String</code> with all attributes
		 * in name = value format.
		 *
		 * @return a <code>String</code> representation 
		 * of this object.
		 */
		public String toString()
		{
		    final String TAB = ", ";
		    
		    String retValue = "";
		    
		    retValue = "BitBoardMove ( "
		        + super.toString() + TAB
		        + "colorIndex = " + Piece.COLORS[this.colorIndex] + TAB
		        + "pieceIndex = " + Piece.NAMES[this.pieceIndex] + TAB
		        + (capture ? "captureType = " + Piece.NAMES[this.captureType] + TAB : "")
		        + (promote ? "promoteTo = " + Piece.NAMES[this.promoteTo] + TAB : "")
		        + (capture ? "captureSquare = " + EngineUtils.toCoord(this.captureSquare) + TAB : "")
		        + "from/to = " + EngineUtils.toCoord(this.fromSquare) + EngineUtils.toCoord(this.toSquare) + TAB
		        + (castle ? "castleDir = " + this.castleDir + TAB : "")
//		        + "xorPattern = " + this.xorPattern + TAB
		        + "castleOff = " + this.castleOff + TAB
		        + (enpassant ? "epFile = " + this.epFile + TAB : "")
		        + " )";
		
		    return retValue;
		}
	}
}
