/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Creature is a "living" MapObject
 * As such, they get (at least some) AI-routines
 * @author nkoiv
 */
public class Creature extends MapObject implements Combatant {
    
    private Direction facing;
    private HashMap<String, SpriteAnimation> spriteAnimations;
    
    /*Attributes are stored in a separate HashMap
    * These are not Flags because they're limited to creatures
    * Would be too easy to accidentally make walls alive if they were
    * TODO: Consider if the above would be awesome
    */
    
    private HashMap<String, Integer> attributes;
    private HashMap<String, Integer> effects;
    //TODO: Consider if effects should be used for MapObjects (can a wall or a rock get debuffs/buffs?)
    private HashMap<String, Action> availableActions;
    
    //Constructor for a creature template with one static image
    public Creature (String name, Image image) {
        super (name, image);
        this.setFacing(Direction.DOWN);
        this.initializeAttributes();
        this.initializeFlags();
    }
    
    //Constructing a creature template with sprite animations
    public Creature (String name, ImageView imageView, int frameCount, int startingXTile, int startingYTile, int frameWidth, int frameHeight) {
        super(name);
        this.setFacing(Direction.DOWN);
        this.setAnimations(imageView, frameCount, startingXTile, startingYTile, frameWidth, frameHeight);
        this.setSprite(new Sprite(this.spriteAnimations.get("downMovement").getCurrentFrame()));
        this.initializeAttributes();
        this.initializeFlags();

    }

    /*Constructing a creature with only a still image
    * TODO: This should not be needed once Libraries are done
    */
    public Creature(String name, Image image, Location location, double xCoor, double yCoor) {
        super(name, image, location, xCoor, yCoor);
        this.setFacing(Direction.DOWN);
        this.initializeAttributes();
        this.initializeFlags();
    }
    
    private void initializeAttributes() {
        this.attributes = new HashMap<>();
        this.setAttribute("Strength", 1);
        this.setAttribute("Agility", 1);
        this.setAttribute("Intelligence", 1);
        this.setAttribute("Speed", 50);
        this.setAttribute("MaxHealth", 100 );
        this.setAttribute("Health", 100);
    }
    
    private void initializeFlags() {
        this.setFlag("isVisible", 1);
        this.setFlag("collisionLevel", 100);
    }
    
    public void setAttribute (String attribute, int value) {
        if (this.attributes.containsKey(attribute)) {
            this.attributes.replace(attribute, value);
        } else {
            this.attributes.put(attribute, value);
        }
    }
    
    /* GetAttribute returns 0 when attribute is not found.
    *  If creature has no Armour, "getAttribute("Armour") gives 0.
    *  Due to this no combat mechanics should ever use division by attributes.
    */
    public int getAttribute(String attribute) {
        if (this.attributes.containsKey(attribute)) {
            return this.attributes.get(attribute);
        } else {
            return 0;
        }
    }
    
    @Override
    public void setEffect(String effect, int value) {
        if (this.effects.containsKey(effect)) {
            this.effects.replace(effect, value);
        } else {
            this.effects.put(effect, value);
        }
    }
    
    @Override
    public int getEffectValue(String effect) {
        if (this.effects.containsKey(effect)) {
            return this.effects.get(effect);
        } else {
            return 0;
        }
    }
    
    public void addAction(Action a) {
        if (this.availableActions == null) this.availableActions = new HashMap<>();
        if (!this.availableActions.containsKey(a.toString())) this.availableActions.put(a.toString(), a);
    }
    
    public void useAction(String name) {
        if (this.availableActions != null) {
            if (this.availableActions.containsKey(name)) this.availableActions.get(name).use(this);
        }
    }
    
    public ArrayList<String> getAvailableActions() {
        ArrayList<String> listOfActions = new ArrayList<>();
        if (this.availableActions != null) {
            for (String actionName : this.availableActions.keySet()) {
                listOfActions.add(actionName);
            }
        }
        return listOfActions;
    }
    
    /* setAnimations is used to easily set all movement animations
    *  It assumes spritesheet is set up with rows for Down, Left, Right, Up - in that order
    *  and that offset between individual frames is 0
    */
    public void setAnimations(ImageView imageview, int frameCount, int startingXTile, int startingYTile, int frameWidth, int frameHeight) {
        this.spriteAnimations = new HashMap<>();
        this.setAnimation("downMovement",
          imageview, frameCount, (startingXTile*frameWidth)+0, (startingYTile*frameHeight)+0, 0, 0, frameWidth, frameHeight      
        );
        this.setAnimation("leftMovement",
          imageview, frameCount, (startingXTile*frameWidth)+0, (startingYTile*frameHeight)+(frameHeight), 0, 0, frameWidth, frameHeight      
        );
        this.setAnimation("rightMovement",
          imageview, frameCount, (startingXTile*frameWidth)+0, (startingYTile*frameHeight)+(frameHeight*2), 0, 0, frameWidth, frameHeight      
        );
        this.setAnimation("upMovement",
          imageview, frameCount, (startingXTile*frameWidth)+0, (startingYTile*frameHeight)+(frameHeight*3), 0, 0, frameWidth, frameHeight      
        );    
    }
    
    public void setAnimation(String animationName, ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
        if (this.spriteAnimations == null) {
            this.spriteAnimations = new HashMap<>();
        }
        if (this.spriteAnimations.containsKey(animationName)) {
            this.spriteAnimations.replace(animationName, new SpriteAnimation(imageView, frameCount, startX, startY, offsetX, offsetY, frameWidth, frameHeight));
        } else {
            this.spriteAnimations.put(animationName, new SpriteAnimation(imageView, frameCount, startX, startY, offsetX, offsetY, frameWidth, frameHeight));
        }    
    }
    
    @Override
    public void update (double time) {
        this.updateSprite();
        this.applyMovement(time);   
    }
    
    /*
    * TODO: General sprite updates for general creatures
    * Is everything animated?
    */
    void updateSprite() {
        if (this.isFlagged("isVisible") && !this.spriteAnimations.isEmpty()) {
            //Mists.logger.log(Level.INFO, "{0} is facing {1}", new Object[]{this.getName(), this.facing});
            switch(this.facing) {
                case UP: this.getSprite().setAnimation(this.spriteAnimations.get("upMovement")); break;
                case DOWN: this.getSprite().setAnimation(this.spriteAnimations.get("downMovement")); break;
                case LEFT: this.getSprite().setAnimation(this.spriteAnimations.get("leftMovement")); break;
                case RIGHT: this.getSprite().setAnimation(this.spriteAnimations.get("rightMovement")); break;
                case UPRIGHT:;
                case UPLEFT: ;
                case DOWNRIGHT: ;
                case DOWNLEFT: ;
                default: break;
            }
        }
        
    }
    
    public void applyMovement(double time){
        /*
        * Check collisions before movement
        * TODO: Add in pixel-based collision detection (compare alphamaps?)
        */
        if (this.getLocation().checkCollisions(this) == null) {
            this.getSprite().update(time); //Collided with nothing, free to move
        } else {
            
            MapObject collidingObject = this.getLocation().checkCollisions(this); //Get the colliding object
            Mists.logger.log(Level.INFO, "{0} bumped into {1}", new Object[]{this, collidingObject});
            double collidingX = collidingObject.getSprite().getXPos()+(collidingObject.getSprite().getWidth()/2);
            double collidingY = collidingObject.getSprite().getYPos()+(collidingObject.getSprite().getHeight()/2);
            double thisX = this.getSprite().getXPos()+(this.getSprite().getWidth()/2);
            double thisY = this.getSprite().getYPos()+(this.getSprite().getHeight()/2);
            
            double xDistance = Math.abs(thisX - collidingX);
            double yDistance = Math.abs(thisY - collidingY);
            //Mists.logger.log(Level.INFO, "At the distance of x: {0} y: {1}", new Object[]{xDistance, yDistance});
            
            /*
            * Prevent further going into colliding object
            * Because every other directino is allowed "jittering" past two colliding objects might be possible
            * TODO: Test collisions on tiles and see if tweaking is needed
            */
            
            if(this.getSprite().getXVelocity() != 0 && (yDistance<xDistance)) {
                if (thisX<collidingX) { //Colliding object is to the right
                    if (this.getSprite().getXVelocity()>0) { // Check if we're trying to move right
                        this.getSprite().setVelocity(0, this.getSprite().getYVelocity()); //Stop movement right
                    }
                } else if (thisX>collidingX) { //Colliding object is to the left 
                    if (this.getSprite().getXVelocity()<0) { //We're trying to move left
                        this.getSprite().setVelocity(0, this.getSprite().getYVelocity());
                    }
                }
            }
           if(this.getSprite().getYVelocity() != 0 && (xDistance<yDistance)) {
                if (thisY<collidingY) { //Colliding object is below
                    if (this.getSprite().getYVelocity()>0) { //We're trying to move down
                        this.getSprite().setVelocity(this.getSprite().getXVelocity(),0);
                    }
                } else if (thisY>collidingY) { //Colliding object is above

                    if (this.getSprite().getYVelocity()<0) { //We're trying to move up
                        this.getSprite().setVelocity(this.getSprite().getXVelocity(),0);
                    }
                }
           }
           this.getSprite().update(time);
            
        }
    }
    
    @Override
    public boolean moveTowards (Direction direction) {
        this.setFlag("Moving", 1);
        switch(direction) {
        case UP: return moveUp();
        case DOWN: return moveDown();
        case LEFT: return moveLeft();
        case RIGHT: return moveRight();
        case UPRIGHT: return moveUpRight();
        case UPLEFT: return moveUpLeft();
        case DOWNRIGHT: return moveDownRight();
        case DOWNLEFT: return moveDownLeft();
        default: break;
    }
        
        return false;
    }
    
    private boolean moveUp() {
        this.facing = Direction.UP;
        this.getSprite().addVelocity(0, -this.getAttribute("Speed"));
        return true;
    }
    
    private boolean moveDown() {
        this.facing = Direction.DOWN;
        this.getSprite().addVelocity(0, this.getAttribute("Speed"));
        return true;
    }
        
    private boolean moveLeft() {
        this.facing = Direction.LEFT;
        this.getSprite().addVelocity(-this.getAttribute("Speed"), 0);
        return true;
    }
    
    private boolean moveRight() {
        this.facing = Direction.RIGHT;
        this.getSprite().addVelocity(this.getAttribute("Speed"), 0);
        return true;
    }

    private boolean moveUpRight() {
        this.moveUp();
        this.moveDown();
        this.facing = Direction.UPRIGHT;
        return true;
    }
    
    private boolean moveUpLeft() {
        this.moveUp();
        this.moveLeft();
        this.facing = Direction.UPLEFT;
        return true;
    }
        
    private boolean moveDownRight() {
        this.moveDown();
        this.moveRight();
        this.facing = Direction.DOWNRIGHT;
        return true;
    }
    
    private boolean moveDownLeft() {
        this.moveDown();
        this.moveLeft();
        this.facing = Direction.DOWNLEFT;
        return true;
    }

    public void stopMovement() {
        this.setFlag("Moving", 0);
        this.getSprite().setVelocity(0, 0);
        this.getSprite().setImage(this.getSprite().getImage());
    }

    public Direction getFacing() {
        return this.facing;
    }
    
    public void setFacing(Direction d) {
        this.facing = d;
    }
    
    //TODO: Calculate attack and defense values from their sources
    @Override
    public int getDV() {
        return 0;
    }

    @Override
    public int getAV() {
        return 0;
    }

    public int getHealth() {
        return this.getAttribute("Health");
    }


    public void setHealth(int health) {
        this.setAttribute("Health", health);
    }


    public int getMaxHealth() {
        return this.getAttribute("MaxHealth");
    }


    public void setMaxHealth(int maxHealth) {
        this.setAttribute("MaxHealth", maxHealth);
    }
    
    public void setSpeed (int s) {
        this.setAttribute("Speed", s);
    }

    @Override
    public void takeDamage(int damage) {
        this.setHealth(this.getHealth()-damage);
        //TODO: Check death
    }
	
    @Override
    public void healHealth(int healing) {
        this.setHealth(this.getHealth()+healing);
        //Prevent healing over MaxHP
        if(this.getHealth()>this.getMaxHealth()){this.setHealth(this.getMaxHealth());} 
    }
    
}
