package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.model.Piece;

public class KingMoveGenerator extends PieceMoveGenerator {

	private static final long EMPTY_WKS = 3L<<5;
	private static final long EMPTY_WQS = 7L<<1;
	private static final long EMPTY_BKS = 3L<<61;
	private static final long EMPTY_BQS = 7L<<57;
	
	// Pre-generated king moves
	public static final long[] KING_MOVES = new long[64];
	static {
		KING_MOVES[0] = 770L;
		KING_MOVES[8] = 197123L;
		KING_MOVES[16] = 50463488L;
		KING_MOVES[24] = 12918652928L;
		KING_MOVES[32] = 3307175149568L;
		KING_MOVES[40] = 846636838289408L;
		KING_MOVES[48] = 216739030602088448L;
		KING_MOVES[56] = 144959613005987840L;
		KING_MOVES[1] = 1797L;
		KING_MOVES[9] = 460039L;
		KING_MOVES[17] = 117769984L;
		KING_MOVES[25] = 30149115904L;
		KING_MOVES[33] = 7718173671424L;
		KING_MOVES[41] = 1975852459884544L;
		KING_MOVES[49] = 505818229730443264L;
		KING_MOVES[57] = 362258295026614272L;
		KING_MOVES[2] = 3594L;
		KING_MOVES[10] = 920078L;
		KING_MOVES[18] = 235539968L;
		KING_MOVES[26] = 60298231808L;
		KING_MOVES[34] = 15436347342848L;
		KING_MOVES[42] = 3951704919769088L;
		KING_MOVES[50] = 1011636459460886528L;
		KING_MOVES[58] = 724516590053228544L;
		KING_MOVES[3] = 7188L;
		KING_MOVES[11] = 1840156L;
		KING_MOVES[19] = 471079936L;
		KING_MOVES[27] = 120596463616L;
		KING_MOVES[35] = 30872694685696L;
		KING_MOVES[43] = 7903409839538176L;
		KING_MOVES[51] = 2023272918921773056L;
		KING_MOVES[59] = 1449033180106457088L;
		KING_MOVES[4] = 14376L;
		KING_MOVES[12] = 3680312L;
		KING_MOVES[20] = 942159872L;
		KING_MOVES[28] = 241192927232L;
		KING_MOVES[36] = 61745389371392L;
		KING_MOVES[44] = 15806819679076352L;
		KING_MOVES[52] = 4046545837843546112L;
		KING_MOVES[60] = 2898066360212914176L;
		KING_MOVES[5] = 28752L;
		KING_MOVES[13] = 7360624L;
		KING_MOVES[21] = 1884319744L;
		KING_MOVES[29] = 482385854464L;
		KING_MOVES[37] = 123490778742784L;
		KING_MOVES[45] = 31613639358152704L;
		KING_MOVES[53] = 8093091675687092224L;
		KING_MOVES[61] = 5796132720425828352L;
		KING_MOVES[6] = 57504L;
		KING_MOVES[14] = 14721248L;
		KING_MOVES[22] = 3768639488L;
		KING_MOVES[30] = 964771708928L;
		KING_MOVES[38] = 246981557485568L;
		KING_MOVES[46] = 63227278716305408L;
		KING_MOVES[54] = -2260560722335367168L;
		KING_MOVES[62] = -6854478632857894912L;
		KING_MOVES[7] = 49216L;
		KING_MOVES[15] = 12599488L;
		KING_MOVES[23] = 3225468928L;
		KING_MOVES[31] = 825720045568L;
		KING_MOVES[39] = 211384331665408L;
		KING_MOVES[47] = 54114388906344448L;
		KING_MOVES[55] = -4593460513685372928L;
		KING_MOVES[63] = 4665729213955833856L;
	}
	
    private void generateCaptureMoves(BitBoard bitBoard, long kingPos, Vector rv) {
        byte player = bitBoard.getPlayer();
        long kingMoves = KING_MOVES[LongUtil.numberOfTrailingZeros(kingPos)];
        kingMoves &= bitBoard.getBitmapOppColor(player);
        while(kingMoves != 0) {
        	long nextMove = LongUtil.highestOneBit(kingMoves);
        	kingMoves ^= nextMove;
        	BitBoardMove bbMove = BitBoard.generateCapture(
        			kingPos, nextMove, player, Piece.KING, bitBoard.getPiece(nextMove));
        	bitBoard.makeMove(bbMove);
            if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard)) {
                rv.addElement(bbMove);
            }
        	bitBoard.unmakeMove();
        }
    }

	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		byte player = bitBoard.getPlayer();
		
		// There can be only one...
		long king = bitBoard.getBitmapColor(player) & bitBoard.getBitmapKings();
		long emptyMoves = KING_MOVES[LongUtil.numberOfTrailingZeros(king)]&(~bitBoard.getAllPieces());
		
		while(emptyMoves != 0) {
			long nextMove = LongUtil.highestOneBit(emptyMoves);
			emptyMoves ^= nextMove;
			BitBoardMove bbMove = BitBoard.generateMove(king, nextMove, player, Piece.KING);
			bitBoard.makeMove(bbMove);
            if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard)) {
                rv.addElement(bbMove);
            }
			bitBoard.unmakeMove();
		}
		
		byte castleFlags = bitBoard.getCastlingOptions();
		if(player == Piece.WHITE && ! alreadyInCheck) {
			if((castleFlags & Board.CASTLE_WKS) != 0 && (bitBoard.getAllPieces() & EMPTY_WKS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king, king<<1, player)) {
                	if(isCastlingPossible(bitBoard, player, Board.CASTLE_WKS)) {
                        rv.addElement(BitBoard.generateCastling(Board.CASTLE_WKS));
                	}
                }
			}
			if((castleFlags & Board.CASTLE_WQS) != 0 && (bitBoard.getAllPieces() & EMPTY_WQS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king, king>>>1, player)) {
                	if(isCastlingPossible(bitBoard, player, Board.CASTLE_WQS)) {
                        rv.addElement(BitBoard.generateCastling(Board.CASTLE_WQS));
                	}
                }
			}
		} else if(player == Piece.BLACK && ! alreadyInCheck) {
			if((castleFlags & Board.CASTLE_BKS) != 0 && (bitBoard.getAllPieces() & EMPTY_BKS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king, king<<1, player)) {
                	if(isCastlingPossible(bitBoard, player, Board.CASTLE_BKS)) {
                        rv.addElement(BitBoard.generateCastling(Board.CASTLE_BKS));
                	}
                }
			}
			if((castleFlags & Board.CASTLE_BQS) != 0 && (bitBoard.getAllPieces() & EMPTY_BQS) == 0) {
                if( ! isIntermediateCheck(bitBoard, king, king>>>1, player)) {
                	if(isCastlingPossible(bitBoard, player, Board.CASTLE_BQS)) {
                        rv.addElement(BitBoard.generateCastling(Board.CASTLE_BQS));
                	}
                }
			}
		}
		
		while(king != 0) {
			long nextPiece = LongUtil.highestOneBit(king);
			king ^= nextPiece;
			this.generateCaptureMoves(bitBoard, nextPiece, rv);
		}
	}

	// Bit twiddling routines...
	private boolean isCastlingPossible(BitBoard bitBoard, byte player, short castleDir) {
		BitBoardMove bbMove = BitBoard.generateCastling(castleDir);
		bitBoard.makeMove(bbMove);
        boolean rv = ! CheckDetector.isPlayerJustMovedInCheck(bitBoard);
		bitBoard.unmakeMove();
		return rv;
	}

	private boolean isIntermediateCheck(BitBoard bitBoard, long fromSquare, long toSquare, byte player) {
		BitBoardMove bbMove = BitBoard.generateMove(fromSquare, toSquare, player, Piece.KING);
		bitBoard.makeMove(bbMove);
        boolean rv = CheckDetector.isPlayerJustMovedInCheck(bitBoard);
		bitBoard.unmakeMove();
		return rv;
	}

	public void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		long kingMap = bitBoard.getBitmapColor() & bitBoard.getBitmapKings();
		long kingCaptures = KING_MOVES[LongUtil.numberOfTrailingZeros(kingMap)] & bitBoard.getBitmapOppColor();
		while(kingCaptures != 0) {
			long nextCapture = LongUtil.highestOneBit(kingCaptures);
			kingCaptures ^= nextCapture;
			BitBoardMove move = BitBoard.generateCapture(
					kingMap, nextCapture, bitBoard.getPlayer(), Piece.KING, bitBoard.getPiece(nextCapture));
			bitBoard.makeMove(move);
			if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard)) {
				rv.addElement(move);
			}
			bitBoard.unmakeMove();
		}
	}
}
