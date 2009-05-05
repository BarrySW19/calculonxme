package nl.zoidberg.calculon.engine;

public class BishopMoveGenerator extends StraightMoveGenerator {

	public static final int[][] DIRECTIONS = new int[][] {
		{ 1, 1 },
		{ -1, 1 },
		{ -1, -1 },
		{ 1, -1 },
	};

	public int[][] getDirections() {
		return DIRECTIONS;
	}
}