package nl.zoidberg.calculon.me;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nl.zoidberg.calculon.engine.Board;
import nl.zoidberg.calculon.engine.ChessEngine;
import nl.zoidberg.calculon.engine.MoveGenerator;
import nl.zoidberg.calculon.model.Game;
import nl.zoidberg.calculon.model.Piece;

public class BoardCanvas extends Canvas {
	private static final String RANKS = "12345678";
	private static final String FILES = "ABCDEFGH";
	private static Image[][] images = new Image[2][16];

	static {
		try {
			images[0][Piece.EMPTY] = Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_l44.png"));

			images[0][Piece.WHITE|Piece.PAWN]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_pll44.png"));
			images[0][Piece.WHITE|Piece.ROOK]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_rll44.png"));
			images[0][Piece.WHITE|Piece.KNIGHT]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_nll44.png"));
			images[0][Piece.WHITE|Piece.BISHOP]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_bll44.png"));
			images[0][Piece.WHITE|Piece.QUEEN]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_qll44.png"));
			images[0][Piece.WHITE|Piece.KING]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_kll44.png"));
			
			images[0][Piece.BLACK|Piece.PAWN]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_pdl44.png"));
			images[0][Piece.BLACK|Piece.ROOK]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_rdl44.png"));
			images[0][Piece.BLACK|Piece.KNIGHT]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_ndl44.png"));
			images[0][Piece.BLACK|Piece.BISHOP]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_bdl44.png"));
			images[0][Piece.BLACK|Piece.QUEEN]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_qdl44.png"));
			images[0][Piece.BLACK|Piece.KING]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_kdl44.png"));

			images[1][Piece.EMPTY] = Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_d44.png"));

			images[1][Piece.WHITE|Piece.PAWN]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_pld44.png"));
			images[1][Piece.WHITE|Piece.ROOK]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_rld44.png"));
			images[1][Piece.WHITE|Piece.KNIGHT]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_nld44.png"));
			images[1][Piece.WHITE|Piece.BISHOP]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_bld44.png"));
			images[1][Piece.WHITE|Piece.QUEEN]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_qld44.png"));
			images[1][Piece.WHITE|Piece.KING]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_kld44.png"));
			
			images[1][Piece.BLACK|Piece.PAWN]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_pdd44.png"));
			images[1][Piece.BLACK|Piece.ROOK]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_rdd44.png"));
			images[1][Piece.BLACK|Piece.KNIGHT]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_ndd44.png"));
			images[1][Piece.BLACK|Piece.BISHOP]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_bdd44.png"));
			images[1][Piece.BLACK|Piece.QUEEN]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_qdd44.png"));
			images[1][Piece.BLACK|Piece.KING]	= Image.createImage(BoardCanvas.class.getResourceAsStream("/img/24px-Chess_kdd44.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Board currentBoard;
	private Hashtable currentMoves;
	private boolean flipped = false;
	private int posX = 0, posY = 0;
	private int fireX = -1, fireY = -1;
	private DialogOverlay currentOverlay = null;
	private int squareSize = 24;
	private String lastMove = null;
	
	public static Image[][] getImages() {
		return images;
	}

	public BoardCanvas() {
		currentBoard = new Board().initialise();
//		FENUtils.loadPosition("1rbq2r1/3pkpp1/2n1p2p/1N1n4/1p1P3N/3Q2P1/1PP2PBP/R3R1K1 w - - 1 16", currentBoard);
//		FENUtils.loadPosition("7k/PP6/8/8/8/1r4R1/r5PP/7K w - - 1 1", currentBoard);
		fireBoardChanged();
	}
	
	public int getSquareSize() {
		return squareSize;
	}

	private void fireBoardChanged() {
		currentMoves = MoveGenerator.getPossibleMoves(currentBoard);
		for(Enumeration e = currentMoves.keys(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			Vector v = (Vector) currentMoves.get(key);
			for(int i = 0; i < v.size(); i++) {
				String toMove = (String) v.elementAt(i);
				if(toMove.indexOf('=') >= 0) {
					String newToMove = toMove.substring(0, toMove.indexOf('='));
					if( ! v.contains(newToMove)) {
						v.addElement(newToMove);
					}
					v.removeElement(toMove);
				}
			}
		}
	}

	protected void paint(Graphics g) {
		g.setColor(0,0,0);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		ImageUtils.drawBorder(g, 0, 0, 8*squareSize+10, 8*squareSize+10);
		g.translate(5, 5);
		
		for(int file = 0; file < 8; file++) {
			for(int rank = 0; rank < 8; rank++) {
				boolean isWhite = (file+rank)%2 == 1;
				Image image = images[isWhite?0:1][currentBoard.getPiece(file, rank)];
				g.drawImage(image, (flipped ? 7-file:file)*squareSize, (flipped ? rank : 7-rank)*squareSize, Graphics.TOP|Graphics.LEFT);
			}
		}
		
		if(lastMove != null) {
			g.setColor(0, 0, 255);
			if(flipped) {
				g.drawRect(squareSize * (7 - FILES.indexOf(lastMove.charAt(0))),
						squareSize * RANKS.indexOf(lastMove.charAt(1)), squareSize-1, squareSize-1);
				g.drawRect(squareSize * (7 - FILES.indexOf(lastMove.charAt(2))),
						squareSize * RANKS.indexOf(lastMove.charAt(3)), squareSize-1, squareSize-1);
			} else {
				g.drawRect(squareSize * FILES.indexOf(lastMove.charAt(0)),
						squareSize * (7 - RANKS.indexOf(lastMove.charAt(1))), squareSize-1, squareSize-1);
				g.drawRect(squareSize * FILES.indexOf(lastMove.charAt(2)),
						squareSize * (7 - RANKS.indexOf(lastMove.charAt(3))), squareSize-1, squareSize-1);
			}
		}
		
		if(fireX != -1) {
			if(currentBoard.getPlayer() == Piece.WHITE) {
				g.setColor(255, 255, 255);
			} else {
				g.setColor(0, 0, 0);
			}
			drawTarget(g, fireX, 7-fireY, true);
		}
		
		if(fireX != posX || fireY != posY) {
			g.setColor(0, 0, 255);
			boolean isClickable = false;
			if((fireX == -1 && isFromTarget()) || (fireX != -1 && isToTarget())) {
				isClickable = true;
			}
			drawTarget(g, posX, 7-posY, isClickable);
		}

		if(currentOverlay != null) {
			currentOverlay.paint(g);
		}
	}

	private void drawTarget(Graphics g, int x, int y, boolean thick) {
		x *= squareSize;
		y *= squareSize;
		int sz = squareSize/4;
		int farX = x+squareSize-1;
		int farY = y+squareSize-1;
		
		g.drawLine(x, y, x+sz-1, y);
		g.drawLine(x, y, x, y+sz-1);
		
		g.drawLine(farX, y, farX-sz+1, y);
		g.drawLine(farX, y, farX, y+sz-1);

		g.drawLine(x, farY, x+sz-1, farY);
		g.drawLine(x, farY, x, farY-sz+1);

		g.drawLine(farX, farY, farX-sz+1, farY);
		g.drawLine(farX, farY, farX, farY-sz+1);
		
		if(thick) {
			g.drawLine(x, y+1, x+sz-1, y+1);
			g.drawLine(x+1, y, x+1, y+sz-1);
			g.drawLine(farX, y+1, farX-sz+1, y+1);
			g.drawLine(farX-1, y, farX-1, y+sz-1);
			g.drawLine(x, farY-1, x+sz-1, farY-1);
			g.drawLine(x+1, farY, x+1, farY-sz+1);
			g.drawLine(farX, farY-1, farX-sz+1, farY-1);
			g.drawLine(farX-1, farY, farX-1, farY-sz+1);
		}
	}

	protected void keyPressed(int keyCode) {
		if(currentOverlay != null) {
			currentOverlay.keyPressed(keyCode);
			return;
		}
		
		if(keyCode == '1') {
			flipped = ! flipped;
			fireX = -1;
			fireY = -1;
			repaint();
			return;
		}
		
		if(keyCode == '2') {
			if(Game.RES_NO_RESULT.equals(currentBoard.getResult())) {
				currentMoves.clear();
				new Thread(new MoveCommand()).start();
				repaint();
			}
			return;
		}
		
		if(keyCode == '3') {
			reset();
			repaint();
		}
		
		switch(getGameAction(keyCode)) {
		case RIGHT:
			posX = (posX < 7 ? posX+1 : posX);
			break;
		case LEFT:
			posX = (posX > 0 ? posX-1 : posX);
			break;
		case UP:
			posY = (posY < 7 ? posY+1 : posY);
			break;
		case DOWN:
			posY = (posY > 0 ? posY-1 : posY);
			break;
		case FIRE:
			doFireKey();
		}
		
		repaint();
	}
	
	private boolean isToTarget() {
		if(fireX == -1) {
			return false;
		}
		int posRank = (flipped ? 7-posY : posY);
		int posFile = (flipped ? 7-posX : posX);
		int fireRank = (flipped ? 7-fireY : fireY);
		int fireFile = (flipped ? 7-fireX : fireX);
		String square = String.valueOf(FILES.charAt(posFile)) + String.valueOf(RANKS.charAt(posRank));
		String fireSq = String.valueOf(FILES.charAt(fireFile)) + String.valueOf(RANKS.charAt(fireRank));
		Vector v = (Vector) currentMoves.get(fireSq);
		return v.contains(square);
	}
	
	private boolean isFromTarget() {
		int posRank = (flipped ? 7-posY : posY);
		int posFile = (flipped ? 7-posX : posX);
		String square = String.valueOf(FILES.charAt(posFile)) + String.valueOf(RANKS.charAt(posRank));

		return currentMoves.get(square) != null;
	}

	private void doFireKey() {
		if(fireX == posX && fireY == posY) {
			fireX = -1;
			fireY = -1;
		} else if(isFromTarget()) {
			fireX = posX;
			fireY = posY;
		} else if(isToTarget()) {
			playCurrentMove();
		}
	}
	
	private void playCurrentMove() {
		int posRank = (flipped ? 7-posY : posY);
		int posFile = (flipped ? 7-posX : posX);
		int fireRank = (flipped ? 7-fireY : fireY);
		int fireFile = (flipped ? 7-fireX : fireX);
		String square = String.valueOf(FILES.charAt(posFile)) + String.valueOf(RANKS.charAt(posRank));
		String fireSq = String.valueOf(FILES.charAt(fireFile)) + String.valueOf(RANKS.charAt(fireRank));
		String move = fireSq + square;
		
		if((Piece.MASK_TYPE & currentBoard.getPiece(fireFile, fireRank)) == Piece.PAWN && (posRank == 0 || posRank == 7)) {
			currentOverlay = new PromoteDialog(
					this, (byte) (Piece.MASK_COLOR & currentBoard.getPiece(fireFile, fireRank)), move);
			repaint();
		} else {
			applyMove(move, true);
		}
	}
	
	private void applyMove(String move, boolean respond) {
		lastMove = move;
		if(lastMove.length() > 4) {
			lastMove = lastMove.substring(0, 4);
		}
		if("O-O".equals(lastMove)) {
			lastMove = currentBoard.getPlayer() == Piece.WHITE ? "E1G1" : "E8G8";
		}
		if("O-O-O".equals(lastMove)) {
			lastMove = currentBoard.getPlayer() == Piece.BLACK ? "E1D1" : "E8D8";
		}
		
		currentMoves.clear();
		currentBoard.applyMove(move);
		fireX = -1;
		fireY = -1;
		if( ! Game.RES_NO_RESULT.equals(currentBoard.getResult())) {
			currentOverlay = new ResultDialog(this, currentBoard.getResult());
			repaint();
			return;
		}
		if(respond) {
			new Thread(new MoveCommand()).start();
		} else {
			fireBoardChanged();
		}
		repaint();
	}

	private class MoveCommand implements Runnable {
		public void run() {
			applyMove(new ChessEngine().getPreferredMove(currentBoard), false);
		}
	}

	public void firePromote(String move) {
		currentOverlay = null;
		applyMove(move, true);
	}

	public void reset() {
		currentOverlay = null;
		lastMove = null;
		currentBoard.initialise();
		fireBoardChanged();
	}

	public void closeDialog() {
		currentOverlay = null;
	}
}
