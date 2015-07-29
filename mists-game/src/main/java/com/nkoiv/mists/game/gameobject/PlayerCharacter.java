/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import javafx.scene.image.Image;

/**
 *
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
        this.facing = Direction.DOWN;
        this.alive = true;
        this.maxHealth = 100;
        this.health = this.maxHealth;
        this.attackValue = 10;
        this.defenseValue = 10;
        this.speed = 20;
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
        this.getSprite().addVelocity(0, -this.speed);
        return true;
    }
    
    private boolean moveDown() {
        this.getSprite().addVelocity(0, this.speed);
        return true;
    }
        
    private boolean moveLeft() {
        this.getSprite().addVelocity(-this.speed, 0);
        return true;
    }
    
    private boolean moveRight() {
        this.getSprite().addVelocity(this.speed, 0);
        return true;
    }

    private boolean moveUpRight() {
        this.moveUp();
        this.moveDown();
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
        return true;
    }
    
    private boolean moveDownLeft() {
        this.moveDown();
        this.moveLeft();
        return true;
    }
}
