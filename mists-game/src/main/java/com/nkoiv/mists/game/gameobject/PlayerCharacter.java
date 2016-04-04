/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.items.Weapon;
import com.nkoiv.mists.game.sprites.SpriteSkeleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * PlayerCharacter is currently designed to be unique per game
 * TODO: Implement "World owner" as the main target instead of player,
 * so that several players can coexist in a single game
 * @author nkoiv
 */
public class PlayerCharacter extends Creature implements Combatant {
    private ArrayList<Creature> companions;
    
    public PlayerCharacter() {
        //Dummy player for testing
        super ("Lini",new Image("/images/lini_test.png"));
        //SpriteSkeleton playerSkeleton = new SpriteSkeleton();
        //playerSkeleton.addPart("body", new Sprite(new Image("/images/lini_test.png"),0,0));
        //this.graphics = playerSkeleton;
        this.equipWeapon((Weapon)Mists.itemLibrary.create("sword"));
        this.addItem(Mists.itemLibrary.create("axe"));
        this.addItem(Mists.itemLibrary.create("himmutoy"));
        
        
        this.setAnimation("downMovement", new ImageView("/images/lini.png"), 3, 0, 0, 0, 0, 32, 32 );
        this.setAnimation("leftMovement", new ImageView("/images/lini.png"), 3, 0, 32, 0, 0, 32, 32 );
        this.setAnimation("rightMovement", new ImageView("/images/lini.png"), 3, 0, 64, 0, 0, 32, 32 );
        this.setAnimation("upMovement", new ImageView("/images/lini.png"), 3, 0, 96, 0, 0, 32, 32 );       
        
        
        this.setFlag("alive", 1);
        this.setMaxHealth(500);
        this.setHealth(this.getMaxHealth());
        this.setSpeed(50);
        this.setAttribute("Strength", 50);
        this.companions = new ArrayList<>();
        this.lightSize = 2;
        this.lightColor = Color.BLUEVIOLET;
    }
    
    public PlayerCharacter(String name) {
        super (name,new Image("/images/himmu.png"));
        this.spriteAnimations = new HashMap<>();
        this.setAnimation("downMovement", new ImageView("/images/himmu_walk_down.png"), 4, 0, 0, 0, 0, 64, 64 );
        this.setAnimation("upMovement", new ImageView("/images/himmu_walk_up.png"), 4, 0, 0, 0, 0, 64, 64 );       
        this.setAnimation("leftMovement", new ImageView("/images/himmu_walk_left.png"), 4, 0, 0, 0, 0, 64, 64 );
        this.setAnimation("rightMovement", new ImageView("/images/himmu_walk_right.png"), 4, 0, 0, 0, 0, 64, 64 );
        this.setFlag("alive", 1);
        this.setMaxHealth(1000);
        this.setHealth(this.getMaxHealth());
        this.setSpeed(50);
        this.companions = new ArrayList<>();
    }
    
    
    private static SpriteSkeleton generatePlayerSkeleton() {
        return null;
    }

    public PlayerCharacter(String name, Image image) {
        super(name, image);
        this.setCollisionLevel(9);
        this.setFacing(Direction.DOWN);
        this.setFlag("alive", 1);
        this.setMaxHealth(100);
        this.setHealth(this.getMaxHealth());
        this.setSpeed(50);
        this.companions = new ArrayList<>();
    }
    
    public void addCompanion (Creature comp) {
        if (this.companions == null) this.companions = new ArrayList<>();
        this.companions.add(comp);
    }
    
    public ArrayList<Creature> getCompanions() {
        return this.companions;
    }
    
    @Override
    public void think(double time) {
        //dont call any AI subroutine
    }

    /**
     * TODO: Is this the right way to handle
     * quest item tracking?
     * @param i Item to be added
     * @return True if the item was added successfully
     */
    @Override
    public boolean addItem(Item i) {
        if (this.inventory == null) return false;
        Mists.logger.info("Attempted to give "+this.getName()+" "+i.getName());
        if (Mists.MistsGame.currentState instanceof LocationState) {
            ((LocationState)Mists.MistsGame.currentState).addTextFloat("Took "+i.getName(), this);
        }
        if (this.inventory.addItem(i)) {
            if (Mists.MistsGame != null) Mists.MistsGame.questManager.registerItemCountInInventory(i, this.inventory.getItemCount(i.getBaseID()));
            return true;
        }
        return false;
    }

    
    @Override
    public void update(double time) {
        if (this.nextTask!=null) {
            if (this.nextTask.taskID != GenericTasks.ID_IDLE) {
                GenericTasks.performTask(location, nextTask, time);
                this.lastTask = nextTask;
                this.nextTask = new Task(GenericTasks.ID_IDLE, this.IDinLocation, null);
            } else {
                this.lastTask = this.nextTask;
                this.nextTask = null;
            }
        }
        this.updateGraphics();
        //this.applyMovement(time);  
    }
    
    public void clearLocation() {
        this.location = null;
        this.IDinLocation = 0;
        for (Creature c : this.companions) {
            c.location = null;
            c.IDinLocation  = 0;
        }
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
