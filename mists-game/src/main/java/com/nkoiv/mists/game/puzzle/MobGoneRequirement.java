/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.Location;

/**
 * Monitor a mob (by ID) in a location, and return
 * true if the mob is gone.
 * @author nikok
 */
public class MobGoneRequirement extends PuzzleRequirement {
    private Location location;
    private MapObject mobToDestroy;
    
    public MobGoneRequirement(MapObject mob, Location location) {
        this.location = location;
        this.mobToDestroy = mob;
    }
    
    @Override
    protected boolean isCompleted() {
        //Should return false if the mob isnt found in the instance, or if the mob found doesnt match the one on file
        return !location.getMapObject(mobToDestroy.getID()).equals(mobToDestroy);
    }
}
