/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.image.Image;

/**
 * PlayerCharacter is currently designed to be unique per game
 * TODO: Implement "World owner" as the main target instead of player,
 * so that several players can coexist in a single game
 * @author daedra
 */
public class PlayerCharacter extends MapObject implements Combatant {
    private Direction facing;
    private boolean alive;
    
    //A bunch of Strings to be used for describing various attacks
    private ArrayList<String> overpoweringAttack;
    private ArrayList<String> overpoweringDefense;
    private ArrayList<String> weakAttack;
    private ArrayList<String> weakDefense;
	
    private boolean gender; //true=male, false=female
	
    //Attributes
    private int speed;
    private int maxHealth;
    private int health;
    private int attackValue;
    private int defenseValue;

    public PlayerCharacter(String name, Image image) {
        super(name, image);
        this.setCollisionLevel(100);
        this.facing = Direction.DOWN;
        this.alive = true;
        this.maxHealth = 100;
        this.health = this.maxHealth;
        this.attackValue = 10;
        this.defenseValue = 10;
        this.speed = 50;
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

    @Override
    public void update (double time) {
        this.applyMovement(time);
        
    }
    
    private void applyMovement(double time){
                /*
        * Check collisions before movement
        * TODO: Add in pixel-based collision detection (compare alphamaps?)
        */
        if (this.getLocation().checkCollisions(this) == null) {
            this.getSprite().update(time); //Collided with nothing, free to move
        } else {
            
            MapObject collidingObject = this.getLocation().checkCollisions(this); //Get the colliding object
            Mists.logger.log(Level.INFO, "{0} bumped into {1}", new Object[]{this, collidingObject});
            double collidingX = collidingObject.getSprite().getXPos()+(collidingObject.getSprite().getImage().getWidth()/2);
            double collidingY = collidingObject.getSprite().getYPos()+(collidingObject.getSprite().getImage().getHeight()/2);
            double thisX = this.getSprite().getXPos()+(this.getSprite().getImage().getWidth()/2);
            double thisY = this.getSprite().getYPos()+(this.getSprite().getImage().getHeight()/2);
            
            double xDistance = Math.abs(thisX - collidingX);
            double yDistance = Math.abs(thisY - collidingY);
            Mists.logger.log(Level.INFO, "At the distance of x: {0} y: {1}", new Object[]{xDistance, yDistance});
            //double allowedXDistance = (this.getSprite().getImage().getWidth()/2)+(collidingObject.getSprite().getImage().getWidth()/2);
            //double allowedYDistance = (this.getSprite().getImage().getHeight()/2)+(collidingObject.getSprite().getImage().getHeight()/2);
            
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
    
    public void stopMovement() {
        this.getSprite().setVelocity(0, 0);
    }
    
    @Override
    public boolean moveTowards (Direction direction) {
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
}
