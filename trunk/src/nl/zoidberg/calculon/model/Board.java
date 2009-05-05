package nl.zoidberg.calculon.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nl.zoidberg.calculon.engine.CheckDetector;
import nl.zoidberg.calculon.engine.EngineUtils;
import nl.zoidberg.calculon.engine.MoveGenerator;
import nl.zoidberg.calculon.engine.SearchNode;
import nl.zoidberg.calculon.notation.FENUtils;
import nl.zoidberg.calculon.notation.PGNUtils;

public class Board {
	
    private static final String BASE64 = "`!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_";
	
    public static final int R_1 = 0;
    public static final int R_2 = 1;
    public static final int R_3 = 2;
    public static final int R_4 = 3;
    public static final int R_5 = 4;
    public static final int R_6 = 5;
    public static final int R_7 = 6;
    public static final int R_8 = 7;

    public static final int F_A = 0;
    public static final int F_B = 1;
    public static final int F_C = 2;
    public static final int F_D = 3;
    public static final int F_E = 4;
    public static final int F_F = 5;
    public static final int F_G = 6;
    public static final int F_H = 7;

    public static final short CASTLE_WQS = 0x01;
    public static final short CASTLE_WKS = 0x02;
    public static final short CASTLE_BQS = 0x04;
    public static final short CASTLE_BKS = 0x08;
    public static final short CASTLE_MASK = 0x0F;

    private static final short EN_PASSANT = 0x10;
    private static final short EN_PASSANT_MASK = 0x7E0;
    private static final short PLAYER_MASK = 0x800;
    public static final short PLAYER_WHITE = 0x0;
    public static final short PLAYER_BLACK = 0x800;

    private byte[] squares = new byte[64];
    private short flags;
    private short moveCount, halfMoveCount;
    private Map<String, Short> history = new HashMap<String, Short>();

    public Board() { }
    
    public Board(byte[] squares, short flags, Map<String, Short> history) {
    	this.squares = squares;
    	this.flags = flags;
    	this.history.clear();
    	this.history.putAll(history);
    }
    
    public void clear() {
    	history.clear();
        Arrays.fill(squares, (byte) 0);
        flags = 0;
    }
    
    public Map<String, Short> getHistory() {
    	return history;
    }
    
    public byte[] getSquares() {
    	return squares;
    }
    
    public short getFlags() {
    	return flags;
    }
    
    public String getResult() {
    	Map<String, SearchNode> map = MoveGenerator.get().generateMoves(this);
    	if(map.size() > 0) {
    		return "*";
    	}

    	if(getRepeatedCount() >= 3) {
    		return Game.RES_DRAW;
    	}
    	if(CheckDetector.alreadyInCheck(this)) {
    		return (flags&PLAYER_MASK) == PLAYER_BLACK ? Game.RES_WHITE_WIN : Game.RES_BLACK_WIN;
    	}
    	return Game.RES_DRAW;
    }

    /**
     * Flip the position.
     * TODO: Reversal needs more thought - not just a simple rotation.
     * 
     * @return
     */
    public Board reverse() {
    	history.clear();
    	byte[] newSquares = new byte[64];
    	for(int r = 0; r < 8; r++) {
    		for(int f = 0; f < 8; f++) {
//    			System.err.println(squares[f<<3|r] + " " + ((byte) (squares[f<<3|r]^Piece.MASK_COLOR)));
    			byte piece = squares[f<<3|r];
    			if((piece&Piece.MASK_TYPE) != 0) {
    				piece ^= Piece.MASK_COLOR;
    			}
    			newSquares[(7-f)<<3|(7-r)] = piece;
    		}
    	}
    	squares = newSquares;
    	if(isEnPassant()) {
    		setEnPassantSquare(7 - getEnPassantFile(), 7 - getEnPassantRank());
    	}
    	short newCastleFlags = (short) ((flags&0x03)<<2|(flags&0x0c)>>2);
       	flags &= ~CASTLE_MASK;
       	flags |= newCastleFlags;
    	setPlayer(getPlayer() == Piece.WHITE ? Piece.BLACK : Piece.WHITE);
    	return this;
    }
    
    /**
     * Set a board to the starting position.
     * 
     * @return
     */
    public Board initialise() {
        clear();
        moveCount = 0;
        halfMoveCount = 0;

        squares[0*8+0] = Piece.ROOK | Piece.WHITE;
        squares[1*8+0] = Piece.KNIGHT | Piece.WHITE;
        squares[2*8+0] = Piece.BISHOP | Piece.WHITE;
        squares[3*8+0] = Piece.QUEEN | Piece.WHITE;
        squares[4*8+0] = Piece.KING | Piece.WHITE;
        squares[5*8+0] = Piece.BISHOP | Piece.WHITE;
        squares[6*8+0] = Piece.KNIGHT | Piece.WHITE;
        squares[7*8+0] = Piece.ROOK | Piece.WHITE;

        squares[0*8+7] = Piece.ROOK | Piece.BLACK;
        squares[1*8+7] = Piece.KNIGHT | Piece.BLACK;
        squares[2*8+7] = Piece.BISHOP | Piece.BLACK;
        squares[3*8+7] = Piece.QUEEN | Piece.BLACK;
        squares[4*8+7] = Piece.KING | Piece.BLACK;
        squares[5*8+7] = Piece.BISHOP | Piece.BLACK;
        squares[6*8+7] = Piece.KNIGHT | Piece.BLACK;
        squares[7*8+7] = Piece.ROOK | Piece.BLACK;

        for(int file = 0; file < 8; file++) {
            squares[file*8+1] = Piece.PAWN | Piece.WHITE;
            squares[file*8+6] = Piece.PAWN | Piece.BLACK;
        }

        flags = CASTLE_WQS | CASTLE_WKS | CASTLE_BQS | CASTLE_BKS;
        history.clear();
        return this;
    }
    
    public short getMoveCount() {
    	return moveCount;
    }
    
    public void setMoveNumber(short moveNumber) {
    	history.clear();
		this.moveCount = (short) ((moveNumber - 1) * 2);
		if(getPlayer() == Piece.BLACK) {
			this.moveCount++;
		}
	}

	public short getMoveNumber() {
    	return (short) (moveCount / 2 + 1);
    }

    public void setHalfMoveCount(short halfMoveCount) {
    	history.clear();
		this.halfMoveCount = halfMoveCount;
	}

	public short getHalfMoveCount() {
		return halfMoveCount;
	}

	public byte getPlayer() {
        return (flags & PLAYER_MASK) == PLAYER_WHITE ? Piece.WHITE : Piece.BLACK;
    }

    public void setPlayer(byte color) {
    	history.clear();
        flags &= ~PLAYER_MASK;
        flags |= (color == Piece.WHITE ? PLAYER_WHITE : PLAYER_BLACK);
        if(color == Piece.BLACK && moveCount%2 == 0) {
        	moveCount++;
        }
        if(color == Piece.WHITE && moveCount%2 == 1) {
        	moveCount--;
        }
    }

    public short getCastlingOptions() {
        return (short) (flags & CASTLE_MASK);
    }

    public void setCastlingOptions(short s) {
    	history.clear();
        flags &= ~CASTLE_MASK;
        flags |= (s & CASTLE_MASK);
    }

    public Board clone() {
        Board board = new Board();
        System.arraycopy(squares, 0, board.squares, 0, squares.length);
        board.flags = flags;
        board.history.putAll(history);
        return board;
    }
    
    /**
     * Performed the specified castling - rules checking is done elsewhere.
     * 
     * @param type
     * @return
     */
    private Board castle(short type) {
        switch(type) {
        case CASTLE_WKS:
            squares[F_G<<3|R_1] = squares[F_E<<3|R_1];
            squares[F_F<<3|R_1] = squares[F_H<<3|R_1];
            squares[F_E<<3|R_1] = Piece.EMPTY;
            squares[F_H<<3|R_1] = Piece.EMPTY;
            flags &= ~(CASTLE_WKS|CASTLE_WQS);
            break;
        case CASTLE_WQS:
            squares[F_C<<3|R_1] = squares[F_E<<3|R_1];
            squares[F_D<<3|R_1] = squares[F_A<<3|R_1];
            squares[F_E<<3|R_1] = Piece.EMPTY;
            squares[F_A<<3|R_1] = Piece.EMPTY;
            flags &= ~(CASTLE_WKS|CASTLE_WQS);
            break;
        case CASTLE_BKS:
            squares[F_G<<3|R_8] = squares[F_E<<3|R_8];
            squares[F_F<<3|R_8] = squares[F_H<<3|R_8];
            squares[F_E<<3|R_8] = Piece.EMPTY;
            squares[F_H<<3|R_8] = Piece.EMPTY;
            flags &= ~(CASTLE_BKS|CASTLE_BQS);
            break;
        case CASTLE_BQS:
            squares[F_C<<3|R_8] = squares[F_E<<3|R_8];
            squares[F_D<<3|R_8] = squares[F_A<<3|R_8];
            squares[F_E<<3|R_8] = Piece.EMPTY;
            squares[F_A<<3|R_8] = Piece.EMPTY;
            flags &= ~(CASTLE_BKS|CASTLE_BQS);
            break;
        }

        flags &= ~(EN_PASSANT|EN_PASSANT_MASK);
        flags ^= PLAYER_MASK;

        return this;
    }
    
    public Board applyPgnMove(String pgn) {
    	return applyMove(PGNUtils.toPgnMoveMap(this).get(pgn));
    }
    
    /**
     * Apply a move - legality checking should already have been done.
     * 
     * @param algebraic
     * @return
     */
    public Board applyMove(String algebraic) {
    	algebraic = algebraic.toUpperCase();
    	moveCount++;
    	halfMoveCount++;
    	
    	String myCacheId = getCacheId();
    	Short repeats = history.get(myCacheId);
    	if(repeats == null) {
    		repeats = new Short((short) 1);
    	} else {
    		repeats = new Short((short) (repeats.shortValue() + 1));
    	}
		history.put(myCacheId, repeats);
    	
        if(algebraic.equals("O-O")) {
            if(getPlayer() == Piece.WHITE) {
                castle(CASTLE_WKS);
                return this;
            }
            if(getPlayer() == Piece.BLACK) {
                castle(CASTLE_BKS);
                return this;
            }
        }
        if(algebraic.equals("O-O-O")) {
            if(getPlayer() == Piece.WHITE) {
                castle(CASTLE_WQS);
                return this;
            }
            if(getPlayer() == Piece.BLACK) {
                castle(CASTLE_BQS);
                return this;
            }
        }

        int fromFile = EngineUtils.FILES.indexOf(algebraic.charAt(0));
        int fromRank = EngineUtils.RANKS.indexOf(algebraic.charAt(1));
        int toFile = EngineUtils.FILES.indexOf(algebraic.charAt(2));
        int toRank = EngineUtils.RANKS.indexOf(algebraic.charAt(3));
        
        // Catch programs which castle by moving the king...
        byte movePiece = squares[fromFile*8+fromRank];
        if((movePiece & Piece.MASK_TYPE) == Piece.KING) {
        	if(Math.abs(toFile-fromFile) == 2) {
        		castle(getPlayer() == Piece.WHITE ? CASTLE_WKS : CASTLE_BKS);
        		return this;
        	}
        	if(Math.abs(toFile-fromFile) == 3) {
        		castle(getPlayer() == Piece.WHITE ? CASTLE_WQS : CASTLE_BQS);
        		return this;
        	}
        }
        
        if((movePiece & Piece.MASK_TYPE) == Piece.PAWN) {
        	halfMoveCount = 0;
        	history.clear();
        }
        
        if((movePiece & Piece.MASK_TYPE) == Piece.PAWN && isEnPassant()
                && toFile == getEnPassantFile() && toRank == getEnPassantRank()) {
            squares[toFile<<3|fromRank] = Piece.EMPTY;
        }

        flags &= ~(EN_PASSANT|EN_PASSANT_MASK);
        if((movePiece & Piece.MASK_TYPE) == Piece.PAWN && Math.abs(fromRank-toRank) == 2) {
            flags |= (EN_PASSANT | fromFile<<8 | ((fromRank+toRank)/2)<<5);
        }

        squares[fromFile<<3|fromRank] = Piece.EMPTY;
        if( ! (squares[toFile<<3|toRank] == Piece.EMPTY)) {
        	halfMoveCount = 0;
        	history.clear();
        }
        
        if((movePiece & Piece.MASK_TYPE) != Piece.PAWN || (toRank != 0 && toRank != 7)) {
            squares[toFile<<3|toRank] = movePiece;
        } else {
            switch(Character.toUpperCase(algebraic.charAt(algebraic.length()-1))) {
            case 'N':
                squares[toFile<<3|toRank] = (byte) (Piece.KNIGHT | (movePiece & Piece.MASK_COLOR));
                break;
            case 'B':
                squares[toFile<<3|toRank] = (byte) (Piece.BISHOP | (movePiece & Piece.MASK_COLOR));
                break;
            case 'R':
                squares[toFile<<3|toRank] = (byte) (Piece.ROOK | (movePiece & Piece.MASK_COLOR));
                break;
            case 'Q':
                squares[toFile<<3|toRank] = (byte) (Piece.QUEEN | (movePiece & Piece.MASK_COLOR));
                break;
            }
        }

        if(fromFile == F_E && fromRank == R_1) {
            flags &= ~(CASTLE_WKS|CASTLE_WQS);
        }
        if(fromFile == F_E && fromRank == R_8) {
            flags &= ~(CASTLE_BKS|CASTLE_BQS);
        }

        if(fromFile == F_A && fromRank == R_1) {
            flags &= ~CASTLE_WQS;
        }
        if(fromFile == F_H && fromRank == R_1) {
            flags &= ~CASTLE_WKS;
        }
        if(fromFile == F_A && fromRank == R_8) {
            flags &= ~CASTLE_BQS;
        }
        if(fromFile == F_H && fromRank == R_8) {
            flags &= ~CASTLE_BKS;
        }
        flags ^= PLAYER_MASK;

        return this;
    }
    
    public int getRepeatedCount() {
    	Short s = history.get(getCacheId());
    	return s == null ? 1 : s.shortValue() + 1;
    }

    public boolean isEnPassant() {
        return (flags & EN_PASSANT) != 0;
    }

    public int getEnPassantSquare() {
        return (flags & EN_PASSANT_MASK)>>5;
    }

    public void setEnPassantSquare(int file, int rank) {
    	history.clear();
    	flags &= ~EN_PASSANT_MASK;
    	if(file == -1 || rank == -1) {
    		flags &= ~EN_PASSANT;
    	} else {
    		flags |= EN_PASSANT;
        	flags |= (file<<3|rank)<<5;
    	}
    }

    public int getEnPassantFile() {
        return (flags & EN_PASSANT_MASK)>>8;
    }

    public int getEnPassantRank() {
        return ((flags & EN_PASSANT_MASK)>>5) & 0x07;
    }
    
    public byte getPiece(String position) {
    	return getPiece(EngineUtils.FILES.indexOf(position.charAt(0)), EngineUtils.RANKS.indexOf(position.charAt(1)));
    }

    public byte getPieceIfOnBoard(int file, int rank) {
    	if(isOnBoard(file, rank)) {
    		return getPiece(file, rank);
    	} else {
    		return 0;
    	}
    }
    
    public byte getPiece(int file, int rank) {
    	try {
    		return squares[file<<3|rank];
    	} catch (ArrayIndexOutOfBoundsException x) {
    		throw x;
    	}
    }
    
    public void setPiece(int file, int rank, byte piece) {
    	history.clear();
    	squares[file<<3|rank] = piece;
    }

    final public static byte getSquareColor(int file, int rank) {
    	return (file%2 == rank%2 ? Piece.BLACK : Piece.WHITE);
    }

    public String toString() {
        return FENUtils.generate(this);
    }

	public static boolean isOnBoard(int file, int rank) {
		return ((file & ~0x07) | (rank & ~0x07)) == 0;
	}

	public String getCacheId() {
		char[] c = new char[17];
		for(int i = 0; i < 64; i+=4) {
			c[i/4] |= (squares[i]<<12|squares[i+1]<<8|squares[i+2]<<4|squares[i+3]);
		}
		c[16] |= flags;
		return new String(c);
	}
	
	public static String cacheToString(String cacheId) {
		char[] cacheBuf = cacheId.toCharArray();
		byte[] buf = new byte[36];
		byte[] outputBuf = new byte[48];
		for(int i = 0; i < cacheBuf.length; i++) {
			buf[i*2] = (byte)((cacheBuf[i]&0xff00)>>8);
			buf[i*2+1] = (byte)(cacheBuf[i]&0xff);
		}
		
		for(int i = 0; i < buf.length; i += 3) {
			int chunk = ((buf[i]<<16&0xff0000) | (buf[i+1]<<8&0xff00) | (buf[i+2]&0xff));
	        for(int j = 0; j < 4; j++) {
	            outputBuf[(3-j) + (i/3*4)] = (byte) BASE64.charAt(chunk&0x3f);
	            chunk >>= 6;
	        }
		}
		
		return new String(outputBuf);
	}
	
	public String getStateInfo() {
		StringBuffer stateInfo = new StringBuffer(cacheToString(getCacheId()));
		for(String s: history.keySet()) {
			stateInfo.append(cacheToString(s));
			stateInfo.append(history.get(s));
		}
		return stateInfo.toString();
	}
}
