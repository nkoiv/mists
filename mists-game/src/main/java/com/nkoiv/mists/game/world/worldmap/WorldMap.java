/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.worldmap;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Worldmap is a background image with a bunch of
 * Nodes that player can move in.
 * @author nikok
 */
public class WorldMap {
    private String name;
    private Image backgroundImage;
    private ArrayList<MapNode> nodesOnMap;
    private ArrayList<MapObject> mobsOnMap;
    private MapNode playerNode;
    private PlayerCharacter player;
    private double lastOffsets[];
    
    public WorldMap(String name, Image backgroundImage) {
        this.name = name;
        this.backgroundImage = backgroundImage;
        this.nodesOnMap = new ArrayList<>();
        this.mobsOnMap = new ArrayList<>();
        lastOffsets = new double[2];
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
        double xOffset = this.getxOffset(gc, playerNode.getXPos());
        double yOffset = this.getyOffset(gc, playerNode.getYPos());
        gc.drawImage(backgroundImage, -xOffset, -yOffset);
        for (MapNode mn : this.nodesOnMap) {
            mn.render(gc, xOffset, yOffset);
        }
        renderLinks(gc, xOffset, yOffset);
        for (MapObject mob : this.mobsOnMap) {
            mob.render(xOffset, yOffset, gc);
        }
    }
    
    private void renderLinks(GraphicsContext gc, double xOffset, double yOffset) {
    	ArrayList<MapNode> handledNodes = new ArrayList<>();
    	for (MapNode mn : this.nodesOnMap) {
    		renderLinks(mn, handledNodes, gc, xOffset, yOffset);
    		handledNodes.add(mn);
    	}
    }
    
    private void renderLinks(MapNode node, ArrayList<MapNode> ignoredLinks, GraphicsContext gc, double xOffset, double yOffset) {

    	for (MapNode neighbour : node.getNeighboursAsAList()) {
    		//if (ignoredLinks.contains(neighbour)) continue; //Dont draw paths to ignored nodes
    		gc.setFill(Color.RED);
    		gc.fillPolygon(new double[]{node.getCenterXPos()-xOffset, node.getCenterXPos()+3-xOffset,  neighbour.getCenterXPos()-xOffset, neighbour.getCenterXPos()+3-xOffset}, new double[]{node.getCenterYPos()-yOffset, node.getCenterYPos()+3-yOffset,  neighbour.getCenterYPos()-yOffset, neighbour.getCenterYPos()+3-yOffset}, 4 );
    		//gc.strokeLine(node.getXPos(), node.getYPos(), neighbour.getXPos(), neighbour.getYPos());
    	}
    }
    
    public void tick(double time) {
        if (playerNode.bigNode) this.player.setPosition(playerNode.getXPos()+16, playerNode.getYPos()+16);
        else this.player.setPosition(playerNode.getXPos(), playerNode.getYPos());
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
        //TODO: Results nullpointer on mapnodes without image. Should we target the circle rather?
        for (MapNode ln : this.nodesOnMap) {
            boolean b = true;
            if (xCoor < ln.getXPos() || xCoor > (ln.getXPos()+ln.getSize())) b = false;
            if (yCoor < ln.getYPos() || yCoor > (ln.getYPos()+ln.getSize())) b = false;
            if (b) return ln;
        }
        return null;
    }
    
    /**
     * xOffset is calculated from the position of the target in
     * regards to the current window width. If the target would be
     * outside viewable area, it's given offset to keep it inside the bounds
     * @param gc GraphicsContext for window bounds
     * @param xPos the xCoordinate of the target we're following
     * @return xOffset for the current screen position
     */
    public double getxOffset(GraphicsContext gc, double xPos){
        double windowWidth = gc.getCanvas().getWidth();
        windowWidth = windowWidth / Mists.graphicScale;
	//Calculate Offset to ensure Player is centered on the screen
        double xOffset = xPos - (windowWidth / 2);
        //Prevent leaving the screen
        if (xOffset < 0) {
            xOffset = 0;
        } else if (xOffset > backgroundImage.getWidth() -(windowWidth)) {
            xOffset = backgroundImage.getWidth() - (windowWidth);
        }
        this.lastOffsets[0] = xOffset;
        return xOffset;
	}

     /**
     * yOffset is calculated from the position of the target in
     * regards to the current window width. If the target would be
     * outside viewable area, it's given offset to keep it inside the bounds
     * @param gc GraphicsContext for window bounds
     * @param yPos the yCoordinate of the target we're following
     * @return yOffset for the current screen position
     */
    public double getyOffset(GraphicsContext gc, double yPos){
        double windowHeight = gc.getCanvas().getHeight();
        windowHeight = windowHeight / Mists.graphicScale;
        //Calculate Offset to ensure Player is centered on the screen
        double yOffset = yPos - (windowHeight / 2);
        //Prevent leaving the screen
        if (yOffset < 0) {
            yOffset = 0;
        } else if (yOffset > backgroundImage.getHeight() -(windowHeight)) {
            yOffset = backgroundImage.getHeight() - (windowHeight);
        }
        this.lastOffsets[1] = yOffset;
        return yOffset;
    }
    
    /**
     * Get the last calculated x and y offsets, most likely
     * in sync with what's on screen.  
     * @return {xOffset, yOffset}
     */
    public double[] getLastOffsets() {
            return this.lastOffsets;
    }
	
    public List<MapNode> getNodes() {
        return this.nodesOnMap;
    }
    
    public MapNode getPlayerNode() {
        if (this.playerNode == null) this.playerNode = this.nodesOnMap.get(0);
        return this.playerNode;
    }
    
    public void setPlayerNode(String nodeName) {
        for (MapNode node : this.nodesOnMap) {
            if (node.name.equals(nodeName)) this.playerNode = node;
        }
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
