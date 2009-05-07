package nl.zoidberg.calculon.me;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class ResultDialog implements DialogOverlay {
	
	private BoardCanvas parent;
	private String message;
	
	public ResultDialog(BoardCanvas parent, String message) {
		this.parent = parent;
		this.message = "    " + message + "    ";
	}
	
	public void keyPressed(int keyCode) {
		switch(parent.getGameAction(keyCode)) {
		case Canvas.FIRE:
			parent.reset();
			break;
		}
		parent.repaint();
	}

	public void paint(Graphics g) {
		Font font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
		g.setFont(font);
		int size = font.stringWidth(message);
		g.translate(parent.getSquareSize()*4-size/2-5, parent.getSquareSize()*4-font.getHeight()/2-5);
		
		ImageUtils.drawBorder(g, 0, 0, size+10, font.getHeight()+16);
		g.translate(5, 5);
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, size, font.getHeight()+6);
		g.setColor(255, 255, 255);
		g.drawString(message, 0, 3, Graphics.TOP|Graphics.LEFT);
	}
}
