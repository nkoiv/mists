/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * Effects are more or less temporary MapObjects.
 * They have start time and endtime.
 * TODO: Will pauses mess up since we're using systemtime? Probably!
 * @author nkoiv
 */
public class Effect extends MapObject {

    protected Action owner;
    protected MapObject linkedObject;
    protected boolean linkedLocation;
    protected double oldLinkX;
    protected double oldLinkY;
    protected long startTime;
    protected long endTime;
    
    public Effect(String name) {
        super(name);
    }
    
    public Effect(Action owner, String name, Location location, double xPos, double yPos, Sprite sprite, int durationMS) {
        super(name);
        this.owner = owner;
        this.setSprite(sprite);
        this.getSprite().setPosition(xPos, yPos);
        this.setLocation(location);
        this.setFlag("durationMS", durationMS);
        this.setFlag("startdurationMS", durationMS);
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + (durationMS);
        this.sprite.refreshCollisionBox();
    }
    
    /**
     * Effects are Owned by actions.
     * These actions dictate what happens when effect
     * lands on something.
     * @return Action that's reponsible for this effect
     */
    public Action getOwner() {
        return this.owner;
    }
    
    public void setLinkedObject(MapObject link) {
        this.linkedObject = link;
        this.linkedLocation = true;
        this.oldLinkX = link.getXPos();
        this.oldLinkY = link.getYPos();
    }
    
    @Override
    public void update(double time) {
        this.sprite.update(time);
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.isFlagged("visible")) {
            if (this.linkedLocation) this.updatePosition();
            this.getSprite().render(xOffset, yOffset, gc);
        }
        if(System.currentTimeMillis() > this.endTime)this.setFlag("removable", 1);
    }   
    
    private void updatePosition() {
        if (linkedObject.getXPos() != oldLinkX) {
            this.getSprite().setXPosition(this.getSprite().getXPos() + (linkedObject.getXPos()- oldLinkX));
            oldLinkX = linkedObject.getXPos();
        }
        if (linkedObject.getYPos() != oldLinkY) {
            this.getSprite().setYPosition(this.getSprite().getYPos() + (linkedObject.getYPos()- oldLinkY));
            oldLinkY = linkedObject.getYPos();
        }
    }

}
