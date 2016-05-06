/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.WoldMapEntranceTrigger;
import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.world.worldmap.MapNode;

/**
 * MapEntrance serves to move players from WorldMap
 * to Location and vice versa. Each MapEntrance is linked
 * to a WorldMap Node, while being a MapObject inside a Location.
 * @author nikok
 */
public class WorldMapEntrance extends Structure {
    private MapNode exitNode;
    
    public WorldMapEntrance(String name, MovingGraphics graphics, int collisionLevel, MapNode exitNode) {
        super(name, graphics, collisionLevel);
        this.exitNode = exitNode;
    }
    
            
    public MapNode getExitNode() {
        return this.exitNode;
    }
    
    public void setExitNode(MapNode exitNode) {
        this.exitNode = exitNode;
    }
    
    @Override
    public Trigger[] getTriggers() {
        if (this.exitNode == null) return new Trigger[1];
        Trigger[] a = new Trigger[]{new WoldMapEntranceTrigger(this, this.exitNode)};
        return a;
    }
    
    @Override
    public String[] getInfoText() {
        String exitNodeName;
        if (this.exitNode != null) exitNodeName = this.exitNode.getName();
        else exitNodeName = "Unspecified";
        String[] s = new String[]{
            this.name,
            "ID "+this.IDinLocation+" @ "+this.location.getName(),
            "X:"+((int)this.getXPos())+" Y:"+((int)this.getYPos()),
            "Exit node: "+exitNodeName};
        return s;
    }
    
    @Override
    public WorldMapEntrance createFromTemplate() {
        WorldMapEntrance ne = new WorldMapEntrance(this.name, this.getGraphics(), 0, this.exitNode);
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getXPos();
                double yOffset = s.getYPos() - this.getYPos();
                ne.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return ne;
    }
    
}
