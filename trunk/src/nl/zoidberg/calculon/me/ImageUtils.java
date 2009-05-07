package nl.zoidberg.calculon.me;

import javax.microedition.lcdui.Graphics;

public class ImageUtils {

	public static void drawBorder(Graphics g, int x, int y, int w, int h) {
		g.setColor(192, 192, 192);
		g.fillRect(0, 0, w, h);
		g.setColor(128,128,128);
		g.drawRect(0, 0, w-1, h-1);
		g.drawRect(4, 4, w-9, h-9);
		
		
	}
}
