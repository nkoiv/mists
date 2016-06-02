/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

/**
 * QuitButton extends the TextButton, and simply calls
 * Platform.exit() and System.exit(0) when clicked, closing the game.
 * @author nikok
 */
public class QuitButton extends TextButton {
    
    public QuitButton (String name, double width, double height) {
        this(name, width, height, 0, 0);
    }
    
    public QuitButton(String name, double width, double height, double xPosition, double yPosition) {
        super(name, width, height, xPosition, yPosition);
    }
    
    @Override
    public void handleMouseEvent(MouseEvent me) {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        Platform.exit();
        //System.exit(0);
    }
    
}
