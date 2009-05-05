package nl.zoidberg.calculon.model;

public class Piece {
	public final static byte EMPTY 	= 0x00;
	
	public final static byte PAWN 	= 0x01;
	public final static byte KNIGHT = 0x02;
	public final static byte BISHOP = 0x03;
	public final static byte ROOK 	= 0x04;
	public final static byte QUEEN 	= 0x05;
	public final static byte KING 	= 0x06;
	
	public final static byte WHITE 	= 0x00;
	public final static byte BLACK	= 0x08;
	
	public final static byte MASK_TYPE	= 0x07;
	public final static byte MASK_COLOR	= 0x08;
}
