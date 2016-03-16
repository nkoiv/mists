/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.Location;

/**
 *
 * @author nikok
 */
public class MobGoneRequirement extends PuzzleRequirement {
    Location location;
    MapObject mobToDestroy;
    
    public MobGoneRequirement(MapObject mob, Location location) {
        this.location = location;
        this.mobToDestroy = mob;
    }
    
    @Override
    protected boolean isCompleted() {
        return !location.getMapObject(mobToDestroy.getID()).equals(mobToDestroy);
    }
}
