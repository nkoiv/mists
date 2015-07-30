/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/** MapObjects are basically anything that can be encountered on in a Location (dungeon/town/whatever)
 *  This generic class is meant for things that don't fit in various subclasses.
 *  MapObject is extended by, for example, PlayerCharacter, Creatures, SpellEffects, etc.
 *  Unlike a mere Sprite (which it heavily utilizes), MapObject is tied to a certain Location.
 * @author nkoiv
 */
public class MapObject implements Global {
    
    private final String name;
    private Sprite sprite;
    private Location location;

    private boolean visible;
    private int collisionLevel;
    
    public MapObject (String name, Image image) {
        this.name = name;
        this.sprite = new Sprite(image);
        this.visible = true;
    }
    
    public MapObject (String name, Image image, Location location, double xCoor, double yCoor) {
        this.name = name;
        this.location = location;
        this.sprite = new Sprite(image);
        this.sprite.setPosition(xCoor, yCoor);
        this.visible = true;
    }
    
    public void setCollisionLevel(int c) {
        this.collisionLevel = c;
    }
    
    public int getCollisionLevel() {
        return this.collisionLevel;
    }
    
    public boolean instersects(MapObject o) {
        return o.getSprite().getBoundary().intersects( this.getSprite().getBoundary() );
    }
    
    public void setVisible(boolean v) {
        this.visible = v;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setLocation(Location l) {
        this.location = l;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public double getxPos(){
        return this.sprite.getXPos();
    }
    
    public double getyPos(){
        return this.sprite.getYPos();
    }
    
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.visible) {
            this.sprite.render(xOffset, yOffset, gc);
        }
    }   
   
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    
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
        s = this.name + " at " + this.location.getName() + ":" + this.sprite.getXPos()+","+this.sprite.getYPos();
        return s;
    }
    
}
