/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.AI.CompanionAI;
import com.nkoiv.mists.game.AI.CreatureAI;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Creature is a "living" MapObject
 * As such, they get (at least some) AI-routines
 * @author nkoiv
 */
public class Creature extends MapObject implements Combatant {
    
    private CreatureAI ai;
    private Direction facing;
    private Direction lastFacing = null;
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
    private ArrayList<Integer> crossableTerrain; //List of terrains we can go through;
    
    //Old position to snap back to when colliding;
    private double oldXPos;
    private double oldYPos;
    
    //Constructor for a creature template with one static image
    public Creature (String name, Image image) {
        super (name, image);
        this.setFacing(Direction.DOWN);
        this.initializeAttributes();
        this.initializeFlags();
        this.crossableTerrain = new ArrayList<>();
        this.crossableTerrain.add(0);
        this.ai = new CreatureAI(this);
    }
       
    //Constructing a creature template with sprite animations
    public Creature (String name, ImageView imageView, int frameCount, int startingXTile, int startingYTile, int xOffset, int yOffset, int frameWidth, int frameHeight) {
        super(name);
        this.setFacing(Direction.DOWN);
        this.setAnimations(imageView, frameCount, startingXTile, startingYTile, frameWidth, frameHeight);
        this.setSprite(new Sprite(this.spriteAnimations.get("downMovement").getCurrentFrame()));
        this.initializeAttributes();
        this.initializeFlags();
        this.ai = new CreatureAI(this);
        this.crossableTerrain = new ArrayList<>();
        this.crossableTerrain.add(0);
    }
    
    public Creature (String name, ImageView imageView, int frameCount, int startingXTile, int startingYTile, int frameWidth, int frameHeight) {
        this(name, imageView, frameCount, startingXTile, startingYTile, 0,0, frameWidth, frameHeight);
    }

    /*Constructing a creature with only a still image
    * TODO: This should not be needed once Libraries are done
    */
    public Creature(String name, Image image, Location location, double xCoor, double yCoor) {
        super(name, image, location, xCoor, yCoor);
        this.setFacing(Direction.DOWN);
        this.initializeAttributes();
        this.initializeFlags();
        this.ai = new CreatureAI(this);
        this.crossableTerrain = new ArrayList<>();
        this.crossableTerrain.add(0);
    }
    
    
    //TODO: Make this load stats from creature library
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
        this.setFlag("visible", 1);
        this.setCollisionLevel(5);
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
        } else {
            Mists.logger.info(this.getName() + " tried using action ["+name+"], but doesnt have the ability." );
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
    
    @Override
    public void update (double time) {
        
        /* Call AI routines
        *  TODO: Separate movements etc to the AI
        */
        //this.moveTowardsPlayer(time); //TODO: Temporary before actual AI and behaviours
        this.ai.act(time);
        this.updateSprite();
        //this.applyMovement(time);  //ApplyMovement moved to the actual movement routine
    }
    

    /*
    * TODO: General sprite updates for general creatures
    * Is everything animated?
    */
    void updateSprite() {
        if (this.isFlagged("visible") && !this.spriteAnimations.isEmpty()) {
            if (this.facing != this.lastFacing) {
            this.lastFacing = this.facing;
            //Mists.logger.log(Level.INFO, "{0} is facing {1}", new Object[]{this.getName(), this.facing});
            switch(this.facing) {
                case UP: this.getSprite().setAnimation(this.spriteAnimations.get("upMovement")); break;
                case DOWN: this.getSprite().setAnimation(this.spriteAnimations.get("downMovement")); break;
                case LEFT: this.getSprite().setAnimation(this.spriteAnimations.get("leftMovement")); break;
                case RIGHT: this.getSprite().setAnimation(this.spriteAnimations.get("rightMovement")); break;
                case UPRIGHT: this.getSprite().setAnimation(this.spriteAnimations.get("upMovement")); break;
                case UPLEFT: this.getSprite().setAnimation(this.spriteAnimations.get("upMovement")); break;
                case DOWNRIGHT: this.getSprite().setAnimation(this.spriteAnimations.get("downMovement")); break;
                case DOWNLEFT: this.getSprite().setAnimation(this.spriteAnimations.get("downMovement")); break;
                default: break;
            }
            }
        }
        
    }
    
    /**
     * Apply movement to the creature.
     * The movement is done by creature sprites x and y velocity,
     * multiplied by time spent moving.
     * @param time Time spent moving
     * @return Return true if movement was possible, false if movement was blocked
     */
    
    public boolean applyMovement(double time){
        /*
        * Check collisions before movement
        * TODO: Add in pixel-based collision detection (compare alphamaps?)
        * TODO: Make collisions respect collisionlevel.
        */
        this.oldXPos = this.getSprite().getXPos();
        this.oldYPos = this.getSprite().getYPos();
        //Mists.logger.info("Old positions: "+this.oldXPos+","+this.oldYPos);
        
        if (this.getLocation().checkCollisions(this).isEmpty()) {
            //Collided with nothing, free to move
            if (this.isFlagged("movementBlocked")) {
                this.setFlag("movementBlocked", 0); //movement went through fine 
                //Mists.logger.info("Movement blocked!");
            }
            this.getSprite().update(time);
            return true;
        } else { 
            //Check which sides we collided on
            HashSet<Direction> collidedSides = this.getLocation().collidedSides(this);
            //Mists.logger.info("Checked collisions, found " +collidedSides.toString());
            if (collidedSides.contains(Direction.UP)) {
                //Block movement up
                if (this.getSprite().getYVelocity() < 0 ) this.getSprite().setYVelocity(0);
                this.getSprite().setYPosition(this.oldYPos);
            }
            if (collidedSides.contains(Direction.DOWN)) {
                //Block movement down
                if (this.getSprite().getYVelocity() > 0 ) this.getSprite().setYVelocity(0);
                this.getSprite().setYPosition(this.oldYPos);
            }
            if (collidedSides.contains(Direction.RIGHT)) {
                //Block movement right
                if (this.getSprite().getXVelocity() > 0 ) this.getSprite().setXVelocity(0);
                this.getSprite().setXPosition(this.oldXPos);
            }
            if (collidedSides.contains(Direction.LEFT)) {
                //Block movement left
                if (this.getSprite().getXVelocity() < 0 ) this.getSprite().setXVelocity(0);
                this.getSprite().setXPosition(this.oldXPos);
            }
            this.getSprite().update(time);
            this.setFlag("movementBlocked", 1); //remember this was a bad way to go to
            return false;
        }
    }
    
    public boolean moveTowards (double xCoor, double yCoor) {
        double xDifference = xCoor - this.getCenterXPos();
        double yDifference = yCoor - this.getCenterYPos();
        if (xDifference < 1) xDifference = 0;
        if (yDifference < 1) yDifference = 0;
        //Stop moving if we're already in our target coordinates
        if (xCoor == this.getXPos() && yCoor == this.getYPos()) return this.stopMovement();
        //If X or Y target is same as where we're at, we can use normal diagonal movements
        if (xCoor == this.getXPos()) {
            if (this.getYPos() <= yCoor) {
                return this.moveDown();
            } else {
                return this.moveUp();
            }
        }
        if (yCoor == this.getYPos()) {
            if (this.getXPos() <= xCoor) {
                return this.moveRight();
            } else {
                return this.moveLeft();
            }
        }
        
        /*
        * Pythagoras lets us calculate diagonal speed:
        * ((AC = sqrt(AB^2 + BC^2)))
        */
        double AB=Math.abs(xCoor-this.getXPos());
        double BC=Math.abs(yCoor-this.getYPos());
        double AC = Math.sqrt(Math.pow(AB, 2) + Math.pow(BC, 2));
        double xSpeedMultiplier = AB/AC;
        double ySpeedMultiplier = BC/AC;
        
        if (this.getXPos() <= xCoor) {
            //Moving Right
            if (this.getYPos() <= yCoor) {
                //Moving Down(&Right)
                this.getSprite().addVelocity(
                this.getAttribute("Speed")*xSpeedMultiplier,
                this.getAttribute("Speed")*ySpeedMultiplier);
            } else {
                //Moving Up(&Right)
                this.getSprite().addVelocity(
                this.getAttribute("Speed")*xSpeedMultiplier,
                -this.getAttribute("Speed")*ySpeedMultiplier);
            }
        } else {
            //Moving Left
            if (this.getYPos() <= yCoor) {
                //Moving Down(&Left)
                this.getSprite().addVelocity(
                -this.getAttribute("Speed")*xSpeedMultiplier,
                this.getAttribute("Speed")*ySpeedMultiplier);
            } else {
                //Moving Up(&Left)
                this.getSprite().addVelocity(
                -this.getAttribute("Speed")*xSpeedMultiplier,
                -this.getAttribute("Speed")*ySpeedMultiplier);
            }
        }
        //Update facing
        if (this.getSprite().getYVelocity() > 0) this.setFacing(Direction.DOWN);
        else this.setFacing(Direction.UP);
        if (Math.abs(this.getSprite().getYVelocity()) < Math.abs(this.getSprite().getXVelocity())) {
            if (this.sprite.getXVelocity() > 0) this.setFacing(Direction.RIGHT);
            else this.setFacing(Direction.LEFT);
        }
        
        return true;
    }
    
    @Override
    public boolean moveTowards (Direction direction) {
        //this.stopMovement(); //clear old movement (velocity)
        this.setFlag("moving", 1);
        switch(direction) {
            case UP: return moveUp();
            case DOWN: return moveDown();
            case LEFT: return moveLeft();
            case RIGHT: return moveRight();
            case UPRIGHT: return moveUpRight();
            case UPLEFT: return moveUpLeft();
            case DOWNRIGHT: return moveDownRight();
            case DOWNLEFT: return moveDownLeft();
            case STAY: return stopMovement();
        default: break;
        }
        
        return false;
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
            this.sprite.render(xOffset, yOffset, gc);
            
            if (this.ai.getPath()!=null && this.getLocation().isFlagged("drawPaths")) {
                this.ai.getPath().drawPath(gc, TILESIZE, xOffset, yOffset);
            }
            
            
        }
    } 
    
    private boolean moveUp() {
        this.facing = Direction.UP;
        this.getSprite().setVelocity(0, -this.getAttribute("Speed"));
        return true;
    }
    
    private boolean moveDown() {
        this.facing = Direction.DOWN;
        this.getSprite().setVelocity(0, this.getAttribute("Speed"));
        return true;
    }
        
    private boolean moveLeft() {
        this.facing = Direction.LEFT;
        this.getSprite().setVelocity(-this.getAttribute("Speed"), 0);
        return true;
    }
    
    private boolean moveRight() {
        this.facing = Direction.RIGHT;
        this.getSprite().setVelocity(this.getAttribute("Speed"), 0);
        return true;
    }

    private boolean moveUpRight() {
        this.getSprite().setVelocity(this.getAttribute("Speed")/1.41, -this.getAttribute("Speed")/1.41);
        this.facing = Direction.UPRIGHT;
        return true;
    }
    
    private boolean moveUpLeft() {
        this.getSprite().setVelocity(-this.getAttribute("Speed")/1.41, -this.getAttribute("Speed")/1.41);
        this.facing = Direction.UPLEFT;
        return true;
    }
        
    private boolean moveDownRight() {
        this.getSprite().setVelocity(this.getAttribute("Speed")/1.41, this.getAttribute("Speed")/1.41);
        this.facing = Direction.DOWNRIGHT;
        return true;
    }
    
    private boolean moveDownLeft() {
        this.getSprite().setVelocity(-this.getAttribute("Speed")/1.41, this.getAttribute("Speed")/1.41);
        this.facing = Direction.DOWNLEFT;
        return true;
    }

    public boolean stopMovement() {
        this.setFlag("moving", 0);
        this.getSprite().setVelocity(0, 0);
        //this.getSprite().setImage(this.getSprite().getImage());
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
        //TODO: Expand death
        if(this.getHealth()<1) this.setFlag("removable", 1);
    }
	
    @Override
    public void healHealth(int healing) {
        this.setHealth(this.getHealth()+healing);
        //Prevent healing over MaxHP
        if(this.getHealth()>this.getMaxHealth()){this.setHealth(this.getMaxHealth());} 
    }
    
    @Override
    public Creature createFromTemplate() {
        Creature nc = new Creature(this.name, this.getSprite().getImage());
        HashMap<String, SpriteAnimation> nanimations = new HashMap<>();
        for (String s : this.spriteAnimations.keySet()) {
            nanimations.put(s, this.spriteAnimations.get(s));
        }
        nc.spriteAnimations = nanimations;
        //Copy over attributes
        for (String a : this.attributes.keySet()) {
            nc.attributes.put(a, this.attributes.get(a));
        }
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
        
        //AI should be the same type as Template
        if (ai instanceof CompanionAI) {
            nc.ai = new CompanionAI(nc);
        }  
        return nc;
    }
    
}
