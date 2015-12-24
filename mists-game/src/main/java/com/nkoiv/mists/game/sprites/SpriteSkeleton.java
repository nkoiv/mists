/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import com.nkoiv.mists.game.items.Item;
import java.util.HashMap;
import javafx.scene.canvas.GraphicsContext;

/**
 * SpriteSkeleton is a collection of sprites moving in uniform
 * Class is mainly made for creatures composed of several sprites (head, arms, legs...)
 * Inside a SpriteSkeleton, each individual sprite only knows its relative position
 *                  
 *   _____________
 *  |X  x----     |
 *  |   |head|    |
 *  |x_ x---- x_  |
 *  ||a||body||a| |
 *  ||r||    ||r| |
 *  ||m||    ||m| |
 *  | -  ----  -  |
 *  |_____________| 
 *  (x marks the spot)
 * @author nikok
 */
public class SpriteSkeleton extends MovingGraphics {
    HashMap<String, Sprite> sprites; //HashMap is used to individualize parts and replace them by name as need be
    CollisionBox collisionBox;
    
    public SpriteSkeleton() {
        this.sprites = new HashMap<>();
        this.collisionBox = new CollisionBox();
    }
    
    public void equipItem(Item item) {
        
    }
    
    public void addPart(String partName, Sprite part) {
        this.sprites.put(partName, part);
        this.updateDimensions();
        this.refreshCollisionBox();
    }
    
    public void removePart(String partName) {
        this.sprites.remove(partName);
        this.updateDimensions();
        this.refreshCollisionBox();
    }
    
    /**
     * SpriteSkeleton is composed of lots of smallest sprites, 
     * so the effective "size" of the skeleton is composite of
     * those individual pieces.
     */
    private void updateDimensions(){
        double smallestX = 0;
        double largestX = 0;
        double smallestY = 0;
        double largestY = 0;
        for (String s : this.sprites.keySet()) {
            Sprite sp = this.sprites.get(s);
            if (sp.getXPos() < smallestX) smallestX = sp.getXPos();
            if (sp.getYPos() < smallestY) smallestY = sp.getYPos();
            if (sp.getXPos()+sp.getWidth() > largestX) largestX = sp.getXPos()+sp.getWidth();
            if (sp.getYPos()+sp.getHeight() > largestY) largestY = sp.getYPos()+sp.getHeight();
        }
        this.width = Math.abs(smallestX) + Math.abs(largestX);
        this.height = Math.abs(smallestY) + Math.abs(largestY);
    }
    
        @Override
    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
        this.refreshCollisionBox();
    }
    
    @Override
    public void setCenterPosition(double x, double y) {
        this.setPosition(x-(this.getWidth()/2), y-(this.getHeight()/2));
        this.refreshCollisionBox();
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.sprites.isEmpty()) return;
        for (String s : this.sprites.keySet()) {
            sprites.get(s).render(this.positionX-xOffset, this.positionY-yOffset, gc);
        }
    }
    
    @Override
    public void update(double time) {
        super.update(time);
        if (this.sprites.isEmpty()) return;
        for (String s : this.sprites.keySet()) {
            sprites.get(s).update(time);
        }
    }

    
}
