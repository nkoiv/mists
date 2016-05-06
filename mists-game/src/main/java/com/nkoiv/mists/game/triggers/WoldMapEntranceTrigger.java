/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.worldmap.MapNode;

/**
 * Toggle to move between WorldMap and Location
 * @author nikok
 */
public class WoldMapEntranceTrigger implements Trigger {
    private WorldMapEntrance entrance;
    private MapNode exitNode;

    public WoldMapEntranceTrigger(WorldMapEntrance entrance, MapNode exitNode) {
        this.entrance = entrance;
        this.exitNode = exitNode;
    }

    public void setEntrance(WorldMapEntrance entrance) {
        this.entrance = entrance;
    }

    public void setExit(MapNode exit) {
        this.exitNode = exit;
    }

    @Override
    public boolean toggle(MapObject toggler) {
        toggler.getLocation().exitLocation(exitNode);
        return true;
    }

    @Override
    public MapObject getTarget() {
        return this.entrance;
    }

    @Override
    public void setTarget(MapObject mob) {
        if(mob instanceof WorldMapEntrance) this.entrance = (WorldMapEntrance)mob;
    }

    @Override
    public String getDescription() {
        return "Map entrance to...";
    }

    @Override
    public WoldMapEntranceTrigger createFromTemplate() {
        WoldMapEntranceTrigger et = new WoldMapEntranceTrigger(this.entrance, this.exitNode);
        return et;
    }

}
