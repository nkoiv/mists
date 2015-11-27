/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author nikok
 */
public class Projectile extends Effect {

    private double xTarget;
    private double yTarget;
    
    public Projectile(String name) {
        super(name);
    }
    
        @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        //Mists.logger.log(Level.INFO, "Rendering the Effect [{0}]", this.getName());
        if (this.isFlagged("visible")) {
            this.getSprite().render(xOffset, yOffset, gc);
        }
        if(System.currentTimeMillis() > this.endTime)this.setFlag("removable", 1);
    }  
    
}
