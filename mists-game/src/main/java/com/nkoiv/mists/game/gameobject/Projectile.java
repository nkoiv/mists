/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.sprites.Sprite;

/**
 * Projectile is a type of effect that has
 * velocity on a vector by default
 * @author nikok
 */
public class Projectile extends Effect {
    
    private double xVelocity;
    private double yVelocity;
    
    public Projectile(Action owner, String name, Sprite sprite, int durationMS, double xVelocity, double yVelocity) {
        super(owner, name, sprite, durationMS);
        ((Sprite)(this.graphics)).setVelocity(xVelocity, yVelocity);
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
    }

    public double getInitialXVelocity() {
        return this.xVelocity;
    }
    
    public double getInitialYVelocity() {
        return this.yVelocity;
    }
    
}
