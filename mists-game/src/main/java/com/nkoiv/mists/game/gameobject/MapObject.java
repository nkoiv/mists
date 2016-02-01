/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.actions.Trigger;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.util.Flags;
import java.util.HashMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

/** MapObjects are basically anything that can be encountered on in a Location (dungeon/town/whatever)
 *  This generic class is meant for things that don't fit in various subclasses.
 *  MapObject is extended by, for example, PlayerCharacter, Creatures, SpellEffects, etc.
 *  Unlike a mere Sprite (which it heavily utilizes), MapObject is tied to a certain Location.
 * @author nkoiv
 */
public class MapObject extends Flags implements Global, Templatable {
    protected int templateID;
    protected final String name;
    protected MovingGraphics graphics;
    
    protected int collisionLevel;
    protected boolean removable;
    protected Location location;
    
    protected int IDinLocation;
    
    public MapObject (String name) {
        this.flags = new HashMap<>();
        this.name = name;
        this.flags.put("visible", 1);
    }

    
    public MapObject (String name, Image image) {
        this(name, new Sprite(image));
    }
    
    public MapObject (String name, MovingGraphics graphics) {
        this(name);
        this.graphics = graphics;
    }
    

    public boolean instersects(MapObject o) {
        return o.getGraphics().intersects(this.getGraphics());
    }
    
    
    public void setLocation(Location l) {
        this.location = l;
    }
    
    public void setPosition (double xPos, double yPos) {
        this.graphics.setPosition(xPos, yPos);
    }
    
    /**
     * Sets the (sprite) position so that the center of the sprite
     * (instead of top left corner) is at the given coordinates.
     * @param xPos Center position of the sprite on X
     * @param yPos Center position of the sprite on Y
     */
    public void setCenterPosition (double xPos, double yPos) {
        this.graphics.setCenterPosition(xPos, yPos);
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
        return this.graphics.getCenterXPos();
    }
    
    public double getCenterYPos(){
        return this.graphics.getCenterYPos();
    }
    
    public double getXPos(){
        return this.graphics.getXPos();
    }
    
    public double getYPos(){
        return this.graphics.getYPos();
    }
    
    public double getWidth() {
        return this.graphics.getWidth();
    }
    
    public double getHeight() {
        return this.graphics.getHeight();
    }
    
    public Double[] getCorner(Direction d) {
        return this.getGraphics().getCorner(d);
    }
    
    public boolean intersects(MapObject mob) {
        return this.graphics.intersects(mob.getGraphics());
    }
    
    public boolean intersects(Shape s) {
        return this.graphics.intersectsWithShape(s);
    }
    
    /**
    * Render draws the Sprite of the MapObject on a given GraphicsContext
    * @param gc GraphicsContext where the object is drawn
    * @param xOffset Used to shift the objects xCoordinate so its drawn where the screen is
    * @param yOffset Used to shift the objects yCoordinate so its drawn where the screen is
    */
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.isFlagged("visible")) {
            this.graphics.render(xOffset, yOffset, gc);
        }
    }
    
    public void renderCollisions(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.isFlagged("visible")) {
            this.graphics.renderCollisions(xOffset, yOffset, gc);
        }
    }
   
    /**
    * Set the MapObject a new Sprite (replacing the old old)
    * @param sprite Sprite to be added
    */
    public void setSprite(Sprite sprite) {
        this.graphics = sprite;
    }
    
    /**
    * Update the position of the MapObject
    * @param time Amount of time passed since the last update
    */
    public void update(double time) {
        this.graphics.update(time);
    }
    
    /**
     * Get the list of triggers that can be performed on
     * this MapObject;
     * @return 
     */
    public Trigger[] getTriggers() {
        return new Trigger[0];
    }
    
    protected MovingGraphics getGraphics() {
        return this.graphics;
    }
    
    public java.awt.Shape getBoundary() {
        return this.graphics.getShape();
    }
    
    public int getTemplateID() {
        return this.templateID;
    }
    
    public void setTemplateID(int baseID) {
        this.templateID = baseID;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Image getSnapshot() {
        return this.graphics.getImage();
    }
    
    public void setRemovable() {
        this.removable = true;
    }
    
    public boolean isRemovable() {
        return this.removable;
    }
    
    public void setID(int IDinLocation) {
        this.IDinLocation = IDinLocation;
    }
    
    public int getID() {
        return this.IDinLocation;
    }
    
    public String[] getInfoText() {
        String[] s = new String[]{
            this.name,
            "ID "+this.IDinLocation+" @ "+this.location.getName(),
            "X:"+((int)this.getXPos())+" Y:"+((int)this.getYPos())};
        return s;
    }
    
    @Override
    public String toString(){
        String s;
        if (this.location == null) s = this.name+" at Limbo";
        else s = this.name + " (ID:"+this.IDinLocation+") @ |"  + this.graphics.getXPos()+","+this.graphics.getYPos()+"|";
        return s;
    }

    @Override
    public Object createFromTemplate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
