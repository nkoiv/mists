/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.util.Flags;
import java.util.HashMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/** MapObjects are basically anything that can be encountered on in a Location (dungeon/town/whatever)
 *  This generic class is meant for things that don't fit in various subclasses.
 *  MapObject is extended by, for example, PlayerCharacter, Creatures, SpellEffects, etc.
 *  Unlike a mere Sprite (which it heavily utilizes), MapObject is tied to a certain Location.
 * @author nkoiv
 */
public class MapObject extends Flags implements Global, Templatable {
    
    protected final String name;
    protected Sprite sprite;
    
    protected int collisionLevel;
    
    protected Location location;
    
    public MapObject (String name) {
        this.flags = new HashMap<>();
        this.name = name;
        this.setFlag("visible", 1);
    }

    
    public MapObject (String name, Image image) {
        this.flags  = new HashMap<>();
        this.name = name;
        this.sprite = new Sprite(image);
        this.setFlag("visible", 1);
    }
    
    public MapObject (String name, Image image, Location location, double xCoor, double yCoor) {
        this.flags  = new HashMap<>();
        this.name = name;
        this.location = location;
        this.sprite = new Sprite(image);
        this.sprite.setPosition(xCoor, yCoor);
        this.setFlag("visible", 1);
    }
    

    public boolean instersects(MapObject o) {
        return o.getSprite().intersects(this.getSprite());
    }
    
    
    public void setLocation(Location l) {
        this.location = l;
    }
    
    public void setPosition (double xPos, double yPos) {
        this.sprite.setPosition(xPos, yPos);
        this.sprite.refreshCollisionBox();
    }
    
    /**
     * Sets the (sprite) position so that the center of the sprite
     * (instead of top left corner) is at the given coordinates.
     * @param xPos Center position of the sprite on X
     * @param yPos Center position of the sprite on Y
     */
    public void setCenterPosition (double xPos, double yPos) {
        this.sprite.setPosition(xPos-(this.getSprite().getWidth()/2), yPos-(this.getSprite().getHeight()/2));
        this.sprite.refreshCollisionBox();
    }
    
    public int getCollisionLevel() {
        return this.collisionLevel;
    }
    
    public void setCollisionLevel(int cl) {
        this.collisionLevel = cl;
    }
       
    public Location getLocation() {
        return this.location;
    }
    
    public double getCenterXPos() {
        return this.sprite.getCenter()[0];
    }
    
    public double getCenterYPos(){
        return this.sprite.getCenter()[1];
    }
    
    public double getXPos(){
        return this.sprite.getXPos();
    }
    
    public double getYPos(){
        return this.sprite.getYPos();
    }
    
    public double getWidth() {
        return this.sprite.getWidth();
    }
    
    public double getHeight() {
        return this.sprite.getHeight();
    }
    
    /**
    * Render draws the Sprite of the MapObject on a given GraphicsContext
    * @param gc GraphicsContext where the object is drawn
    * @param xOffset Used to shift the objects xCoordinate so its drawn where the screen is
    * @param yOffset Used to shift the objects yCoordinate so its drawn where the screen is
    */
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.isFlagged("visible")) {
            this.sprite.render(xOffset, yOffset, gc);
        }
    }   
   
    /**
    * Set the MapObject a new Sprite (replacing the old old)
    * @param sprite Sprite to be added
    */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.sprite.refreshCollisionBox();
    }
    
    /**
    * Update the position of the MapObject
    * @param time Amount of time passed since the last update
    */
    public void update(double time) {
        this.sprite.update(time);
    }
    
    public Sprite getSprite() {
        return this.sprite;
    }
    
    public String getName() {
        return this.name;
    }

    @Override
    public String toString(){
        String s;
        if (this.location == null) s = this.name+" at Limbo";
        else s = this.name + " @ " + this.location.getName() + ":" + this.sprite.getXPos()+","+this.sprite.getYPos();
        return s;
    }

    @Override
    public Object createFromTemplate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
