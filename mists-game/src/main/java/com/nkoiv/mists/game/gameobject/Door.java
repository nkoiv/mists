/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class Door  extends Structure {
    
    private boolean open;
    private Image openImage;
    private Image closedImage;
    
    public Door(String name, Image closedImage, Image openImage, int collisionLevel) {
        super(name, closedImage, collisionLevel);
        this.closedImage = closedImage;
        this.openImage = openImage;
        this.open = false;
    }
    
    public void toggle() {
        
    }
    
    public void open() {
        
       
    }
    
    public void close() {
        
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
}
