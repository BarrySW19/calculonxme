package nl.zoidberg.calculon.engine;


public class QueenMoveGenerator extends StraightMoveGenerator {

	public static int[][] DIRECTIONS = new int[][] {
		{ 1, 0 },	{ 1, 1 },
		{ 0, 1 },	{ -1, 1 },
		{ -1, 0 },	{ -1, -1 },
		{ 0, -1 },	{ 1, -1 },
	};

	public int[][] getDirections() {
		return DIRECTIONS;
	}
}
