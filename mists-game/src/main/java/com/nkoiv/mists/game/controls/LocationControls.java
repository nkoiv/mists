/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.Random;
import java.util.logging.Level;
import javafx.scene.image.ImageView;


/**
 * LocationControls are a layer that relays commands to the location
 * The idea behind separate LocationControls is that externally loaded
 * config / world data can utilize these via scripting.
 * @author nikok
 */
public class LocationControls {
    
    private final Game game;
    
    public LocationControls(Game game) {
        this.game = game;
    }
    
    private Location currentLoc() {
        return this.game.getCurrentLocation();
    }
    
    public Game getGame() {
        return this.game;
    }
    
    
    /**
     * Trigger is used for interpreting a command,
     * either via console or by some external script
     * @param command the supplied command to execute
     * @param arguments for the command
     * @return returns true if command was executed
     */
    
    public boolean trigger(String command, String ...arguments) {
        switch (command) {
            case "toggleFlag": if (arguments!=null) this.toggleFlag(arguments[0]); return true;
            
                
            default: return false;
        }
        
    }
    
    //------------Individual triggers for controlling a location----
    
    public void toggleFlag(String flag) {
        this.game.getCurrentLocation().toggleFlag(flag);
    }
    
    public void printClearanceMapIntoConsole(){
        game.getCurrentLocation().getPathFinder().printClearanceMapIntoConsole(0);
    }
    
    public void printCollisionMapIntoConsole() {
        game.getCurrentLocation().getPathFinder().printCollisionMapIntoConsole();
    }
    
    public void toggleLocationMenu() {
        LocationState LS = (LocationState)this.game.currentState;
        LS.toggleGameMenu();
    }
    
    public void increseLightLevel() {
        this.game.getCurrentLocation().setMinLightLevel(this.game.getCurrentLocation().getMinLightLevel()+0.1);
    }
    
    public void reduceLightLevel() {
        this.game.getCurrentLocation().setMinLightLevel(this.game.getCurrentLocation().getMinLightLevel()-0.1);
    }
    
    
    //----------Player controls-----
    
    public void playerAttack() {
        game.getPlayer().useAction("melee");
    }
    
    public void playerMove(Direction direction) {
        switch (direction) {
            case UP: game.getPlayer().moveTowards(Direction.UP); break;         
            case DOWN: game.getPlayer().moveTowards(Direction.DOWN); break;
            case LEFT: game.getPlayer().moveTowards(Direction.LEFT); break;
            case RIGHT: game.getPlayer().moveTowards(Direction.RIGHT);break;
            case UPRIGHT: game.getPlayer().moveTowards(Direction.UPRIGHT);break;
            case UPLEFT: game.getPlayer().moveTowards(Direction.UPLEFT);break;
            case DOWNRIGHT: game.getPlayer().moveTowards(Direction.DOWNRIGHT);break;
            case DOWNLEFT: game.getPlayer().moveTowards(Direction.DOWNLEFT);break;
            default: break;
        } 
    }
    
    //-------- Mob creation ------
    
    public void addCreature(String mobTemplate) {
        if ("".equals(mobTemplate)) addCreature(); 
        else {
            if ("blob".equals(mobTemplate)) addBlob();
        }
        
    }
    
    /**
     * Create a random mob at spot mouse cursor is at
     */
    public void addCreature() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        double x = p.x - Mists.primaryStage.getX();
        double y = p.y - Mists.primaryStage.getY();
        Random rnd = new Random();
        int startX = rnd.nextInt(1);
        int startY = rnd.nextInt(1);
        Mists.logger.log(Level.INFO, "Creating monster from sprite sheet position {0},{1} at coordinates {2}+{3}x{4}+{5}", new Object[]{startX, startY, x, game.getCurrentLocation().getLastxOffset(), y, game.getCurrentLocation().getLastyOffset()});
        Creature monster = new Creature("Otus", new ImageView("/images/monster_small.png"), 3, startX*3, startY*4, 32, 32);
        monster.getSprite().setCollisionAreaShape(2);
        game.getCurrentLocation().addCreature(monster, x+game.getCurrentLocation().getLastxOffset(), y+game.getCurrentLocation().getLastyOffset());   
    }
    
    public void addBlob() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        double x = p.x - Mists.primaryStage.getX();
        double y = p.y - Mists.primaryStage.getY();
        Creature monster = new Creature("Blob", new ImageView("/images/blob.png"), 3, 0, 0, 84, 84);
        monster.getSprite().setCollisionAreaShape(2);
        game.getCurrentLocation().addCreature(monster, x+game.getCurrentLocation().getLastxOffset(), y+game.getCurrentLocation().getLastyOffset());   
    }
    
    //----------Location creation------------
    
    public void createLoc(String location) {
        if (location.equals("testmap")) {
            //game.moveToState(0);
            Location newlocation = new Location ("ConsoleLoc", new TileMap("/mapdata/pathfinder_test.map"));
            game.moveToLocation(newlocation);
            //game.moveToState(1);
        }
        
    }
    
}
