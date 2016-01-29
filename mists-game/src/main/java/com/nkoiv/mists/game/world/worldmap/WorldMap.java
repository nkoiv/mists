/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.worldmap;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class WorldMap {
    private String name;
    private Image backgroundImage;
    private ArrayList<MapNode> nodesOnMap;
    private ArrayList<MapObject> mobsOnMap;
    private MapNode playerNode;
    private PlayerCharacter player;
    private double xOffset;
    private double yOffset;
    
    public WorldMap(String name, Image backgroundImage) {
        this.name = name;
        this.backgroundImage = backgroundImage;
        this.nodesOnMap = new ArrayList<>();
        this.mobsOnMap = new ArrayList<>();
    }
    
    public void addNode(MapNode node, double xCoordinate, double yCoordinate) {
        this.nodesOnMap.add(node);
        node.setPosition(xCoordinate, yCoordinate);
    }
    
    public void addMapObject(MapObject mob) {
        this.mobsOnMap.add(mob);
    }
    
    public void setPlayerCharacter(PlayerCharacter player) {
        this.player = player;
        if (playerNode != null) player.setPosition(playerNode.getXPos(), playerNode.getYPos());
        this.mobsOnMap.add(player);
    }
    
    public void render(GraphicsContext gc) {
        gc.drawImage(backgroundImage, xOffset, yOffset);
        for (MapNode mn : this.nodesOnMap) {
            mn.render(gc, xOffset, yOffset);
        }
        for (MapObject mob : this.mobsOnMap) {
            mob.render(xOffset, yOffset, gc);
        }
    }
    
    public void tick(double time) {
        this.player.setPosition(playerNode.getXPos(), playerNode.getYPos());
        this.player.update(time);
        
    }
    
    public MapObject mobAtCoordinates(double xCoor, double yCoor) {
        for (MapObject mob : this.mobsOnMap) {
            boolean b = true;
            if (xCoor < mob.getXPos() || xCoor > (mob.getXPos()+mob.getWidth())) b = false;
            if (yCoor < mob.getYPos() || yCoor > (mob.getYPos()+mob.getHeight())) b = false;
            
            if (b) return mob;
        }
        return null;
    }
    
    public MapNode nodeAtCoordinates (double xCoor, double yCoor) {
        for (MapNode ln : this.nodesOnMap) {
            boolean b = true;
            if (xCoor < ln.getXPos() || xCoor > (ln.getXPos()+ln.getImage().getWidth())) b = false;
            if (yCoor < ln.getYPos() || yCoor > (ln.getYPos()+ln.getImage().getHeight())) b = false;
            
            if (b) return ln;
        }
        return null;
    }
    
    public MapNode getPlayerNode() {
        return this.playerNode;
    }
    
    public void setPlayerNode(MapNode node) {
        this.playerNode = node;
    }
    
    public String getName() {
        return this.name;
    }
    
    /**
     * 
     */
    
    
   
    
   
}
