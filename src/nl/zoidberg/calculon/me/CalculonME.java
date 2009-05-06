package nl.zoidberg.calculon.me;

import java.util.Date;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;

public class CalculonME extends MIDlet implements CommandListener {

    private Command exitCommand; // The exit command
    private Display display;     // The display for this MIDlet

    public CalculonME() {
        display = Display.getDisplay(this);
        exitCommand = new Command("Exit", Command.EXIT, 0);
    }

    public void startApp() {
    	String text = new Date(System.currentTimeMillis()).toString() + "\n";
        
        String move = "";//new SearchNode(new Board().initialise()).getPreferredMove();
        text = text + move + "\n";
    	text = text + new Date(System.currentTimeMillis()).toString() + "\n";

    	Canvas c = new BoardCanvas();
    	TextBox t = new TextBox("Hello", text, 256, 0);

        t.addCommand(exitCommand);
        t.setCommandListener(this);

        display.setCurrent(c);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable s) {
        if (c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        } 
    }
}
