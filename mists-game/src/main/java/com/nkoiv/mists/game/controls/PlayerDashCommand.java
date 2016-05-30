/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;

/**
 *
 * @author nikok
 */
public class PlayerDashCommand extends KeyBinding {
    private LocationControls loc;
    private Direction d;
    
    public PlayerDashCommand(LocationControls loc, Direction d) {
        this.loc = loc;
        this.d = d;
    }
    
    @Override
    public boolean execute() {
        if (Mists.MistsGame.getPlayer().dashOnCooldown()) return false;
        loc.playerDash(d);
        return true;
    }
}
