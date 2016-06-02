/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import java.util.logging.Level;

/**
 *
 * @author nikok
 */
public class PuzzleTrigger implements Trigger {
    private MapObject targetMob;
    
    public PuzzleTrigger(MapObject mob) {
        this.targetMob = mob;
    }

    @Override
    public String getDescription() {
        String s = "Trigger to toggle "+targetMob.getName();
        return s;
    }
    
    @Override
    public boolean toggle(MapObject toggler) {
        if (targetMob instanceof PuzzleTile) {
            ((PuzzleTile)targetMob).shiftMode();
            return true;
        } else {
            Mists.logger.log(Level.WARNING, "PuzzleTrigger set to manipulate a non-PuzzleTile mob: {0}", targetMob.toString());
            return false;
        }
    }

    @Override
    public PuzzleTrigger createFromTemplate() {
        PuzzleTrigger tt = new PuzzleTrigger(this.targetMob);
        return tt;
    }

    @Override
    public MapObject getTarget() {
        return this.targetMob;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.targetMob = mob;
    }

}
