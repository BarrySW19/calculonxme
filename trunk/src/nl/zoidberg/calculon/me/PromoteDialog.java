package nl.zoidberg.calculon.me;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import nl.zoidberg.calculon.model.Piece;

public class PromoteDialog implements DialogOverlay {
	
	private BoardCanvas parent;
	private byte color;
	private String move;
	private int selection = 0;
	
	public PromoteDialog(BoardCanvas parent, byte color, String move) {
		this.parent = parent;
		this.color = color;
		this.move = move;
	}

	public void keyPressed(int keyCode) {
		switch(parent.getGameAction(keyCode)) {
		case Canvas.RIGHT:
			selection = (selection < 3 ? selection+1 : selection);
			break;
		case Canvas.LEFT:
			selection = (selection > 0 ? selection-1 : selection);
			break;
		case Canvas.FIRE:
			parent.firePromote(move + "=" + getSelectedPiece());
			break;
		}
		parent.repaint();
	}

	private String getSelectedPiece() {
		switch(selection) {
		case 1:
			return "R";
		case 2:
			return "B";
		case 3:
			return "N";
		default:
			return "Q";
		}
	}

	public void paint(Graphics g) {
		g.translate(parent.getSquareSize()*2, parent.getSquareSize()*3);
		ImageUtils.drawBorder(g, 0, 0, parent.getSquareSize()*4+10, parent.getSquareSize()+10);
		
		g.translate(5, 5);
		int bgColor = color == Piece.WHITE ? 1 : 0;
		g.drawImage(BoardCanvas.getImages()[bgColor][color|Piece.QUEEN], 0*parent.getSquareSize(), 0, Graphics.TOP|Graphics.LEFT);
		g.drawImage(BoardCanvas.getImages()[bgColor][color|Piece.ROOK], 1*parent.getSquareSize(), 0, Graphics.TOP|Graphics.LEFT);
		g.drawImage(BoardCanvas.getImages()[bgColor][color|Piece.BISHOP], 2*parent.getSquareSize(), 0, Graphics.TOP|Graphics.LEFT);
		g.drawImage(BoardCanvas.getImages()[bgColor][color|Piece.KNIGHT], 3*parent.getSquareSize(), 0, Graphics.TOP|Graphics.LEFT);
		
		g.setColor(0, 0, 255);
		g.drawRect(selection*parent.getSquareSize(), 0, parent.getSquareSize()-1, parent.getSquareSize()-1);
		g.drawRect(selection*parent.getSquareSize()+1, 1, parent.getSquareSize()-3, parent.getSquareSize()-3);
	}
}
