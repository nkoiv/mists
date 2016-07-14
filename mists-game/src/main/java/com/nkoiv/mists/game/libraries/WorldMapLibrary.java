/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.worldmap.LocationNode;
import com.nkoiv.mists.game.world.worldmap.MapNode;
import com.nkoiv.mists.game.world.worldmap.WorldMap;

import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class WorldMapLibrary {
    
	private HashMap<String, MapNode> nodesByName;
	private HashMap<String, WorldMap> mapsByName;
	private HashMap<Integer, MapNode> nodesByID;
	private HashMap<Integer, WorldMap> mapsByID;
	
	
	public WorldMapLibrary() {
		this.nodesByName = new HashMap<>();
		this.nodesByID = new HashMap<>();
		this.mapsByName = new HashMap<>();
		this.mapsByID = new HashMap<>();
	}
	
	public void addMapNodeTemplate(MapNode mapnode) {
		if (!(mapnode instanceof MapNode)) return;
		String key = mapnode.getName().toLowerCase();
		if (nodesByName.containsKey(mapnode.getName())) Mists.logger.warning("WorldMapLibrary already contains node with name#"+mapnode.getName()+" - overwriting!");
		nodesByName.put(key, mapnode);
		if (nodesByID.containsKey(mapnode.getID())) Mists.logger.warning("WorldMapLibrary already contains node with ID#"+mapnode.getID()+" - overwriting!");
		nodesByID.put(mapnode.getID(), mapnode);
	}
	
	public void addWorldMap(WorldMap worldmap) {
		if (!(worldmap instanceof WorldMap)) return;
		String lowercasename = worldmap.getName().toLowerCase();
		if (mapsByName.containsKey(lowercasename)) Mists.logger.warning("WorldMapLibrary already contains node with name#"+worldmap.getName()+" - overwriting!");
		mapsByName.put(lowercasename, worldmap);
		if (mapsByID.containsKey(worldmap.getID())) Mists.logger.warning("WorldMapLibrary already contains node with ID#"+worldmap.getID()+" - overwriting!");
		mapsByID.put(worldmap.getID(), worldmap);
	}
	
	public MapNode getMapNodeTemplate(String nodeName) {
		String lowercasename = nodeName.toLowerCase();
		return this.nodesByName.get(lowercasename);
	}
	
	public MapNode getMapNodeTemplate(int nodeID) {
		return this.nodesByID.get(nodeID);
	}
	
	public WorldMap getWorldMap(String mapname) {
		String lowercasename = mapname.toLowerCase();
		return this.mapsByName.get(lowercasename);
	}
	
	public WorldMap getWorldMap(int mapID) {
		return this.mapsByID.get(mapID);
	}
	
	public MapNode createMapNode(String nodeName) {
		MapNode n = this.getMapNodeTemplate(nodeName);
		if (n instanceof MapNode) {
			MapNode mn = n.createFromTemplate();
			return mn;
		}
		return null;
	}
	
    public WorldMap generateWorldMapFromYAML(Map worldmapData) {
        WorldMap wm = null;
        //TODO: Generate worldmaps from yaml :)
        return wm;
    }
    
    public static void populateWorldMapWithNodesFromYAML(WorldMap worldmap, String mapnodeYaml) {
        Map<Integer, MapNode> nodes = generateWorldMapNodesFromYAML(mapnodeYaml);
        for (int id : nodes.keySet()) {
            worldmap.addNode(nodes.get(id), nodes.get(id).getID(), nodes.get(id).getXPos(), nodes.get(id).getYPos());
        }   
    }
    
    public static Map<Integer, MapNode> generateWorldMapNodesFromYAML(String mapnodeYaml) {
        Map<Integer, MapNode> nodes = new HashMap<>();
        Map<Integer, Map> nodesYAML = new HashMap<>();
        File libraryYAML = new File(mapnodeYaml);
        try {
            Mists.logger.info("Attempting to read YAML from "+libraryYAML.getCanonicalPath());
            YamlReader reader = new YamlReader(new FileReader(libraryYAML));
            //Generate the nodes
            while (true) {
                Object object = reader.read();
                if (object == null) break;
                try {
                   int nodeID = Integer.parseInt((String)((Map)object).get("id"));
                   MapNode node = generateWorldMapNodeFromYAML((Map)object);
                   nodes.put(nodeID, node);
                   nodesYAML.put(nodeID, (Map)object);
                } catch (Exception e) {
                    Mists.logger.warning("Failed parsing "+object.toString());
                    Mists.logger.warning(e.toString());
                }
            }
            //Link the nodes
            for (int nodeID : nodesYAML.keySet()) {
                generateLinksBetweenNodesFromYAML(nodes, nodesYAML.get(nodeID));
            }

        } catch (Exception e) {
            Mists.logger.warning("Was unable to read structure data!");
            Mists.logger.warning(e.toString());
        }
        
        return nodes;
    }
    
    
    /**
     * Generate a MapNode from YAML,
     * building the base (but not the links) for the node.
     * @param nodesData YAML-format nodeData
     * @return Unlinked MapNode (possibly LocationNode) from the data
     */
    public static MapNode generateWorldMapNodeFromYAML(Map nodesData) {
        MapNode mn;
        String nodeName = (String)nodesData.get("name");
        Image nodeImage;
        int id = Integer.parseInt((String)nodesData.get("id"));
        int xCoordinate = Integer.parseInt((String)nodesData.get("xCoordinate"));
        int yCoordinate = Integer.parseInt((String)nodesData.get("yCoordinate"));
        if (nodesData.keySet().contains("image")) {
            nodeImage = new Image((String)nodesData.get("image"));
        } else {
            nodeImage = null;
        }
        if (nodesData.keySet().contains("enterLocationID")) {
            mn = new LocationNode(nodeName, nodeImage, Integer.parseInt((String)nodesData.get("enterLocationID")));
        } else {
            mn = new MapNode(nodeName, nodeImage);
        }
        mn.setID(id);
        mn.setXPos(xCoordinate);
        mn.setYPos(yCoordinate);
        return mn;
    }
    
    /**
     * Construct the links between worldmap nodes from YAML
     * @param nodes Map of nodes, each keyed to their ID
     * @param nodesData parsed from YAML, containing the "links" map
     */
    public static void generateLinksBetweenNodesFromYAML(Map<Integer, MapNode> nodes, Map nodesData) {
        int nodeID = Integer.parseInt((String)(nodesData.get("id")));
        int link;
        if (nodesData.containsKey("links")) {
            if (((Map)nodesData.get("links")).containsKey("Up")) {
                link = Integer.parseInt((String)((Map)nodesData.get("links")).get("Up"));
                if (nodes.containsKey(link)) {
                    nodes.get(nodeID).setNeighbour(nodes.get(link), Direction.UP);
                }
            }
            if (((Map)nodesData.get("links")).containsKey("UpRight")) {
                link = Integer.parseInt((String)((Map)nodesData.get("links")).get("UpRight"));
                if (nodes.containsKey(link)) {
                    nodes.get(nodeID).setNeighbour(nodes.get(link), Direction.UPRIGHT);
                }
            }
            if (((Map)nodesData.get("links")).containsKey("Right")) {
                link = Integer.parseInt((String)((Map)nodesData.get("links")).get("Right"));
                if (nodes.containsKey(link)) {
                    nodes.get(nodeID).setNeighbour(nodes.get(link), Direction.RIGHT);
                }
            }
            if (((Map)nodesData.get("links")).containsKey("DownRight")) {
                link = Integer.parseInt((String)((Map)nodesData.get("links")).get("DownRight"));
                if (nodes.containsKey(link)) {
                    nodes.get(nodeID).setNeighbour(nodes.get(link), Direction.DOWNRIGHT);
                }
            }
            if (((Map)nodesData.get("links")).containsKey("Down")) {
                link = Integer.parseInt((String)((Map)nodesData.get("links")).get("Down"));
                if (nodes.containsKey(link)) {
                    nodes.get(nodeID).setNeighbour(nodes.get(link), Direction.DOWN);
                }
            }
            if (((Map)nodesData.get("links")).containsKey("DownLeft")) {
                link = Integer.parseInt((String)((Map)nodesData.get("links")).get("DownLeft"));
                if (nodes.containsKey(link)) {
                    nodes.get(nodeID).setNeighbour(nodes.get(link), Direction.DOWNLEFT);
                }
            }
            if (((Map)nodesData.get("links")).containsKey("Left")) {
                link = Integer.parseInt((String)((Map)nodesData.get("links")).get("Left"));
                if (nodes.containsKey(link)) {
                    nodes.get(nodeID).setNeighbour(nodes.get(link), Direction.LEFT);
                }
            }
            if (((Map)nodesData.get("links")).containsKey("UpLeft")) {
                link = Integer.parseInt((String)((Map)nodesData.get("links")).get("UpLeft"));
                if (nodes.containsKey(link)) {
                    nodes.get(nodeID).setNeighbour(nodes.get(link), Direction.UPLEFT);
                }
            }
        }
        
    }
    
}
