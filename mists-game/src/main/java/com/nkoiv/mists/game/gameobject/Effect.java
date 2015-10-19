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

/**
 * Effects are more or less temporary MapObjects.
 * They have start time and endtime.
 * TODO: Will pauses mess up since we're using systemtime? Probably!
 * @author nkoiv
 */
public class Effect extends MapObject {

    private Action owner;
    private long startTime;
    private long endTime;
    
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
    
    public Action getOwner() {
        return this.owner;
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
