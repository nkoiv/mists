/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
import java.util.HashMap;
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
    
    /*Flags are stored in integers for utility, 
    * but can be manipulad as booleans (>0) by "isFlagged" & "addFlag" methods
    */
    private HashMap<String, Integer> flags;
    
    private Location location;
    
    public MapObject (String name) {
        this.flags = new HashMap<>();
        this.name = name;
        this.addFlag("isVisible");
    }

    
    public MapObject (String name, Image image) {
        this.flags  = new HashMap<>();
        this.name = name;
        this.sprite = new Sprite(image);
        this.addFlag("isVisible");
    }
    
    public MapObject (String name, Image image, Location location, double xCoor, double yCoor) {
        this.flags  = new HashMap<>();
        this.name = name;
        this.location = location;
        this.sprite = new Sprite(image);
        this.sprite.setPosition(xCoor, yCoor);
        this.addFlag("isVisible");
    }
    
    public void setFlag(String flag, int value) {
        if (this.flags.containsKey(flag)) {
            this.flags.replace(flag, value);
        } else {
            this.flags.put(flag, value);
        }   
    }
    
    public int getFlag(String flag) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag);
        } else {
            return 0;
        }
    }
    
    //TODO: Consider if adding flag should NOT reset flag value to 1 if flag already exists
    public void addFlag(String flag){
       if (this.flags.containsKey(flag)) {
            this.flags.replace(flag, 1);
        } else {
            this.flags.put(flag, 1);
        } 
    }
    
    public boolean isFlagged (String flag) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag) > 0;
        } else {
            return false;
        }
    }
    

    public boolean instersects(MapObject o) {
        return o.getSprite().intersects(this.getSprite());
    }
    
    
    public void setLocation(Location l) {
        this.location = l;
    }
    
    public void setPosition (double xPos, double yPos) {
        this.sprite.setPosition(xPos, yPos);
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
        if (this.isFlagged("isVisible")) {
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
