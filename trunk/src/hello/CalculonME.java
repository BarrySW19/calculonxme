package hello;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import nl.zoidberg.calculon.engine.SearchNode;
import nl.zoidberg.calculon.model.Board;

public class CalculonME extends MIDlet implements CommandListener {

    private Command exitCommand; // The exit command
    private Display display;     // The display for this MIDlet

    public CalculonME() {
        display = Display.getDisplay(this);
        exitCommand = new Command("Exit", Command.EXIT, 0);
    }

    public void startApp() {
        TextBox t = new TextBox("Hello", new SearchNode(new Board().initialise()).getPreferredMove(), 256, 0);

        t.addCommand(exitCommand);
        t.setCommandListener(this);

        display.setCurrent(t);
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
