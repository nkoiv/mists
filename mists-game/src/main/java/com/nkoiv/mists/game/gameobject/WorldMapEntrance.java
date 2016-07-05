/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.triggers.WorldMapEntranceTrigger;
import com.nkoiv.mists.game.world.worldmap.MapNode;

/**
 * MapEntrance serves to move players from WorldMap
 * to Location and vice versa. Each MapEntrance is linked
 * to a WorldMap Node, while being a MapObject inside a Location.
 * @author nikok
 */
public class WorldMapEntrance extends Structure {
	private int graphicsTemplateID; 
    private MapNode exitNode;
    private int exitNodeID;
    
    
    public WorldMapEntrance() {
        super();
    }
    
    public WorldMapEntrance(String name, int graphicsTemplateID, int collisionLevel, MapNode exitNode) {
        this(name, Mists.structureLibrary.create(graphicsTemplateID).graphics, collisionLevel, exitNode);
        this.graphicsTemplateID = graphicsTemplateID;
    }
    
    public WorldMapEntrance(String name, MovingGraphics graphics, int collisionLevel, MapNode exitNode) {
        super(name, graphics, collisionLevel);
        this.exitNode = exitNode;
    }
    
            
    public MapNode getExitNode() {
    	if (this.exitNode == null) this.exitNode = Mists.MistsGame.getCurrentWorldMap().getNodeWithID(exitNodeID);
        return this.exitNode;
    }
    
    public void setExitNode(MapNode exitNode) {
        this.exitNode = exitNode;
        this.exitNodeID = exitNode.getID();
    }
    
    public void setGraphicsTemplateID(int id) {
    	this.graphicsTemplateID = id;
    }
    
    @Override
    public Trigger[] getTriggers() {
    	if (this.exitNode == null) this.exitNode = Mists.MistsGame.getCurrentWorldMap().getNodeWithID(exitNodeID);
        if (this.exitNode == null) return new Trigger[1];
        Trigger[] a = new Trigger[]{new WorldMapEntranceTrigger(this, this.exitNode)};
        return a;
    }
    
    @Override
    public String[] getInfoText() {
    	if (this.exitNode == null) this.exitNode = Mists.MistsGame.getCurrentWorldMap().getNodeWithID(exitNodeID);
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
        ne.setPosition(0, 0);
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getXPos();
                double yOffset = s.getYPos() - this.getYPos();
                ne.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return ne;
    }
    
    @Override
    public void write(Kryo kryo, Output output) {
            super.write(kryo, output);
            output.writeInt(exitNodeID);
            output.writeInt(graphicsTemplateID);
    }


    @Override
    public void read(Kryo kryo, Input input) {
            super.read(kryo, input);
            this.exitNodeID = input.readInt();
            this.graphicsTemplateID = input.readInt();
            if (Mists.structureLibrary != null) {
                Structure dummy = Mists.structureLibrary.create(graphicsTemplateID);
                if (dummy == null) return;
                dummy.setPosition(this.getXPos(), this.getYPos());
                this.graphics = dummy.graphics;
                this.extraSprites = dummy.extraSprites;
            }
    }
    

}
