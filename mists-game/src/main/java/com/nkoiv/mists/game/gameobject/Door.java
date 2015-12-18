/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.actions.Trigger;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class Door  extends Structure {
    
    private boolean open;
    private Image openImage;
    private Image closedImage;
    private Action toggleAction;
    
    public Door(String name, Image closedImage, Image openImage, int collisionLevel) {
        super(name, closedImage, collisionLevel);
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
       this.getSprite().setImage(openImage);
    }
    
    public void close() {
        this.open = false;
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
        
    }
}
