/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.MovingGraphics;

/**
 * Water is a type of terrain obstacle that needs
 * to be tiled to look nice. Not much unlike Walls.
 * @author nikok
 */
public class Water extends MapObject implements HasNeighbours{
    private boolean[] neighbours;
    
    
    public Water(String name, MovingGraphics graphics) {
        super(name, graphics);
    }

    @Override
    public boolean[] checkNeighbours() {
        boolean[] newNeighbours = new boolean[9];
        
        
        return newNeighbours;
    }
    
    @Override
    public boolean[] getNeighbours() {
        return this.neighbours;
    }

    @Override
    public void setNeighbours(boolean[] neighbours) {
        this.neighbours = neighbours;
    }
    
    /**
     * Add a neighbouring wall to this wall
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * @param n The number of the neighbour added
     */
    @Override
    public void addNeighbour(int n) {
        this.neighbours[n] = true;
    }
    
     /**
     * Remove a neighbouring wall from this wall
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * 
     * In imagefile:
     * [6][1][3][4] Cardinal walls
     * [0][5][7][2] Optional diagonal walls
     * @param n The number of the neighbour to remove
     */
    @Override
    public void removeNeighbour(int n) {
        this.neighbours[n] = false;
    }
    
    
}
