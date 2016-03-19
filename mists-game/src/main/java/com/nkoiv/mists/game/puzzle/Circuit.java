/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.Direction;
import java.util.Arrays;

/**
 * Circuit is a part of the Circuit-puzzle.
 * It's a "conductive" tile-element that relays
 * power.
 * @author nikok
 */
public class Circuit {
    /*Paths dictate which directions the circuit can flow
    * Since the circuits can rotate, the naming isn't really
    * that stationary - only the relation stays (North is
    * always opposite to South, etc)
    *[ ][N][ ]  [ ][0][ ]
    *[W][ ][E]  [3][ ][1]
    *[ ][S][ ]  [ ][2][ ]
    */
    private boolean[] openPaths;
    private boolean hasPower;
    private boolean innatePower;
    
    private Circuit[] neighbours;
    /*Neighbours
    *[ ][0][ ] 0 = North
    *[3][ ][1] 1 = East
    *[ ][2][ ] 2 = South
    *          3 = West
    */
    private boolean[] poweredFrom;
    
    public Circuit(boolean[] openPaths) {
        this.openPaths = openPaths;
        this.neighbours = new Circuit[4];
        this.poweredFrom = new boolean[4];
    }
    
    public Circuit() {
        this(new boolean[4]);
    }
    
    /**
     * Give power to the circuit from a given direction
     * if this circuit has a path to that direction, its powered up.
     * @param d Direction the power is coming from
     */
    public void getPowerFrom(Direction d) {
        if (d == Direction.UP && openPaths[0]) this.powerUp(0);
        if (d == Direction.RIGHT && openPaths[1]) this.powerUp(1);
        if (d == Direction.DOWN && openPaths[2]) this.powerUp(2);
        if (d == Direction.LEFT && openPaths[3]) this.powerUp(3);
    }
    
    /**
     * If powered on, give power to neighbouring tiles
     */
    public void givePowerToNeighbours() {
        if (!hasPower) return;
        if (neighbours[0] != null && openPaths[0]) neighbours[0].getPowerFrom(Direction.DOWN);
        if (neighbours[1] != null && openPaths[1]) neighbours[1].getPowerFrom(Direction.LEFT);
        if (neighbours[2] != null && openPaths[2]) neighbours[2].getPowerFrom(Direction.UP);
        if (neighbours[3] != null && openPaths[3]) neighbours[3].getPowerFrom(Direction.RIGHT);
    }
    
    /**
     * Tell the neighbouring tiles you're not powering them anymore
     */
    private void stopPoweringNeighbours() {
        for (int i = 0; i < neighbours.length; i++) {
            neighbours[i].powerDown(oppositeSide(i));
        }
    }
    
    public boolean isPowered() {
        if (this.innatePower) return true;
        return this.hasPower;
    }
    
    public boolean isInnatePowered() {
        return this.innatePower;
    }
    
    public void loseAllPower() {
        this.poweredFrom = new boolean[4];
        if (!innatePower) this.hasPower = false;
    }
    
    private void powerDown(int directionNumber) {
        if (this.innatePower) return;
        this.poweredFrom[directionNumber] = false;
        boolean stillPowered = false;
        //Loop through the poweredFrom dictions to see if someone is still giving us power
        for (boolean b : this.poweredFrom) {
            if (b) stillPowered = true;
        }
        this.hasPower = stillPowered;
        if (!hasPower) stopPoweringNeighbours();
    }
    
    private void powerUp(int directionNumber) {
        if (this.innatePower) return;
        this.hasPower = true;
        this.poweredFrom[directionNumber] = true;
    }
    
    /**
     * Set a neighbour to the selected side.
     *[ ][N][ ]  [ ][0][ ]
     *[W][ ][E]  [3][ ][1]
     *[ ][S][ ]  [ ][2][ ]
     * @param c
     * @param side 
     */
    public void setNeighbour(Circuit c, int side) {
        if (side < 0 || side > 3 || c == null) return;
        this.neighbours[side] = c;
    }
    
    public void setInnatePower(boolean alwaysPowered) {
        this.innatePower = alwaysPowered;
        this.hasPower = true;
    }
    
    public boolean[] getOpenPaths() {
        return this.openPaths;
    }
    
    public void rotateCW() {
        boolean[] newPaths = new boolean[4];
        if (openPaths[0]) newPaths[1] = true;
        if (openPaths[1]) newPaths[2] = true;
        if (openPaths[2]) newPaths[3] = true;
        if (openPaths[3]) newPaths[0] = true;
        this.openPaths = newPaths;
        stopPoweringNeighbours();
        if (this.hasPower) givePowerToNeighbours();
    }
    
    public void rotateCCW() {
        boolean[] newPaths = new boolean[4];
        if (openPaths[0]) newPaths[3] = true;
        if (openPaths[1]) newPaths[0] = true;
        if (openPaths[2]) newPaths[1] = true;
        if (openPaths[3]) newPaths[2] = true;
        this.openPaths = newPaths;
        stopPoweringNeighbours();
        if (this.hasPower) givePowerToNeighbours();
    }
    
    public void setPaths(boolean north, boolean south, boolean east, boolean west) {
        this.openPaths = new boolean[4];
        if (north) openPaths[0] = true;
        if (south) openPaths[2] = true;
        if (east) openPaths[1] = true;
        if (west) openPaths[3] = true;
    }
    
    /**
     * Return the general shape of the circuit
     * @return 
     */
    public String getCircuitShape() {
        if (Arrays.equals(openPaths,new boolean[]{true, false, true, false})
            || Arrays.equals(openPaths, new boolean[]{false, true, false, true})) 
            return "I";
        if (Arrays.equals(openPaths, new boolean[]{true, true, true, true}))
            return "X";
        if (Arrays.equals(openPaths, new boolean[]{true, true, false, false})
            || Arrays.equals(openPaths, new boolean[]{false, true, true, false})
            || Arrays.equals(openPaths, new boolean[]{false, false, true, true})
            || Arrays.equals(openPaths, new boolean[]{true, false, false, true}))
            return "L";
        if (Arrays.equals(openPaths, new boolean[]{true, false, true, false})
            ||Arrays.equals(openPaths, new boolean[]{false, true, true, false})
            || Arrays.equals(openPaths, new boolean[]{false, false, true, false})
            || Arrays.equals(openPaths, new boolean[]{false, false, false, true}))
            return "S";
        if (Arrays.equals(openPaths, new boolean[]{true, true, true, false})
            ||Arrays.equals(openPaths, new boolean[]{false, true, true, true})
            || Arrays.equals(openPaths, new boolean[]{true, false, true, true})
            || Arrays.equals(openPaths, new boolean[]{true, true, false, true}))
            return "T";
        return "S";
    }
    
    /**
     * Get the side number for the opposite side to the one given
     * [ ][N][ ]  [ ][0][ ]
     * [W][ ][E]  [3][ ][1]
     * [ ][S][ ]  [ ][2][ ]
     * @param sidenumber Side to get opposite side for
     * @return 
     */
    private int oppositeSide(int sidenumber) {
        switch (sidenumber) {
            case 0: return 2;
            case 1: return 3;
            case 2: return 0;
            case 3: return 1;
            default: return 0;
        }
    }
    
}
