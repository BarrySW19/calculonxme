package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.model.Piece;

public class KingMoveGenerator extends PieceMoveGenerator {

	private static final long EMPTY_WKS = 3L<<5;
	private static final long EMPTY_WQS = 7L<<1;
	private static final long EMPTY_BKS = 3L<<61;
	private static final long EMPTY_BQS = 7L<<57;
	
	// Pre-generated king moves
	public static final long[] kmBitmap = new long[64];
	public static final long[] km2Bitmap = new long[64];
	static {
		kmBitmap[0] = 770L;
		kmBitmap[1] = 197123L;
		kmBitmap[2] = 50463488L;
		kmBitmap[3] = 12918652928L;
		kmBitmap[4] = 3307175149568L;
		kmBitmap[5] = 846636838289408L;
		kmBitmap[6] = 216739030602088448L;
		kmBitmap[7] = 144959613005987840L;
		kmBitmap[8] = 1797L;
		kmBitmap[9] = 460039L;
		kmBitmap[10] = 117769984L;
		kmBitmap[11] = 30149115904L;
		kmBitmap[12] = 7718173671424L;
		kmBitmap[13] = 1975852459884544L;
		kmBitmap[14] = 505818229730443264L;
		kmBitmap[15] = 362258295026614272L;
		kmBitmap[16] = 3594L;
		kmBitmap[17] = 920078L;
		kmBitmap[18] = 235539968L;
		kmBitmap[19] = 60298231808L;
		kmBitmap[20] = 15436347342848L;
		kmBitmap[21] = 3951704919769088L;
		kmBitmap[22] = 1011636459460886528L;
		kmBitmap[23] = 724516590053228544L;
		kmBitmap[24] = 7188L;
		kmBitmap[25] = 1840156L;
		kmBitmap[26] = 471079936L;
		kmBitmap[27] = 120596463616L;
		kmBitmap[28] = 30872694685696L;
		kmBitmap[29] = 7903409839538176L;
		kmBitmap[30] = 2023272918921773056L;
		kmBitmap[31] = 1449033180106457088L;
		kmBitmap[32] = 14376L;
		kmBitmap[33] = 3680312L;
		kmBitmap[34] = 942159872L;
		kmBitmap[35] = 241192927232L;
		kmBitmap[36] = 61745389371392L;
		kmBitmap[37] = 15806819679076352L;
		kmBitmap[38] = 4046545837843546112L;
		kmBitmap[39] = 2898066360212914176L;
		kmBitmap[40] = 28752L;
		kmBitmap[41] = 7360624L;
		kmBitmap[42] = 1884319744L;
		kmBitmap[43] = 482385854464L;
		kmBitmap[44] = 123490778742784L;
		kmBitmap[45] = 31613639358152704L;
		kmBitmap[46] = 8093091675687092224L;
		kmBitmap[47] = 5796132720425828352L;
		kmBitmap[48] = 57504L;
		kmBitmap[49] = 14721248L;
		kmBitmap[50] = 3768639488L;
		kmBitmap[51] = 964771708928L;
		kmBitmap[52] = 246981557485568L;
		kmBitmap[53] = 63227278716305408L;
		kmBitmap[54] = -2260560722335367168L;
		kmBitmap[55] = -6854478632857894912L;
		kmBitmap[56] = 49216L;
		kmBitmap[57] = 12599488L;
		kmBitmap[58] = 3225468928L;
		kmBitmap[59] = 825720045568L;
		kmBitmap[60] = 211384331665408L;
		kmBitmap[61] = 54114388906344448L;
		kmBitmap[62] = -4593460513685372928L;
		kmBitmap[63] = 4665729213955833856L;
		
		for(int i = 0; i < 64; i++) {
			km2Bitmap[i] = kmBitmap[((i&0x38)>>3)|((i&0x07)<<3)];
		}
	}
	
    private Vector generateCaptureMoves(Board board, int file, int rank) {
    	Vector rv = new Vector();

        BitBoard bitBoard = board.getBitBoard();
        
        long kingMoves = kmBitmap[file<<3|rank];
        kingMoves &= bitBoard.getBitmapOppColor(board.getPlayer());
        while(kingMoves != 0) {
        	long nextMove = LongUtil.highestOneBit(kingMoves);
        	kingMoves ^= nextMove;
        	
        	int pos = LongUtil.numberOfTrailingZeros(nextMove);
            String move = EngineUtils.toSimpleAlgebraic(file, rank, pos&0x07, pos>>3);
            Board nextBoard = board.clone().applyMove(move);
            if( ! CheckDetector.inCheck(nextBoard, false)) {
                rv.addElement(new Move(move, nextBoard));
            }
        }
        
        return rv;
    }

	public Vector generateMoves(Board board) {
		Vector rv = new Vector();
		byte player = board.getPlayer();
		
		BitBoard bitBoard = board.getBitBoard();
		// There can be only one...
		long king = bitBoard.getBitmapColor(player) & bitBoard.getBitmapKings();
		long emptyMoves = km2Bitmap[LongUtil.numberOfTrailingZeros(king)]&(~bitBoard.getAllPieces());
		
		while(emptyMoves != 0) {
			long nextMove = LongUtil.highestOneBit(emptyMoves);
			emptyMoves ^= nextMove;
			long kingMove = king | nextMove;
			bitBoard.bitmaps[BitBoard.MAP_KINGS] ^= kingMove;
			bitBoard.bitmaps[player] ^= kingMove;
            if( ! CheckDetector.inCheck(bitBoard, player, false)) {
            	String move = EngineUtils.toCoord(king) + EngineUtils.toCoord(nextMove);
                rv.addElement(new Move(move, null, board));
            }
			bitBoard.bitmaps[BitBoard.MAP_KINGS] ^= kingMove;
			bitBoard.bitmaps[player] ^= kingMove;
		}
		
		short castleFlags = board.getCastlingOptions();
		if(player == Piece.WHITE && ! board.isInCheck()) {
			if((castleFlags & Board.CASTLE_WKS) != 0 && (bitBoard.getAllPieces() & EMPTY_WKS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king | king<<1, player)) {
                	if(isCastlingPossible(bitBoard, player, 5L<<4, 5L<<5)) {
                        rv.addElement(new Move("O-O", null, board));
                	}
                }
			}
			if((castleFlags & Board.CASTLE_WQS) != 0 && (bitBoard.getAllPieces() & EMPTY_WQS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king | king>>>1, player)) {
                	if(isCastlingPossible(bitBoard, player, 5L<<2, 9L)) {
                        rv.addElement(new Move("O-O-O", null, board));
                	}
                }
			}
		} else if(player == Piece.BLACK && ! board.isInCheck()) {
			if((castleFlags & Board.CASTLE_BKS) != 0 && (bitBoard.getAllPieces() & EMPTY_BKS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king | king<<1, player)) {
                	if(isCastlingPossible(bitBoard, player, 5L<<60, 5L<<61)) {
                        rv.addElement(new Move("O-O", null, board));
                	}
                }
			}
			if((castleFlags & Board.CASTLE_BQS) != 0 && (bitBoard.getAllPieces() & EMPTY_BQS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king | king>>>1, player)) {
                	if(isCastlingPossible(bitBoard, player, 5L<<58, 9L<<56)) {
                        rv.addElement(new Move("O-O-O", null, board));
                	}
                }
			}
		}
		
		while(king != 0) {
			long nextPiece = LongUtil.highestOneBit(king);
			king ^= nextPiece;
			int[] coords = BitBoard.toCoords(nextPiece);
			EngineUtils.addAll(rv, this.generateCaptureMoves(board, coords[0], coords[1]));
		}
		
		return rv;
	}

	// Bit twiddling routines...
	private boolean isCastlingPossible(BitBoard bitBoard, byte player, long mKing, long mRook) {
		bitBoard.bitmaps[BitBoard.MAP_KINGS] ^= mKing;
		bitBoard.bitmaps[BitBoard.MAP_ROOKS] ^= mRook;
		bitBoard.bitmaps[player] ^= (mKing|mRook);
        boolean rv = ! CheckDetector.inCheck(bitBoard, player, false);
		bitBoard.bitmaps[BitBoard.MAP_KINGS] ^= mKing;
		bitBoard.bitmaps[BitBoard.MAP_ROOKS] ^= mRook;
		bitBoard.bitmaps[player] ^= (mKing|mRook);
		return rv;
	}

	private boolean isIntermediateCheck(BitBoard bitBoard, long moveMap, byte player) {
		bitBoard.bitmaps[BitBoard.MAP_KINGS] ^= moveMap;
		bitBoard.bitmaps[player] ^= moveMap;
        boolean rv = CheckDetector.inCheck(bitBoard, player, false);
		bitBoard.bitmaps[BitBoard.MAP_KINGS] ^= moveMap;
		bitBoard.bitmaps[player] ^= moveMap;
		return rv;
	}
}
