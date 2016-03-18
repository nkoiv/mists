/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import java.util.ArrayList;

/**
 * In Circuit tiles must be rotated to convey
 * power from point A to point B (and possibly C, D, E...)
 * @author nikok
 */
public class CircuitPuzzle {
    private ArrayList<Circuit> circuits;
    private ArrayList<Circuit> innatePoweredCircuits;
    
    
    public void routePower() {
        for (Circuit c : this.innatePoweredCircuits) {
            c.givePowerToNeighbours();
        }
    }
    
    
    /**
     * Clear the current power routing and
     * route power again from innate powered circuits onwards
     */
    public void checkPower() {
        this.innatePoweredCircuits.clear();
        for (Circuit c : this.circuits) {
            c.loseAllPower();
            if (c.isInnatePowered()) {
                innatePoweredCircuits.add(c);
            }
        }
        for (Circuit c : this.innatePoweredCircuits) {
            c.givePowerToNeighbours();
        }    
    }
    
    public void addCircuit(Circuit c) {
        this.circuits.add(c);
        if (c.isInnatePowered()) this.innatePoweredCircuits.add(c);
    }
    
}
