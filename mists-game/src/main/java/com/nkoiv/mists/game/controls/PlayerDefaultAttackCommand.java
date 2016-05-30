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
public class PlayerDefaultAttackCommand extends KeyBinding {
    private LocationControls loc;
    
    public PlayerDefaultAttackCommand(LocationControls loc, KeyCode[] primaryBind) {
        this(loc);
        this.primaryKey = primaryBind;
    }
    
    public PlayerDefaultAttackCommand(LocationControls loc) {
        this.loc = loc;
    }
    
    @Override
    public boolean execute() {
        loc.playerAttack();
        return true;
    }
}
