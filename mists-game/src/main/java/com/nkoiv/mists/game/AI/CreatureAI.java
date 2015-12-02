/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.pathfinding.Path;
import com.nkoiv.mists.game.world.util.Flags;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.shape.Circle;

/**
 * CreatureAI is the main AI routine for creatures.
 * Each acting creature should have its own CreatureAI, as it stores
 * the creatures states and intentions.
 * Most general AI functions are stored here, while more
 * specific ones are located in the classes that extend this one
 * @author nikok
 */
public class CreatureAI extends Flags{
    
    protected Creature creep;
    protected double timeSinceAction;
    protected Path pathToMoveOn;
    protected boolean active;
        
    public CreatureAI (Creature creep) {
        this.creep = creep;
        this.timeSinceAction = 0;
    }
    
    /**
    * act() is the main loop for AI
    * it's called whenever it's given creatures
    * turn to do things
    * TimeSinceAction governs creature decisionmaking
    * No creature needs to act on every tick!
    * @param time Time passed since the last action
    * @return Returns true if the creature was able to act
    */
    public boolean act(double time) {
        if (!this.creep.getLocation().isFlagged("testFlag")) return false; //Dont my unless flagged TODO: This is for testing
        //TODO: For now all creatures just want to home in on player
        if (this.timeSinceAction > 0.5) { //Acting twice per second
            //Mists.logger.info(this.getCreature().getName()+" decided to act!");
            this.creep.stopMovement(); //clear old movement
            //this.moveTowardsPlayer(time);
            this.moveTowardsMob(creep.getLocation().getPlayer(), time);
            this.timeSinceAction = 0;
        } else {
            this.getCreature().applyMovement(time);
            this.timeSinceAction = this.timeSinceAction+time;
        }
        
        //this.moveTowardsMob(this.creep.getLocation().getPlayer(), time);
        return true;
    }
    

    protected void moveTowardsMob(MapObject mob, double time) {
        //double collisionSize = creep.getSprite().getWidth();
        double collisionSize = 32; 
        //TODO: CollisionSize should be based on creep size, but sprites are a mess right now
        //Using default tilesize atm to make pathing work
        double offset;
        if (collisionSize > creep.getLocation().getPathFinder().getTileSize()) {
            offset = creep.getLocation().getPathFinder().getTileSize()/2;
        } else {
            offset = collisionSize/2;
        }
        double targetXCoordinate = mob.getCenterXPos();
        double targetYCoordinate = mob.getCenterYPos();
        Path pathToMob = this.creep.getLocation().getPathFinder().findPath(collisionSize,this.creep.getCrossableTerrain(),creep.getXPos()+offset, creep.getYPos()+offset, targetXCoordinate, targetYCoordinate);
        this.pathToMoveOn = pathToMob;
        if (pathToMob.getLength() <= 1) {
            /* No path was found to target 
            *  just move in the general direction of target
            */
            //Mists.logger.info("Got a short path or no path");
            this.creep.moveTowards(mob.getCenterXPos(), mob.getCenterYPos());
            this.pathToMoveOn = null;
        }
        if (pathToMob.getLength() == 2) {
            /* We're next to target
            *  Try to get even closer (TODO: Is this the right course of action?
            */
            //Mists.logger.info("Next to target");
            this.creep.moveTowards(mob.getCenterXPos(), mob.getCenterYPos());
        }
        if (pathToMob.getLength() > 2) {
            /* There's tiles between us and the target
            *  Try to move towards the next tile
            */
            //Mists.logger.info("Got a path: " +pathToMob.toString());
            double nextTileX = pathToMob.getNode(1).getX()*pathToMob.getNode(0).getSize();
            double nextTileY = pathToMob.getNode(1).getY()*pathToMob.getNode(0).getSize();
            targetXCoordinate = nextTileX;
            targetYCoordinate = nextTileY;
            //Mists.logger.log(Level.INFO, "Pathfinder tile {0},{1} converted into {2},{3}",
            //new Object[]{pathToMob.getNode(1).getX(), pathToMob.getNode(1).getY(), targetXCoordinate, targetYCoordinate});
            this.creep.moveTowards(nextTileX, nextTileY);

            this.creep.applyMovement(time);
        }
        
    }
    
    /**
     * Choose a random direction (can be STAY)
     * and move towards it
     * @param time time spent moving
     */
    protected void moveRandomly(double time) {
        Random rnd = new Random();
        int randomint = rnd.nextInt(9);
        switch (randomint) {
            case 0: this.creep.moveTowards(Direction.UP); break;
            case 1: this.creep.moveTowards(Direction.UPRIGHT); break;
            case 2: this.creep.moveTowards(Direction.RIGHT); break;
            case 3: this.creep.moveTowards(Direction.DOWNRIGHT); break;
            case 4: this.creep.moveTowards(Direction.DOWN); break;
            case 5: this.creep.moveTowards(Direction.DOWNLEFT); break;
            case 6: this.creep.moveTowards(Direction.LEFT); break;
            case 7: this.creep.moveTowards(Direction.UPLEFT); break;
            case 8: this.creep.moveTowards(Direction.STAY); break;
            default: this.creep.moveTowards(Direction.STAY); break;
        }
        this.creep.applyMovement(time);     
        
    }
    
    /**
     * Surrounding creatures gets all the creatures that are within
     * range of a position
     * @param xCoor xCoordinate of the centre of the search
     * @param yCoor yCoordinate of the centre of the search
     * @param range range to perform the search on (radius)
     * @return List of creatures within range
     */
    protected ArrayList<Creature> surroundingCreatures(double xCoor, double yCoor, double range) {
        ArrayList<Creature> nearbyCreatures = new ArrayList<>();
        for (Creature mob : creep.getLocation().getCreatures()) {
            double euclideanDistance = Math.sqrt(Math.pow(this.getCreature().getXPos() - mob.getXPos(), 2)
                            + Math.pow(this.getCreature().getYPos() - mob.getYPos(), 2));
            if (euclideanDistance <= range) nearbyCreatures.add(mob);
        }
        return nearbyCreatures;
    }
    
    
    protected void turnTowardsMapObject(MapObject mob) {
        Direction d = AIutil.getDirectionTowards(creep.getCenterXPos(), creep.getCenterYPos(),mob.getCenterXPos(), mob.getCenterYPos());
        creep.setFacing(d);
    }
    
   
    
    protected void useMeleeTowards(MapObject target) {
        if (creep.getAvailableActions() == null) return;
        if (!creep.getAvailableActions().isEmpty()) {
            this.turnTowardsMapObject(target);
            if (creep.getAvailableActionNames().contains("melee")) {
                //Try to use "melee" ability if possible
                creep.useAction("melee");
            } else {
                //If not available, use first available action
                //TODO: Is this necessary?
                creep.useAction(creep.getAvailableActionNames().get(0));
            }
        }
    }
    
    
    /**
     * line of sight checks if there's structures between target
     * and this creature
     * @param target end point for line
     * @return True if no Structures block the line of sight
     */
    protected boolean isInLineOfSight(MapObject target) {
        return AIutil.isInLineOfSight(creep, target);
    }
    
    /*
    * mobsInBetween will return both the Creep and the Target. 
    * If there's anything else on the list, consider path blocked.
    * @param Target target to draw path to
    * @return returns true if there is a map object between creep and target
    */
    protected boolean hasNothingInBetween(MapObject target) {
        ArrayList<MapObject> mobsInBetween = creep.getLocation().checkCollisions(creep.getCenterXPos(), creep.getCenterXPos(), target.getCenterXPos(), target.getCenterYPos());
        for (MapObject m : mobsInBetween) {
            if (m != creep && m != target) {
               return false;
           }
        }
        return true;
    }
    
    /**
     * Distance based follow sets the creep to follow the target,
     * unless it's too far (over 10 tiles) away. If target is close
     * (two or less tiles), then the creep just wanders around randomly
     * @param time time spent for moving
     * @param target target to follow
     */
    protected void distanceBasedFollow(double time, MapObject target) {
        creep.stopMovement(); //clear old movement
        Random rnd = new Random();
        if (this.distanceToMob(target) > 10 * Mists.TILESIZE) {
            //Mists.logger.log(Level.INFO, "{0} too far to follow {1}", new Object[]{creep.getName(), creep.getLocation().getPlayer().getName()});
        } else if (this.distanceToMob(target) > 2 * Mists.TILESIZE) {
            this.moveTowardsMob(target, time);
            //Mists.logger.log(Level.INFO, "{0} moving towards {1}", new Object[]{creep.getName(), creep.getLocation().getPlayer().getName()});
        } else {
            int r = rnd.nextInt(10); //50% chance to move around
            if (r < 5) this.moveRandomly(time);
        }
    }
    
    protected void goMelee(MapObject target, double time) {
        this.creep.stopMovement(); //clear old movement
            if (this.inRange(0, target)) {
                //Mists.logger.info("In range to hit player");
                this.useMeleeTowards(target);
            } else {
                this.moveTowardsMob(target, time);
            }
    }
    
    public Path getPath() {
        if (this.pathToMoveOn != null) return this.pathToMoveOn;
        else return null;
    }
    
    public Creature getCreature() {
        return this.creep;
    }
    
    protected boolean inRange(double range, MapObject target) {
        Circle rangeCircle = new Circle(creep.getCenterXPos(), creep.getCenterYPos(), (creep.getSprite().getWidth()/2)+range);
        return target.getSprite().intersectsWithShape(rangeCircle);
    }
    
    protected double distanceToMob(MapObject mob) {
        //returns euclidean distance to target mob
        double euclideanDistance = Math.sqrt(Math.pow(this.getCreature().getXPos() - mob.getXPos(), 2)
                            + Math.pow(this.getCreature().getYPos() - mob.getYPos(), 2));
        return euclideanDistance;
    }
    
    //TODO: Is set-function really needed? Should AI be hardlinked to a creature?
    public void setCreature(Creature creep) {
        this.creep = creep;
    }
        
}
