/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.pathfinding.Path;
import java.util.Random;

/**
 * CreatureAI is the main AI routine for creatures.
 * Each acting creature should have its own CreatureAI, as it stores
 * the creatures states and intentions
 * @author nikok
 */
public class CreatureAI {
    
    protected Creature creep;
    protected double timeSinceAction;
    protected Path pathToMoveOn;
    
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
 
    
    public Path getPath() {
        if (this.pathToMoveOn != null) return this.pathToMoveOn;
        else return null;
    }
    
    public Creature getCreature() {
        return this.creep;
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
