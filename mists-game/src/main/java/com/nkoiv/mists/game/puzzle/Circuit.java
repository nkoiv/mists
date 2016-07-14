/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import java.util.Arrays;
import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;

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
     * @param directionNumber Direction the power is coming from: N=0, E=1, S=2, W=3
     */
    public void getPowerFrom(int directionNumber) {
        this.powerUp(directionNumber);
    }
    
    /**
     * If powered on, give power to neighbouring tiles
     */
    public void givePowerToNeighbours() {
        if (!hasPower) return;
        for (int i = 0; i < this.openPaths.length; i++) {
            if (openPaths[i] && !poweredFrom[i] && neighbours[i] != null) neighbours[i].getPowerFrom(oppositeSide(i));
        }
    }
    
    /**
     * Tell the neighbouring tiles you're not powering them anymore
     */
    private void stopPoweringNeighbours() {
        for (int i = 0; i < neighbours.length; i++) {
            if (neighbours[i] != null) neighbours[i].powerDown(oppositeSide(i));
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
        if (!this.hasPower) return;
        if (this.innatePower) return;
        this.poweredFrom[directionNumber] = false;
        this.hasPower = stillHasPower();
        if (!hasPower) stopPoweringNeighbours();
    }
    
    private void powerUp(int directionNumber) {
        Mists.logger.info("Circuit "+Arrays.toString(this.openPaths)+" powering up from "+directionNumber);
        if (this.innatePower) return;
        this.poweredFrom[directionNumber] = true;
        if (this.openPaths[directionNumber]) this.hasPower = true;
        if (this.hasPower) givePowerToNeighbours();
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
    
    private boolean stillHasPower() {
        if (this.innatePower) return true;
        for (int i = 0; i< this.openPaths.length; i++) {
            if (this.openPaths[i] && this.poweredFrom[i]) return true;
        }
        return false;
    }
    
    public void rotateCW() {
        boolean[] newPaths = new boolean[4];
        if (openPaths[0]) newPaths[1] = true;
        if (openPaths[1]) newPaths[2] = true;
        if (openPaths[2]) newPaths[3] = true;
        if (openPaths[3]) newPaths[0] = true;
        this.openPaths = newPaths;
        this.hasPower = stillHasPower();
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
        this.hasPower = stillHasPower();
        stopPoweringNeighbours();
        if (this.hasPower) givePowerToNeighbours();
    }
    
    public void setPaths(boolean north, boolean east, boolean south, boolean west) {
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
        if (Arrays.equals(openPaths, new boolean[]{false, false, false, false}))
            return "O";
        return "S";
    }
    
    public boolean[] getPowerChart() {
        return this.poweredFrom;
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
            default: {
                Mists.logger.log(Level.WARNING, "Failure assigning opposite side in circuit puzzle for: {0}", sidenumber);
                return -1;
            }
        }
    }
    
}
