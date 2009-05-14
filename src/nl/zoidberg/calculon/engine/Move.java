package nl.zoidberg.calculon.engine;

public class Move {
	
	private String move;
	private Board board;
	private Board parent;
	
	public Move(String move, Board board) {
		this.move = move;
		this.board = board;
	}

	public Move(String move, Board board, Board parent) {
		this.move = move;
		this.board = board;
		this.parent = parent;
	}
	
	public String getMove() {
		return move;
	}
	public void setMove(String move) {
		this.move = move;
	}
	public Board getBoard() {
		return (board == null ? (board = parent.clone().applyMove(move)) : board);
	}
	public void setBoard(Board board) {
		this.board = board;
	}
	
	public String toString() {
		return move;
	}
}
