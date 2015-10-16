/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author nikok
 */
public interface PathfinderAlgorithm {
    
    public Path findPath(CollisionMap map, int tilesize, List<Integer> crossableTerrain, int startX, int startY, int goalX, int goalY);
    
    public List<Node> Neighbours(CollisionMap map, List<Integer> crossableTerrain, int x, int y);
    public List<Node> Neighbours(CollisionMap map, int clearanceNeed, List<Integer> crossableTerrain, int x, int y);
    public List<Node> DiagonalNeighbours(CollisionMap map, List<Integer> crossableTerrain, int x, int y);
    public List<Node> DiagonalNeighbours(CollisionMap map, int clearanceNeed, List<Integer> crossableTerrain, int x, int y);
    
    public HashMap<Integer, int[][]> getClearanceMaps();
}
