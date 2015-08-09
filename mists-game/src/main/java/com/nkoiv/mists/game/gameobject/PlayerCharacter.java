/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * PlayerCharacter is currently designed to be unique per game
 * TODO: Implement "World owner" as the main target instead of player,
 * so that several players can coexist in a single game
 * TODO: Consider having PlayerCharacter extend Creature to make things easier
 * @author nkoiv
 */
public class PlayerCharacter extends Creature implements Combatant {
    
    
    //A bunch of Strings to be used for describing various attacks
    private ArrayList<String> overpoweringAttack;
    private ArrayList<String> overpoweringDefense;
    private ArrayList<String> weakAttack;
    private ArrayList<String> weakDefense;
	
    private boolean gender; //true=male, false=female
    
    //Sprite-Arrays for animated movement
    /*
    * TODO: Make a separate SpriteSheet-cutter class
    * to manage different animations via one picture
    */
    private SpriteAnimation walkUp;
    private SpriteAnimation walkDown;
    private SpriteAnimation walkLeft;
    private SpriteAnimation walkRight;

    
    public PlayerCharacter() {
        //Dummy player for testing
        super ("Himmu",new Image("/images/himmu.png"));
        this.walkDown = new SpriteAnimation (
                new ImageView("/images/himmu_walk_down.png"), 4, 0, 0, 64, 64 );
        this.walkUp = new SpriteAnimation (
                new ImageView("/images/himmu_walk_up.png"), 4, 0, 0, 64, 64 );       
        this.walkLeft = new SpriteAnimation (
                new ImageView("/images/himmu_walk_left.png"), 4, 0, 0, 64, 64 );
        this.walkRight = new SpriteAnimation (
                new ImageView("/images/himmu_walk_right.png"), 4, 0, 0, 64, 64 );
        this.setFacing(Direction.DOWN);
        this.setAlive(true);
        this.setMaxHealth(100);
        this.setHealth(this.getMaxHealth());
        this.setAV(10);
        this.setDV(10);
        this.setSpeed(50);
    }
    

    public PlayerCharacter(String name, Image image) {
        super(name, image);
        this.setCollisionLevel(100);
        this.setFacing(Direction.DOWN);
        this.setAlive(true);
        this.setMaxHealth(100);
        this.setHealth(this.getMaxHealth());
        this.setAV(10);
        this.setDV(10);
        this.setSpeed(50);
    }
    
    @Override
    public void update (double time) {
        this.updateSprite();
        super.applyMovement(time);
        
    }
    
    private void updateSprite() {
        if (this.isMoving()) {
            //Mists.logger.log(Level.INFO, "{0} is moving {1}", new Object[]{this.getName(), this.facing});
            switch(this.getFacing()) {
                case UP: this.getSprite().setAnimation(this.walkUp); break;
                case DOWN: this.getSprite().setAnimation(this.walkDown);break;
                case LEFT: this.getSprite().setAnimation(this.walkLeft);break;
                case RIGHT: this.getSprite().setAnimation(this.walkRight);break;
                case UPRIGHT:;
                case UPLEFT: ;
                case DOWNRIGHT: ;
                case DOWNLEFT: ;
                default: break;
            }
        }
        
    }
    

}
