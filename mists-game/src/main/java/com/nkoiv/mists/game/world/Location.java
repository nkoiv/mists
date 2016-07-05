/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.GameMode;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.HasNeighbours;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.TriggerPlate;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.gameobject.Water;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.networking.LocationServer;
import com.nkoiv.mists.game.puzzle.PuzzleManager;
import com.nkoiv.mists.game.sprites.Roof;
import com.nkoiv.mists.game.ui.Overlay;
import com.nkoiv.mists.game.world.mapgen.DungeonGenerator;
import com.nkoiv.mists.game.world.pathfinding.CollisionMap;
import com.nkoiv.mists.game.world.pathfinding.PathFinder;
import com.nkoiv.mists.game.world.util.Flags;
import com.nkoiv.mists.game.world.worldmap.MapNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Location is the main playfield of the game. It could be a castle, forest, dungeon or anything in between.
 * A Location stores the background data via the Map classes, and everything on top of it by MapObjects.
 * 
 * @author nkoiv
 */
public class Location extends Flags implements KryoSerializable {
    //private QuadTree mobQuadTree; //Used for collision detection //retired idea for now
    private int baseLocationID;
    private HashMap<Integer, HashSet> creatureSpatial; //New idea for lessening collision detection load
    private HashMap<Integer, HashSet> structureSpatial; //New idea for lessening collision detection load
    private ArrayList<Creature> creatures;
    private ArrayList<Structure> structures;
    private ArrayList<TriggerPlate> triggerPlates;
    private List<Effect> effects;
    
    private final Stack<MapObject> incomingMobs = new Stack<>();
    
    public boolean loading;
    private final HashMap<Integer, MapObject> mobs = new HashMap<>();
    private int nextID = 1;
    
    private ArrayList<Roof> roofs;
    
    private List<MapObject> targets;
    private String name;
    private GameMap map;
    private LocationEnvironment environment;
    private CollisionMap collisionMap;
    private PathFinder pathFinder;
    private DungeonGenerator mapGen;
    private PuzzleManager puzzleManager;
    private LightsRenderer lights;
    private RayShadows shadows;
    private final double[] lastOffsets = new double[2];
    private List<MapObject> lastRenderedMapObjects = new ArrayList<>();
    private MapObject screenFocus;
    private PlayerCharacter player;
    private final HashMap<Integer, double[]> entryPoints = new HashMap<>();
    
    public boolean mobsChanged;
    
    /*
    * Constructor for demofield
    * TODO: load this from some XML or somesuch
    */
    
    public Location() {
    	
    }
    
    public Location(String name, String mapFileName, int maptype) {
        this.name = name;
        this.creatures = new ArrayList<>();
        this.structures = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.triggerPlates = new ArrayList<>();
        Mists.logger.info("Loading map");
        if (maptype == 0) this.loadMap(new BGMap(new Image(mapFileName)));
        if (maptype == 1) this.loadMap(new TileMap(mapFileName));
        this.localizeMap();
        Mists.logger.info("Map localized, mapgeneration done");
    }
    
    public Location(String name, GameMap map) {
        Mists.logger.info("Generating location "+name+"...");
        this.name = name;
        this.creatures = new ArrayList<>();
        this.structures = new ArrayList<>();        
        this.effects = new ArrayList<>();
        this.triggerPlates = new ArrayList<>();
        //this.mapGen = new DungeonGenerator();
        //Mists.logger.info("Generating new BSP dungeon...");
        this.loadMap(map);
        Mists.logger.info("Dungeon generated");
        Mists.logger.info("Localizing map...");
        this.localizeMap();
        Mists.logger.info("Map localized, map generation done");
    }
    
    /**
     * Construct the little things needed to make
     * the map playable (collisionmaps, lights...)
     */
    private void localizeMap() {
        this.environment = new LocationEnvironment();
        this.collisionMap = new CollisionMap(this, Mists.TILESIZE);
        Mists.logger.info("Collisionmap generated");
        this.collisionMap.setStructuresOnly(true);
        Mists.logger.info("CollisionMap set to structures only");
        this.collisionMap.updateCollisionLevels();
        Mists.logger.info("Collisionlevels updated");
        this.collisionMap.printMapToConsole();
        this.pathFinder = new PathFinder(this.collisionMap, 100, true);
        this.puzzleManager = new PuzzleManager();
        this.lights = new LightsRenderer(this);
        this.shadows = new RayShadows();
        this.targets = new ArrayList<>();
        this.creatureSpatial = new HashMap<>();
        this.structureSpatial = new HashMap<>();
        this.roofs = new ArrayList<>();
        //this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,this.map.getWidth(),this.map.getHeight()));
        Mists.logger.log(Level.INFO, "Map ({0}x{1}) localized", new Object[]{map.getWidth(), map.getHeight()});
    }

    /**
    * MapLoader takes in a Map and initializes all the static structures from it for the Location
    * @param map Map to load
    */
    public void loadMap(GameMap map) {
        boolean wasLoading  = false;
        if (this.loading) wasLoading = true;
        else this.loading = true;
        this.map = map;
        // Add in all the static structures from the selected map
        ArrayList<Structure> staticStructures = map.getStaticStructures();
        Mists.logger.info("Map has "+staticStructures.size()+" static structures");
        ArrayList<Wall> walls = new ArrayList<>();
        for (Structure s : staticStructures) {
            this.addStructure(s, s.getXPos(), s.getYPos());
            if (s instanceof Wall) walls.add((Wall)s);
        }
        for (Wall w : walls) {
            w.updateGraphicsBasedOnNeighbours();
        }
        Mists.logger.info("Walls updated");
        if (!wasLoading) this.loading = false;
    }
    
    public PuzzleManager getPuzzleManager() {
        return this.puzzleManager;
    }
    
    public int getMobCount() {
        return this.mobs.size();
    }
    
    /**
     * Pull the creature by ID from the general MOBs table
     * @param ID Location-specific Identifier for the mob
     * @return MapObject if it exists in the location
     */
    public MapObject getMapObject(int ID) {
        return this.mobs.get(ID);
    }
    
    /**
    * Find the first (random) Creature on the map with the given name.
    * Returns null if no creature is found with the name.
    * @param name The name of the creature.
    * @return The creature in question
    */
    public Creature getCreatureByName(String name) {
        for (Creature c : this.creatures) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * When getting a MapObject by coordinates with mouseclick
     * or something, it's often needed to substract xOffset and yOffset
     * from coords. Returns the FIRST creature found at the spot.
     * If no creature is found, returns the first Structure at location.
     * 
     * @param xCoor xCoordinate of the search spot
     * @param yCoor yCoordinate of the search spot
     * @return Creature found at the coordinates
     */
    public MapObject getMobAtLocation(double xCoor, double yCoor) {
        MapObject mobAtLocation = null;
        int spatialID = this.getSpatial(xCoor, yCoor);
        if (!this.creatureSpatial.isEmpty()) {
            HashSet<Creature> spatial = this.creatureSpatial.get(spatialID);
            if (spatial != null) {
                for (Creature mob : spatial) {
                    if (xCoor >= mob.getXPos() && xCoor <= mob.getXPos()+mob.getWidth() &&
                            yCoor >= mob.getYPos() && yCoor <= mob.getYPos()+mob.getHeight()) {
                            //Do a pixelcheck on the mob;
                            //if (Sprite.pixelCollision(xCoor, yCoor, Mists.pixel, mob.getXPos(), mob.getYPos(), mob.getSprite().getImage())) {
                            return mob;
                            //}   
                        }
                }
            }
        }
        Structure s = (getStructureAtLocation(xCoor, yCoor));
        if (s!=null) mobAtLocation = s;
        return mobAtLocation;
    }
    
    public Structure getStructureAtLocation(double xCoor, double yCoor) {
        if (!this.structures.isEmpty()) {
            //Check the collisionmap first
            //No structure on collisionmap means no need to iterate through all structures (or spatials)
            if (this.collisionMap.isBlocked(0, (int)xCoor * collisionMap.nodeSize, (int)yCoor * collisionMap.nodeSize)) {
                for (Structure mob : this.structures) {
                    if (xCoor >= mob.getXPos() && xCoor <= mob.getXPos()+mob.getWidth()) {
                        if (yCoor >= mob.getYPos() && yCoor <= mob.getYPos()+mob.getHeight()) {
                            return mob;
                        }
                    }

                }
            }
        }
        return null;
    }
    
    /**
    * Find a random opening on the map via getRandomOpenSpot and set the given MapObject in it.
    * @param mob MapObject to be positioned
    */
    public void setMobInRandomOpenSpot (MapObject mob) {
        double[] openSpot = this.getRandomOpenSpot(mob.getWidth());
        mob.setPosition(openSpot[0], openSpot[1]);
    }
    
    /**
     * Retrieve the entrancenode to worldmap
     * if there are several, return the first on the list
     * TODO: might be good to key getters for individual nodes
     * @return (World)MapEntrace for this Location
     */
    private WorldMapEntrance getEntrace() {
        for (Structure s : this.structures) {
            if (s instanceof WorldMapEntrance) return (WorldMapEntrance)s;
        }
        return null;
    }
    
    private double[] getEntryPoint(MapNode entryNode) {
        /*TODO: use the HashMap entryPoints to select
        * where the player lands when he first enters the location
        */
        for (Structure s : this.structures) {
            if (s instanceof WorldMapEntrance) return new double[]{s.getXPos(), s.getYPos()};
        }
        double[] newEntrance = this.getRandomOpenSpot(32);
        
        //Just return an open random spot if there's no structure library (for unit tests)
        if (Mists.structureLibrary == null) return this.getRandomOpenSpot(32);
        Mists.logger.log(Level.INFO, "No entrance for the location was found, so generating new one at {0}x{1}", new Object[]{newEntrance[0], newEntrance[1]});
        WorldMapEntrance newStairs = (WorldMapEntrance)Mists.structureLibrary.create("DungeonStairsUp");
        newStairs.setExitNode(entryNode);
        this.addMapObject(newStairs, newEntrance[0], newEntrance[1]);
        return newEntrance;
    }
            
    /**
    * Brute force setting a dummy around the map until we find a an open spot
    * TODO: This could get stuck in infinite loop
    * @param sizeRequirement Size of the item to be placed (in pixel width)
    * @return An array with x and y coordinates for the (center) of the free spot
    */
    private double[] getRandomOpenSpot(double sizeRequirement) {
        //
        Creature collisionTester = new Creature("CollisionTester", new Image("/images/himmutoy.png"));
        
        //collisionTester.getSprite().setWidth(sizeRequirement);
        //collisionTester.getSprite().setHeight(sizeRequirement);
        Random rnd = new Random();
        boolean foundSpot = false;
        int openX = 0;
        int openY = 0;
        if (this.getCollisionMap() != null) {
            //Use the collisionmap to find random spot if possible
            while (!foundSpot) {
                openX = rnd.nextInt(this.getCollisionMap().mapTileWidth);
                openY = rnd.nextInt(this.getCollisionMap().mapTileHeight);
                if (!this.getCollisionMap().isBlocked(0, openX, openY)) {
                    if ((sizeRequirement > this.getCollisionMap().nodeSize)
                            && (this.getCollisionMap().isBlocked(0, openX+1, openY+1))) {
                        //Bigger things need more space
                        foundSpot = false;
                    } else {
                        foundSpot = true;
                        openX = openX * this.getCollisionMap().nodeSize;
                        openY = openY * this.getCollisionMap().nodeSize;
                    }
                }
            }
        } else {
            //If no collisionmap was available, just keep poking 'till you find a spot
            while (!foundSpot) {
                openX = (int)sizeRequirement + rnd.nextInt(((int)(map.getWidth()-sizeRequirement)));
                openX = (openX / Global.TILESIZE) * Global.TILESIZE;
                openY = (int)sizeRequirement + rnd.nextInt(((int)(map.getHeight()-sizeRequirement)));
                openY = (openY / Global.TILESIZE) * Global.TILESIZE;
                collisionTester.setCenterPosition(openX, openY);
                if (this.checkCollisions(collisionTester).isEmpty()) foundSpot = true;
            }
        }
        
        
        return new double[]{openX, openY};
    }
    
    /**
    * Returns the PathFinder for this Location
    * @return The PathFinder for this location
    */
    public PathFinder getPathFinder() {
        if (this.pathFinder == null) this.pathFinder = new PathFinder(this.collisionMap, 50, true);
        return this.pathFinder;
    }
    
    /**
     * Give the MapObject an unique location-specific ID-number
     * @param mob MapObject to set the ID to
     */
    private void giveID(MapObject mob) {
        if (mob == null) return;
        if (nextID == Integer.MAX_VALUE) nextID = Integer.MIN_VALUE;
        mob.setID(this.nextID);
        nextID++;
        if (nextID == 0) {
            Mists.logger.warning("Out of MapObject IDs, cleaning up");
            this.cleanupIDs();
        } 
    }
    
    /**
     * If ID's run out, clean up the ID list.
     */
    private void cleanupIDs() {
        this.nextID = 1;
        if (this.mobs.isEmpty()) return;
        for (Integer mobID : this.mobs.keySet()) {
            this.giveID(this.mobs.get(mobID));
        }
        //TODO: Inform possible clients that ID's have changed.
    }
    
    public int peekNextID() {
        return this.nextID;
    }
    
    public void setNextID(int id) {
        this.nextID = id;
    }

    public void clearAllMapObjects() {
        for (int mobID : this.mobs.keySet()) {
            this.removeMapObject(mobID);
        }
    }
    
    public void removeMapObject(int mobID) {
        MapObject mob = this.mobs.get(mobID);
        if (mob!=null) this.removeMapObject(mob);            
    }
    
    private void removeMapObject(MapObject mob) {
        Mists.logger.info("removeMapObject "+mob.getName());
        if (mob instanceof Structure) {
            if (mob instanceof Wall) {
                double xPos = mob.getCenterXPos();
                double yPos = mob.getCenterYPos();
                updateWallsAt(xPos, yPos);
            }
            this.structures.remove((Structure)mob);
        }
        if (mob instanceof Creature) {
            this.creatures.remove((Creature)mob);
        }
        if (mob instanceof Effect) {
            this.effects.remove((Effect)mob);
        }
        if (mob instanceof TriggerPlate) {
            this.triggerPlates.remove((TriggerPlate)mob);
        }
        this.mobs.remove(mob.getID());
    }
    
    /**
     * Insert a mob into the Location with given mobID.
     * If this mobID is already taken by another object,
     * the previous object is removed to make room for the new one.
     * @param mob MapObject to add to the Location
     * @param mobID LocationID to give to the new MapObject
     */
    public void addMapObject(MapObject mob, int mobID) {
    	if (this.getMapObject(mobID) != null) this.removeMapObject(mobID);
        if (mob == null) return;
        mob.setID(mobID);
        if (mob instanceof Structure) {
            this.structures.add((Structure)mob);
            this.structures.sort(new StructureYComparator());
        }
        if (mob instanceof Creature) {
            this.creatures.add((Creature)mob);
        }
        if (mob instanceof Effect) {
            this.effects.add((Effect)mob);
        }
        if (mob instanceof TriggerPlate) {
            this.triggerPlates.add((TriggerPlate)mob);
        }
        this.mobs.put(mob.getID(), mob);
        mob.setLocation(this);
    }
        
    /**
     * Generic MapObject insertion.
     * @param mob MapObject to insert in the map
     */
    public void addMapObject(MapObject mob) {
        if (mob == null) {
            Mists.logger.warning("Tried to add NULL mob to "+this.getName());
        }
        if (Mists.gameMode == GameMode.CLIENT && (!this.loading && (!(mob instanceof Effect)))) {
            //Clientmode should use ClientAddMapObject
            Mists.logger.warning("Client mode tried to add a map object directly ("+mob.toString()+")");
            return;
        } 
        if (!(mob instanceof Effect)) this.giveID(mob);
        if (mob instanceof Structure) {
            this.structures.add((Structure)mob);
            this.structures.sort(new StructureYComparator());
        }
        if (mob instanceof Creature) {
            this.creatures.add((Creature)mob);
        }
        if (mob instanceof Effect) {
            this.effects.add((Effect)mob);
        }
        if (mob instanceof TriggerPlate) {
            this.triggerPlates.add((TriggerPlate)mob);
        }
        if (!(mob instanceof Effect)) this.mobs.put(mob.getID(), mob);
        mob.setLocation(this);
    }
    
    public void addMapObject(MapObject mob, double xPos, double yPos) {
        if (mob == null) return;
        addMapObject(mob);
        mob.setPosition(xPos, yPos);
        if (this.pathFinder != null && mob instanceof Structure) this.pathFinder.setMapOutOfDate(true);
    }
    
    /**
    * Adds a Structure to the location
    * @param s The structure to be added
    * @param xPos Position for the structure on the X-axis
    * @param yPos Position for the structure on the Y-axis
    */
    private void addStructure(Structure s, double xPos, double yPos) {
        if (!this.structures.contains(s)) {
            this.addMapObject(s);
        }
        s.setLocation(this);
        s.setPosition(xPos, yPos);
        if (this.pathFinder != null) this.pathFinder.setMapOutOfDate(true);
    }
    
    /** Adds a Creature to the location
    * @param c The creature to be added
    * @param xPos Position for the creature on the X-axis
    * @param yPos Position for the creature on the Y-axis
    */
    private void addCreature(Creature c, double xPos, double yPos) {
        if (!this.creatures.contains(c)) {
            this.addMapObject(c);
        } else {
            //No need to re-add the creature if it's already in. Just give it a new ID.
            this.removeMapObject(c);
            this.giveID(c);
            Mists.logger.log(Level.WARNING, "Tried to add a {3} to {0} but {3} was already in it. Gave the {3} new ID: {1}", new Object[]{this.getName(), c.getID(), c.getName()});
            this.addMapObject(c);
        }
        c.setLocation(this);
        c.setPosition(xPos, yPos);
    }
    
    public void addEffectThreadSafe(Effect e, double xPos, double yPos) {
        if (!this.effects.contains(e)) {
            this.incomingMobs.push(e);
        }
        e.setLocation(this);
        e.setPosition(xPos, yPos);
    }
    
    /** Adds an Effect to the location
    * Effects do not use location based mobID,
    * so they're safe handled totally clientside
    * @param e The effect to be added
    * @param xPos Position for the effect on the X-axis
    * @param yPos Position for the effect on the Y-axis
    */
    public void addEffect(Effect e, double xPos, double yPos) {
        if (!this.effects.contains(e)) {
            this.addMapObject(e);
        }
        e.setLocation(this);
        e.setPosition(xPos, yPos);
    }
    
    /** Adds an Effect to the location
    * @param p The effect to be added
    * @param xPos Position for the effect on the X-axis
    * @param yPos Position for the effect on the Y-axis
    */
    public void addPlayerCharacter(PlayerCharacter p, double xPos, double yPos) {
        if (!this.creatures.contains(p)) {
            this.addMapObject(p);
        } else {
            //No need to re-add the player if it's already in. Just give it a new ID
            this.removeMapObject(p);
            this.giveID(p);
            Mists.logger.log(Level.WARNING, "Tried to add a player to {0} but player was already in it. Gave the player new ID: {1}", new Object[]{this.getName(), p.getID()});
            this.addMapObject(p);
        }
        p.setLocation(this);
        p.setPosition(xPos, yPos);
        Mists.logger.info("Added player "+p.getName()+" to "+this.getName());
    }
    
    public void addRoof(Roof r) {
        this.roofs.add(r);
    }
    
    public ArrayList<Roof> getRoofs() {
        return this.roofs;
    }
    
    public void clearRoofs() {
        this.roofs.clear();
    }
    
    public void setMapGen (DungeonGenerator mg) {
        this.mapGen = mg;
    }
    
    public DungeonGenerator getMapGen() {
        return this.mapGen;
    }
    
    public Set<Integer> getAllMobsIDs() {
        return this.mobs.keySet();
    }
    
    public List<Creature> getCreatures() {
        return this.creatures;
    }
    
    public ArrayList<Structure> getStructures() {
        return this.structures;
    }
    
    
    public void removeEffect (Effect e) {
        if(this.effects.contains(e)) this.effects.remove(e);
    }
    
    public GameMap getMap() {
        return this.map;
    }
    
    public void setPlayer(PlayerCharacter p) {
        this.player = p;
    }
    
    public PlayerCharacter getPlayer() {
        return this.player;
    }
    
    /**
     * The target of the screen focus is what the camera follows.
     * The view of the location is centered on this target (normally the player)
     * @param focus MapObject to focus on
     */
    public void setScreenFocus(MapObject focus) {
        this.screenFocus = focus;
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
        } else if (xOffset > map.getWidth() -(windowWidth)) {
            xOffset = map.getWidth() - (windowWidth);
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
        } else if (yOffset > map.getHeight() -(windowHeight)) {
            yOffset = map.getHeight() - (windowHeight);
        }
        this.lastOffsets[1] = yOffset;
        return yOffset;
    }
    
    public double getLastxOffset () {
        return this.lastOffsets[0];
    }
    public double getLastyOffset () {
        return this.lastOffsets[1];
    }
    
    /**
    * Update is the main "tick" of the Location.
    * Movement, combat and triggers should all be handled here.
    * If no server is given (=working in singleplayer), this uses
    * update(time, server) with Null as server.
    * TODO: Not everything needs to happen on every tick. Mobs should make new decisions only ever so often
    * @param time Time since the last update
    */
    public void update (double time) {
        this.update(time, null);
    }
    
    /**
     * Update is the main "tick" of the Location.
     * Movement, combat and triggers should all be handled here
     * 
     * If a Server is given as argument, the update operations
     * done are pushed to the servers update stack, to be relayed
     * to clients.
     * @param time Time since the last update
     * @param server Server to relay the updates to
     */
    public void update (double time, LocationServer server) {
        //TEMP: exit player if outside bounds
        if (player != null && (player.getXPos() < 0 || player.getYPos() < 0 || player.getXPos() > map.getWidth() || player.getYPos() > map.getHeight())) {
            exitLocationToWorldMap(null);
        }
        
        //Threadsafe additions
        handleIncomingMobStack();
        //AI-stuff
        if (!this.creatures.isEmpty()) {
            for (Creature mob : this.creatures) { //Mobs do whatever mobs do
                mob.think(time);
                mob.update(time);
                if (server!=null)server.addServerUpdate(mob.getLastTask());
            }
        }
        this.updateEffects(time);
        this.updateTriggerPlates(time);
        this.puzzleManager.tick(time);
        if (server!=null) {
            server.compileRemovals(creatureCleanup());
            server.compileRemovals(structureCleanup());
            server.compileRemovals(effectCleanup());
            this.updateCreatureSpatial();
            this.collisionMap.updateCollisionLevels();
        } else this.fullCleanup(true, true, true);
    }
    
    private void handleIncomingMobStack() {
        while (!this.incomingMobs.isEmpty()){
            this.addMapObject(incomingMobs.pop());
        }
    }
    
    public void fullCleanup(boolean cleanCreatures, boolean cleanStructures, boolean cleanEffects) {
        if (cleanCreatures) creatureCleanup();
        if (cleanStructures) structureCleanup();
        if (cleanEffects) effectCleanup();
        this.updateCreatureSpatial();
        this.collisionMap.updateCollisionLevels();
    }
    
    public void updateEffects(double time) {
        if (!this.effects.isEmpty()) {
            //Mists.logger.info("Effects NOT empty");
            for (Effect e : this.effects) { //Handle effects landing on something
                e.update(time);
            }
        }
    }
    
    public void updateTriggerPlates(double time) {
        if (!this.triggerPlates.isEmpty()) {
            for (TriggerPlate t : this.triggerPlates) {
                t.update(time);
            }
        }
    }
    
    private void triggerplateCleanup(Stack<Integer> stackToAddIDsOn) {
        if (!this.triggerPlates.isEmpty()) {
            Iterator<TriggerPlate> triggerplateIterator = triggerPlates.iterator();
            while (triggerplateIterator.hasNext()) {
                TriggerPlate tp = triggerplateIterator.next();
                if (tp.isRemovable()) {
                    triggerplateIterator.remove();
                    this.triggerPlates.remove(tp);
                    stackToAddIDsOn.add(tp.getID());
                }
            }
        }
    }
    
    /**
     * structureCleanup cleans all the "removable"
     * flagged structures.
     */
    private Stack<Integer> structureCleanup() {
        //Structure cleanup
        Stack<Integer> removedStructureIDs = new Stack<>();
        if (!this.structures.isEmpty()) {
            ArrayList<Wall> removedWalls = new ArrayList();
            Iterator<Structure> structureIterator = structures.iterator(); //Cleanup of mobs
            while (structureIterator.hasNext()) {
                MapObject mob = structureIterator.next();
                if (mob.isRemovable()) {
                    if (mob instanceof Wall) {
                        removedWalls.add((Wall)mob);
                        //Update the surrounding walls as per needed
                        //this.updateWallsAt(mob.getCenterXPos(), mob.getCenterYPos());   
                    }
                    structureIterator.remove();
                    this.mobs.remove(mob.getID());
                    removedStructureIDs.add(mob.getID());
                    this.pathFinder.setMapOutOfDate(true);
                    if (this.targets.contains(mob)) this.targets.remove(mob);
                }
            }  
            this.restructureWalls(removedWalls);
        }
        triggerplateCleanup(removedStructureIDs);
        
        return removedStructureIDs;
    }
    
    /**
    * creatureCleanup cleans all the "removable"
    * flagged creatures.
    */
    private Stack<Integer> creatureCleanup() {
        //Creature cleanup
        Stack<Integer> removedCreatureIDs = new Stack<>();
        if (!this.creatures.isEmpty()) {
            Iterator<Creature> creatureIterator = creatures.iterator(); //Cleanup of mobs
            while (creatureIterator.hasNext()) {
                MapObject mob = creatureIterator.next();
                if (mob.isRemovable()) {
                    creatureIterator.remove();
                    int mobID = mob.getID();
                    this.mobs.remove(mobID);
                    removedCreatureIDs.add(mobID);
                    //this.pathFinder.setMapOutOfDate(true); //Creatures are not on pathFindermap atm
                    if (this.targets.contains(mob)) this.targets.remove(mob);
                }
            }     
        }
        return removedCreatureIDs;
    }
    
     /**
     * effectCleanup cleans all the "removable"
     * flagged Effects.
     */
    private Stack<Integer> effectCleanup() {
        //Effects cleanup
        Stack<Integer> removedEffectIDs = new Stack<>();
        if (!this.effects.isEmpty()) {
            Iterator<Effect> effectsIterator = effects.iterator(); //Cleanup of effects
            while (effectsIterator.hasNext()) {
                Effect e = effectsIterator.next();
                if (e.isRemovable()) {
                    effectsIterator.remove();
                    int effectID = e.getID();
                    this.mobs.remove(effectID);
                    removedEffectIDs.add(effectID);
                } 
            }
        }
        return removedEffectIDs;
    }
    
    private void updateCreatureSpatial() {
        if (this.creatureSpatial == null) this.creatureSpatial = new HashMap<>();
        if (this.creatureSpatial != null) this.creatureSpatial.clear();
        
        for (MapObject creep : this.creatures) {
            HashSet<Integer> mobSpatials = getSpatials(creep);
            for (Integer i : mobSpatials) {
                addToSpatial(creep, i, this.creatureSpatial);
            }
        }

        /* [ 0][ 1][ 2][ 3][ 4]
        *  [ 5][ 6][ 7][ 8][ 9]
        *  [10][11][12][13][14]
        *  [15][16][17][18][19]
        * Above a spatial node is the node number -5,
        * below it is node number +5 and sides are +/- 1
        */
        
    }
    
    private void updateStructureSpatial() {
        if (this.structureSpatial == null) this.structureSpatial = new HashMap<>();
        if (this.structureSpatial != null) this.structureSpatial.clear();
        
        for (MapObject struct : this.structures) {
            HashSet<Integer> mobSpatials = getSpatials(struct);
            for (Integer i : mobSpatials) {
                addToSpatial(struct, i, this.structureSpatial);
            }
        }
    }
    
    private static void addToSpatial (MapObject mob, int spatialID, HashMap<Integer, HashSet> spatial) {
        if (spatial.get(spatialID) == null) spatial.put(spatialID, new HashSet<MapObject>());
        spatial.get(spatialID).add(mob);
    }
    
    /**
     * Get all the spatial hash IDs a given mob is located in
     * @param mob MapObject to check for
     * @return set of spatial hash ID's
     */
    private HashSet<Integer> getSpatials(MapObject mob) {
        Double[] spatialUpLeft;
        Double[] spatialUpRight;
        Double[] spatialDownRight;
        Double[] spatialDownLeft;
        HashSet<Integer> spatialList = new HashSet<>();
        spatialUpLeft = mob.getCorner(Direction.UPLEFT);
        spatialList.add(getSpatial(spatialUpLeft[0],spatialUpLeft[1]));

        spatialUpRight = mob.getCorner(Direction.UPRIGHT);
        spatialList.add(getSpatial(spatialUpRight[0],spatialUpRight[1]));
        
        spatialDownRight = mob.getCorner(Direction.DOWNRIGHT);
        spatialList.add(getSpatial(spatialDownRight[0],spatialDownRight[1]));
        
        spatialDownLeft = mob.getCorner(Direction.DOWNLEFT);
        spatialList.add(getSpatial(spatialDownLeft[0],spatialDownLeft[1]));
        
        return spatialList;
    }
    
    /**
     * Get the spatial hash ID for given coordinates
     * @param xCoor
     * @param yCoor
     * @return Spatial hash ID
     */
    private int getSpatial (double xCoor, double yCoor) {
        int spatialsPerRow = 5; //TODO: Calculate these from map size?
        int spatialRows = 5; //Or maybe map fillrate?
        int sC = (int)this.map.getWidth()/spatialsPerRow;
        int sR = (int)this.map.getHeight()/spatialRows;
        
        return ((int)(xCoor / sC) * (int)(yCoor / sR));
    }
    
    private void restructureWalls (ArrayList<Wall> removedWalls) {
        if (removedWalls.isEmpty()) return;
        for (Wall w : removedWalls) {
            updateWallsAt(w.getCenterXPos(), w.getCenterYPos());
        }
    }
    
    public void updateAllVariableGraphicStructures() {
        for (Structure s : this.structures) {
            if (s instanceof HasNeighbours) {
                ((HasNeighbours)s).setNeighbours(((HasNeighbours)s).checkNeighbours());
                ((HasNeighbours)s).updateGraphicsBasedOnNeighbours();
            }
        }
    }
    
    /**
     * If a wall gets added or removed, the walls around it
     * need to be updated accordingly
     * TODO: This is probably pointless stuff
     * @param xCenterPos xCenter of the happening
     * @param yCenterPos yCenter of the happening
     */
    private void updateWallsAt(double xCenterPos, double yCenterPos) {
        //ArrayList<MapObject> surroundingWalls = new ArrayList();
        //boolean[] boolwalls = new boolean[8];
        /*
         [0][1][2]
         [3]   [4]   
         [5][6][7]
        */
        
        //Note: It's okay to add Nulls here (most will be). Instanceof will take care of that
        //Cardinal directions
        MapObject mob;
        mob = (this.getMobAtLocation(xCenterPos-Mists.TILESIZE, yCenterPos)); //Left
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(4); w.updateGraphicsBasedOnNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos+Mists.TILESIZE, yCenterPos)); //Right
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(3); w.updateGraphicsBasedOnNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos, yCenterPos-Mists.TILESIZE)); //Up
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(6); w.updateGraphicsBasedOnNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos, yCenterPos+Mists.TILESIZE)); //Down
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(1); w.updateGraphicsBasedOnNeighbours();}
        //Diagonal directions
        mob = (this.getMobAtLocation(xCenterPos-Mists.TILESIZE, yCenterPos-Mists.TILESIZE)); //UpLeft
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(7); w.updateGraphicsBasedOnNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos+Mists.TILESIZE, yCenterPos-Mists.TILESIZE)); //UpRight
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(5); w.updateGraphicsBasedOnNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos-Mists.TILESIZE, yCenterPos+Mists.TILESIZE)); //DownLeft
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(2); w.updateGraphicsBasedOnNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos+Mists.TILESIZE, yCenterPos+Mists.TILESIZE)); //DownRight
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(0); w.updateGraphicsBasedOnNeighbours();}

    }
    
    /**
     * Check (only) creatures colliding with given map object
     * @param o The map object to check collisions with
     * @return List of colliding creatures;
     */
    public ArrayList<Creature> checkCreatureCollisions(MapObject o) {
        ArrayList<Creature> collidingObjects = new ArrayList<>();
        HashSet<Integer> mobSpatials = getSpatials(o);
        //Spatials cover the creature collisions
        for (Integer i : mobSpatials) {
            addMapObjectCollisions(o, this.creatureSpatial.get(i), collidingObjects);
        }
        return collidingObjects;
    }
    
    /** CheckCollisions for a given MapObjects
    * Returns a List with all the objects that collide with MapObject o
    * Now with quad tree to check only objects nearby
    * @param o The MapObject to check collisions with
    * @return a List with all the objects that collide with MapObject o
    */
    public ArrayList<MapObject> checkCollisions (MapObject o) {
        
        ArrayList<MapObject> collidingObjects = new ArrayList<>();
        HashSet<Integer> mobSpatials = getSpatials(o);
        //Spatials cover the creature collisions
        for (Integer i : mobSpatials) {
            addMapObjectCollisions(o, this.creatureSpatial.get(i), collidingObjects);
        }
        
        //Check collisions for structures too
        //For creatures, check the collision versus collisionmap first
        //Note that this returns false on structures the creature is allowed to pass through
        if (o instanceof Creature) {
            if (collidesOnCollisionMap((Creature)o)) {
                addMapObjectCollisions(o, this.structures, collidingObjects);
            }
            Iterator<MapObject> mobIter = collidingObjects.iterator();
            while (mobIter.hasNext()) {
                MapObject mob = mobIter.next();
                if (((Creature)o).getCrossableTerrain().contains(mob.getCollisionLevel())) {
                    mobIter.remove();
                }
            }
        } else {
            addMapObjectCollisions(o, this.structures, collidingObjects);
        }
        
        
        return collidingObjects;
        
    }
    /*
    public ArrayList<MapObject> checkCollisions (Sprite s) {
        ArrayList<MapObject> collidingObjects = new ArrayList<>();
        HashSet<Integer> spriteSpatials = getSpatials(s);
        for (Integer i : spriteSpatials) {
            addMapObjectCollisions(s, this.spatial.get(i), collidingObjects);
        }
        
        addMapObjectCollisions(s, this.structures, collidingObjects);
       
        return collidingObjects;
    }
    */
    /**
     * Check the supplied iterable list of objects for collisions
     * The method is supplied an arraylist instead of returning one,
     * because same arraylist may go through several cycles of addMapObjectCollisions
     * @param o MapObject to check collisions for
     * @param mapObjectsToCheck List of objects
     * @param collidingObjects List to add the colliding objects on
     */
    private void addMapObjectCollisions(MapObject mob, Iterable mapObjectsToCheck, ArrayList collidingObjects) {         
        if (mapObjectsToCheck == null) return;
        Iterator<MapObject> mobIter = mapObjectsToCheck.iterator();
        while ( mobIter.hasNext() )
        {
            MapObject collidingObject = mobIter.next();
            if (collidingObject.equals(mob)) continue;
            //If the objects are further away than their combined width/height, they cant collide
            if ((Math.abs(collidingObject.getCenterXPos() - mob.getCenterXPos())
                 > (collidingObject.getWidth() + mob.getWidth()))
                || (Math.abs(collidingObject.getCenterYPos() - mob.getCenterYPos())
                 > (collidingObject.getHeight() + mob.getHeight()))) {
                //Objects are far enough from oneanother
            } else {
                if (!collidingObject.equals(mob) && mob.intersects(collidingObject)) { 
                    // Colliding with yourself is not really a collision
                    //Mists.logger.info(mob.getName()+" collided with "+collidingObject.getName());
                    //if (collidingObject instanceof Structure) Mists.logger.info("Collision between "+mob.getName()+" and "+collidingObject.getName()+" ID:"+collidingObject.getID());
                    collidingObjects.add(collidingObject);
                }
            }
            
        }
    }
    
    
    
    /**
     * Check if a MapObject hits something on the collision map.
     * Useful for pruning down the list of objects that needs true
     * collision detection.
     * TODO: using this, a HUGE creature could get stuck on certain structures?
     * @param mob
     * @return True if collision map had something at mobs coordinates
     */
    private boolean collidesOnCollisionMap(Creature mob) {
        Double[] upleft = mob.getCorner(Direction.UPLEFT);
        Double[] upright = mob.getCorner(Direction.UPRIGHT);
        Double[] downleft = mob.getCorner(Direction.DOWNLEFT);
        Double[] downright = mob.getCorner(Direction.DOWNRIGHT);
        if (this.collisionMap.isBlocked(mob.getCrossableTerrain(),(int)(upleft[0]/collisionMap.nodeSize), (int)(upleft[1]/collisionMap.nodeSize))) return true;
        if (this.collisionMap.isBlocked(mob.getCrossableTerrain(),(int)(upright[0]/collisionMap.nodeSize), (int)(upright[1]/collisionMap.nodeSize))) return true;
        if (this.collisionMap.isBlocked(mob.getCrossableTerrain(),(int)(downleft[0]/collisionMap.nodeSize), (int)(downleft[1]/collisionMap.nodeSize))) return true;
        return (this.collisionMap.isBlocked(mob.getCrossableTerrain(),(int)(downright[0]/collisionMap.nodeSize), (int)(downright[1]/collisionMap.nodeSize)));
    }
    
 
    
    /**
     * Check Collisions for a line drawn between two points.
     * This is useful for determining for example line of sight
     * @param xStart x Coordinate of the starting point
     * @param yStart y Coordinate of the starting point
     * @param xGoal x Coordinate of the far end
     * @param yGoal y Coordinate of the far end
     * @return list with all the colliding mapObjects(Creatures and Structures)
     */
    public ArrayList<MapObject> checkCollisions(double xStart, double yStart, double xGoal, double yGoal) {
        ArrayList<MapObject> collidingObjects = new ArrayList<>();
        double xDistance = xGoal - xStart;
        double yDistance = yGoal - yStart;
        Line line = new Line(xStart, yStart, xGoal, yGoal);
        Iterator<Creature> creaturesIter = creatures.iterator();
        while ( creaturesIter.hasNext() )
        {
            MapObject collidingObject = creaturesIter.next();
            //If the objects are further away than their combined width/height, they cant collide
            if ((Math.abs(collidingObject.getCenterXPos() - xStart)
                 > (collidingObject.getWidth() + Math.abs(xDistance)))
                || (Math.abs(collidingObject.getCenterYPos() - yStart)
                 > (collidingObject.getHeight() + Math.abs(yDistance)))) {
                //Objects are far enough from oneanother
            } else {
                if (collidingObject.intersects(line) ) 
                 {
                    collidingObjects.add(collidingObject);
                }
            }
            
        }
        Iterator<Structure> structuresIter = structures.iterator();
        while ( structuresIter.hasNext() )
        {
            MapObject collidingObject = structuresIter.next();
            //If the objects are further away than their combined width/height, they cant collide
            if ((Math.abs(collidingObject.getCenterXPos() - xStart)
                 > (collidingObject.getWidth() + Math.abs(xDistance)))
                || (Math.abs(collidingObject.getCenterYPos() - yStart)
                 > (collidingObject.getHeight() + Math.abs(yDistance)))) {
                //Objects are far enough from oneanother
            } else {
                if (collidingObject.intersects(line)) 
                 {
                    collidingObjects.add(collidingObject);
                }
            }
            
        }
        
        
        return collidingObjects;
    }
    
    public EnumSet<Direction> collidedSides (MapObject mob) {
        ArrayList<MapObject> collidingObjects = this.checkCollisions(mob); //Get the colliding object(s)
        return this.collidedSides(mob, collidingObjects);
    }
    
    public EnumSet<Direction> collidedSides (MapObject mob, ArrayList<MapObject> collidingObjects) {
        EnumSet<Direction> collidedDirections = EnumSet.of(Direction.STAY);
        for (MapObject collidingObject : collidingObjects) {
            //Mists.logger.log(Level.INFO, "{0} bumped into {1}", new Object[]{this, collidingObject});
            double collidingX = collidingObject.getCenterXPos();//+(collidingObject.getSprite().getWidth()/2);
            double collidingY = collidingObject.getCenterYPos();//+(collidingObject.getSprite().getHeight()/2);
            double thisX = mob.getCenterXPos();//+(this.getSprite().getWidth()/2);
            double thisY = mob.getCenterYPos();//+(this.getSprite().getHeight()/2);
            double xDistance = (thisX - collidingX);
            double yDistance = (thisY - collidingY);
            if (Math.abs(xDistance) >= Math.abs(yDistance)) {
                //Collided primary on the X (Left<->Right)
                if (mob.getCenterXPos() <= collidingObject.getCenterXPos()) {
                    //CollidingObject is RIGHT of the mob
                    collidedDirections.add(Direction.RIGHT);
                } else {
                    //CollidingObject is LEFT of the mob
                    collidedDirections.add(Direction.LEFT);
                }
            } else {
                //Collided primary on the Y (Up or Down)
                if (mob.getCenterYPos() >= collidingObject.getCenterYPos()) {
                    //CollidingObject is UP of the mob
                    collidedDirections.add(Direction.UP);
                } else {
                    //CollidingObject is DOWN of the mob
                    collidedDirections.add(Direction.DOWN);
                }
            }
        }
        if (mob.getXPos() <= 0) collidedDirections.add(Direction.LEFT);
        if (mob.getYPos() <= 0) collidedDirections.add(Direction.UP);
        if (mob.getCenterXPos() >= this.map.getWidth()) collidedDirections.add(Direction.RIGHT);
        if (mob.getCenterYPos() >= this.map.getHeight()) collidedDirections.add(Direction.DOWN);
        return collidedDirections;
    }
    
    /**
     * General render method for the location, called 60 times per second
     * by default. Render only updates the given GraphicsContext with what's
     * on the current viewport (dictated by xOffset, yOffset and screen width/height)
     * All the location logic should be handled under tick()
     * @param gc GraphicsContext for the location graphics
     * @param sc Shadow layer drawn on top of the graphics
     */
    public void render (GraphicsContext gc, GraphicsContext sc) {
        /*
        * Update Offsets first to know which parts of the location are drawn
        */
        double xOffset = getxOffset(gc, screenFocus.getXPos());
        double yOffset = getyOffset(gc, screenFocus.getYPos());
        //Mists.logger.info("Offset: "+xOffset+","+yOffset);
        this.renderMap(gc, xOffset, yOffset);
        this.lastRenderedMapObjects = this.renderMobs(gc, xOffset, yOffset);
        this.renderLights(sc, lastRenderedMapObjects, xOffset, yOffset);
        this.renderStructureExtras(gc, lastRenderedMapObjects, xOffset, yOffset);
        this.renderExtras(gc, xOffset, yOffset);
        this.renderRoofs(gc, xOffset, yOffset);
    }
    

    /**
     * Render the Roof graphics on top of anything that's on the screen
     * and under a roof. If a player has vision (or resides) under a roof,
     * the roof should be drawn transparent or translucent
     * @param gc GraphicsContext the location is being rendered on
     * @param xOffset xOffset for camera position on the location
     * @param yOffset yOffset for the camera position on the location
     */
    private void renderRoofs(GraphicsContext gc, double xOffset, double yOffset) {
        for (Roof r : this.roofs) {
            //No need to render the roof if no part of it is on visible area
            if (xOffset> (r.getXCoor()+r.getWidth()) || yOffset > (r.getYCoor()+r.getHeigth())) continue;
            if (xOffset+gc.getCanvas().getWidth() < r.getXCoor() || yOffset+gc.getCanvas().getHeight() < r.getYCoor()) continue;
            //Continue with rendering the roof
            boolean v = lights.containsVisibleTiles(r.getHiddenArea().minX, r.getHiddenArea().minY, r.getHiddenArea().maxX - r.getHiddenArea().minX, r.getHiddenArea().maxY -r.getHiddenArea().minY);
            r.renderWithPlayerVision(v, xOffset, yOffset, gc);
        }
    }
    
    //TODO: Move this to a separate class
    private void renderLights(GraphicsContext sc, List<MapObject> MOBsOnScreen, double xOffset, double yOffset) {
        //Raycast from player to all screen corners and to corners of all visible structures
        /*
        List<Structure> StructuresOnScreen = new ArrayList<>();
        //List<Creature> CreaturesOnScreen = new ArrayList<>();
        for (MapObject mob : MOBsOnScreen) {
            if (mob instanceof Structure) StructuresOnScreen.add((Structure)mob);
            //if (mob instanceof Creature) CreaturesOnScreen.add((Creature)mob);
        }
        shadows.setLight(player.getCenterXPos()-xOffset, player.getCenterYPos()-yOffset);
        shadows.setScreenSize(sc.getCanvas().getWidth(), sc.getCanvas().getHeight());
        shadows.updateStructures(StructuresOnScreen, xOffset, yOffset);
        shadows.paintLights(sc, xOffset, yOffset);
        */
        
        //Calculate the player vision range to see what's hidden behind walls (used on fex. roofs)
        lights.paintVision(player.getCenterXPos(), player.getCenterYPos(), player.getVisionRange());
        //Render black blocks on top of hidden segments
        //lights.renderLightMap(gc, xOffset, yOffset);
        
        sc.save();
        /*
        //Old black fill
        sc.setFill(Color.BLACK);
        sc.setGlobalAlpha(1);
        sc.fillRect(0, 0, sc.getCanvas().getWidth(), sc.getCanvas().getHeight());
        */
        sc.setFill(environment.getShadowColor());
        sc.setGlobalAlpha(environment.getShadowDepth());
        //Shadow the layer, as light punches holes in it
        sc.fillRect(0, 0, sc.getCanvas().getWidth(), sc.getCanvas().getHeight());
        sc.restore();
        //Render lightsources around mobs that have them (should be at least player)
        for (MapObject mob : MOBsOnScreen) {
            if (mob.getLightSize() > 0) {
                lights.renderLightSource(sc, (mob.getCenterXPos()-xOffset)*Mists.graphicScale, (mob.getCenterYPos()-yOffset)*Mists.graphicScale,mob.getLightSize()*environment.getLightlevel(), mob.getLightColor());
            }
        }
        
    }
    
    /**
     * Render all the MOBs (creature & structure, Effects are separate (TODO: Why?)
     * on the location that is visible. Returns the list of objects that were rendered
     * @param gc Graphics context to render on
     * @param xOffset Offset for rendering (centered on player usually)
     * @param yOffset Offset for rendering (centered on player usually)
     */
    private List<MapObject> renderMobs(GraphicsContext gc, double xOffset, double yOffset) {
        List<MapObject> renderedMOBs = new ArrayList<>();
        renderedMOBs.addAll(renderStructures(gc, xOffset, yOffset));
        renderedMOBs.addAll(renderCreatures(gc, xOffset, yOffset));
        return renderedMOBs;
    }
    
    
    private List<MapObject> renderCreatures(GraphicsContext gc, double xOffset, double yOffset) {
         /*
        * TODO: Consider rendering mobs in order so that those closer to bottom of the screen overlap those higher up.
        */
        List<MapObject> renderedMOBs = new ArrayList<>();
        //Find the creatures to render
        if (!this.creatures.isEmpty()) {
            for (Creature mob : this.creatures) {
                if (mob.getXPos()-xOffset < -mob.getWidth() ||
                    mob.getXPos()-xOffset > gc.getCanvas().getWidth()) {
                    //Mob is not in window
                } else if (mob.getYPos()-yOffset < -mob.getHeight() ||
                    mob.getYPos()-yOffset > gc.getCanvas().getHeight()) {
                    //Mob is not in window
                } else {
                    //Mob is in window
                    renderedMOBs.add(mob);
                    if (Mists.DRAW_COLLISIONS) { // Draw collision boxes for debugging purposes, if the Global variable is set
                        this.renderCollisions(mob, gc, xOffset, yOffset);
                    }
                }
            }
        }
        renderedMOBs.sort(new CoordinateComparator());
        for (MapObject mob : renderedMOBs) {
            mob.render(xOffset, yOffset, gc); //Draw objects on the ground
        }
        
        
        return renderedMOBs;
    }
    
    private List<MapObject> renderStructures(GraphicsContext gc, double xOffset, double yOffset) {
        /*
        * TODO: Consider rendering mobs in order so that those closer to bottom of the screen overlap those higher up.
        */
        List<MapObject> renderedMOBs = new ArrayList<>();
        
        if (!this.structures.isEmpty()) {
            for (Structure mob : this.structures) {
                if (mob.getXPos()-xOffset < -mob.getWidth() ||
                    mob.getXPos()-xOffset > gc.getCanvas().getWidth()) {
                    //Mob is not in window
                } else if (mob.getYPos()-yOffset < -mob.getHeight() ||
                    mob.getYPos()-yOffset > gc.getCanvas().getHeight()) {
                    //Mob is not in window
                } else {
                    //Mob is in window
                    mob.render(xOffset, yOffset, gc); //Draw objects on the ground
                    renderedMOBs.add(mob);
                    if (Mists.DRAW_COLLISIONS) { // Draw collision boxes for debugging purposes, if the Global variable is set
                       this.renderCollisions(mob, gc, xOffset, yOffset);
                    }
                }
            }
        }
        return renderedMOBs;
    }
    
    /**
     * Structure extras are rendered separately for two main reasons:
     * Firstly, they need to be on top of everything else, hence draw
     * them after Structs and Creatures.
     * Secondly, they're painted with the same lightlevel as the tile
     * they're standing on, even if they are larger than the tile 
     * (fex a tree and its leaves, arching over to several blocks radius)
     * @param gc Graphics Context used for rendering
     * @param renderedMOBs List of MapObjects we rendered on the screen. Only those need extras visible
     * @param xOffset Offset for rendering (centered on player usually)
     * @param yOffset Offset for rendering (centered on player usually)
     */
    
    private void renderStructureExtras(GraphicsContext gc, List<MapObject> renderedMOBs, double xOffset, double yOffset) {
         // Render extras should be called whenever the structure is rendered
        // This paints them on top of everything again, creatures go "behind" trees
        gc.save();
        
        gc.setGlobalAlpha(0.8);
        //double lightlevel;
        //ColorAdjust lightmap = new ColorAdjust();
        if (!renderedMOBs.isEmpty()) {
            for (MapObject struct : renderedMOBs) {
                if (struct instanceof Structure) {
                    /*
                    int structX = (int)struct.getXPos()/Mists.TILESIZE;
                    int structY = (int)struct.getYPos()/Mists.TILESIZE;
                    if (structX >= lights.lightmap.length || structY >= lights.lightmap[0].length) lightlevel = 1;
                    else lightlevel = lights.lightmap[structX][structY];
                    lightlevel = lightlevel-1;
                    
                    lightmap.setBrightness(lightlevel); gc.setEffect(lightmap);
                    
                    if (lightlevel!=-1) {
                    */
                        //Don't render stuff that is in total darkness
                        ((Structure)struct).renderExtras(xOffset, yOffset, gc);
                    //}
                }
            }
        }
        gc.restore();
    }
    
    public List<MapObject> getLastRenderedMobs() {
        return this.lastRenderedMapObjects;
    }
    
    private void renderExtras(GraphicsContext gc, double xOffset, double yOffset) {
        //Draw extra effects (battle swings, projectiles, spells...) on the screen
        if (!this.effects.isEmpty()) {
            for (Effect e : this.effects) {
                e.render(xOffset, yOffset, gc);
                if (Mists.DRAW_COLLISIONS) { // Draw collision boxes for debugging purposes, if the Global variable is set
                       this.renderCollisions(e, gc, xOffset, yOffset);
                }
            }
        }
        if (!this.targets.isEmpty()) {
            for (MapObject mob : this.targets) {
                Overlay.drawTargettingCircle(gc, mob);
            }
        }
    }
    
    private void renderCollisions(MapObject mob, GraphicsContext gc, double xOffset, double yOffset) {
        mob.renderCollisions(xOffset, yOffset, gc);
    }
    
    private void renderMap(GraphicsContext gc, double xOffset, double yOffset) {
        this.map.render(-xOffset, -yOffset, gc); //First we draw the underlying map
        
        if (Mists.DRAW_GRID) {
            gc.setStroke(Color.ANTIQUEWHITE);
            double tileHeight = map.getHeight()/Mists.TILESIZE;
            double tileWidth = map.getWidth()/Mists.TILESIZE;
            for (int i=0; i<tileHeight;i++) {
                gc.strokeLine(0, (i*Mists.TILESIZE)-yOffset, map.getWidth(), (i*Mists.TILESIZE)-yOffset);
            }
            for (int i=0; i<tileWidth;i++) {
                gc.strokeLine((i*Mists.TILESIZE)-xOffset, 0, (i*Mists.TILESIZE)-xOffset, map.getHeight());
            }
            
        }
    }
    
    public void setTarget(MapObject mob) {
        this.targets.clear();
        if (mob!=null)this.targets.add(mob);
    }
    
    public void clearTarget() {
        this.targets.clear();
    }
    
    public void addTarget(MapObject mob) {
        this.targets.add(mob);
    }
    
    public List<MapObject> getTargets() {
        return this.targets;
    }
    
    public LocationEnvironment getEnvironment() {
        return this.environment;
    }
    
    public void setMinLightLevel(double lightlevel) {
        double ll = lightlevel;
        if (ll>1.0) ll=1.0;
        if (ll<0.0) ll=0.0;
        this.lights.setMinLightLevel(ll);
    }
    
    /**
     * Check the light level (Player vision) on the given tile
     * @param xCoor xCoordinate of the TILE in question (default width Mists.TILESIZE)
     * @param yCoor yCoordinate of the TILE in question (default height Mists.TILESIZE)
     * @return 
     */
    public double getLightLevel(double xCoor, double yCoor) {
        double lightlevel = 0;
        int xTile = (int)xCoor/Mists.TILESIZE;
        int yTile = (int)yCoor/Mists.TILESIZE;
        if (xTile < lights.lightmap.length || yTile < lights.lightmap[0].length) lightlevel = lights.lightmap[xTile][yTile];
        return lightlevel;
    }
    
    /**
     * Check if player has explored the given tile on the map
     * @param xCoor xCoordinate of the TILE in question (default width Mists.TILESIZE)
     * @param yCoor yCoordinate of the TILE in question (default height Mists.TILESIZE)
     * @return 
     */
    public boolean isExplored(double xCoor, double yCoor) {
        int xTile = (int)xCoor/Mists.TILESIZE;
        int yTile = (int)yCoor/Mists.TILESIZE;
        if (xTile < lights.explored.length || yTile < lights.explored[0].length) {
            return lights.explored[xTile][yTile];
        } else {
            return false;
        }
    }
    
    public void printLightMapIntoConsole() {
        this.lights.printLightMapToConsole();
    }
    
    public double getMinLightLevel() {
        return this.lights.getMinLightLevel();
    }
    
    public CollisionMap getCollisionMap() {
        return this.collisionMap;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setBaseID(int ID) {
        this.baseLocationID = ID;
    }
    
    public int getBaseID() {
        return this.baseLocationID;
    }
    
    @Override
    public String toString() {
        String s;
        s = this.name+", a "+this.map.getWidth()+"x"+this.map.getHeight()+" area with "
                +this.creatures.size()+" creatures and "+this.structures.size()+" structures";
        return s;
    }
    
    public void changeLocation(int targetLocationID, double targetXCoor, double targetYCoor) {
        exitLocation();
        Mists.MistsGame.moveToLocation(targetLocationID, targetXCoor, targetYCoor);
    }
    
    /**
     * Exit from the location and go to a specific exitNode on the worldmap.
     * @param exitNode 
     */
    public void exitLocationToWorldMap(MapNode exitNode) {
        exitLocation();
        if (exitNode != null) Mists.MistsGame.getCurrentWorldMap().setPlayerNode(exitNode);
        Mists.MistsGame.moveToState(Game.WORLDMAP);
        //TODO: This doesn't feel like the clean way to do this. Rethink!
    }

    /**
     *  ExitLocation should clean up the location for re-entry later on
     */
    public void exitLocation() {
        Mists.logger.log(Level.INFO, "Location {0} exited, cleaning up...", this.getName());
        //Release player from Location
        
        if (this.player != null) {
            this.player.remove();
            for (Creature c : player.getCompanions()) {
                c.remove();
            }
            //this.removeMapObject(this.player);
            this.player.clearLocation();
            this.player = null;
        }
        
        //TODO: The cleanup
    }
    
    /**
     * Prepare the Location for entering a place
     * PlayerCharacter within.
     * @param p PlayerCharacter that's entering the location
     * @param entryPoint X and Y coordinates for where the player enters
     */
    private void enterLocation(PlayerCharacter p, double[] entryPoint) {
        Mists.logger.info("Location "+this.getName()+" entered. Preparing area...");
        if (p.isRemovable()) p.setRemovable(false);
        this.setPlayer(p);
        this.addPlayerCharacter(p, entryPoint[0], entryPoint[1]);
        Mists.logger.info("Placing player at "+entryPoint[0]+"x"+entryPoint[1]);
        //Add companions)
        if (p.getCompanions() != null) {
            for (Creature c : p.getCompanions()) {
                if (c.isRemovable()) c.setRemovable(false);
                this.addCreature(c, entryPoint[0], entryPoint[1]);
                Mists.logger.info("Placing "+c.getName()+" at "+entryPoint[0]+"x"+entryPoint[1]);
            }
        }
        this.screenFocus = p;
    }
    
    /**
     * Entering Location directly to target coordinates,
     * like from another Location
     * @param p PlayerCharacter that's entering the location
     * @param xCoor XCoordinate for where player is entering
     * @param yCoor YCoordinate for where player is entering
     */
    public void enterLocation(PlayerCharacter p, double xCoor, double yCoor) {
        this.enterLocation(p, new double[]{xCoor, yCoor});
    }
    
    /**
     * Entering Location from a WorldMap node (entranceNode)
     * Should prepare the location for player.
     * @param p PlayerCharacter entering the location
     * @param entranceNode WorldMap node the player enters from
     */
    public void enterLocation(PlayerCharacter p, MapNode entranceNode) {
        if (this.getEntrace() != null && entranceNode != null) {
            Mists.logger.log(Level.INFO, "Setting exit node to {0}", entranceNode.getName());
            if (this.getEntrace().getExitNode() == null) this.getEntrace().setExitNode(entranceNode);
        }
        double[] entryPoint = this.getEntryPoint(entranceNode);
        this.enterLocation(p, entryPoint);
    }
    
    private class CoordinateComparator implements Comparator<MapObject> {

        @Override
        public int compare(MapObject m1, MapObject m2) {
            return (int)(m1.getCenterYPos() - m2.getCenterYPos());
        }
        
    }
    
    @Override
    public void write(Kryo kryo, Output output) {
        super.write(kryo, output);

        output.writeInt(this.baseLocationID);
        output.writeString(this.name);
        //Write Map
        
        byte mapType = -1;
        if (this.map instanceof TileMap) mapType = 1;
        if (this.map instanceof BGMap) mapType = 2;
        output.writeByte(mapType);
        if (mapType != -1) kryo.writeClassAndObject(output, this.map);
        
        //Write Environment
        kryo.writeClassAndObject(output, this.environment);
        //Write Structures
        output.writeInt(this.structures.size());
        int structureCount = 0;
        for (Structure s : this.structures) {
                kryo.writeClassAndObject(output, s);
                if(!(s instanceof Water))Mists.logger.info("Wrote structure "+s.getName()+" in "+this.getName()+" as #"+structureCount);
                structureCount++;
        }
        //Write Creatures
        int creatureCount = this.creatures.size();
        if (this.creatures.contains(this.player)) creatureCount--;
        output.writeInt(creatureCount);
        for (Creature c : this.creatures) {
                if (!c.equals(this.player))kryo.writeClassAndObject(output, c);
        }
        //Write TriggerPlates
        output.writeInt(this.triggerPlates.size());
        for (TriggerPlate t : this.triggerPlates) {
                kryo.writeClassAndObject(output, t);
        }
        //NOTE: Effects are not saved or loaded right now. 
        //TODO: Consider if it saving Effects would make sense
        output.writeInt(this.roofs.size());
        for (Roof r : this.roofs) {
                kryo.writeClassAndObject(output, r);
        }

    }

    @Override
    public void read(Kryo kryo, Input input) {

        //Clean base
        this.creatures = new ArrayList<>();
        this.structures = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.triggerPlates = new ArrayList<>();
        super.read(kryo, input);
        //Basic info
        this.baseLocationID = input.readInt();
        this.name = input.readString();
        Mists.logger.info("Initialized map: "+this.name+" ID: "+this.baseLocationID);
        //Read map;
        
        byte mapType = input.readByte();
        switch (mapType) {
            case 1:  //Map is a TileMap
                Mists.logger.info("Reading tilemap");
                this.map = (TileMap)kryo.readClassAndObject(input);
                ((TileMap)this.map).buildMap();
                break;
            case 2: //Map is a BGMap
                Mists.logger.info("Reading BGMap");
                this.map = (BGMap)kryo.readClassAndObject(input);
                break;
            default: //Map is of unknown type 
                Mists.logger.warning("Tried to deserialize a location with unknown maptype!");
                break;
        }
        Mists.logger.info("Map loaded succesfully");
        this.localizeMap();
        
        //Mists.logger.info("Map Localized");
        //Read Environment
        this.environment = (LocationEnvironment)kryo.readClassAndObject(input);
        Mists.logger.info("Environment loaded succesfully");
        //Read Structures
        int structureCount = input.readInt();
        int loadedStructures = 0;
        Mists.logger.info("Loading "+structureCount+" Structures");
        for (int i = 0; i < structureCount; i++) {
                Structure s = (Structure)kryo.readClassAndObject(input);
                if (s == null) {
                	Mists.logger.warning("Error loading unknown Structure #"+i+" in Location "+this.name+" ("+this.baseLocationID+")");
                	continue;
                }
                if (s instanceof Structure) this.addMapObject(s, s.getID());
                loadedStructures++;
        }
        this.updateAllVariableGraphicStructures();
        Mists.logger.info(loadedStructures+"/"+structureCount+" Structures loaded succesfully");
        //Read Creatures
        int creatureCount = input.readInt();
        Mists.logger.info("Loading "+creatureCount+" Creatures");
        for (int i = 0; i < creatureCount; i++) {
                Creature c = (Creature)kryo.readClassAndObject(input);
                if (c == null) {
                	Mists.logger.warning("Error loading unknown Creature in Location "+this.name+" ("+this.baseLocationID+")");
                	continue;
                }
                if (c instanceof Creature) this.addMapObject(c, c.getID());
        }
        Mists.logger.info("Creatures loaded succesfully");
        //Read TriggerPlates
        int tpCount = input.readInt();
        Mists.logger.info("Loading "+tpCount+" TriggerPlates");
        for (int i = 0; i < tpCount; i++) {
                TriggerPlate t = (TriggerPlate)kryo.readClassAndObject(input);
                if (t == null) {
                	Mists.logger.warning("Error loading unknown TriggerPlate in Location "+this.name+" ("+this.baseLocationID+")");
                	continue;
                }
                if (t instanceof TriggerPlate) this.addMapObject(t, t.getID());
        }
        Mists.logger.info("TriggerPlates loaded succesfully");
        //Read Roofs
        int roofCount = input.readInt();
        for (int i = 0; i < roofCount; i++){
                Roof r = (Roof)kryo.readClassAndObject(input);
                this.roofs.add(r);
        }
        Mists.logger.info("Roofs loaded succesfully");
    }
    
}
