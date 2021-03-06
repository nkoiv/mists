/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.worldmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Worldmap is a background image with a bunch of
 * Nodes that player can move in.
 * @author nikok
 */
public class WorldMap implements KryoSerializable {
	private int mapID;  
    private String name;
    private String bgImageName;
    private Image backgroundImage;
    private HashMap<Integer, MapNode> nodesOnMap;
    private ArrayList<MapObject> mobsOnMap;
    private MapNode playerNode;
    private PlayerCharacter player;
    private double lastOffsets[];
    private int nextFreeNodeID = 0;
    
    public WorldMap() {
    	this.nodesOnMap = new HashMap<>();
        this.mobsOnMap = new ArrayList<>();
        lastOffsets = new double[2];
    }
    
    
    public WorldMap(String name, String bgImageName) {
    	this.name = name;
    	this.bgImageName = bgImageName;
    	this.backgroundImage = Mists.graphLibrary.getImage(bgImageName);
    	this.nodesOnMap = new HashMap<>();
        this.mobsOnMap = new ArrayList<>();
        lastOffsets = new double[2];
    }

    /**
     * Recursively search for a free nodeID
     * @param startID identifier to start the search from (going upwards)
     * @return ID that's not in use by any of the current Nodes On Map
     */
    private int getFreeNodeID() {
    	//Note: In theory we can overflow integer here...
    	int id = nextFreeNodeID;
    	boolean idTaken = false;
    	for (Integer mnID : this.nodesOnMap.keySet()) {
    		if (id == mnID) {
    			idTaken = true;
    			break;
    		}
    	}
    	if (!idTaken) return id;
    	else {
    		nextFreeNodeID++;
    		return getFreeNodeID();
    	}
    }
    
    public void addNode(MapNode node, int nodeID, double xCoordinate, double yCoordinate) {
    	node.setID(nodeID);
    	this.nodesOnMap.put(nodeID, node);
        node.setPosition(xCoordinate, yCoordinate);
    }
    
    public void addNode(MapNode node, double xCoordinate, double yCoordinate) {
    	int idForNode = getFreeNodeID();
    	
    	this.addNode(node, idForNode, xCoordinate, yCoordinate);
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
        if (this.backgroundImage!=null) gc.drawImage(backgroundImage, -xOffset, -yOffset);
        for (MapNode mn : this.nodesOnMap.values()) {
            mn.render(gc, xOffset, yOffset);
        }
        renderLinks(gc, xOffset, yOffset);
        for (MapObject mob : this.mobsOnMap) {
            mob.render(xOffset, yOffset, gc);
        }
    }
    
    private void renderLinks(GraphicsContext gc, double xOffset, double yOffset) {
    	ArrayList<MapNode> handledNodes = new ArrayList<>();
    	for (MapNode mn : this.nodesOnMap.values()) {
    		renderLinks(mn, handledNodes, gc, xOffset, yOffset);
    		handledNodes.add(mn);
    	}
    }
    
    private void renderLinks(MapNode node, ArrayList<MapNode> ignoredLinks, GraphicsContext gc, double xOffset, double yOffset) {

    	for (Integer neighbourID : node.getNeighboursAsAList()) {
    		MapNode neighbour = this.nodesOnMap.get(neighbourID);
    		if (neighbour == null) continue;
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
    
    public MapNode getNode(int nodeID) {
    	if (nodeID == -1) return null;
    	return nodesOnMap.get(nodeID);
    }
    
    public MapNode getNode(String nodeName) {
    	for (MapNode n : nodesOnMap.values()) {
    		if (nodeName.equals(n.name)) return n;
    	}
    	return null;
    }
    
    public MapNode getNodeAtCoordinates(double xCoor, double yCoor) {
        //TODO: Results nullpointer on mapnodes without image. Should we target the circle rather?
        for (MapNode ln : this.nodesOnMap.values()) {
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
	
    public Set<Integer> getNodeIDs() {
        return this.nodesOnMap.keySet();
    }
    
    public ArrayList<MapNode> getNodes() {
    	ArrayList<MapNode> nodes = new ArrayList<>();
    	for (int key : this.nodesOnMap.keySet()) {
    		nodes.add(nodesOnMap.get(key));
    	}
    	return nodes;
    }
    
    public MapNode getPlayerNode() {
        if (this.playerNode == null) this.playerNode = this.nodesOnMap.get(0);
        return this.playerNode;
    }
    
    public void setPlayerNode(String nodeName) {
        for (MapNode node : this.nodesOnMap.values()) {
            if (node.name.equals(nodeName)) this.playerNode = node;
        }
    }
    
    public void setPlayerNode(int nodeID) {
    	this.playerNode = nodesOnMap.get(nodeID);
    }
    
    public void setPlayerNode(MapNode node) {
        this.playerNode = node;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setID(int mapID) {
    	this.mapID = mapID;
    }
    
    public int getID() {
    	return this.mapID;
    }

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeString(this.name);
		output.writeInt(this.mapID);
		output.writeString(this.bgImageName);
		output.writeInt(nodesOnMap.keySet().size());
		for (int id : nodesOnMap.keySet()) {
			kryo.writeClassAndObject(output, nodesOnMap.get(id));
		}
		int playerNodeID = -1;
		if (playerNode instanceof MapNode) playerNodeID = playerNode.getID();
		output.writeInt(playerNodeID); 
		output.writeDouble(lastOffsets[0]);
		output.writeDouble(lastOffsets[1]);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.name = input.readString();
		this.mapID = input.readInt();
		this.bgImageName = input.readString();
		int nodeCount = input.readInt();
		for (int i = 0; i < nodeCount; i++) {
			MapNode mn = (MapNode)kryo.readClassAndObject(input);
			this.nodesOnMap.put(mn.getID(), mn);
		}
		this.setPlayerNode(input.readInt());
		this.lastOffsets = new double[2];
		this.lastOffsets[0] = input.readDouble();
		this.lastOffsets[1] = input.readDouble();
		loadGraphics();
	}
	
	private void loadGraphics() {
		Image wmi = Mists.graphLibrary.getImage(bgImageName);
		if (wmi!=null) this.backgroundImage = wmi;
		else Mists.logger.warning("Could not load backgroundimage for WorldMap "+name+" #"+mapID+ " - missing from graphics library!");
	}
    

   
}
