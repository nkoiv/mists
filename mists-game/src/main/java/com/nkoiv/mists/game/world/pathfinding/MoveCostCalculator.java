/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import java.util.List;

/**
 * MoveCostCalculator calculates the distance between point A and point B
 * This is done on a gridMap, with the calculation function being selectable between
 * Manhattan, Diagonal and Euclidean.
 * @author nikok
 */
public class MoveCostCalculator {
    
    private int type;
    private int MANHATTAN_DISTANCE = 0;
    private int DIAGONAL_DISTANCE = 1;
    private int EUCLIDEAN_DISTANCE = 2;
    
    
    public MoveCostCalculator () {
        this.type = MANHATTAN_DISTANCE;
    }
    
    public MoveCostCalculator (int type) {
        this.type = type;
    }
    
    public double getCost(CollisionMap map, List<Integer> movementAbility, int currentX, int currentY, int goalX, int goalY) {
        //TODO: start using the collisionMap and movementAbility to calculate varying costs per tile (swamp, tar...)
        switch (this.type) {
            case 0: return this.ManhattanDistance(currentX, currentY, goalX, goalY);
            case 1: return this.DiagonalDistance(currentX, currentY, goalX, goalY);
            case 2: return this.EuclideanDistance(currentX, currentY, goalX, goalY);
            default: return this.ManhattanDistance(currentX, currentY, goalX, goalY);    
        }
    }

    private int ManhattanDistance(int currentX, int currentY, int goalX, int goalY) {
        //Linear distance of nodes
        int manhattanDistance = Math.abs(currentX - goalX) + 
                        Math.abs(currentY - goalY);
        return manhattanDistance;
    }
    
    private int DiagonalDistance(int currentX, int currentY, int goalX, int goalY) {
        //Diagonal distanceassumes going diagonally costs the same as going cardinal
        int diagonalDistance = Math.max(Math.abs(currentX - goalX),
                        Math.abs(currentY - goalY));
        return diagonalDistance;
    }
    
    private double EuclideanDistance(int currentX, int currentY, int goalX, int goalY) {
        /*With euclidean the diagonal movement is considered to be
        * slightly more expensive than cardinal movement
        * ((AC = sqrt(AB^2 + BC^2))), 
        * where AB = x2 - x1 and BC = y2 - y1 and AC will be [x3, y3]
        */
        double euclideanDistance = Math.sqrt(Math.pow(currentX - goalX, 2)
                            + Math.pow(currentY - goalY, 2));
        return euclideanDistance;
    }
    
}
