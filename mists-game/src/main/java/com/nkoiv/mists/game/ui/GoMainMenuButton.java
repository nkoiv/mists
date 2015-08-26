/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author nikok
 */
public class GoMainMenuButton extends TextButton {
    private Game game;
    
    public GoMainMenuButton (Game game, double width, double height) {
        this(game, width, height, 0, 0);
    }
    
    public GoMainMenuButton(Game game, double width, double height, double xPosition, double yPosition) {
        super("Main menu", width, height, xPosition, yPosition);
        this.game = game;
    }
    
    @Override
    public void onClick(MouseEvent me) {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        this.game.moveToState(Game.MAINMENU);
    }
    
}