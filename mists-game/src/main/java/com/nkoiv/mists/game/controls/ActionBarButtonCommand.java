/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.ui.ActionButton;
import com.nkoiv.mists.game.ui.TextButton;
import com.nkoiv.mists.game.ui.TiledWindow;
import com.nkoiv.mists.game.ui.UIComponent;

import javafx.scene.input.KeyCode;

/**
 *
 * @author nikok
 */
public class ActionBarButtonCommand extends KeyBinding {
    private LocationControls loc;
    private int buttonID;
    
    public ActionBarButtonCommand(LocationControls loc, int buttonID, KeyCode[] kc) {
        this(loc, buttonID);
        this.primaryKey = kc;
    }
    
    public ActionBarButtonCommand(LocationControls loc, int buttonID) {
        this.loc = loc;
        this.buttonID = buttonID;
    }
    
    @Override
    public boolean execute() {
        UIComponent actionBar = Mists.MistsGame.currentState.getUIComponent("Actionbar");
        if (actionBar instanceof TiledWindow) {
            TiledWindow ab = (TiledWindow)actionBar;
            if (ab.getSubComponents().get(buttonID) instanceof ActionButton) {
                ((ActionButton)ab.getSubComponents().get(buttonID)).buttonPress();
            } else if (ab.getSubComponents().get(buttonID) instanceof TextButton) {
                ((TextButton)ab.getSubComponents().get(buttonID)).buttonPress();
            }
            return true;
        }
        return false;
    }
}
