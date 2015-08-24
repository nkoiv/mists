/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.pathfinding.Path;
import java.util.logging.Level;

/**
 * CreatureAI is the main AI routine for creatures.
 * Each acting creature should have its own CreatureAI, as it stores
 * the creatures states and intentions
 * @author nikok
 */
public class CreatureAI {
    
    private Creature creep;
    private double timeSinceAction;
    
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
        //TODO: For now all creatures just want to home in on player
        if (this.timeSinceAction > 0.5) { //Acting twice per second
            //Mists.logger.info(this.getCreature().getName()+" decided to act!");
            this.creep.stopMovement(); //clear old movement
            this.moveTowardsPlayer(time);
            this.timeSinceAction = 0;
        } else {
            this.getCreature().applyMovement(time);
            this.timeSinceAction = this.timeSinceAction+time;
        }
        //this.moveTowardsMob(this.creep.getLocation().getPlayer(), time);
        return true;
    }
    
    
    /*
    **  TODO: Temporary for just following player
    */
    public void moveTowardsPlayer(double time) {        
        if (!this.creep.getLocation().isFlagged("testFlag")) return; //Dont my unless flagged
        /*
        * Since pathfinding moves in full tiles, it's possible for units
        * to get stuck on the corners. We alleviate this with the following
        */
        Direction directionToMoveTowards =
            (this.creep.getLocation().getPathFinder().directionTowards
            (this.creep.getSprite().getWidth(), this.creep.getCrossableTerrain(), this.creep.getCenterXPos(), this.creep.getCenterYPos(),
            this.creep.getLocation().getPlayer().getCenterXPos(), this.creep.getLocation().getPlayer().getCenterYPos()));
        //Mists.logger.log(Level.INFO, "Trying to move towards {0}", directionToMoveTowards);
        this.creep.moveTowards(directionToMoveTowards);
        //TODO: If applyMovement returns false, we didnt actually move anywhere.
        //In that case, head towards a better location!
        this.creep.applyMovement(time);
    }
    
    public void moveTowardsMob(MapObject mob, double time) {
        if (!this.creep.isFlagged("testFlag")) return; //Dont my unless flagged TODO: This is for testing
        double targetXCoordinate = mob.getCenterXPos();
        double targetYCoordinate = mob.getCenterYPos();
        int targetX = (int)targetXCoordinate / this.creep.getLocation().getPathFinder().getTileSize();
        int targetY = (int)targetYCoordinate / this.creep.getLocation().getPathFinder().getTileSize();
        int currentX = (int)this.creep.getCenterXPos() / this.creep.getLocation().getPathFinder().getTileSize();
        int currentY = (int)this.creep.getCenterYPos() / this.creep.getLocation().getPathFinder().getTileSize();
        Path pathToMob = this.creep.getLocation().getPathFinder().findPath(this.creep.getCrossableTerrain(),currentX, currentY, targetX, targetY);
        if (pathToMob.getLength() <= 1) {
            /* No path was found to target 
            *  just move in the general direction of target
            */
            Mists.logger.info("Got a short path or no path");
            this.creep.moveTowards(mob.getCenterXPos(), mob.getCenterYPos());
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
            double nextTileX = pathToMob.getNode(1).getX()*this.creep.getLocation().getPathFinder().getTileSize();
            double nextTileY = pathToMob.getNode(1).getY()*this.creep.getLocation().getPathFinder().getTileSize();
            targetXCoordinate = nextTileX;
            targetYCoordinate = nextTileY;
            //Mists.logger.log(Level.INFO, "Pathfinder tile {0},{1} converted into {2},{3}",
            //new Object[]{pathToMob.getNode(1).getX(), pathToMob.getNode(1).getY(), targetXCoordinate, targetYCoordinate});
            this.creep.moveTowards(nextTileX, nextTileY);

            this.creep.applyMovement(time);
        }
        
    }
    /*
    private void moveTowardsCenterOfTheTile() {
        double xTargetTile = Math.round(this.getCenterXPos()/Global.TILESIZE);
           double yTargetTile = Math.round(this.getCenterYPos()/Global.TILESIZE);
           double xTarget = (xTargetTile*Global.TILESIZE);
           double yTarget = (yTargetTile*Global.TILESIZE);
           this.setFlag("xTarget", (int)xTarget);
           this.setFlag("yTarget", (int)yTarget);
           Mists.logger.info("Movement was blocked. Going from "
                   +this.getCenterXPos()+","+this.getCenterYPos()+ " to "+
                   xTarget+","+yTarget);
           Direction directionToMoveTowards =
           PathFinder.getDirection(this.getFlag("xTarget") - this.getCenterXPos(),
                    this.getFlag("yTarget") - this.getCenterYPos());
           this.moveTowards(directionToMoveTowards);
    }
    */
    
    public Creature getCreature() {
        return this.creep;
    }
    //TODO: Is set-function really needed? Should AI be hardlinked to a creature?
    public void setCreature(Creature creep) {
        this.creep = creep;
    }
    
}
