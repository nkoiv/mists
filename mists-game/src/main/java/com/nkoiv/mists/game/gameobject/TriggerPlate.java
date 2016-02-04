/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.Sprite;

/**
 * Triggerplate is an invisible structure
 * with collisionlevel 0 that can be placed
 * on the ground to have something happen
 * when it's touched.
 * @author nikok
 */
public class TriggerPlate extends Effect {
    
    public TriggerPlate(String name, double width, double height) {
        super(name, new Sprite(Mists.graphLibrary.getImage("blank")), 0);
        super.getSprite().setWidth(width);
        super.getSprite().setHeight(height);
    }
    
    
    
    
}
