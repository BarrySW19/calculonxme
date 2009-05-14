package nl.zoidberg.calculon.analyzer;

import java.util.Enumeration;
import java.util.Vector;

import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.ChessEngine;
import nl.zoidberg.calculon.engine.Move;
import nl.zoidberg.calculon.engine.MoveGenerator;
import nl.zoidberg.calculon.notation.FENUtils;

public class SpeedTest {

//	Bullet          1422         22    20     3    45   1536 (11-May-2009)
//	Blitz           1237        448   287    67   802   1387 (10-May-2009)
//	Standard        1270         95    91    13   199   1574 (06-May-2009)
	
	public static void main(String[] args) throws Exception {
		
		Board board = new Board();
		FENUtils.loadPosition("1rbq2r1/3pkpp1/2n1p2p/1N1n4/1p1P3N/3Q2P1/1PP2PBP/R3R1K1 b - - 1 16", board);
		ChessEngine engine = new ChessEngine();
		engine.setDepth(3);

		long pre = System.currentTimeMillis();
		engine.getPreferredMove(board);
        System.out.println("Time (search): " + (System.currentTimeMillis() - pre) + " ms");

        pre = System.currentTimeMillis();
		engine.getPreferredMove(board);
        System.out.println("Time (search2): " + (System.currentTimeMillis() - pre) + " ms");

        pre = System.currentTimeMillis();
		engine = new ChessEngine();
		engine.getPreferredMove(board);
        System.out.println("Time (search3): " + (System.currentTimeMillis() - pre) + " ms");

        pre = System.currentTimeMillis();
		engine = new ChessEngine();
		engine.setDepth(5);
		engine.getPreferredMove(board);
        System.out.println("Time (search4): " + (System.currentTimeMillis() - pre) + " ms");

		pre = System.currentTimeMillis();
        RunningCount rc = new RunningCount();
		generateToDepth(5, board, rc);
        System.out.print("Time (perft): " + (System.currentTimeMillis() - pre) + " ms" 
        		+ "  [" + (115196793000L / (System.currentTimeMillis() - pre)) + " nodes/sec]");
        System.out.println(", status = " + (rc.count == 115196793 ? "OK" : "Error! " + rc.count));
	}

	private static void generateToDepth(int depth, Board board, RunningCount runningCount) {
		if(depth == 1) {
			Vector allMoves = new MoveGenerator(board).getAllRemainingMoves();
			runningCount.count += allMoves.size();
			return;
		}
		for(Enumeration e = new MoveGenerator(board).getAllRemainingMoves().elements(); e.hasMoreElements(); ) {
			Move move = (Move) e.nextElement();
			generateToDepth(depth-1, move.getBoard(), runningCount);
		}
	}

	private static class RunningCount {
		private long count = 0;
		
		public void reset() {
			count = 0;
		}
	}
}
