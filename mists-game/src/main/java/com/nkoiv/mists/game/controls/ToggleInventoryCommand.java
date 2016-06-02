/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.controls;

import javafx.scene.input.KeyCode;

/**
 *
 * @author nikok
 */
public class ToggleInventoryCommand extends KeyBinding {
    private LocationControls loc;
    
    public ToggleInventoryCommand(LocationControls loc, KeyCode[] kc) {
        this(loc);
        this.primaryKey = kc;
    }
    
    public ToggleInventoryCommand(LocationControls loc) {
        this.loc = loc;
    }
    
    @Override
    public boolean execute() {
        loc.togglePlayerInventoryPanel();
        return true;
    }
}
