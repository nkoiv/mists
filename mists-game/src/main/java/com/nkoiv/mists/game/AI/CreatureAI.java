/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.pathfinding.Path;
import com.nkoiv.mists.game.world.util.Flags;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import java.util.Random;

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
    * @return Returns the task the creature decided to perform
    */
    public Task act(double time) {
        //TODO: For now all creatures just want to home in on player
        //TODO: Adjust the action-picking speed dynamically based on AI load?
        if (this.timeSinceAction > 0.3 || creep.getLastTask() == null) { //Acting thrice per second
            //Mists.logger.info(this.getCreature().getName()+" decided to act!");
            this.timeSinceAction = 0;
            return this.pickNewAction(time);
        } else {
            //TODO: Continue current chosen action. Atm this means just movement
            this.timeSinceAction = this.timeSinceAction+time;
            return creep.getLastTask();
        }
    }
    
    /**
     * PickNewAction is the root of the AI decision tree
     * @param time Time used on the action //TODO: Actually unneeded for AI
     * @return Task the AI deicided to perform next
     */
    protected Task pickNewAction(double time) {
        if (creep.getLocation().getPlayer() == null) return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
        if (!this.active) {
            if (Toolkit.distance(creep.getCenterXPos(), creep.getCenterYPos(), creep.getLocation().getPlayer().getCenterXPos(), creep.getLocation().getPlayer().getCenterYPos())
                    < 10 * 32) {
                if (this.isInLineOfSight(creep.getLocation().getPlayer())) {
                    this.active = true;
                }
            }
            return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
        }
        else {
            return this.goMelee(creep.getLocation().getPlayer(), time);
        }
    }

    /**
     * MoveTowardsMob uses A* pathfinding to route a path towards the
     * target, and then moves towards the next step on the path.
     * The path is stored to AI:s "pathToMoveOn", so it can be used
     * repeatedly before discarded as outdated
     * @param mob MapObject to move towards
     * @param time Time we can spend on moving
     * @return Task on what the creature is doing (for networking)
     */
    protected Task moveTowardsMob(MapObject mob, double time) {
        //Drop fractions for collisionssize for now
        //TODO: Make monster collisionboxes adher to pixelsize
        double collisionSize = (int)(creep.getWidth()/Mists.TILESIZE) * Mists.TILESIZE;
        //TODO: CollisionSize should be based on creep size, but sprites are a mess right now
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
            this.pathToMoveOn = null;
            return new Task(GenericTasks.ID_MOVE_TOWARDS_COORDINATES, creep.getID(), new int[]{((int)mob.getCenterXPos()), ((int)mob.getCenterYPos())});
        }
        if (pathToMob.getLength() == 2) {
            /* We're next to target
            *  Try to get even closer (TODO: Is this the right course of action?)
            */
            //Mists.logger.info("Next to target");
            return new Task(GenericTasks.ID_MOVE_TOWARDS_COORDINATES, creep.getID(), new int[]{((int)mob.getCenterXPos()), ((int)mob.getCenterYPos())});
        }
        else { //if (pathToMob.getLength() > 2)
            /* There's tiles between us and the target
            *  Try to move towards the next tile
            */
            //Mists.logger.info("Got a path: " +pathToMob.toString());
            int nextTileX = pathToMob.getNode(1).getX();//*pathToMob.getNode(0).getSize();
            int nextTileY = pathToMob.getNode(1).getY();//*pathToMob.getNode(0).getSize();
            return new Task(GenericTasks.ID_MOVE_TOWARDS_COORDINATES, creep.getID(), new int[]{nextTileX*Mists.TILESIZE, nextTileY*Mists.TILESIZE});
        }
    }
    
    protected Task goMelee(MapObject target, double time) {
        if (AIutil.inRange(creep, 0, target)) {
            //Mists.logger.log(Level.INFO, "{0} in range to hit {1}", new Object[]{creep.getName(), target.getName()});
            return new Task(GenericTasks.ID_USE_MELEE_TOWARDS_MOB, creep.getID(), new int[]{target.getID()});
        } else {
            //return new Task(GenericTasks.ID_MOVE_TOWARDS_TARGET, creep.getID(), new int[]{target.getID()});
            return this.moveTowardsMob(target, time);
        }
    }
    
    
    /**
     * Choose a random direction (can be STAY)
     * and move towards it
     * @param time time spent moving
     * @return the movement done as a task
     */
    protected Task moveRandomly(double time) {
        Random rnd = new Random();
        int randomint = rnd.nextInt(9);  
        return new Task(GenericTasks.ID_MOVE_TOWARDS_DIRECTION, creep.getID(), new int[]{randomint});
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
     * @return the task the creature decided to perform
     */
    protected Task distanceBasedFollow(double time, MapObject target) {
        //creep.stopMovement(); //clear old movement
        Random rnd = new Random();
        if (AIutil.distanceToMob(creep, target.getID()) > 10 * Mists.TILESIZE) {
            //Mists.logger.log(Level.INFO, "{0} too far to follow {1}", new Object[]{creep.getName(), creep.getLocation().getPlayer().getName()});
            return new Task(GenericTasks.ID_IDLE, this.creep.getID(), null);
        } else if (AIutil.distanceToMob(creep, target.getID()) > 2 * Mists.TILESIZE) {
            return this.moveTowardsMob(target, time);
            //Mists.logger.log(Level.INFO, "{0} moving towards {1}", new Object[]{creep.getName(), creep.getLocation().getPlayer().getName()});
        } else {
            int r = rnd.nextInt(10); //50% chance to move around
            if (r < 5) return this.moveRandomly(time);
            else return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
        } 
    }
    
    
    
    protected Task attackNearest(ArrayList<Creature> nearbyMobs, double time) {
        if (!nearbyMobs.isEmpty()) {
            Creature target = null;
            double distanceToNearest = Double.MAX_VALUE;
            for (Creature c : nearbyMobs) {
                if (this.isInLineOfSight(c)) {
                    if (distanceToNearest > Toolkit.distance(c.getCenterXPos(), c.getCenterYPos(), creep.getCenterXPos(), creep.getCenterYPos())) {
                        target = c;
                    }
                }
            }
            if (target == null) return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
            else {
                //Mists.logger.log(Level.INFO, "{0} trying to attack {1}", new Object[]{creep.getName(), target.getName()});
                return this.goMelee(target, time);
            }
        }
        return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
    }
    
    public Path getPath() {
        if (this.pathToMoveOn != null) return this.pathToMoveOn;
        else return null;
    }
    
    public Creature getCreature() {
        return this.creep;
    }
     
    //TODO: Is set-function really needed? Should AI be hardlinked to a creature?
    public void setCreature(Creature creep) {
        this.creep = creep;
    }
        
}
