/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

/**
 * Shapechangers are things that can be interacted with
 * to adjust them in some way. In practice this is a good
 * hook for Triggers.
 * Good examples of shapechangers are various visible Puzzle
 * -components that can be manipulated to solve the puzzle.
 * @author nikok
 */
public interface Shapechanger {
    
    public boolean shiftMode();
    
}
