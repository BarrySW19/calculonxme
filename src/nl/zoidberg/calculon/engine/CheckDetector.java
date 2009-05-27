package nl.zoidberg.calculon.engine;

import nl.zoidberg.calculon.model.Piece;

public class CheckDetector {
	private static final int[] DIR_U	= { 0, 1 };
	private static final int[] DIR_D	= { 0, -1 };
	private static final int[] DIR_R	= { 1, 0 };
	private static final int[] DIR_L	= { -1, 0 };
	private static final int[] DIR_UR	= { 1, 1 };
	private static final int[] DIR_DR	= { 1, -1 };
	private static final int[] DIR_DL	= { -1, -1 };
	private static final int[] DIR_UL	= { -1, 1 };
	
	/**
	 * Tests whether the player with the move is in check - i.e. whether the last move played was a checking move.
	 */
	public static boolean isPlayerToMoveInCheck(Board board) {
        return inCheck(board.getBitBoard(), board.getPlayer(), false);
    }

	/**
	 * Tests whether the player who just moved has left themselves in check - i.e. it was an illegal move.
	 * 
	 * @param board
	 * @return
	 */
    public static boolean isPlayerJustMovedInCheck(Board board, boolean pinCheckOnly) {
        byte color = board.getPlayer() == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
        return inCheck(board.getBitBoard(), color, pinCheckOnly);
    }

	public static boolean isPlayerJustMovedInCheck(BitBoard bitBoard) {
    	byte color = bitBoard.getPlayer() == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
    	return inCheck(bitBoard, color, false);
	}
	
    public static boolean isPlayerJustMovedInCheck(BitBoard bitBoard, boolean pinCheckOnly) {
    	byte color = bitBoard.getPlayer() == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
    	return inCheck(bitBoard, color, pinCheckOnly);
    }
    
	public static boolean isPlayerToMoveInCheck(BitBoard bitBoard) {
    	return inCheck(bitBoard, bitBoard.getPlayer(), false);
	}
	
    public static boolean isPlayerToMoveInCheck(BitBoard bitBoard, boolean pinCheckOnly) {
    	return inCheck(bitBoard, bitBoard.getPlayer(), pinCheckOnly);
    }
    
    /**
     * The pin check flag can be used for tests when a piece other than the king moved,
     * possibly exposing the king to check. As this sort of check could only be by a
     * bishop, rook or queen it is not necessary to check for checks by enemy pawns,
     * knights or king.
     * 
     * @param board
     * @param color
     * @param pinCheckOnly
     * @return
     */
    private static boolean inCheck(BitBoard bitBoard, byte color, boolean pinCheckOnly) {
    	long kingMap = bitBoard.getBitmapColor(color) & bitBoard.getBitmapKings();
    	int kingIdx = LongUtil.numberOfTrailingZeros(kingMap);
        int[] kingPos = BitBoard.toCoords(kingMap);
        
        if( ! pinCheckOnly) {
	        long enemyPawns = bitBoard.getBitmapOppColor(color) & bitBoard.getBitmapPawns();
	        
	        // Theoretically, we would incorrectly find pawns on the 1st/8th rank with this, but there shouldn't
	        // be any there :)
	        long checkPawns = color == Piece.WHITE
	        		? ((kingMap & ~BitBoard.getFileMap(7))<<9 | (kingMap & ~BitBoard.getFileMap(0))<<7)
	        		: ((kingMap & ~BitBoard.getFileMap(0))>>>9 | (kingMap & ~BitBoard.getFileMap(7))>>>7);
	        
	        if((enemyPawns & checkPawns) != 0) {
	        	return true;
	        }
	        
	        long kingMoves = KingMoveGenerator.KING_MOVES[kingIdx];
	        if((kingMoves & bitBoard.getBitmapKings() & bitBoard.getBitmapOppColor(color)) != 0) {
	        	return true;
	        }
	        
	        long knightMoves = KnightMoveGenerator.KNIGHT_MOVES[kingIdx];
	        if((knightMoves & bitBoard.getBitmapKnights() & bitBoard.getBitmapOppColor(color)) != 0) {
	        	return true;
	        }
        }

        int kingPosIdx = LongUtil.numberOfTrailingZeros(kingMap);
        long allEnemies = bitBoard.getBitmapOppColor(color);

        long lineAttackers = allEnemies & (bitBoard.getBitmapRooks()|bitBoard.getBitmapQueens());
        if((lineAttackers & Bitmaps.cross2Map[kingPosIdx]) != 0) {
        
	        if((Bitmaps.maps2[Bitmaps.BM_U][kingPosIdx] & lineAttackers) != 0) {
	        	if(detectLineAttackingPiece(bitBoard, kingPos, DIR_U, color, lineAttackers)) {
	        		return true;
	        	}
	        }
	        if((Bitmaps.maps2[Bitmaps.BM_D][kingPosIdx] & lineAttackers) != 0) {
	        	if(detectLineAttackingPiece(bitBoard, kingPos, DIR_D, color, lineAttackers)) {
	        		return true;
	        	}
	        }
	        if((Bitmaps.maps2[Bitmaps.BM_L][kingPosIdx] & lineAttackers) != 0) {
	        	if(detectLineAttackingPiece(bitBoard, kingPos, DIR_L, color, lineAttackers)) {
	        		return true;
	        	}
	        }
	        if((Bitmaps.maps2[Bitmaps.BM_R][kingPosIdx] & lineAttackers) != 0) {
	        	if(detectLineAttackingPiece(bitBoard, kingPos, DIR_R, color, lineAttackers)) {
	        		return true;
	        	}
	        }
        }
        
        long diagAttackers = allEnemies & (bitBoard.getBitmapBishops()|bitBoard.getBitmapQueens());
        if((diagAttackers & Bitmaps.diag2Map[kingPosIdx]) != 0) {
	        if((Bitmaps.maps2[Bitmaps.BM_UR][kingPosIdx] & diagAttackers) != 0) {
	        	if(detectLineAttackingPiece(bitBoard, kingPos, DIR_UR, color, diagAttackers)) {
	        		return true;
	        	}
	        }
	        if((Bitmaps.maps2[Bitmaps.BM_UL][kingPosIdx] & diagAttackers) != 0) {
	        	if(detectLineAttackingPiece(bitBoard, kingPos, DIR_UL, color, diagAttackers)) {
	        		return true;
	        	}
	        }
	        if((Bitmaps.maps2[Bitmaps.BM_DR][kingPosIdx] & diagAttackers) != 0) {
	        	if(detectLineAttackingPiece(bitBoard, kingPos, DIR_DR, color, diagAttackers)) {
	        		return true;
	        	}
	        }
	        if((Bitmaps.maps2[Bitmaps.BM_DL][kingPosIdx] & diagAttackers) != 0) {
	        	if(detectLineAttackingPiece(bitBoard, kingPos, DIR_DL, color, diagAttackers)) {
	        		return true;
	        	}
	        }
        }
        
        return false;
    }
    
    private static boolean detectLineAttackingPiece(BitBoard bitBoard, int[] kingPos, int[] dir, byte color, long attackers) {
        int nFile = kingPos[0] + dir[0];
        int nRank = kingPos[1] + dir[1];
        while(Board.isOnBoard(nFile, nRank)) {
        	if((bitBoard.getBitmapColor(color) & 1L<<(nRank<<3)<<nFile) != 0) {
        		// There is a piece of my color in the way.
        		return false;
        	}
        	
        	if((bitBoard.getBitmapOppColor(color) & 1L<<(nRank<<3)<<nFile) != 0) {
        		// There is a piece of opponents color here.
            	return ((attackers & 1L<<(nRank<<3)<<nFile) != 0);
        	}
        	
            nFile += dir[0];
            nRank += dir[1];
        }
        return false;
    }
}
