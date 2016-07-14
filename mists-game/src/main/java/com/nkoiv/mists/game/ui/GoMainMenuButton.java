/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import java.util.logging.Level;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.LocationState;

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
    public void buttonPress() {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        if (game.currentState instanceof LocationState) {
            ((LocationState)game.currentState).gameMenuOpen = false;
            game.currentState.removeUIComponent("GameMenu");
            ((LocationState)game.currentState).paused = false;
        }
        this.game.moveToState(Game.MAINMENU);
    }
    
}