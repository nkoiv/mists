/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.pathfinding.CollisionMap;
import com.nkoiv.mists.game.world.pathfinding.PathFinder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

/**
 *
 * @author nkoiv
 */
public class Location implements Global {
    
    /*
    * TODO: Lists for various types of MapObjects, from creatures to frills.
    */
    private final List<MapObject> mapObjects;
    private final List<Effect> effects;
    private String name;
    private GameMap map;
    private CollisionMap collisionMap;
    private PathFinder pathFinder;
    
    private MapObject screenFocus;
    private PlayerCharacter player;
    
    /*
    * Constructor for demofield
    * TODO: load this from some XML or somesuch
    */
    
    
    public Location(String name) {
        this.loadMap(new BGMap(new Image("/images/pocmap.png")));
        this.collisionMap = new CollisionMap(this, 32);
        this.mapObjects = new ArrayList<>();
        this.effects = new ArrayList<>();
        this.pathFinder = new PathFinder(this.collisionMap, 50, true);
    }
    
    public Location() {
        /*TODO: This general constructor is just for the Proof of Concept -map
        * and should be removed later to avoid misuse
        */
        this.name = "POCmap";
        this.mapObjects = new ArrayList<>();
        this.effects = new ArrayList<>();
        //this.loadMap(new BGMap(new Image("/images/pocmap.png")));
        this.loadMap(new TileMap("/mapdata/tilemaptest.map"));
        this.collisionMap = new CollisionMap(this, 32);
        this.collisionMap.setStructuresOnly(true);
        this.pathFinder = new PathFinder(this.collisionMap, 200, true);
        
        PlayerCharacter himmu = new PlayerCharacter();
        himmu.getSprite().setCollisionAreaShape(2);
        himmu.addAction(new MeleeAttack());
        this.setPlayer(himmu);
        this.addCreature(himmu, 8*TILESIZE, 6*TILESIZE);
        this.screenFocus = himmu;
        
        //TODO: Create structures from structure library once its finished
        Structure rock = new Structure("Rock", new Image("/images/block.png"), this, 10*TILESIZE, 7*TILESIZE);
        this.mapObjects.add(rock);
        
        Structure tree1 = new Structure("Tree", new Image("/images/tree_stump.png"), this, 6*TILESIZE, 5*TILESIZE);
        tree1.addExtra(new Image("/images/tree.png"), -35, -96);
        this.mapObjects.add(tree1);
        
        Structure tree2 = new Structure("Tree", new Image("/images/tree_stump.png"), this, 4*TILESIZE, 9*TILESIZE);
        tree2.addExtra(new Image("/images/tree.png"), -35, -96);
        this.mapObjects.add(tree2);
        
        Creature monster1 = new Creature("Otus", new ImageView("/images/monster_small.png"), 3, 0, 0, 32, 32);
        monster1.getSprite().setCollisionAreaShape(2);
        this.addCreature(monster1, 2*TILESIZE, 10*TILESIZE);   
    }
    
    private void loadMap(GameMap map) {
        this.map = map;
        // Add in all the static structures from the selected map
        for (Structure s : map.getStaticStructures(this)) {
            this.mapObjects.add(s);
        }
    }
    
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
    
    public PathFinder getPathFinder() {
        if (this.pathFinder == null) this.pathFinder = new PathFinder(this.collisionMap, 50, true);
        return this.pathFinder;
    }
    
    public void addStructure(Structure s, double xPos, double yPos) {
        this.mapObjects.add(s);
        s.setLocation(this);
        s.getSprite().setPosition(xPos, yPos);
    }
    
    public void addCreature(Creature c, double xPos, double yPos) {
        this.mapObjects.add(c);
        c.setLocation(this);
        c.getSprite().setPosition(xPos, yPos);
    }
    
    public void addEffect(Effect e, double xPos, double yPos) {
        this.effects.add(e);
        e.setLocation(this);
        e.getSprite().setPosition(xPos, yPos);
    }
    
    public void addPlayerCharacter(PlayerCharacter p) {
        this.mapObjects.add(p);
        p.setLocation(this);
        
    }
    
    public void setMap(GameMap m) {
        this.map = m;
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
    
    public void setScreenFocus(MapObject focus) {
        this.screenFocus = focus;
    }
    
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
        
        return xOffset;
	}
	
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
        
        return yOffset;
	}
    
    public void update (double time) {
        /*
        * Update is the main "tick" of the Location.
        * Movement, combat and triggers should all be handled here
        */
        this.collisionMap.updateCollisionLevels();
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) { //Mobs do whatever mobs do
                mob.update(time);
            }
            Iterator<MapObject> mobIterator = mapObjects.iterator(); //Cleanup of mobs
            while (mobIterator.hasNext()) {
                if (mobIterator.next().isFlagged("removable")) {
                    mobIterator.remove();
                }
            }
        }
        if (!this.effects.isEmpty()) {
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
    
    public ArrayList<MapObject> checkCollisions (MapObject o) {
        /*
        * TODO: Maybe only check collisions from nearby objects?
        */
        ArrayList<MapObject> collidingObjects = new ArrayList<>();
        
        Iterator<MapObject> mapObjectsIter = mapObjects.iterator();
        while ( mapObjectsIter.hasNext() )
        {
            MapObject collidingObject = mapObjectsIter.next();
            if (!collidingObject.equals(o)) { // Colliding with yourself is not really a collision
                if ( o.instersects(collidingObject) ) 
                 {
                    collidingObjects.add(collidingObject);
                }
            }
        }
        
        return collidingObjects;
    }
    
    public HashSet<Direction> collidedSides (MapObject mob) {
        ArrayList<MapObject> collidingObjects = this.checkCollisions(mob); //Get the colliding object(s)
        HashSet<Direction> collidedDirections = new HashSet<>();   
        
        for (MapObject collidingObject : collidingObjects) {
            Mists.logger.log(Level.INFO, "{0} bumped into {1}", new Object[]{this, collidingObject});
            double collidingX = collidingObject.getCenterXPos();//+(collidingObject.getSprite().getWidth()/2);
            double collidingY = collidingObject.getCenterYPos();//+(collidingObject.getSprite().getHeight()/2);
            double thisX = mob.getCenterXPos();//+(this.getSprite().getWidth()/2);
            double thisY = mob.getCenterYPos();//+(this.getSprite().getHeight()/2);
            double xDistance = (thisX - collidingX);
            double yDistance = (thisY - collidingY);
            if (Math.abs(xDistance) >= Math.abs(yDistance)) {
                //Collided primary on the X (Left<->Right)
                if (mob.getCenterXPos() <= collidingObject.getCenterXPos()) {
                    //CollidingObject is LEFT of the mob
                    collidedDirections.add(Direction.LEFT);
                } else {
                    //CollidingObject is RIGHT of the mob
                    collidedDirections.add(Direction.RIGHT);
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
        
        /*
        * TODO: Consider rendering mobs in order so that those closer to bottom of the screen overlap those higher up.
        */
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) {
                mob.render(xOffset, yOffset, gc); //Draw objects on the ground
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
        
        if (!this.mapObjects.isEmpty()) {
            for (MapObject struct : this.mapObjects) {
                if (struct instanceof Structure) {
                    ((Structure)struct).renderExtras(xOffset, yOffset, gc); //Draw extra frill (leaves on trees etc)
                }
            }
        }
        //Draw extra effects (battle swings, projectiles, spells...) on the screen
        if (!this.effects.isEmpty()) {
            for (Effect e : this.effects) {
                e.render(xOffset, yOffset, gc);
            }
        }
        
        
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
    
}
