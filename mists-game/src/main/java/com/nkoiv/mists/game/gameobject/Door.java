/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.DoorTrigger;
import com.nkoiv.mists.game.triggers.Trigger;
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
    
    
    public Door(String name, Image closedImage, Image openImage, int collisionLevel) {
        super(name, closedImage, collisionLevel);
        this.closedCollisionLevel = collisionLevel;
        this.closedImage = closedImage;
        this.openImage = openImage;
        this.open = false;
    }
    
    @Override
    public Sprite getSprite() {
        return (Sprite)this.graphics;
    }
    
    public void toggle() {
        Mists.logger.info("Toggling door");
        if (this.isFlagged("locked")) {
            this.addTextPopup("Locked!");
        } else {
            if (this.isOpen()) this.close();
            else this.open();
            this.location.getCollisionMap().updateCollisionLevels();
            this.location.getPathFinder().setMapOutOfDate(true);
        }
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

    public boolean isLocked() {
        return isFlagged("locked");
    }

    public void setLocked(boolean locked) {
        if (locked) {
            this.setFlag("locked", 1);
        } else {
            this.setFlag("locked", 0);
        }
    }
    
    
    @Override
    public Trigger[] getTriggers() {
        Trigger[] a = new Trigger[]{new DoorTrigger(this)};
        return a;
    }
    
    
    @Override
    public Door createFromTemplate() {
        Door nd = new Door(this.name, this.closedImage, this.openImage, this.collisionLevel);
        for (String f : this.flags.keySet()) {
            nd.setFlag(f, this.flags.get(f));
        }
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getXPos();
                double yOffset = s.getYPos() - this.getYPos();
                nd.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return nd;
    }
} 