/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.gameobject;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.sprites.SpriteSkeleton;
import com.nkoiv.mists.game.ui.InfoPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * PlayerCharacter is a creature that is controlled directly by a player.
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

        this.setWalkAnimations("/images/lini.png", 3, 32);
        this.setDashAnimations("/Images/lini_dash.png", 3, 32);
        
        this.setFlag("alive", 1);
        this.setMaxHealth(300);
        this.setHealth(this.getMaxHealth());
        this.setSpeed(80);
        this.setAttribute("Strength", 20);
        this.companions = new ArrayList<>();
        this.lightSize = 2;
        this.lightColor = Color.BLUEVIOLET;
        this.availableActions = new HashMap<>();
        
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
        this.availableActions = new HashMap<>();
    }
    
    protected void setWalkAnimations(String imagepath, int frameCount, int imageSize) {
    	ImageView iw = new ImageView(imagepath);
        this.setAnimation("downMovement", iw, frameCount, 0, 0, 0, 0, imageSize, imageSize );
        this.setAnimation("leftMovement", iw, frameCount, 0, imageSize, 0, 0, imageSize, imageSize );
        this.setAnimation("rightMovement", iw, frameCount, 0, imageSize*2, 0, 0, imageSize, imageSize );
        this.setAnimation("upMovement", iw, frameCount, 0, imageSize*3, 0, 0, imageSize, imageSize );   
        
    }
    
    protected void setDashAnimations(String imagepath, int frameCount, int imageSize) {
    	ImageView iw = new ImageView(imagepath);
    	this.setAnimation("downDash", iw, frameCount, 0, 0, 0, 0, imageSize, imageSize );
        this.setAnimation("leftDash", iw, frameCount, 0, imageSize, 0, 0, imageSize, imageSize );
        this.setAnimation("rightDash", iw, frameCount, 0, imageSize*2, 0, 0, imageSize, imageSize );
        this.setAnimation("upDash", iw, frameCount, 0, imageSize*3, 0, 0, imageSize, imageSize );
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
        this.availableActions = new HashMap<>();
    }
    
    public void addCompanion (Creature comp) {
        if (this.companions == null) this.companions = new ArrayList<>();
        this.companions.add(comp);
    }
    
    public ArrayList<Creature> getCompanions() {
        return this.companions;
    }
    
    @Override
    protected void die() {
        //TODO: Spawn tombstone/corpse/whatever
        //TODO: This should not work like this in multiplayer
        Mists.logger.log(Level.INFO, "{0} was killed!", name);
        this.remove();
        if (Mists.MistsGame == null) return;
        deathPopup();
    }
    
    private void deathPopup() {
        InfoPanel ip = new InfoPanel(Mists.MistsGame.currentState, "You are dead", 300, 200, 300, 200, Mists.graphLibrary.getImageSet("panelBlue"));
        ip.setText("You are dead\nUse esc and main menu to quit\nor start anew.");
        Mists.MistsGame.currentState.addUIComponent(ip);
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
        if (i == null) return false;
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
        if (!MoveState.DASH.equals(movestate)) {
            handleNextTask(time);
        } else {
            this.applyMovement(time); 
        }
        tickActionCooldowns(time);
        checkupMoveState(time);
        this.updateGraphics();
    }
    
    private void handleNextTask(double time) {
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
    }
    
    /**
     * Clear all the Location -links from the player,
     * as well as do the same for any companions the player
     * might have.
     * This only clears the links from Player TO Location
     * and Location TO Player side might still be in effect
     * (Location.mapObjects, Location.spatials, etc)
     */
    public void clearLocation() {
        this.location = null;
        this.IDinLocation = 0;
        for (Creature c : this.companions) {
            c.location = null;
            c.IDinLocation  = 0;
        }
    }
    
    @Override
    public void write(Kryo kryo, Output output) {
        super.write(kryo, output);    
    }


    @Override
    public void read(Kryo kryo, Input input) {
        super.read(kryo, input);
    }
    
}
