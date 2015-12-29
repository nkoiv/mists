/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.world.mapgen.DungeonGenerator;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.ui.Overlay;
import com.nkoiv.mists.game.world.pathfinding.CollisionMap;
import com.nkoiv.mists.game.world.pathfinding.PathFinder;
import com.nkoiv.mists.game.world.util.Flags;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Location is the main playfield of the game. It could be a castle, forest, dungeon or anything in between.
 * A Location stores the background data via the Map classes, and everything on top of it by MapObjects.
 * 
 * @author nkoiv
 */
public class Location extends Flags implements Global {
    
    /*
    * TODO: Lists for various types of MapObjects, from creatures to frills.
    */
    //private QuadTree mobQuadTree; //Used for collision detection //retired idea for now
    private HashMap<Integer, HashSet> spatial; //New idea for lessening collision detection load
    private ArrayList<Creature> creatures;
    private ArrayList<Structure> structures;
    private List<Effect> effects;
    private List<MapObject> targets;
    private String name;
    private GameMap map;
    private CollisionMap collisionMap;
    private PathFinder pathFinder;
    private DungeonGenerator mapGen;
    private LightsRenderer lights;
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
    
    
    public Location(String name, String mapPath, int maptype) {
        this.name = name;
        this.creatures = new ArrayList<>();
        this.structures = new ArrayList<>();
        //---Quad Tree stuff----
        //this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,800,600));        
        //----------------------
        this.effects = new ArrayList<>();
        if (maptype == 0) this.loadMap(new BGMap(new Image(mapPath)));
        if (maptype == 1) this.loadMap(new TileMap(mapPath));
        this.localizeMap();
    }
    
    public Location(String name, GameMap map) {
        this.name = name;
        this.creatures = new ArrayList<>();
        this.structures = new ArrayList<>();
        //---Quad Tree stuff----
        //this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,800,600));
        //----------------------
        this.effects = new ArrayList<>();
        this.loadMap(map);
        this.localizeMap();
    }
    
    
    /**TODO: This general constructor is just for the Proof of Concept -map
    * and should be removed later to avoid misuse
    * @param player Player to construct the (TEST)location around
    */
    public Location(PlayerCharacter player) {
        Mists.logger.info("Generating POC location...");
        this.name = "POCmap";
        //this.mapObjects = new ArrayList<>();
        this.creatures = new ArrayList<>();
        this.structures = new ArrayList<>();
        //---Quad Tree stuff----
        //this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,800,600));
        //----------------------
        this.effects = new ArrayList<>();
        this.mapGen = new DungeonGenerator();
        //this.loadMap(new BGMap(new Image("/images/pocmap.png")));
        //this.loadMap(new TileMap("/mapdata/pathfinder_test.map"));
        Mists.logger.info("Generating new BSP dungeon...");
        this.loadMap(DungeonGenerator.generateDungeon(this, 60, 40));
        Mists.logger.info("Dungeon generated");
        Mists.logger.info("Localizing map...");
        this.localizeMap();
        Mists.logger.info("Map localized");
        this.setPlayer(player);
        this.addCreature(player, 8*TILESIZE, 6*TILESIZE); // <-TODO: Replace with putting player to start
        this.screenFocus = player;
        //TODO: Create structures from structure library once its finished
        Mists.logger.info("Generating random structures and creatures");
        Structure rock = Mists.structureLibrary.create("Rock", this, 0, 0);

        this.setMobInRandomOpenSpot(rock);
        
        for (int i = 0; i<10;i++) {
            //Make a bunch of trees
            Structure tree = Mists.structureLibrary.create("Tree");
            this.addStructure(tree, 2*TILESIZE, 10*TILESIZE);   
            this.setMobInRandomOpenSpot(tree);
            Mists.logger.info("Created a "+tree.getName()+" at "+(int)tree.getCenterXPos()+"x"+(int)tree.getCenterYPos());
        }
        
        for (int i = 0; i<10;i++) {
            //Make a bunch of itempiles
            ItemContainer pile = new ItemContainer("ItemPile", new Sprite(Mists.graphLibrary.getImage("blank")));
            pile.setRenderContent(true);
            pile.addItem(Mists.itemLibrary.create("sword"));
            pile.addItem(Mists.itemLibrary.create("himmutoy"));
            this.addStructure(pile, 0, 0);
            this.setMobInRandomOpenSpot(pile);
            Mists.logger.info("Created a "+pile.getName()+" at "+(int)pile.getCenterXPos()+"x"+(int)pile.getCenterYPos());
        }
        
        for (int i = 0; i < 20; i++) {
            //Make a bunch of monsters
            //Random graphic from sprite sheet
            Random rnd = new Random();
            int randomMob = rnd.nextInt(4);
            Creature monster;
            switch (randomMob) {
                case 0: monster = Mists.creatureLibrary.create("worm"); break;
                case 1: monster = Mists.creatureLibrary.create("swampy"); break;
                case 2: monster = Mists.creatureLibrary.create("eggy"); break;
                case 3: monster = Mists.creatureLibrary.create("rabbit"); break;
                default: monster = Mists.creatureLibrary.create("worm");
            }
            
            this.addCreature(monster, 2*TILESIZE, 10*TILESIZE);   
            this.setMobInRandomOpenSpot(monster);
        }
        
        Mists.logger.info("Location generation complete");
        
        this.setMobInRandomOpenSpot(player);
        
        this.lights.setMinLightLevel(0);
        
    }
    
    /**
     * Construct the little things needed to make
     * the map playable (collisionmaps, lights...)
     */
    private void localizeMap() {
        this.collisionMap = new CollisionMap(this, 32);
        this.collisionMap.setStructuresOnly(true);
        this.collisionMap.updateCollisionLevels();
        this.collisionMap.printMapToConsole();
        this.pathFinder = new PathFinder(this.collisionMap, 100, true);
        this.lights = new LightsRenderer(this);
        this.targets = new ArrayList<>();
        this.spatial = new HashMap<>();
        //this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,this.map.getWidth(),this.map.getHeight()));
        Mists.logger.log(Level.INFO, "Map ({0}x{1}) localized", new Object[]{map.getWidth(), map.getHeight()});
    }

    /**
    * MapLoader takes in a Map and initializes all the static structures from it for the Location
    * @param map Map to load
    */
    public void loadMap(GameMap map) {
        this.map = map;
        // Add in all the static structures from the selected map
        int addedStructures = 0;
        ArrayList<Structure> staticStructures = map.getStaticStructures(this);
        for (Structure s : staticStructures) {
            this.addStructure(s, s.getXPos(), s.getYPos());
            addedStructures++;
        }
        Mists.logger.log(Level.INFO, "{0} structures generated", addedStructures);
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
        if (!this.creatures.isEmpty()) {
            for (Creature mob : this.creatures) {
                if (xCoor >= mob.getXPos() && xCoor <= mob.getXPos()+mob.getWidth()) {
                    if (yCoor >= mob.getYPos() && yCoor <= mob.getYPos()+mob.getHeight()) {
                        //Do a pixelcheck on the mob;
                        //if (Sprite.pixelCollision(xCoor, yCoor, Mists.pixel, mob.getXPos(), mob.getYPos(), mob.getSprite().getImage())) {
                            return mob;
                        //}
                    }
                }
                
            }
        }
        if (!this.structures.isEmpty()) {
            for (Structure mob : this.structures) {
                if (xCoor >= mob.getXPos() && xCoor <= mob.getXPos()+mob.getWidth()) {
                    if (yCoor >= mob.getYPos() && yCoor <= mob.getYPos()+mob.getHeight()) {
                        return mob;
                    }
                }
                
            }
        }
        return mobAtLocation;
    }
    
    /**
    * Find a random opening on the map via getRandomOpenSpot and set the given MapObject in it.
    * @param mob MapObject to be positioned
    */
    public void setMobInRandomOpenSpot (MapObject mob) {
        double[] openSpot = this.getRandomOpenSpot(mob.getWidth());
        mob.setPosition(openSpot[0], openSpot[1]);
    }
    
    private double[] getEntryPoint() {
        /*TODO: use the HashMap entryPoints to select
        * where the player lands when he first enters the location
        */
        return this.getRandomOpenSpot(this.getPlayer().getWidth());
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
        while (!foundSpot) {
            openX = (int)sizeRequirement + rnd.nextInt(((int)(map.getWidth()-sizeRequirement)));
            openX = (openX / Global.TILESIZE) * Global.TILESIZE;
            openY = (int)sizeRequirement + rnd.nextInt(((int)(map.getHeight()-sizeRequirement)));
            openY = (openY / Global.TILESIZE) * Global.TILESIZE;
            collisionTester.setCenterPosition(openX, openY);
            if (this.checkCollisions(collisionTester).isEmpty()) foundSpot = true;
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
    * Adds a Structure to the location
    * @param s The structure to be added
    * @param xPos Position for the structure on the X-axis
    * @param yPos Position for the structure on the Y-axis
    */
    public void addStructure(Structure s, double xPos, double yPos) {
        if (!this.structures.contains(s)) {
            this.structures.add(s);    
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
    public void addCreature(Creature c, double xPos, double yPos) {
        if (!this.creatures.contains(c)) {
            this.creatures.add(c);
        }
        c.setLocation(this);
        c.setCenterPosition(xPos, yPos);
    }
    
    /** Adds an Effect to the location
    * @param e The effect to be added
    * @param xPos Position for the effect on the X-axis
    * @param yPos Position for the effect on the Y-axis
    */
    public void addEffect(Effect e, double xPos, double yPos) {
        if (!this.effects.contains(e)) {
            this.effects.add(e);
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
            this.creatures.add(p);
        }
        p.setLocation(this);
        p.setPosition(xPos, yPos);
    }
    
    private void setMap(GameMap m) {
        this.map = m;
    }
    
    public void setMapGen (DungeonGenerator mg) {
        this.mapGen = mg;
    }
    
    public DungeonGenerator getMapGen() {
        return this.mapGen;
    }
    
    public List<Creature> getCreatures() {
        return this.creatures;
    }
    
    public List<Structure> getStructures() {
        return this.structures;
    }
    
    public void removeMapObject (MapObject o) {
        if (o instanceof Creature) {
            if(this.creatures.contains((Creature)o)) this.creatures.remove(o);
        } else if (o instanceof Structure) {
            if(this.structures.contains((Structure)o)) this.structures.remove(o);
        } else if (o instanceof Effect) {
            if(this.effects.contains((Effect)o)) this.effects.remove(o);
        }
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
    * Movement, combat and triggers should all be handled here
    * TODO: Not everything needs to happen on every tick. Mobs should make new decisions only ever so often
    * @param time Time since the last update
    */
    public void update (double time) {
        this.updateSpatials();
        this.collisionMap.updateCollisionLevels();
        structureCleanup();
        if (!this.creatures.isEmpty()) {
            for (Creature mob : this.creatures) { //Mobs do whatever mobs do
                mob.update(time);
            }
        }
        creatureCleanup();
        //Check collisions
        if (!this.effects.isEmpty()) {
            //Mists.logger.info("Effects NOT empty");
            for (Effect e : this.effects) { //Handle effects landing on something
                e.update(time);
                ArrayList<MapObject> collisions = this.checkCollisions(e);
                if (!collisions.isEmpty()) {       
                    e.getOwner().hitOn(collisions);
                }
            }
        }
        effectCleanup();
    }
    
    /**
     * structureCleanup cleans all the "removable"
     * flagged structures.
     */
    private void structureCleanup() {
        //Structure cleanup
        if (!this.structures.isEmpty()) {
            ArrayList<Wall> removedWalls = new ArrayList();
            Iterator<Structure> structureIterator = structures.iterator(); //Cleanup of mobs
            while (structureIterator.hasNext()) {
                MapObject mob = structureIterator.next();
                if (mob.isFlagged("removable")) {
                    if (mob instanceof Wall) {
                        removedWalls.add((Wall)mob);
                        //Update the surrounding walls as per needed
                        //this.updateWallsAt(mob.getCenterXPos(), mob.getCenterYPos());   
                    }
                    structureIterator.remove();
                    this.pathFinder.setMapOutOfDate(true);
                }
            }  
            this.restructureWalls(removedWalls);
        }
    }
    
     /**
     * creatureCleanup cleans all the "removable"
     * flagged creatures.
     */
    private void creatureCleanup() {
        //Creature cleanup
        if (!this.creatures.isEmpty()) {
            Iterator<Creature> creatureIterator = creatures.iterator(); //Cleanup of mobs
            while (creatureIterator.hasNext()) {
                MapObject mob = creatureIterator.next();
                if (mob.isFlagged("removable")) {
                    creatureIterator.remove();
                    //this.pathFinder.setMapOutOfDate(true); //Creatures are not on pathFindermap atm
                }
            }     
        }
    }
    
     /**
     * effectCleanup cleans all the "removable"
     * flagged Effects.
     */
    private void effectCleanup() {
        //Effects cleanup
        if (!this.effects.isEmpty()) {
            Iterator<Effect> effectsIterator = effects.iterator(); //Cleanup of effects
            while (effectsIterator.hasNext()) {
                if (effectsIterator.next().isFlagged("removable")) {
                    effectsIterator.remove();
                } 
            }
        }
        
    }

    /**
     * Update the QuadTree by clearing it and adding
     * all mobs in the mapObjects.
     * TODO: Consider keeping structures and creatures separate as creatures are updated more often(?)
     */
    /*
    private void updateQuadTree() {
        mobQuadTree.clear();
        for (Creature creature : creatures) {
            mobQuadTree.insert(creature);
        }
        for (Structure structure : structures) {
            mobQuadTree.insert(structure);
        }
    }
    */
    
    private void updateSpatials() {
        if (this.spatial == null) this.spatial = new HashMap<>();
        clearSpatials();
        for (MapObject mob : this.creatures) {
            HashSet<Integer> mobSpatials = getSpatials(mob);
            for (Integer i : mobSpatials) {
                addToSpatial(mob, i, this.spatial);
            }
        }
        
        /* [ 1][ 2][ 3][ 3]
        *  [ 4][ 5][ 6][ 7]
        *  [ 8][ 9][10][11]
        *  [12][13][14][15]
        * Above a spatial node is the node number -5,
        * below it is node number +5 and sides are +/- 1
        */
        
    }
    
    private static void addToSpatial (MapObject mob, int spatialID, HashMap<Integer, HashSet> spatial) {
        if (spatial.get(spatialID) == null) spatial.put(spatialID, new HashSet<MapObject>());
        spatial.get(spatialID).add(mob);
    }
    
    private void clearSpatials () {
        if (this.spatial != null) this.spatial.clear();
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
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(4); w.updateNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos+Mists.TILESIZE, yCenterPos)); //Right
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(3); w.updateNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos, yCenterPos-Mists.TILESIZE)); //Up
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(6); w.updateNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos, yCenterPos+Mists.TILESIZE)); //Down
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(1); w.updateNeighbours();}
        //Diagonal directions
        mob = (this.getMobAtLocation(xCenterPos-Mists.TILESIZE, yCenterPos-Mists.TILESIZE)); //UpLeft
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(7); w.updateNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos+Mists.TILESIZE, yCenterPos-Mists.TILESIZE)); //UpRight
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(5); w.updateNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos+Mists.TILESIZE, yCenterPos+Mists.TILESIZE)); //DownLeft
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(2); w.updateNeighbours();}
        mob = (this.getMobAtLocation(xCenterPos+Mists.TILESIZE, yCenterPos+Mists.TILESIZE)); //DownRight
        if (mob instanceof Wall) {Wall w = (Wall)mob; w.removeNeighbour(0); w.updateNeighbours();}

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
            addMapObjectCollisions(o, this.spatial.get(i), collidingObjects);
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
            //If the objects are further away than their combined width/height, they cant collide
            if ((Math.abs(collidingObject.getCenterXPos() - mob.getCenterXPos())
                 > (collidingObject.getWidth() + mob.getWidth()))
                || (Math.abs(collidingObject.getCenterYPos() - mob.getCenterYPos())
                 > (collidingObject.getHeight() + mob.getHeight()))) {
                //Objects are far enough from oneanother
            } else {
                if (!collidingObject.equals(mob)) { // Colliding with yourself is not really a collision
                if ( mob.intersects(collidingObject) ) 
                 {
                    collidingObjects.add(collidingObject);
                }
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
        if (this.collisionMap.isBlocked(mob.getCrossableTerrain(),(int)(downright[0]/collisionMap.nodeSize), (int)(downright[1]/collisionMap.nodeSize))) return true;
        return false;
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
        return collidedDirections;
    }
    
    public void render (GraphicsContext gc) {
        /*
        * Update Offsets first to know which parts of the location are drawn
        */
        double xOffset = getxOffset(gc, screenFocus.getXPos());
        double yOffset = getyOffset(gc, screenFocus.getYPos());
        //Mists.logger.info("Offset: "+xOffset+","+yOffset);
        this.renderMap(gc, xOffset, yOffset);
        
        this.lastRenderedMapObjects = this.renderMobs(gc, xOffset, yOffset);
        this.renderLights(gc, lastRenderedMapObjects, xOffset, yOffset);
        this.renderStructureExtras(gc, lastRenderedMapObjects, xOffset, yOffset);
        this.renderExtras(gc, xOffset, yOffset);
        
        
    }
    

    
    
    //TODO: Move this to a separate class
    private void renderLights(GraphicsContext gc, List<MapObject> MOBsOnScreen, double xOffset, double yOffset) {
        //Raycast from player to all screen corners and to corners of all visible structures
        List<Structure> StructuresOnScreen = new ArrayList<>();
        //List<Creature> CreaturesOnScreen = new ArrayList<>();
        for (MapObject mob : MOBsOnScreen) {
            if (mob instanceof Structure) StructuresOnScreen.add((Structure)mob);
            //if (mob instanceof Creature) CreaturesOnScreen.add((Creature)mob);
        }
        MapObject[] structures = new MapObject[StructuresOnScreen.size()];
        for (int i = 0; i < StructuresOnScreen.size(); i++) {
            structures[i] = StructuresOnScreen.get(i);
        }
        lights.updateObstacles(structures, xOffset, yOffset);
        lights.paintVision(player.getCenterXPos(), player.getCenterYPos(), 8);
        lights.renderLightMap(gc, xOffset, yOffset);
        //lights.renderLight(gc, player.getXPos()-xOffset, player.getYPos()-yOffset, 1, 1);
        
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
                    if (DRAW_COLLISIONS) { // Draw collision boxes for debugging purposes, if the Global variable is set
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
                    if (DRAW_COLLISIONS) { // Draw collision boxes for debugging purposes, if the Global variable is set
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
        double lightlevel;
        ColorAdjust lightmap = new ColorAdjust();
        if (!renderedMOBs.isEmpty()) {
            for (MapObject struct : renderedMOBs) {
                if (struct instanceof Structure) {
                    lightlevel = lights.lightmap[(int)struct.getXPos()/Mists.TILESIZE][(int)struct.getCenterYPos()/Mists.TILESIZE];
                    lightlevel = lightlevel-1;
                    lightmap.setBrightness(lightlevel); gc.setEffect(lightmap);
                    ((Structure)struct).renderExtras(xOffset, yOffset, gc); //Draw extra frill (leaves on trees etc)
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
                if (DRAW_COLLISIONS) { // Draw collision boxes for debugging purposes, if the Global variable is set
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
        
        if (DRAW_GRID) {
            gc.setStroke(Color.ANTIQUEWHITE);
            double tileHeight = map.getHeight()/TILESIZE;
            double tileWidth = map.getWidth()/TILESIZE;
            for (int i=0; i<tileHeight;i++) {
                gc.strokeLine(0, (i*TILESIZE)-yOffset, map.getWidth(), (i*TILESIZE)-yOffset);
            }
            for (int i=0; i<tileWidth;i++) {
                gc.strokeLine((i*TILESIZE)-xOffset, 0, (i*TILESIZE)-xOffset, map.getHeight());
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
    
    public void setMinLightLevel(double lightlevel) {
        double ll = lightlevel;
        if (ll>1.0) ll=1.0;
        if (ll<0.0) ll=0.0;
        this.lights.setMinLightLevel(ll);
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
    
    @Override
    public String toString() {
        String s;
        s = this.name+", a "+this.map.getWidth()+"x"+this.map.getHeight()+" area with "
                +this.creatures.size()+" creatures and "+this.structures.size()+" structures";
        return s;
    }

    /**
     *  ExitLocation should clean up the location for re-entry later on
     */
    public void exitLocation() {
        Mists.logger.log(Level.INFO, "Location {0} exited, cleaning up...", this.getName());
    }

    /**
     *  EnterLocation should prepare the location for player
     * @param p PlayerCharacter entering the location
     */
    public void enterLocation(PlayerCharacter p) {
        Mists.logger.info("Location "+this.getName()+" entered. Preparing area...");
        this.setPlayer(p);
        double[] entryPoint = this.getEntryPoint();
        this.addPlayerCharacter(p, entryPoint[0], entryPoint[1]);
        //Add companions)
        if (p.getCompanions() != null) {
            for (Creature c : p.getCompanions()) {
                this.addCreature(c, entryPoint[0], entryPoint[1]);
            }
        }
        this.screenFocus = p;
        
    }
    
    private class CoordinateComparator implements Comparator<MapObject> {

        @Override
        public int compare(MapObject m1, MapObject m2) {
            return (int)(m1.getCenterYPos() - m2.getCenterYPos());
        }
        
    }
    
}
