package nl.zoidberg.calculon.engine;

import java.util.Vector;

import nl.zoidberg.calculon.engine.BitBoard.BitBoardMove;
import nl.zoidberg.calculon.model.Piece;

public class KnightMoveGenerator extends PieceMoveGenerator {
	
	// Pre-generated knight moves
	public static final long[] KNIGHT_MOVES = new long[64];
	static {
		KNIGHT_MOVES[0] = 132096L;
		KNIGHT_MOVES[8] = 33816580L;
		KNIGHT_MOVES[16] = 8657044482L;
		KNIGHT_MOVES[24] = 2216203387392L;
		KNIGHT_MOVES[32] = 567348067172352L;
		KNIGHT_MOVES[40] = 145241105196122112L;
		KNIGHT_MOVES[48] = 288234782788157440L;
		KNIGHT_MOVES[56] = 1128098930098176L;
		KNIGHT_MOVES[1] = 329728L;
		KNIGHT_MOVES[9] = 84410376L;
		KNIGHT_MOVES[17] = 21609056261L;
		KNIGHT_MOVES[25] = 5531918402816L;
		KNIGHT_MOVES[33] = 1416171111120896L;
		KNIGHT_MOVES[41] = 362539804446949376L;
		KNIGHT_MOVES[49] = 576469569871282176L;
		KNIGHT_MOVES[57] = 2257297371824128L;
		KNIGHT_MOVES[2] = 659712L;
		KNIGHT_MOVES[10] = 168886289L;
		KNIGHT_MOVES[18] = 43234889994L;
		KNIGHT_MOVES[26] = 11068131838464L;
		KNIGHT_MOVES[34] = 2833441750646784L;
		KNIGHT_MOVES[42] = 725361088165576704L;
		KNIGHT_MOVES[50] = 1224997833292120064L;
		KNIGHT_MOVES[58] = 4796069720358912L;
		KNIGHT_MOVES[3] = 1319424L;
		KNIGHT_MOVES[11] = 337772578L;
		KNIGHT_MOVES[19] = 86469779988L;
		KNIGHT_MOVES[27] = 22136263676928L;
		KNIGHT_MOVES[35] = 5666883501293568L;
		KNIGHT_MOVES[43] = 1450722176331153408L;
		KNIGHT_MOVES[51] = 2449995666584240128L;
		KNIGHT_MOVES[59] = 9592139440717824L;
		KNIGHT_MOVES[4] = 2638848L;
		KNIGHT_MOVES[12] = 675545156L;
		KNIGHT_MOVES[20] = 172939559976L;
		KNIGHT_MOVES[28] = 44272527353856L;
		KNIGHT_MOVES[36] = 11333767002587136L;
		KNIGHT_MOVES[44] = 2901444352662306816L;
		KNIGHT_MOVES[52] = 4899991333168480256L;
		KNIGHT_MOVES[60] = 19184278881435648L;
		KNIGHT_MOVES[5] = 5277696L;
		KNIGHT_MOVES[13] = 1351090312L;
		KNIGHT_MOVES[21] = 345879119952L;
		KNIGHT_MOVES[29] = 88545054707712L;
		KNIGHT_MOVES[37] = 22667534005174272L;
		KNIGHT_MOVES[45] = 5802888705324613632L;
		KNIGHT_MOVES[53] = -8646761407372591104L;
		KNIGHT_MOVES[61] = 38368557762871296L;
		KNIGHT_MOVES[6] = 10489856L;
		KNIGHT_MOVES[14] = 2685403152L;
		KNIGHT_MOVES[22] = 687463207072L;
		KNIGHT_MOVES[30] = 175990581010432L;
		KNIGHT_MOVES[38] = 45053588738670592L;
		KNIGHT_MOVES[46] = -6913025356609880064L;
		KNIGHT_MOVES[54] = 1152939783987658752L;
		KNIGHT_MOVES[62] = 4679521487814656L;
		KNIGHT_MOVES[7] = 4202496L;
		KNIGHT_MOVES[15] = 1075839008L;
		KNIGHT_MOVES[23] = 275414786112L;
		KNIGHT_MOVES[31] = 70506185244672L;
		KNIGHT_MOVES[39] = 18049583422636032L;
		KNIGHT_MOVES[47] = 4620693356194824192L;
		KNIGHT_MOVES[55] = 2305878468463689728L;
		KNIGHT_MOVES[63] = 9077567998918656L;
	}
	
	private void generateMoves(BitBoard bitBoard, long pieceMap, boolean alreadyInCheck, boolean safeFromCheck, Vector rv) {
        byte player = bitBoard.getPlayer();
        long knightMoves = KNIGHT_MOVES[LongUtil.numberOfTrailingZeros(pieceMap)];
        knightMoves &= ~bitBoard.getBitmapColor();

        while(knightMoves != 0) {
        	long nextMove = LongUtil.highestOneBit(knightMoves);
        	knightMoves ^= nextMove;
        	
        	BitBoardMove bbMove;
        	if((nextMove & bitBoard.getBitmapOppColor(player)) != 0) {
				bbMove = BitBoard.generateCapture(pieceMap, nextMove, player, Piece.KNIGHT, bitBoard.getPiece(nextMove));
        	} else {
				bbMove = BitBoard.generateMove(pieceMap, nextMove, player, Piece.KNIGHT);
        	}
        	
			if(safeFromCheck) {
                rv.addElement(bbMove);
			} else {
				bitBoard.makeMove(bbMove);
				if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard, ! alreadyInCheck)) {
	                rv.addElement(bbMove);
				}
				bitBoard.unmakeMove();
			}
        }
	}

	public void generateMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
		
		long pieces = bitBoard.getBitmapColor() & bitBoard.getBitmapKnights();
		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			this.generateMoves(bitBoard, nextPiece, alreadyInCheck, safeFromCheck, rv);
		}
	}
	
	public void generateThreatMoves(BitBoard bitBoard, boolean alreadyInCheck, long potentialPins, Vector rv) {
        byte player = bitBoard.getPlayer();
		long pieces = bitBoard.getBitmapColor() & bitBoard.getBitmapKnights();

		while(pieces != 0) {
			long nextPiece = LongUtil.highestOneBit(pieces);
			pieces ^= nextPiece;
			boolean safeFromCheck = ((nextPiece & potentialPins) == 0) & !alreadyInCheck;
			
	        long capturesAndChecks = bitBoard.getBitmapOppColor();
	        long knightThreats = capturesAndChecks & KNIGHT_MOVES[LongUtil.numberOfTrailingZeros(nextPiece)];
	        
	        while(knightThreats != 0) {
		        long nextThreat = LongUtil.highestOneBit(knightThreats);
		        knightThreats ^= nextThreat;

		        BitBoardMove bbMove;
	        	if((nextThreat & bitBoard.getBitmapOppColor()) != 0) {
					bbMove = BitBoard.generateCapture(nextPiece, nextThreat, player, Piece.KNIGHT, bitBoard.getPiece(nextThreat));
	        	} else {
					bbMove = BitBoard.generateMove(nextPiece, nextThreat, player, Piece.KNIGHT);
	        	}
	        	
				if(safeFromCheck) {
	                rv.addElement(bbMove);
				} else {
					bitBoard.makeMove(bbMove);
					if( ! CheckDetector.isPlayerJustMovedInCheck(bitBoard, ! alreadyInCheck)) {
		                rv.addElement(bbMove);
					}
					bitBoard.unmakeMove();
				}
	        }
		}
	}
}
