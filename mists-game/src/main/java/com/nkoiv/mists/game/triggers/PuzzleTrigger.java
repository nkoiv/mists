/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
            ((PuzzleTile)targetMob).toggleLit();
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
