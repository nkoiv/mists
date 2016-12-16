/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Direction;

import javafx.scene.input.KeyCode;

/**
 *
 * @author nikok
 */
public class PlayerMoveTowardsCommand extends KeyBinding {
    private LocationControls loc;
    private Direction d;
    
    public PlayerMoveTowardsCommand(LocationControls loc, Direction d, KeyCode[] kc) {
        this(loc, d);
        this.primaryKey = kc;
    }
    
    public PlayerMoveTowardsCommand(LocationControls loc, Direction d) {
        this.loc = loc;
        this.d = d;
        this.enableExecuteOnKeyDown = true;
    }
    
    public Direction getDirection() {
        return this.d;
    }
    
    @Override
    public boolean execute() {
        loc.playerMove(d);
        return true;
    }
    
}
