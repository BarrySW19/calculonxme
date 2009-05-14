package nl.zoidberg.calculon.engine;

import java.util.Vector;




public class KnightMoveGenerator extends PieceMoveGenerator {
	
	// Pre-generated knight moves
	public static final long[] kmBitmap = new long[64];
	static {
		kmBitmap[0] = 132096L;
		kmBitmap[1] = 33816580L;
		kmBitmap[2] = 8657044482L;
		kmBitmap[3] = 2216203387392L;
		kmBitmap[4] = 567348067172352L;
		kmBitmap[5] = 145241105196122112L;
		kmBitmap[6] = 288234782788157440L;
		kmBitmap[7] = 1128098930098176L;
		kmBitmap[8] = 329728L;
		kmBitmap[9] = 84410376L;
		kmBitmap[10] = 21609056261L;
		kmBitmap[11] = 5531918402816L;
		kmBitmap[12] = 1416171111120896L;
		kmBitmap[13] = 362539804446949376L;
		kmBitmap[14] = 576469569871282176L;
		kmBitmap[15] = 2257297371824128L;
		kmBitmap[16] = 659712L;
		kmBitmap[17] = 168886289L;
		kmBitmap[18] = 43234889994L;
		kmBitmap[19] = 11068131838464L;
		kmBitmap[20] = 2833441750646784L;
		kmBitmap[21] = 725361088165576704L;
		kmBitmap[22] = 1224997833292120064L;
		kmBitmap[23] = 4796069720358912L;
		kmBitmap[24] = 1319424L;
		kmBitmap[25] = 337772578L;
		kmBitmap[26] = 86469779988L;
		kmBitmap[27] = 22136263676928L;
		kmBitmap[28] = 5666883501293568L;
		kmBitmap[29] = 1450722176331153408L;
		kmBitmap[30] = 2449995666584240128L;
		kmBitmap[31] = 9592139440717824L;
		kmBitmap[32] = 2638848L;
		kmBitmap[33] = 675545156L;
		kmBitmap[34] = 172939559976L;
		kmBitmap[35] = 44272527353856L;
		kmBitmap[36] = 11333767002587136L;
		kmBitmap[37] = 2901444352662306816L;
		kmBitmap[38] = 4899991333168480256L;
		kmBitmap[39] = 19184278881435648L;
		kmBitmap[40] = 5277696L;
		kmBitmap[41] = 1351090312L;
		kmBitmap[42] = 345879119952L;
		kmBitmap[43] = 88545054707712L;
		kmBitmap[44] = 22667534005174272L;
		kmBitmap[45] = 5802888705324613632L;
		kmBitmap[46] = -8646761407372591104L;
		kmBitmap[47] = 38368557762871296L;
		kmBitmap[48] = 10489856L;
		kmBitmap[49] = 2685403152L;
		kmBitmap[50] = 687463207072L;
		kmBitmap[51] = 175990581010432L;
		kmBitmap[52] = 45053588738670592L;
		kmBitmap[53] = -6913025356609880064L;
		kmBitmap[54] = 1152939783987658752L;
		kmBitmap[55] = 4679521487814656L;
		kmBitmap[56] = 4202496L;
		kmBitmap[57] = 1075839008L;
		kmBitmap[58] = 275414786112L;
		kmBitmap[59] = 70506185244672L;
		kmBitmap[60] = 18049583422636032L;
		kmBitmap[61] = 4620693356194824192L;
		kmBitmap[62] = 2305878468463689728L;
		kmBitmap[63] = 9077567998918656L;
	}
	
	public static int[][] MOVES = new int[][] {
		{ 1, 2 }, 	{ 1, -2 },
		{ -1, 2 }, 	{ -1, -2 },
		{ 2, 1 },	{ 2, -1 },
		{ -2, 1 },	{ -2, -1 },
	};

	private Vector generateMoves(Board board, int file, int rank) {
		boolean alreadyInCheck = board.isInCheck();
		Vector rv = new Vector();
        BitBoard bitBoard = board.getBitBoard();
        
        long knightMoves = kmBitmap[file<<3|rank];
        knightMoves &= ~bitBoard.getBitmapColor(board.getPlayer());
        while(knightMoves != 0) {
        	long nextMove = LongUtil.highestOneBit(knightMoves);
        	knightMoves ^= nextMove;
        	if((nextMove & bitBoard.getBitmapOppColor(board.getPlayer())) != 0) {
            	int pos = LongUtil.numberOfTrailingZeros(nextMove);
                String move = EngineUtils.toSimpleAlgebraic(file, rank, pos&0x07, pos>>3);
    			Board nextBoard = board.clone().applyMove(move);
    			if( ! CheckDetector.inCheck(nextBoard, !alreadyInCheck)) {
    				rv.addElement(new Move(move, nextBoard));
    			}
        	} else {
        		long moveMask = nextMove | 1L<<(rank<<3)<<file;
        		bitBoard.bitmaps[BitBoard.MAP_KNIGHTS] ^= moveMask;
        		bitBoard.bitmaps[board.getPlayer()] ^= moveMask;
    			if( ! CheckDetector.inCheck(bitBoard, board.getPlayer(), ! alreadyInCheck)) {
                	String move = EngineUtils.toCoord(1L<<(rank<<3)<<file) + EngineUtils.toCoord(nextMove);
                    rv.addElement(new Move(move, null, board));
    			}
        		bitBoard.bitmaps[BitBoard.MAP_KNIGHTS] ^= moveMask;
        		bitBoard.bitmaps[board.getPlayer()] ^= moveMask;
        	}
        }
		
		return rv;
	}

	public Vector generateMoves(Board board) {
		Vector rv = new Vector();
		
		BitBoard bitBoard = board.getBitBoard();
		long pieces = bitBoard.getBitmapColor(board.getPlayer()) & bitBoard.getBitmapKnights();
		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			int[] coords = BitBoard.toCoords(nextPiece);
			EngineUtils.addAll(rv, this.generateMoves(board, coords[0], coords[1]));
		}
		
		return rv;
	}
}
