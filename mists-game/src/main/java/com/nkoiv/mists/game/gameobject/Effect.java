/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.gameobject;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;

/**
 * Effects are more or less temporary MapObjects.
 * They have start time and endtime.
 * @author nkoiv
 */
public class Effect extends MapObject {

    protected Action owner;
    protected double elapsedTime;
    protected double endTime;
    
    protected MapObject linkedObject;
    protected boolean linkedLocation;
    protected double oldLinkX;
    protected double oldLinkY;
    
    public Effect(String name) {
        super(name);
    }
    
    public Effect(Action owner, String name, Sprite sprite, int durationMS) {
        super(name);
        this.owner = owner;
        this.graphics = sprite;
        this.setFlag("durationMS", durationMS);
        this.setFlag("startdurationMS", durationMS);
        this.elapsedTime = 0;
        this.endTime = durationMS;
    }
    
    public void setOwner(Action owner) {
        this.owner = owner;
    }
    
    /**
     * Effects can be Owned by actions.
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
    
    public Sprite getSprite() {
        return (Sprite)this.graphics;
    }
    
    @Override
    public void update(double time) {
        this.graphics.update(time);
        this.elapsedTime = elapsedTime+(time*1000);
        if (endTime < 0) return;
        if(elapsedTime > endTime)this.remove();
        if (!isRemovable()) doCollisions();
    }
    
    private void doCollisions() {
        if (this.owner==null) return;
        ArrayList<MapObject> collisions = this.location.checkCollisions(this);
        if (!collisions.isEmpty()) {       
            this.getOwner().hitOn(this, collisions);
        }
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.isFlagged("visible")) {
            if (this.linkedLocation) this.updatePosition();
            this.getSprite().render(xOffset, yOffset, gc);
        }
    }   
    
    protected void updatePosition() {
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
