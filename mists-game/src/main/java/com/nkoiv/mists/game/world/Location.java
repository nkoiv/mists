/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.world.pathfinding.CollisionMap;
import com.nkoiv.mists.game.world.pathfinding.PathFinder;
import com.nkoiv.mists.game.world.util.QuadTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Location is the main playfield of the game. It could be a castle, forest, dungeon or anything in between.
 * A Location stores the background data via the Map classes, and everything on top of it by MapObjects.
 * 
 * @author nkoiv
 */
public class Location implements Global {
    
    /*
    * TODO: Lists for various types of MapObjects, from creatures to frills.
    */
    private QuadTree mobQuadTree; //Used for collision detection
    private ArrayList<MapObject> mapObjects;
    //private List<MapObject> mapObjects;
    private List<Effect> effects;
    private String name;
    private GameMap map;
    private CollisionMap collisionMap;
    private PathFinder pathFinder;
    private MapGenerator mapGen;
    private LightsRenderer lights;
    private final double[] lastOffsets = new double[2];
    private MapObject screenFocus;
    private PlayerCharacter player;
    private final HashMap<String, Integer> flags = new HashMap<>();
    private final HashMap<Integer, double[]> entryPoints = new HashMap<>();
    
    public boolean mobsChanged;
    
    /*
    * Constructor for demofield
    * TODO: load this from some XML or somesuch
    */
    
    
    public Location(String name, String mapPath, int maptype) {
        this.name = name;
        this.mapObjects = new ArrayList<>();
        this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,800,600));
        this.effects = new ArrayList<>();
        if (maptype == 0) this.loadMap(new BGMap(new Image(mapPath)));
        if (maptype == 1) this.loadMap(new TileMap(mapPath));
        this.localizeMap();
    }
    
    public Location(String name, GameMap map) {
        this.name = name;
        this.mapObjects = new ArrayList<>();
        this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,800,600));
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
        this.mapObjects = new ArrayList<>();
        this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,800,600));
        this.effects = new ArrayList<>();
        this.mapGen = new MapGenerator();
        //this.loadMap(new BGMap(new Image("/images/pocmap.png")));
        //this.loadMap(new TileMap("/mapdata/pathfinder_test.map"));
        Mists.logger.info("Generating new BSP dungeon...");
        this.loadMap(MapGenerator.generateDungeon(this, 60, 40));
        Mists.logger.info("Dungeon generated");
        Mists.logger.info("Localizing map...");
        this.localizeMap();
        Mists.logger.info("Map localized");
        this.setPlayer(player);
        this.addCreature(player, 8*TILESIZE, 6*TILESIZE); // <-TODO: Replace with putting player to start
        this.screenFocus = player;
        //TODO: Create structures from structure library once its finished
        Mists.logger.info("Generating random structures and creatures");
        Structure rock = new Structure("Rock", new Image("/images/block.png"), this, 10*TILESIZE, 7*TILESIZE);
        this.mapObjects.add(rock);
        this.setMobInRandomOpenSpot(rock);
        
        for (int i = 0; i<10;i++) {
            //Make a bunch of trees
            Structure tree = new Structure("Tree", new Image("/images/tree_stump.png"), this, 6*TILESIZE, 5*TILESIZE);
            tree.addExtra(new Image("/images/tree.png"), -35, -96);
            this.mapObjects.add(tree);
            this.setMobInRandomOpenSpot(tree);
        
        }
        
        for (int i = 0; i < 10 ; i++) {
            //Make a bunch of monsters
            //Random graphic from sprite sheet
            Random rnd = new Random();
            int startX = rnd.nextInt(1);
            int startY = rnd.nextInt(2);
            Mists.logger.info("Creating monster from sprite sheet position "+startX+","+startY);
            Creature monster = new Creature("Otus", new ImageView("/images/monster_small.png"), 3, startX*3, startY*4, 0, 0, 32, 32);
            monster.getSprite().setCollisionAreaShape(2);
            this.addCreature(monster, 2*TILESIZE, 10*TILESIZE);   
            this.setMobInRandomOpenSpot(monster);
        }
        
        Creature monster = new Creature("Otus", new ImageView("/images/monster_small.png"), 3, 0, 0, 32, 32);
        monster.getSprite().setCollisionAreaShape(2);
        this.addCreature(monster, 2*TILESIZE, 10*TILESIZE);   
        this.setMobInRandomOpenSpot(monster);
        
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
        this.mobQuadTree = new QuadTree(0, new Rectangle(0,0,this.map.getWidth(),this.map.getHeight()));
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
            this.mapObjects.add(s);
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
        ArrayList<Creature> creatures = new ArrayList<>();
        for (MapObject mob : this.mapObjects) {
            if (mob instanceof Creature) creatures.add((Creature)mob);
        }
        for (Creature c : creatures) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * When getting a MapObject by coordinates with mouseclick
     * or something, it's often needed to substract xOffset and yOffset
     * from coords.
     * 
     * @param xCoor
     * @param yCoor
     * @return Creature found at the coordinates
     */
    public MapObject getMobAtLocation(double xCoor, double yCoor) {
        MapObject mobAtLocation = null;
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) {
                if (xCoor >= mob.getXPos() && xCoor <= mob.getXPos()+mob.getSprite().getWidth()) {
                    if (yCoor >= mob.getYPos() && yCoor <= mob.getYPos()+mob.getSprite().getHeight()) {
                        mobAtLocation = mob;
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
        double[] openSpot = this.getRandomOpenSpot(mob.getSprite().getWidth());
        mob.setPosition(openSpot[0], openSpot[1]);
    }
    
    private double[] getEntryPoint() {
        /*TODO: use the HashMap entryPoints to select
        * where the player lands when he first enters the location
        */
        return this.getRandomOpenSpot(this.getPlayer().getSprite().getWidth());
    }
            
    /**
    * Brute force setting a dummy around the map until we find a an open spot
    * TODO: This could get stuck in infinite loop
    * @param sizeRequirement Size of the item to be placed (in pixel width)
    * @return An array with x and y coordinates for the (center) of the free spot
    */
    private double[] getRandomOpenSpot(double sizeRequirement) {
        //
        Creature collisionTester = new Creature("CollisionTester", new Image("/images/himmuToy.png"));
        collisionTester.getSprite().setWidth(sizeRequirement);
        collisionTester.getSprite().setHeight(sizeRequirement);
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
        if (!this.mapObjects.contains(s)) {
            this.mapObjects.add(s);    
        }
        s.setLocation(this);
        s.getSprite().setPosition(xPos, yPos);
        this.pathFinder.setMapOutOfDate(true);
    }
    
    /** Adds a Creature to the location
    * @param c The creature to be added
    * @param xPos Position for the creature on the X-axis
    * @param yPos Position for the creature on the Y-axis
    */
    public void addCreature(Creature c, double xPos, double yPos) {
        if (!this.mapObjects.contains(c)) {
            this.mapObjects.add(c);
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
        if (!this.mapObjects.contains(e)) {
            this.effects.add(e);
        }
        e.setLocation(this);
        e.getSprite().setPosition(xPos, yPos);
    }
    
    /** Adds an Effect to the location
    * @param p The effect to be added
    * @param xPos Position for the effect on the X-axis
    * @param yPos Position for the effect on the Y-axis
    */
    public void addPlayerCharacter(PlayerCharacter p, double xPos, double yPos) {
        if (!this.mapObjects.contains(p)) {
            this.mapObjects.add(p);
        }
        p.setLocation(this);
        p.getSprite().setPosition(xPos, yPos);
    }
    
    private void setMap(GameMap m) {
        this.map = m;
    }
    
    public void setMapGen (MapGenerator mg) {
        this.mapGen = mg;
    }
    
    public MapGenerator getMapGen() {
        return this.mapGen;
    }
    
    public List<MapObject> getMOBList() {
        return this.mapObjects;
    }
    
    public void removeMapObject (MapObject o) {
        if(this.mapObjects.contains(o)) this.mapObjects.remove(o);
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
        //this.updateQuadTree();
        ArrayList<Wall> removedWalls = new ArrayList();
        this.collisionMap.updateCollisionLevels();
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) { //Mobs do whatever mobs do
                mob.update(time);
            }
            Iterator<MapObject> mobIterator = mapObjects.iterator(); //Cleanup of mobs
            while (mobIterator.hasNext()) {
                MapObject mob = mobIterator.next();
                if (mob.isFlagged("removable")) {
                    if (mob instanceof Wall) {
                        removedWalls.add((Wall)mob);
                        //Update the surrounding walls as per needed
                        //this.updateWallsAt(mob.getCenterXPos(), mob.getCenterYPos());   
                    }
                    mobIterator.remove();
                    this.pathFinder.setMapOutOfDate(true);
                }
            }   
        }
        this.restructureWalls(removedWalls);
        
        if (!this.effects.isEmpty()) {
            //Mists.logger.info("Effects NOT empty");
            for (Effect e : this.effects) { //Handle effects landing on something
                if (!this.checkCollisions(e).isEmpty()) {
                    
                    e.getOwner().hitOn(this.checkCollisions((e)));
                }
            }
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
    private void updateQuadTree() {
        mobQuadTree.clear();
        for (MapObject mapObject : mapObjects) {
            mobQuadTree.insert(mapObject);
        }
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
    *  Returns a List with all the objects that collide with MapObject o
    * Now with quad tree to check only objects nearby
    * @param o The MapObject to check collisions with
    * @return a List with all the objects that collide with MapObject o
    */
    public ArrayList<MapObject> checkCollisions (MapObject o) {

        /* New QuadTree based collision detection
        ArrayList<MapObject> nearbyObjects = new ArrayList<>();
        mobQuadTree.retrieve(nearbyObjects, o);
        //System.out.println("nearby objects: "+nearbyObjects.size());
        Iterator<MapObject> nearbyObjectsIter = nearbyObjects.iterator();
        
        while (nearbyObjectsIter.hasNext()) {
            MapObject collidingObject = nearbyObjectsIter.next();
            if (!collidingObject.instersects(o) || collidingObject.equals(o)) {
               nearbyObjectsIter.remove();
            }
        }
        
        
        //System.out.println("colliding objects: "+nearbyObjects.size());
        return nearbyObjects;
        */
        
        //Old collision code (pre QuadTree)
        ArrayList<MapObject> collidingObjects = new ArrayList<>();
        Iterator<MapObject> mapObjectsIter = mapObjects.iterator();
        while ( mapObjectsIter.hasNext() )
        {
            MapObject collidingObject = mapObjectsIter.next();
            //If the objects are further away than their combined width/height, they cant collide
            if ((Math.abs(collidingObject.getCenterXPos() - o.getCenterXPos())
                 > (collidingObject.getSprite().getWidth() + o.getSprite().getWidth()))
                || (Math.abs(collidingObject.getCenterYPos() - o.getCenterYPos())
                 > (collidingObject.getSprite().getHeight() + o.getSprite().getHeight()))) {
                //Objects are far enough from oneanother
            } else {
                if (!collidingObject.equals(o)) { // Colliding with yourself is not really a collision
                if ( o.instersects(collidingObject) ) 
                 {
                    collidingObjects.add(collidingObject);
                }
            }
            }
            
        }
        return collidingObjects;
        
        
    }
    
    public HashSet<Direction> collidedSides (MapObject mob) {
        ArrayList<MapObject> collidingObjects = this.checkCollisions(mob); //Get the colliding object(s)
        HashSet<Direction> collidedDirections = new HashSet<>();   
        
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
        double xOffset = getxOffset(gc, screenFocus.getSprite().getXPos());
        double yOffset = getyOffset(gc, screenFocus.getSprite().getYPos());
        //Mists.logger.info("Offset: "+xOffset+","+yOffset);
        this.renderMap(gc, xOffset, yOffset);
        
        List<MapObject> renderedMOBs = this.renderMobs(gc, xOffset, yOffset);
        this.renderLights(gc, renderedMOBs, xOffset, yOffset);
        this.renderStructureExtras(gc, renderedMOBs, xOffset, yOffset);
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
     * Render all the MOBs (creature, structure... anything derived from MapObject)
     * on the location that is visible. Returns the list of objects that were rendered
     * @param gc Graphics context to render on
     * @param xOffset Offset for rendering (centered on player usually)
     * @param yOffset Offset for rendering (centered on player usually)
     */
    
    private List<MapObject> renderMobs(GraphicsContext gc, double xOffset, double yOffset) {
         /*
        * TODO: Consider rendering mobs in order so that those closer to bottom of the screen overlap those higher up.
        */
        List<MapObject> renderedMOBs = new ArrayList<>();
        
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) {
                if (mob.getXPos()-xOffset < -mob.getSprite().getWidth() ||
                    mob.getXPos()-xOffset > gc.getCanvas().getWidth()) {
                    //Mob is not in window
                } else if (mob.getYPos()-yOffset < -mob.getSprite().getHeight() ||
                    mob.getYPos()-yOffset > gc.getCanvas().getHeight()) {
                    //Mob is not in window
                } else {
                    //Mob is in window
                    mob.render(xOffset, yOffset, gc); //Draw objects on the ground
                    renderedMOBs.add(mob);
                    if (DRAW_COLLISIONS) { // Draw collision boxes for debugging purposes, if the Global variable is set
                        gc.setStroke(Color.RED);
                        if (mob.getSprite().getCollisionAreaType() == 1) {
                            gc.strokeRect(mob.getSprite().getXPos()-xOffset, mob.getSprite().getYPos()-yOffset,
                            mob.getSprite().getWidth(), mob.getSprite().getHeight());
                        } else if (mob.getSprite().getCollisionAreaType() == 2) {
                            gc.strokeOval(mob.getSprite().getXPos()-xOffset, mob.getSprite().getYPos()-yOffset,
                            mob.getSprite().getWidth(), mob.getSprite().getHeight());
                        }

                    }
                }
            }
        }
        return renderedMOBs;
    }
    
    private void renderStructureExtras(GraphicsContext gc, List<MapObject> renderedMOBs, double xOffset, double yOffset) {
         // Render extras should be called whenever the structure is rendered
        // This paints them on top of everything again, creatures go "behind" trees
        gc.save();
        lights.paintVision(player.getCenterXPos(), player.getCenterYPos(), 8);
        double lightlevel;
        ColorAdjust lightmap = new ColorAdjust();
        if (!renderedMOBs.isEmpty()) {
            for (MapObject struct : renderedMOBs) {
                if (struct instanceof Structure) {
                    //lightlevel = this.lights.getLightLevel((int)struct.getXPos()/Mists.TILESIZE, (int)struct.getYPos()/Mists.TILESIZE);
                    lightlevel = lights.lightmap[(int)struct.getXPos()/Mists.TILESIZE][(int)struct.getCenterYPos()/Mists.TILESIZE];
                    lightlevel = lightlevel-1;
                    lightmap.setBrightness(lightlevel); gc.setEffect(lightmap);
                    ((Structure)struct).renderExtras(xOffset, yOffset, gc); //Draw extra frill (leaves on trees etc)
                    
                }
            }
        }
        gc.restore();
    }
    
    private void renderExtras(GraphicsContext gc, double xOffset, double yOffset) {
        //Draw extra effects (battle swings, projectiles, spells...) on the screen
        if (!this.effects.isEmpty()) {
            for (Effect e : this.effects) {
                e.render(xOffset, yOffset, gc);
            }
        }
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
    
    /**
    * Flags store any soft information for the location
    * @param flag The name of the flag
    * @param value Value for the flag (0 or less is not flagged)
    */
    public void setFlag(String flag, int value) {
        if (this.flags.containsKey(flag)) {
            this.flags.replace(flag, value);
        } else {
            this.flags.put(flag, value);
        }   
    }
    
    /**
    * Toggle flag on or off. If Flag was more than 0, it's now 0.
    * If it was less or equal to 0 or didnt exist, it's now 1
    * @param flag Flag to toggle
    */
    public void toggleFlag(String flag) {
        if (this.isFlagged(flag)) {
            this.setFlag(flag, 0);
        } else {
            this.setFlag(flag, 1);
        }
        
    }
    
    /**
    * Return the value for the given flag
    * @param flag Desired flag
    * @return Returns the value of the flag
    */
    public int getFlag(String flag) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag);
        } else {
            return 0;
        }
    }
    
    /**
    * Check if the Location has the given flag
    * @param flag Flag to check
    * @return returns true if Location has given flag at more than 0
    */
    public boolean isFlagged (String flag) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag) > 0;
        } else {
            return false;
        }
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
        s = this.name+", a "+this.map.getWidth()+"x"+this.map.getHeight()+" area with "+this.mapObjects.size()+" mobs";
        return s;
    }

    /**
     *  ExitLocation should clean up the location for re-entry later on
     */
    public void exitLocation() {
        Mists.logger.info("Location "+this.getName()+" exited, cleaning up...");
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
        this.screenFocus = p;
        
    }
    
}
