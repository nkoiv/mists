/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * AIUtil contains miscellaneous static functions
 * that are useful in AI routines
 * @author nikok
 */
public class AIutil {
    
     public static Direction getDirectionTowards(double xStart, double yStart, double xTarget, double yTarget) {
        double xDistance = (xTarget - xStart);
        double yDistance = (yTarget - yStart);
        Direction directionToTurn = Direction.STAY;
        if (xDistance > 0 && yDistance > 0) directionToTurn = Direction.DOWNRIGHT;
        if (xDistance > 0 && yDistance <= 0) directionToTurn = Direction.UPRIGHT;
        if (xDistance <= 0 && yDistance > 0) directionToTurn = Direction.DOWNLEFT;
        if (xDistance <= 0 && yDistance <= 0) directionToTurn = Direction.UPLEFT;
        if (Math.abs(xDistance) > Math.abs(yDistance)*3) {
            if (xDistance > 0 ) directionToTurn = Direction.RIGHT;
            if (xDistance <= 0 ) directionToTurn = Direction.LEFT;
        } else if (Math.abs(yDistance) > Math.abs(xDistance)*3) {
            if (yDistance > 0 ) directionToTurn = Direction.DOWN;
            if (yDistance <= 0 ) directionToTurn = Direction.UP;
        }
        return directionToTurn;
    }
    
     /**
     * line of sight checks if there's structures between target
     * and given coordinates. Neither start nor target block the line
     * @param start start point for the line
     * @param target end point for line
     * @return True if no Structures block the line of sight
     */
    protected static boolean isInLineOfSight(MapObject start, MapObject target) {
        Location l = start.getLocation();
        if (l == null) return true;
        ArrayList<MapObject> mobsInBetween = l.checkCollisions(start.getCenterXPos(), start.getCenterXPos(), target.getCenterXPos(), target.getCenterYPos());
        for (MapObject mob : mobsInBetween) {
           if (mob != start && mob != target && mob instanceof Structure) {
               //Mists.logger.log(Level.INFO, "Line of sight between {0},{1} and {2} blocked by {3}", new Object[]{(int)xCoor, (int)yCoor, target.getName(), mob.getName()});
               return false;
           }
        }
        return true;
    }
     
}
