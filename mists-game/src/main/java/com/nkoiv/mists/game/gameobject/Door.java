/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.actions.Trigger;
import com.nkoiv.mists.game.sprites.Sprite;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class Door  extends Structure {
    private int closedCollisionLevel;
    private boolean open;
    private Image openImage;
    private Image closedImage;
    private Action toggleAction;
    
    public Door(String name, Image closedImage, Image openImage, int collisionLevel) {
        super(name, closedImage, collisionLevel);
        this.closedCollisionLevel = collisionLevel;
        this.closedImage = closedImage;
        this.openImage = openImage;
        this.open = false;
    }
    
    public void toggle() {
        if (this.isOpen()) this.close();
        else this.open();
    }
    
    public void open() {
       this.open = true; 
       this.setCollisionLevel(0);
       this.getSprite().setImage(openImage);
    }
    
    public void close() {
        this.open = false;
        this.setCollisionLevel(closedCollisionLevel);
        this.getSprite().setImage(closedImage);
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    @Override
    public Trigger[] getTriggers() {
        Trigger[] a = new Trigger[]{new doorTrigger(this)};
        return a;
    }
    
    private class doorTrigger implements Trigger {
        private final Door door;
        
        public doorTrigger(Door d) {
            this.door = d;
        }
        
        @Override
        public void toggle() {
            this.door.toggle();
        }

        @Override
        public MapObject getTarget() {
            return this.door;
        }
        
    }
    
    @Override
    public Door createFromTemplate() {
        Door nd = new Door(this.name, this.openImage, this.closedImage, this.collisionLevel);
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getSprite().getXPos();
                double yOffset = s.getYPos() - this.getSprite().getYPos();
                nd.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return nd;
    }
}
