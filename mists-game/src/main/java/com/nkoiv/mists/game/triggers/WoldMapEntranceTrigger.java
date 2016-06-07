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
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.worldmap.MapNode;

/**
 * Toggle to move between WorldMap and Location
 * @author nikok
 */
public class WoldMapEntranceTrigger implements Trigger {
	private int entranceID;
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
    
    private void updateEntrance(Location loc) {
    	MapObject mob = loc.getMapObject(entranceID);
    	if (mob instanceof WorldMapEntrance) this.entrance = (WorldMapEntrance)mob;
    }

    @Override
    public boolean toggle(MapObject toggler) {
    	if (entrance == null) updateEntrance(toggler.getLocation());
        toggler.getLocation().exitLocationToWorldMap(exitNode);
        return true;
    }

    @Override
    public MapObject getTarget() {
    	if (entrance == null) updateEntrance(Mists.MistsGame.getCurrentLocation());
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
