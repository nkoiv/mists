/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.AI.CompanionAI;
import com.nkoiv.mists.game.AI.CreatureAI;
import com.nkoiv.mists.game.AI.MonsterAI;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.actions.ActionType;
import com.nkoiv.mists.game.actions.AttackAction;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.items.Weapon;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.sprites.SpriteSkeleton;
import com.nkoiv.mists.game.triggers.DialogueTrigger;
import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Creature is a "living" MapObject
 * As such, they get (at least some) AI-routines
 * @author nkoiv
 */
public class Creature extends MapObject implements Combatant, HasInventory {
    
    private CreatureAI ai;
    protected Task lastTask;
    protected Task nextTask;
    protected long lastTaskSet; //For networking purposes
    
    protected MoveState animationMovestate;
    protected MoveState movestate = MoveState.STAND;
    private Direction facing = Direction.DOWN;
    private Direction lastFacing = null;
    protected HashMap<String, SpriteAnimation> spriteAnimations;
    protected Inventory inventory;
    protected Weapon equippedWeapon; //TODO: Proper inventorymanagement isntead of this
    
    protected Dialogue currentDialogue;
    
    /*Attributes are stored in a separate HashMap
    * These are not Flags because they're limited to creatures
    * Would be too easy to accidentally make walls alive if they were
    * TODO: Consider if the above would be awesome
    */
    protected int visionRange;
    protected double dashCooldown = 2000;
    protected double dashCooldownRemaining;
    protected double dashDuration = 250;
    protected double dashRemaining;
    protected double dashMultiplier = 6;
    
    protected HashMap<String, Integer> attributes;
    protected HashMap<String, Integer> effects;
    //TODO: Consider if effects should be used for MapObjects (can a wall or a rock get debuffs/buffs?)
    protected HashMap<String, Action> availableActions;
    protected ArrayList<Integer> crossableTerrain; //List of terrains we can go through;
    
    //Old position to snap back to when colliding;
    private double oldXPos;
    private double oldYPos;
    
    //Constructor for a creature template with one static image
    public Creature (String name, MovingGraphics graphics) {
        super (name, graphics);
        this.initializeAttributes();
        this.initializeFlags();
        this.inventory = new Inventory(this);
        this.crossableTerrain = new ArrayList<>();
        this.crossableTerrain.add(0);
        this.ai = new CreatureAI(this);
        this.availableActions = new HashMap<>();
    }
    
    public Creature (String name, Image image) {
        this(name, new Sprite(image));
    }
       
    //Constructing a creature template with sprite animations
    public Creature (String name, ImageView imageView, int frameCount, int startingXTile, int startingYTile, int xOffset, int yOffset, int frameWidth, int frameHeight) {
        super(name);
        this.setAnimations(imageView, frameCount, startingXTile, startingYTile, frameWidth, frameHeight);
        this.setSprite(new Sprite(this.spriteAnimations.get("downMovement").getCurrentFrame()));
        this.initializeAttributes();
        this.initializeFlags();
        this.inventory = new Inventory(this);
        this.ai = new CreatureAI(this);
        this.crossableTerrain = new ArrayList<>();
        this.crossableTerrain.add(0);
        this.availableActions = new HashMap<>();
    }
    
    public Creature (String name, ImageView imageView, int frameCount, int startingXTile, int startingYTile, int frameWidth, int frameHeight) {
        this(name, imageView, frameCount, startingXTile, startingYTile, 0,0, frameWidth, frameHeight);
    }

    /*Constructing a creature with only a still image
    * TODO: This should not be needed once Libraries are done
    */
    /*
    public Creature(String name, Image image, Location location, double xCoor, double yCoor) {
        super(name, image, location, xCoor, yCoor);
        this.setFacing(Direction.DOWN);
        this.initializeAttributes();
        this.initializeFlags();
        this.ai = new CreatureAI(this);
        this.crossableTerrain = new ArrayList<>();
        this.crossableTerrain.add(0);
    }
    */
    
    //TODO: Make this load stats from creature library
    private void initializeAttributes() {
        this.visionRange = 10;
        this.attributes = new HashMap<>();
        this.setAttribute("Strength", 1);
        this.setAttribute("Agility", 1);
        this.setAttribute("Intelligence", 1);
        this.setAttribute("Speed", 50);
        this.setAttribute("MaxHealth", 100 );
        this.setAttribute("Health", 100);
    }
    
    private void initializeFlags() {
        this.setFlag("visible", 1);
        this.setCollisionLevel(5);
    }
    
     
    public void setVisionRange(int visionRange) {
        this.visionRange = visionRange;
        if (this.visionRange < 0) this.visionRange = 0;
        if (this.visionRange > 20) this.visionRange = 20;
    }
    
    public int getVisionRange() {
        return this.visionRange;
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
        if (!this.availableActions.containsKey(a.toString())) {
            this.availableActions.put(a.toString(), a);
            a.setOwner(this);
        }
        
    }
    
    public void useAction(String name) {
        if (this.availableActions != null) {
            if (this.availableActions.containsKey(name)) this.availableActions.get(name).use(this);
            else {
                Mists.logger.log(Level.INFO, "{0} tried using action [{1}], but doesnt have the ability.", new Object[]{this.getName(), name});
            }
        }
    }
    
    public void useAction(String name, MapObject target) {
        if (this.availableActions != null) {
            if (this.availableActions.containsKey(name)) {
                this.availableActions.get(name).use(this, target.getCenterXPos(), target.getCenterYPos());
            } else {
                Mists.logger.log(Level.INFO, "{0} tried using action [{1}], but doesnt have the ability.", new Object[]{this.getName(), name});
            }
        }
    }
    
    public void useAction(String name, double xTarget, double yTarget) {
        if (this.availableActions != null) {
            if (this.availableActions.containsKey(name)) this.availableActions.get(name).use(this, xTarget, yTarget);
        } else {
            Mists.logger.log(Level.INFO, "{0} tried using action [{1}], but doesnt have the ability.", new Object[]{this.getName(), name});
        }
    }
    
    public ArrayList<String> getAvailableActionNames() {
        ArrayList<String> listOfActions = new ArrayList<>();
        if (this.availableActions != null) {
            for (String actionName : this.availableActions.keySet()) {
                listOfActions.add(actionName);
            }
        }
        return listOfActions;
    }
    
    public HashMap<String, Action> getAvailableActions(){
        return this.availableActions;
    }
    
    @Override
    public double getAttackRange() {
        //TODO: Actually hceck attack range of abilities, based on what's being wielded(?)
        return 1;
    }
    
    @Override
    public Action getAttack(ActionType actionType) {
        if (this.availableActions == null) return null;
        for (String actionName : this.availableActions.keySet()) {
            Action a = this.availableActions.get(actionName);
            if (a.getActionType() == actionType && a instanceof AttackAction) return a;
        }
        return null;
    }
    
    /* setAnimations is used to easily set all movement animations
    *  It assumes spritesheet is set up with rows for Down, Left, Right, Up - in that order
    *  and that offset between individual frames is 0
    */
    public void setAnimations(ImageView imageview, int frameCount, int startingXTile, int startingYTile, int frameWidth, int frameHeight) {
        this.setAnimations(imageview, frameCount, startingXTile, startingYTile, 0, 0, frameWidth, frameHeight);
    }
    
    public void setAnimations(ImageView imageview, int frameCount, int startingXTile, int startingYTile, int xOffset, int yOffset, int frameWidth, int frameHeight) {
                this.spriteAnimations = new HashMap<>();
        this.setAnimation("downMovement",
          imageview, frameCount, (startingXTile*frameWidth)+0, (startingYTile*frameHeight)+0, xOffset, yOffset, frameWidth, frameHeight      
        );
        this.setAnimation("leftMovement",
          imageview, frameCount, (startingXTile*frameWidth)+0, (startingYTile*frameHeight)+(frameHeight), xOffset, yOffset, frameWidth, frameHeight      
        );
        this.setAnimation("rightMovement",
          imageview, frameCount, (startingXTile*frameWidth)+0, (startingYTile*frameHeight)+(frameHeight*2), xOffset, yOffset, frameWidth, frameHeight      
        );
        this.setAnimation("upMovement",
          imageview, frameCount, (startingXTile*frameWidth)+0, (startingYTile*frameHeight)+(frameHeight*3), xOffset, yOffset, frameWidth, frameHeight      
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
    
    public void think(double time) {
        if (this.location == null) return;
        this.lastTask = this.nextTask;
        this.setNextTask(this.ai.think(time));
    }
    
    public void setCurrentDialogue(Dialogue d) {
        this.currentDialogue = d;
    }
    
    public Dialogue getCurrentDialogue() {
        return this.currentDialogue;
    }
    
    /**
     * Get the list of triggers that can be performed on
     * this MapObject;
     * @return 
     */
    @Override
    public Trigger[] getTriggers() {
        if (this.currentDialogue != null) {
            DialogueTrigger dt = new DialogueTrigger(this, this.currentDialogue);
            return new Trigger[]{dt};
        }
        else return new Trigger[0];
    }
    
    @Override
    public void update (double time) {
        //Mists.logger.info(this.name+" doing "+lastTask.toString());        
        if ((System.currentTimeMillis()-this.lastTaskSet)>1500) {
            this.stopMovement();
            setNextTask(new Task(GenericTasks.ID_IDLE, this.IDinLocation, null));
        }
        GenericTasks.performTask(location, nextTask, time);
        if (nextTask.taskID > 20) setNextTask(new Task(GenericTasks.ID_IDLE, this.IDinLocation, null));
        tickActionCooldowns(time);
        checkupMoveState(time);
        updateGraphics();
    }

    protected void checkupMoveState(double time) {
        if (dashRemaining > 0) dashRemaining = dashRemaining - (time*1000);
        if (dashRemaining <= 0) endDash();
    }
    
    protected void tickActionCooldowns(double time) {
        for (Action a : this.availableActions.values()) {
            a.tickCooldown(time);
        }
        if (this.dashCooldownRemaining > 0) {
            dashCooldownRemaining = dashCooldownRemaining - (time*1000);
            if (dashCooldownRemaining < 0) dashCooldownRemaining = 0;
        }
        
    }
    
    public boolean dashOnCooldown() {
        return this.dashCooldownRemaining>0;
    }
    
    public Task getLastTask() {
        return this.lastTask;
    }
    
    public Task getNextTask() {
        return this.nextTask;
    }
    
    public void setNextTask(Task t) {
        this.nextTask = t;
        this.lastTaskSet = System.currentTimeMillis();
    }

    public MoveState getMoveState() {
        return this.movestate;
    }
    
    public void setMoveState(MoveState movestate) {
        this.movestate = movestate;
    }
    
    protected void updateGraphics() {
        if (this.graphics instanceof Sprite) this.updateSprite();
        if (this.graphics instanceof SpriteSkeleton) this.updateSpriteSkeleton();
        this.animationMovestate = movestate;
        this.lastFacing = this.facing;
    }
    
    /*
    * TODO: General sprite updates for general creatures
    * Is everything animated?
    */
    private void updateSprite() {
        if (movestate.equals(animationMovestate) && facing.equals(lastFacing)) return; //If we're already in the right animationstate, nothing to update
        if (!(this.graphics instanceof Sprite)) return;
        if (this.spriteAnimations == null) return;
        
        switch (movestate) {
            case WALK: updateSpriteToWalk(); break;
            case STAND: updateSpriteToStand(); break;
            case DASH: updateSpriteToDash(); break;
            default: break;
        }
        
    }
    
    private void updateSpriteToDash() {
        switch(this.facing) {
            case UP: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("upDash")); break;
            case DOWN: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("downDash")); break;
            case LEFT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("leftDash")); break;
            case RIGHT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("rightDash")); break;
            case UPRIGHT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("upDash")); break;
            case UPLEFT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("upDash")); break;
            case DOWNRIGHT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("downDash")); break;
            case DOWNLEFT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("downDash")); break;
            default: break;
        }
    }
    
    private void updateSpriteToWalk() {
        switch(this.facing) {
            case UP: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("upMovement")); break;
            case DOWN: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("downMovement")); break;
            case LEFT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("leftMovement")); break;
            case RIGHT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("rightMovement")); break;
            case UPRIGHT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("upMovement")); break;
            case UPLEFT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("upMovement")); break;
            case DOWNRIGHT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("downMovement")); break;
            case DOWNLEFT: ((Sprite)this.graphics).setAnimation(this.spriteAnimations.get("downMovement")); break;
            default: break;
        }
    }
    
    private void updateSpriteToStand() {
        updateSpriteToWalk();
        //TODO: Idle standing animations
    }
    
    private void updateSpriteSkeleton() {
        
    }
    
        
    /**
    * Render draws the Sprite of the Creature on a given GraphicsContext
    * This overrides the normal MapObject render, because Creatures might have some
    * extra stuff to draw (like pathfinding path for demo/testing purposes)
    * @param gc GraphicsContext where the object is drawn
    * @param xOffset Used to shift the objects xCoordinate so its drawn where the screen is
    * @param yOffset Used to shift the objects yCoordinate so its drawn where the screen is
    */
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.isFlagged("visible")) {
            if (this.graphics instanceof SpriteSkeleton) ((SpriteSkeleton)this.graphics).render(xOffset, yOffset, gc, this.facing);
            else this.graphics.render(xOffset, yOffset, gc);
            if (this.ai.getPath()!=null && this.getLocation().isFlagged("drawPaths")) {
                this.ai.getPath().drawPath(gc, TILESIZE, xOffset, yOffset);
            }
        }
    } 
    
    
    //-------------movement-----------
   
    /**
     * Apply movement to the creature.
     * The movement is done by creature sprites x and y velocity,
     * multiplied by time spent moving.
     * @param time Time spent moving
     * @return Return true if movement was possible, false if it was blocked
     */ 
    public boolean applyMovement(double time){
        /*
        * Check collisions before movement
        * TODO: Add in pixel-based collision detection (compare alphamaps?)
        * TODO: Make collisions respect collisionlevel.
        */
        this.oldXPos = this.getGraphics().getXPos();
        this.oldYPos = this.getGraphics().getYPos();
        //Mists.logger.info("Old positions: "+this.oldXPos+","+this.oldYPos);
        
        ArrayList<MapObject> collidedObjects = this.getLocation().checkCollisions(this);
        if (collidedObjects.isEmpty()) {
            //Collided with nothing, free to move
            if (this.isFlagged("movementBlocked")) {
                this.setFlag("movementBlocked", 0); //movement went through fine 
                //Mists.logger.info("Movement blocked!");
            }
            this.getGraphics().update(time);
            return true;
        } else { 
            //Check which sides we collided on
            EnumSet<Direction> collidedSides = this.getLocation().collidedSides(this, collidedObjects);
            //Mists.logger.info("Checked collisions, found " +collidedSides.toString());
            if (collidedSides.contains(Direction.UP)) {
                //Block movement up
                if (this.getGraphics().getYVelocity() < 0 ) { 
                    //this.getGraphics().setYVelocity(0);
                    this.getGraphics().setYVelocity(-this.getGraphics().getYVelocity()/2);
                }
                this.getGraphics().setYPosition(this.oldYPos);
            }
            if (collidedSides.contains(Direction.DOWN)) {
                //Block movement down
                if (this.getGraphics().getYVelocity() > 0 ) {
                    //this.getGraphics().setYVelocity(0);
                    this.getGraphics().setYVelocity(-this.getGraphics().getYVelocity()/2);
                }
                this.getGraphics().setYPosition(this.oldYPos);
            }
            if (collidedSides.contains(Direction.RIGHT)) {
                //Block movement right
                if (this.getGraphics().getXVelocity() > 0 ) {
                    //this.getGraphics().setXVelocity(0);
                    this.getGraphics().setXVelocity(-this.getGraphics().getXVelocity()/2);
                }
                this.getGraphics().setXPosition(this.oldXPos);
            }
            if (collidedSides.contains(Direction.LEFT)) {
                //Block movement left
                if (this.getGraphics().getXVelocity() < 0 ) {
                    //this.getGraphics().setXVelocity(0);
                    this.getGraphics().setXVelocity(-this.getGraphics().getXVelocity()/2);
                }
                this.getGraphics().setXPosition(this.oldXPos);
                
            }
            this.getGraphics().update(time);
            this.setFlag("movementBlocked", 1); //remember this was a bad way to go to
            return false;
        }
    }
    
    private void startDash() {
        this.dashRemaining = this.dashDuration;
        this.movestate = MoveState.DASH;
        this.dashCooldownRemaining = dashCooldown;
    }
    
    private void endDash() {
        this.dashRemaining = 0;
        this.movestate = MoveState.STAND;
    }
    
    public boolean dashTowards (double xCoor, double yCoor) {
        this.startDash();
        //TODO: Decide on if we're Walking, Running or Dashing based on relative movement speed?
        this.getGraphics().setVelocity(0, 0);
        double[] direction = Toolkit.getDirectionXY(this.getCenterXPos(), this.getCenterYPos(), xCoor, yCoor);
        direction[0] = direction[0] * this.getAttribute("Speed")*dashMultiplier;
        direction[1] = direction[1] * this.getAttribute("Speed")*dashMultiplier;
        this.graphics.addVelocity(direction[0], direction[1]);
        //Update facing
        this.setFacing(Toolkit.getDirection(0, 0, direction[0], direction[1]));
        return true;
    }
    
    public boolean moveTowards (double xCoor, double yCoor) {
        this.movestate = MoveState.WALK;
        //TODO: Decide on if we're Walking, Running or Dashing based on relative movement speed?
        this.getGraphics().setVelocity(0, 0);
        double[] direction = Toolkit.getDirectionXY(this.getCenterXPos(), this.getCenterYPos(), xCoor, yCoor);
        direction[0] = direction[0] * this.getAttribute("Speed");
        direction[1] = direction[1] * this.getAttribute("Speed");
        this.graphics.addVelocity(direction[0], direction[1]);
        //Update facing
        this.setFacing(Toolkit.getDirection(0, 0, direction[0], direction[1]));
        return true;
    }
    
    public boolean dashTowards (Direction direction) {
        this.startDash();
        switch(direction) {
            case UP: {
                this.facing = Direction.UP;
                this.getGraphics().setVelocity(0, -this.getAttribute("Speed")*dashMultiplier);
                return true;
            }
            case DOWN: {
                this.facing = Direction.DOWN;
                this.getGraphics().setVelocity(0, this.getAttribute("Speed")*dashMultiplier);
                return true;
            }
            case LEFT: {
                this.facing = Direction.LEFT;
                this.getGraphics().setVelocity(-this.getAttribute("Speed")*dashMultiplier, 0);
                return true;
            }
            case RIGHT: {
                this.facing = Direction.RIGHT;
                this.getGraphics().setVelocity(this.getAttribute("Speed")*dashMultiplier, 0);
                return true;
            }
            case UPRIGHT: { 
                this.getGraphics().setVelocity((this.getAttribute("Speed")/1.41)*dashMultiplier, -(this.getAttribute("Speed")/1.41)*dashMultiplier);
                this.facing = Direction.UPRIGHT;
                return true;
            }
            case UPLEFT: {
                this.getGraphics().setVelocity((-this.getAttribute("Speed")/1.41)*dashMultiplier, (-this.getAttribute("Speed")/1.41)*dashMultiplier);
                this.facing = Direction.UPLEFT;
                return true;
            }
            case DOWNRIGHT: {
                this.getGraphics().setVelocity((this.getAttribute("Speed")/1.41)*dashMultiplier, (this.getAttribute("Speed")/1.41)*dashMultiplier);
                this.facing = Direction.DOWNRIGHT;
                return true;
            }
            case DOWNLEFT: {
                this.getGraphics().setVelocity((-this.getAttribute("Speed")/1.41)*dashMultiplier, (this.getAttribute("Speed")/1.41)*dashMultiplier);
                this.facing = Direction.DOWNLEFT;
                return true;
            }
            case STAY: {
                return stopMovement();
            }
        default: break;
        }
        return false;
    }
    
    @Override
    public boolean moveTowards (Direction direction) {
        //this.stopMovement(); //clear old movement (velocity)
        this.movestate = MoveState.WALK;
        switch(direction) {
            case UP: {
                this.facing = Direction.UP;
                this.getGraphics().setVelocity(0, -this.getAttribute("Speed"));
                return true;
            }
            case DOWN: {
                this.facing = Direction.DOWN;
                this.getGraphics().setVelocity(0, this.getAttribute("Speed"));
                return true;
            }
            case LEFT: {
                this.facing = Direction.LEFT;
                this.getGraphics().setVelocity(-this.getAttribute("Speed"), 0);
                return true;
            }
            case RIGHT: {
                this.facing = Direction.RIGHT;
                this.getGraphics().setVelocity(this.getAttribute("Speed"), 0);
                return true;
            }
            case UPRIGHT: { 
                this.getGraphics().setVelocity(this.getAttribute("Speed")/1.41, -this.getAttribute("Speed")/1.41);
                this.facing = Direction.UPRIGHT;
                return true;
            }
            case UPLEFT: {
                this.getGraphics().setVelocity(-this.getAttribute("Speed")/1.41, -this.getAttribute("Speed")/1.41);
                this.facing = Direction.UPLEFT;
                return true;
            }
            case DOWNRIGHT: {
                this.getGraphics().setVelocity(this.getAttribute("Speed")/1.41, this.getAttribute("Speed")/1.41);
                this.facing = Direction.DOWNRIGHT;
                return true;
            }
            case DOWNLEFT: {
                this.getGraphics().setVelocity(-this.getAttribute("Speed")/1.41, this.getAttribute("Speed")/1.41);
                this.facing = Direction.DOWNLEFT;
                return true;
            }
            case STAY: {
                return stopMovement();
            }
        default: break;
        }
        
        return false;
    }

    public boolean stopMovement() {
        this.getGraphics().setVelocity(0, 0);
        switch(this.movestate) {
            case WALK: setMoveState(MoveState.STAND); break;
            case RUN: setMoveState(MoveState.STAND); break; 
            default: break;
        }
        //this.getGraphics().setImage(this.getGraphics().getImage());
        return true;
    }

    public ArrayList<Integer> getCrossableTerrain() {
        return this.crossableTerrain;
    }
    
    public Direction getFacing() {
        return this.facing;
    }
    
    public void setFacing(Direction d) {
        this.facing = d;
    }
    
    //---------------inventory--------------
    
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
    
    @Override
    public boolean addItem(Item i) {
        Mists.logger.info("Attempted to give "+this.getName()+" "+i.getName());
        if (Mists.MistsGame.currentState instanceof LocationState) {
            ((LocationState)Mists.MistsGame.currentState).addTextFloat("Took "+i.getName(), this);
        }
        return(this.inventory.addItem(i));
    }
    
    //---TEMP methods for testing weapon usage via actions---
    public Weapon getWeapon() {
        return this.equippedWeapon;
    }
    
    public void equipWeapon(Weapon w) {
        if (w==null) return;
        this.equippedWeapon = w;
        if (this.graphics instanceof SpriteSkeleton) {
            Mists.logger.info("Added "+w.getName()+" to "+this.getName()+" spriteskeleton");
            ((SpriteSkeleton)graphics).addPart("weapon", new Sprite(w.getImage(), 5, 10));
        }
    }
    
    //------------------------combat-------------------
    
    
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

    public void setAI(CreatureAI ai) {
        this.ai = ai;
    }
    
    @Override
    public void takeDamage(int damage) {
        this.setHealth(this.getHealth()-damage);
        Mists.logger.log(Level.INFO,"{0} took {1}"+"points of damage"+" ({2}/{3})",
                new Object[]{this.getName(), damage, this.getHealth(), this.getMaxHealth()});
        //TODO: Expand death - Himmu cant die
        if(this.getHealth()<1) this.die();
        if (Mists.MistsGame == null) return; //Needed for UnitTests
        if (Mists.MistsGame.currentState instanceof LocationState) {
            ((LocationState)Mists.MistsGame.currentState).addDamageFloat(damage, this);
        }

    }
    
    protected void die() {
        //TODO: Spawn tombstone/corpse/whatever
        this.setRemovable();
        if (Mists.MistsGame == null) return;
        Mists.MistsGame.questManager.registerMobDeath(this);
    }
	
    @Override
    public void healHealth(int healing) {
        this.setHealth(this.getHealth()+healing);
        //Prevent healing over MaxHP
        if(this.getHealth()>this.getMaxHealth()){this.setHealth(this.getMaxHealth());} 
    }
    
    
    //-------------------initializing, serializing, etc--------------------
    @Override
    public Creature createFromTemplate() {
        Creature nc = new Creature(this.name, this.graphics.getImage());
        nc.setTemplateID(this.templateID);
        HashMap<String, SpriteAnimation> nanimations = new HashMap<>();
        for (String s : this.spriteAnimations.keySet()) {
            nanimations.put(s, this.spriteAnimations.get(s));
        }
        nc.spriteAnimations = nanimations;
        //Copy over attributes
        for (String a : this.attributes.keySet()) {
            nc.attributes.put(a, this.attributes.get(a));
        }
        nc.healHealth(nc.getMaxHealth());
        //Flags
        for (String f : this.flags.keySet()) {
            nc.flags.put(f, this.flags.get(f));
        }
        //Crossable terrain
        ArrayList<Integer> newCrossable = new ArrayList<>();
        for (int t : this.crossableTerrain) {
            newCrossable.add(t);
        }
        nc.crossableTerrain = newCrossable;
        //Abilities
        HashMap<String, Action> newActions = new HashMap<>();
        if (this.availableActions != null) {
            for (String actionName : this.availableActions.keySet()) {
                Action newAction = Mists.actionLibrary.create(actionName);
                if (newAction != null) {
                    newAction.setOwner(nc);
                    newActions.put(actionName, newAction);
                }
            }
        }
        nc.availableActions = newActions;
        
        if (this.currentDialogue != null) {
            nc.setCurrentDialogue(currentDialogue.createFromTemplate());
        }
        
        //AI should be the same type as Template
        //TODO: This needs rethinking
        if (ai instanceof CompanionAI) {
            nc.ai = new CompanionAI(nc);
        }  
        if (ai instanceof MonsterAI) {
            nc.ai = new MonsterAI(nc);
        }
        for (String flag : ai.getFlags()) {
            nc.ai.setFlag(flag, ai.getFlag(flag));
        }
        return nc;
    }
    
    @Override
    public String[] getInfoText() {
        if (this.lastTask == null) this.lastTask = new Task(GenericTasks.ID_IDLE, this.IDinLocation, null);
        String[] s = new String[]{
            this.name + " - BaseID: "+this.templateID ,
            "ID "+this.IDinLocation+" @ "+this.location.getName(),
            "X:"+((int)this.getXPos())+" Y:"+((int)this.getYPos()),
            this.getHealth() + "/"+this.getMaxHealth()+" hp",
            "Last: "+this.getLastTask().toString(),
            "Facing: "+this.getFacing().toString()
        };
        return s;
    }
    
    @Override
    public String toString() {
        String n = this.name + " @ "+"|"+(int)this.getCenterXPos()+"x"+(int)this.getCenterYPos()+"|";
        return n;
    }
    
    public String longString() {
        String n = this.name + " @ "+"|"+(int)this.getCenterXPos()+"x"+(int)this.getCenterYPos()+"|\n";
        n = n+this.location+"\n";
        n = n+"Health: "+this.getHealth()+" / "+this.getMaxHealth()+"\n";
        n = n+"Abilities: "+this.availableActions.keySet().toString()+"\n";
        return n;
    }
}
