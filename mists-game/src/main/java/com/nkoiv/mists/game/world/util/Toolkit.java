/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.util;

import com.nkoiv.mists.game.Direction;

/**
 * Toolkit (TODO: poor name, rename) contains
 * small static methods that are used throughout
 * the code
 * @author nikok
 */
public abstract class Toolkit {
    
    /**
     * Convert the vector between two points
     * into a Direction enum. Cardinal directions
     * are returned only if x or y movement
     * is exactly 0. Diagonal direction is far more
     * common return value.
     * @param xFrom
     * @param yFrom
     * @param xTo
     * @param yTo
     * @return Direction from x/yFrom to x/yTo
     */
    public static Direction getDirection(double xFrom, double yFrom, double xTo, double yTo) {
        double xMovement = xTo - xFrom;
        double yMovement = yTo - yFrom;
        Direction d = Direction.STAY;
        if (xMovement < 0) {
            //We're going Right
            if (yMovement>0) d = Direction.DOWNRIGHT;
            else if (yMovement<0) d = Direction.UPRIGHT;
            else d = Direction.RIGHT;
        } else if (xMovement >0) {
            //We're going Left
            if (yMovement>0) d = Direction.DOWNLEFT;
            else if (yMovement<0) d = Direction.UPLEFT;
            else d = Direction.LEFT;
        } else if (xMovement == 0) {
            if (yMovement >0) d = Direction.DOWN;
            else if (yMovement < 0) d = Direction.UP;
        }
        
        
        return d;
    }
    
    /**
     * Convert the vector between two coordinates into
     * convenient x and y doubles, both within range of
     * -1 and +1. This method effectively gets rid of the
     * length of the vector while keeping the direction.
     * @param xFrom Point A x
     * @param yFrom Point A y
     * @param xTo Point B x
     * @param yTo Point B y
     * @return x and y direction in -1 - +1 range
     */
    public static double[] getDirectionXY(double xFrom, double yFrom, double xTo, double yTo) {
        double xDif = xTo - xFrom;
        double yDif = yTo - yFrom;
        boolean left = false;
        boolean up = false;
        if (xDif<0) left = true;
        if (yDif<0) up = true;
        double xFactor;
        double yFactor;
        if (Math.abs(xDif) > Math.abs(yDif)) {
            yFactor = Math.abs(yDif) / Math.abs(xDif);
            xFactor = 1-yFactor;
        } else {
            xFactor = Math.abs(xDif) / Math.abs(yDif);
            yFactor = 1-xFactor;
        }
        if (left) xFactor = -xFactor;
        if (up) yFactor = -yFactor;
        
        double[] xy = new double[]{xFactor, yFactor};
        return xy;
    }
    
    /**
     * Convert direction enum into x/y vector
     * @param d Direction to get x and y for
     * @return x and y towards direction given
     */
    public static double[] getDirectionXY(Direction d) {
        switch (d) {
            case UP: return new double[]{0,-1};
            case DOWN: return new double[]{0,1};
            case RIGHT: return new double[]{1,0};
            case LEFT: return new double[]{-1,0};
            case UPRIGHT: return new double[]{0.5,-0.5};
            case UPLEFT: return new double[]{-0.5,-0.5};
            case DOWNRIGHT: return new double[]{0.5,0.5};
            case DOWNLEFT: return new double[]{-0.5,0.5};
            case STAY: return new double[]{0,0};
            default: return new double[]{0,0};
        }
    }
    
    /**
     * Simple euclidean distance from point A to point B
     * @param fromX Point A x
     * @param fromY Point A y
     * @param toX Point B x
     * @param toY Point B y
     * @return distance between the two points
     */
    public static double distance(double fromX, double fromY, double toX, double toY) {
        /*With euclidean the diagonal movement is considered to be
        * slightly more expensive than cardinal movement
        * ((AC = sqrt(AB^2 + BC^2))), 
        * where AB = x2 - x1 and BC = y2 - y1 and AC will be [x3, y3]
        */
        double euclideanDistance = Math.sqrt(Math.pow(fromX - toX, 2)
                            + Math.pow(fromX - toX, 2));
        return euclideanDistance;
    }
 
}
