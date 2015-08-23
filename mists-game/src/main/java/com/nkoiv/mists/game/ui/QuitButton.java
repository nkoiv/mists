/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

/**
 *
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
    public void onClick(MouseEvent me) {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        Platform.exit();
        System.exit(0);
    }
    
}
