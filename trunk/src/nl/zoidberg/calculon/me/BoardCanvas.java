package nl.zoidberg.calculon.me;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import nl.zoidberg.calculon.engine.MoveGenerator;
import nl.zoidberg.calculon.engine.SearchNode;
import nl.zoidberg.calculon.model.Board;
import nl.zoidberg.calculon.model.Game;
import nl.zoidberg.calculon.model.Piece;
import nl.zoidberg.calculon.notation.FENUtils;

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
	
	public static Image[][] getImages() {
		return images;
	}

	public BoardCanvas() {
		currentBoard = new Board().initialise();
//		FENUtils.loadPosition("1rbq2r1/3pkpp1/2n1p2p/1N1n4/1p1P3N/3Q2P1/1PP2PBP/R3R1K1 b - - 1 16", currentBoard);
		FENUtils.loadPosition("7k/PP6/8/8/8/1r4R1/r5PP/7K w - - 1 1", currentBoard);
		fireBoardChanged();
	}
	
	public int getSquareSize() {
		return squareSize;
	}

	private void fireBoardChanged() {
		currentMoves = MoveGenerator.get().getPossibleMoves(currentBoard);
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
		
		if(fireX != -1) {
			g.setColor(255, 0, 0);
			g.drawRect(fireX * squareSize, (7-fireY)*squareSize, 23, 23);
			g.drawRect(fireX*squareSize + 1, (7-fireY)*squareSize + 1, 21, 21);
		}
		
		if(fireX != posX || fireY != posY) {
			g.setColor(0, 0, 255);
			if((fireX == -1 && isFromTarget()) || (fireX != -1 && isToTarget())) {
				g.setColor(0, 192, 0);
			}
			g.drawRect(posX * squareSize, (7-posY)*squareSize, 23, 23);
			g.drawRect(posX*squareSize + 1, (7-posY)*squareSize + 1, 21, 21);
		}

		if(currentOverlay != null) {
			currentOverlay.paint(g);
		}
	}

	protected void keyPressed(int keyCode) {
		if(currentOverlay != null) {
			currentOverlay.keyPressed(keyCode);
			return;
		}
		
		if(keyCode == '1') {
			flipped = ! flipped;
			repaint();
			return;
		}
		
		if(keyCode == '2') {
			currentMoves.clear();
			new Thread(new MoveCommand()).start();
			repaint();
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
			applyMove(new SearchNode(currentBoard).getPreferredMove(), false);
		}
	}

	public void firePromote(String move) {
		currentOverlay = null;
		applyMove(move, true);
	}

	public void reset() {
		currentOverlay = null;
		currentBoard.initialise();
		fireBoardChanged();
	}
}
