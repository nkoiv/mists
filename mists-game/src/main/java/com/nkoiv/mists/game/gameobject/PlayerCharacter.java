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
        this.getSprite().setVelocity(0, -10);
        return true;
    }
    
    private boolean moveDown() {
        this.getSprite().setVelocity(0, 10);
        return true;
    }
        
    private boolean moveLeft() {
        this.getSprite().setVelocity(-10, 0);
        return true;
    }
    
    private boolean moveRight() {
        this.getSprite().setVelocity(10, 0);
        return true;
    }

    private boolean moveUpRight() {
        this.getSprite().setVelocity(5, -5);
        return true;
    }
    
    private boolean moveUpLeft() {
        this.getSprite().setVelocity(-5, -5);
        return true;
    }
        
    private boolean moveDownRight() {
        this.getSprite().setVelocity(5, 5);
        return true;
    }
    
    private boolean moveDownLeft() {
        this.getSprite().setVelocity(-5, 5);
        return true;
    }
}
