/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.sprites.Sprite;
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
    //private Location location;

    private boolean visible;
    
    public MapObject (String name, Image image) {
        this.name = name;
        this.sprite = new Sprite(image);
    }
    
    public MapObject (String name, Image image, double xCoor, double yCoor) {
        this.name = name;
        this.sprite = new Sprite(image);
        this.sprite.setPosition(xCoor, yCoor);
    }
    
    public void render(GraphicsContext gc) {
        if (this.visible) {
            this.sprite.render(gc);
        }
    }
    
    public void setPosition (double xCoor, double yCoor) {
        this.sprite.setPosition(xCoor, yCoor);
    }
    
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    
    public Sprite getSprite() {
        return this.sprite;
    }
    
    public String getName() {
        return this.name;
    }

    
}
