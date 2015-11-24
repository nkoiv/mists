/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * PlayerCharacter is currently designed to be unique per game
 * TODO: Implement "World owner" as the main target instead of player,
 * so that several players can coexist in a single game
 * @author nkoiv
 */
public class PlayerCharacter extends Creature implements Combatant {
    
    
    //A bunch of Strings to be used for describing various attacks
    //TODO: Consider moving these into a map
    private ArrayList<String> overpoweringAttack;
    private ArrayList<String> overpoweringDefense;
    private ArrayList<String> weakAttack;
    private ArrayList<String> weakDefense;
    
    //Sprite-Arrays for animated movement
    /*
    * TODO: Make a separate SpriteSheet-cutter class
    * to manage different animations via one picture
    
    private SpriteAnimation walkUp;
    private SpriteAnimation walkDown;
    private SpriteAnimation walkLeft;
    private SpriteAnimation walkRight;
    */
    
    private HashMap<String, SpriteAnimation> spriteAnimations;
    private ArrayList<PlayerCharacter> companions;
    
    public PlayerCharacter() {
        //Dummy player for testing
        /* Himmu
        super ("Himmu",new Image("/images/himmu.png"));
        this.spriteAnimations = new HashMap<>();
        this.setAnimation("downMovement", new ImageView("/images/himmu_walk_down.png"), 4, 0, 0, 0, 0, 64, 64 );
        this.setAnimation("upMovement", new ImageView("/images/himmu_walk_up.png"), 4, 0, 0, 0, 0, 64, 64 );       
        this.setAnimation("leftMovement", new ImageView("/images/himmu_walk_left.png"), 4, 0, 0, 0, 0, 64, 64 );
        this.setAnimation("rightMovement", new ImageView("/images/himmu_walk_right.png"), 4, 0, 0, 0, 0, 64, 64 );
        this.setFlag("alive", 1);
        this.setMaxHealth(100);
        this.setHealth(this.getMaxHealth());
        this.setSpeed(50);
        */
        super ("Lini",new Image("/images/himmutoy.png"));
        this.spriteAnimations = new HashMap<>();
        this.setAnimation("downMovement", new ImageView("/images/lini.png"), 3, 0, 0, 0, 0, 32, 32 );
        this.setAnimation("leftMovement", new ImageView("/images/lini.png"), 3, 0, 32, 0, 0, 32, 32 );
        this.setAnimation("rightMovement", new ImageView("/images/lini.png"), 3, 0, 64, 0, 0, 32, 32 );
        this.setAnimation("upMovement", new ImageView("/images/lini.png"), 3, 0, 96, 0, 0, 32, 32 );       
        this.setFlag("alive", 1);
        this.setMaxHealth(100);
        this.setHealth(this.getMaxHealth());
        this.setSpeed(50);
        
        
    }
       

    public PlayerCharacter(String name, Image image) {
        super(name, image);
        this.setCollisionLevel(9);
        this.setFacing(Direction.DOWN);
        this.setFlag("alive", 1);
        this.setMaxHealth(100);
        this.setHealth(this.getMaxHealth());
        this.setSpeed(50);
    }
    
    public void addCompanion (PlayerCharacter comp) {
        this.companions.add(comp);
    }
    
    public ArrayList<PlayerCharacter> getCompanions() {
        return this.companions;
    }
    
    @Override
    public void update(double time) {
        this.updateSprite();
        this.applyMovement(time);  
    }
     
    @Override
    public void useAction(String action ) {
        if (this.getAvailableActions() != null) {
            if (this.getAvailableActions().containsKey(action)) this.getAvailableActions().get(action).use(this);
        } else {
            Mists.logger.log(Level.INFO, "{0} tried using action [{1}], but doesnt have the ability.", new Object[]{this.getName(), action});
        }
        
    }
    
}
