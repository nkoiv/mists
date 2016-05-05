/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

/**
 * Some MapObject are tiled in such a way,
 * that changing their neighbouring tiles
 * changes their graphics. Water and Walls
 * are prime examples of this.
 * @author nikok
 */
public interface HasNeighbours {

    public boolean[] getNeighbours();
    
    /**
     * Supply the object with a new set of neighbours
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * @param neighbours Array of neighbours for this object
     */
    public void setNeighbours(boolean[] neighbours);
    
    /**
     * Add a neighbouring wall to this wall
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * @param n The number of the neighbour added
     */
    public void addNeighbour(int n);
    
     /**
     * Remove a neighbouring wall from this wall
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * 
     * @param n The index number of the neighbour to remove
     */
    public void removeNeighbour(int n);
    
    
}
