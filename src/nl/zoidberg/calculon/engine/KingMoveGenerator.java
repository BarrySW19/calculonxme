package nl.zoidberg.calculon.engine;

import java.util.Hashtable;

import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Piece;

public class KingMoveGenerator extends PieceMoveGenerator {

    public Hashtable generateMoves(Board board, int file, int rank) {
            return this.generateMoves(board, file, rank, false);
    }
	
    public Hashtable generateMoves(Board board, int file, int rank, boolean oneOnly) {
        Hashtable rv = new Hashtable();

        for(int finc = -1; finc <= 1; finc++) {
            for(int rinc = -1; rinc <= 1; rinc++) {
                if(finc == 0 && rinc == 0) {
                    continue;
                }
                if(isEmptyOrCaptureTarget(board, file+finc, rank+rinc)) {
                    String move = EngineUtils.toSimpleAlgebraic(file, rank, file+finc, rank+rinc);
                    Board nextBoard = board.clone().applyMove(move);
                    if( ! CheckDetector.inCheck(nextBoard)) {
                        rv.put(move, new SearchNode(nextBoard));
                        if(oneOnly) {
                        	return rv;
                        }
                    }
                }
            }
        }

        if(CheckDetector.alreadyInCheck(board)) {
            return rv;
        }

        if(board.getPlayer() == Piece.WHITE && (board.getCastlingOptions() & Board.CASTLE_WKS) != 0) {
            if(isEmpty(board, Board.F_F, Board.R_1) && isEmpty(board, Board.F_G, Board.R_1)) {
                if( ! CheckDetector.inCheck(board.clone().applyMove("E1F1"))) {
                    Board nextBoard = board.clone().applyMove("O-O");
                    if( ! CheckDetector.inCheck(nextBoard)) {
                        rv.put("O-O", new SearchNode(nextBoard));
                    }
                }
            }
        }
        if(board.getPlayer() == Piece.WHITE && (board.getCastlingOptions() & Board.CASTLE_WQS) != 0) {
            if(isEmpty(board, Board.F_B, Board.R_1)
                    && isEmpty(board, Board.F_C, Board.R_1) && isEmpty(board, Board.F_D, Board.R_1)) {
                if( ! CheckDetector.inCheck(board.clone().applyMove("E1D1"))) {
                    Board nextBoard = board.clone().applyMove("O-O-O");
                    if( ! CheckDetector.inCheck(nextBoard)) {
                        rv.put("O-O-O", new SearchNode(nextBoard));
                    }
                }
            }
        }
        if(board.getPlayer() == Piece.BLACK && (board.getCastlingOptions() & Board.CASTLE_BKS) != 0) {
            if(isEmpty(board, Board.F_F, Board.R_8) && isEmpty(board, Board.F_G, Board.R_8)) {
                if( ! CheckDetector.inCheck(board.clone().applyMove("E8F8"))) {
                    Board nextBoard = board.clone().applyMove("O-O");
                    if( ! CheckDetector.inCheck(nextBoard)) {
                        rv.put("O-O", new SearchNode(nextBoard));
                    }
                }
            }
        }
        if(board.getPlayer() == Piece.BLACK && (board.getCastlingOptions() & Board.CASTLE_BQS) != 0) {
            if(isEmpty(board, Board.F_B, Board.R_8)
                    && isEmpty(board, Board.F_C, Board.R_8) && isEmpty(board, Board.F_D, Board.R_8)) {
                if( ! CheckDetector.inCheck(board.clone().applyMove("E8D8"))) {
                    Board nextBoard = board.clone().applyMove("O-O-O");
                    if( ! CheckDetector.inCheck(nextBoard)) {
                        rv.put("O-O-O", new SearchNode(nextBoard));
                    }
                }
            }
        }

        return rv;
    }
}
