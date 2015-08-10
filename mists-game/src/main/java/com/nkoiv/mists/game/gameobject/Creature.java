/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.world.Location;
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
    private boolean moving;
    private boolean alive;
    private boolean visible;
    
    /*Attributes
    * These are not Flags because they're mandatory and limited to creatures
    * Would be too easy to accidentally make walls alive if they were
    * TODO: Consider if the above would be awesome
    * TODO: Consider making an "attributes" HashMap for these
    */
    private int strength;
    private int agility;
    private int intelligence;
    private int speed;
    private int maxHealth;
    private int health;
    private int attackValue;
    private int defenseValue;
    
    
    //Constructor for a creature with one static image
    public Creature (String name, Image image) {
        super (name, image);
        this.visible = true;
    }
    
    //Constructing a creature with sprite animations
    public Creature (String name, ImageView imageView, int frameCount, int startingXTile, int startingYTile, int frameWidth, int frameHeight) {
        super(name);
        this.setAnimations(imageView, frameCount, startingXTile, startingYTile, frameWidth, frameHeight);
        this.setSprite(new Sprite(this.spriteAnimations.get("downMovement").getCurrentFrame()));

    }

    public Creature(String name, Image image, Location location, double xCoor, double yCoor) {
        super(name, image, location, xCoor, yCoor);
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
        if (this.moving && !this.spriteAnimations.isEmpty()) {
            //Mists.logger.log(Level.INFO, "{0} is moving {1}", new Object[]{this.getName(), this.facing});
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
            * 
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
        this.moving = true;
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
        this.getSprite().addVelocity(0, -this.speed);
        return true;
    }
    
    private boolean moveDown() {
        this.facing = Direction.DOWN;
        this.getSprite().addVelocity(0, this.speed);
        return true;
    }
        
    private boolean moveLeft() {
        this.facing = Direction.LEFT;
        this.getSprite().addVelocity(-this.speed, 0);
        return true;
    }
    
    private boolean moveRight() {
        this.facing = Direction.RIGHT;
        this.getSprite().addVelocity(this.speed, 0);
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
    
    public boolean isMoving() {
        return this.moving;
    }
    
    private void setMoving(boolean b) {
        this.moving = b;
    }
    
    public void stopMovement() {
        this.moving = false;
        this.getSprite().setVelocity(0, 0);
    }

    public Direction getFacing() {
        return this.facing;
    }
    
    public void setFacing(Direction d) {
        this.facing = d;
    }
    
    public boolean isAlive() {
            return alive;
    }

    public void setAlive(boolean alive) {
            this.alive = alive;
    }
    
    @Override
    public int getDV() {
        return defenseValue;
    }


    @Override
    public void setDV(int defenseValue) {
        this.defenseValue = defenseValue;
    }


    @Override
    public int getAV() {
        return attackValue;
    }


    @Override
    public void setAV(int attackValue) {
        this.attackValue = attackValue;
    }


    public int getHealth() {
        return health;
    }


    public void setHealth(int health) {
        this.health = health;
    }


    public int getMaxHealth() {
        return maxHealth;
    }


    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    
    public void setSpeed (int s) {
        this.speed = s;
    }

    @Override
    public void takeDamage(int damage) {
        this.health-=damage;
        //TODO: Check death
    }
	
    @Override
    public void healHealth(int healing) {
        this.health+=healing;
        //Prevent healing over MaxHP
        if(this.health>this.maxHealth){this.health=this.maxHealth;} 
    }
    
}
