/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import static com.nkoiv.mists.game.Global.TILESIZE;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.world.LightsRenderer;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.MapGenerator;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.pathfinding.CollisionMap;
import com.nkoiv.mists.game.world.pathfinding.PathFinder;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;


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
        return this.game.currentLocation;
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
        this.game.currentLocation.toggleFlag(flag);
    }
    
    public void printClearanceMapIntoConsole(){
        game.currentLocation.getPathFinder().printClearanceMapIntoConsole(0);
    }
    
    public void printCollisionMapIntoConsole() {
        game.currentLocation.getPathFinder().printCollisionMapIntoConsole();
    }
    
    public void toggleLocationMenu() {
        LocationState LS = (LocationState)this.game.currentState;
        LS.toggleGameMenu();
    }
    
    public void increseLightLevel() {
        this.game.currentLocation.setMinLightLevel(this.game.currentLocation.getMinLightLevel()+0.1);
    }
    
    public void reduceLightLevel() {
        this.game.currentLocation.setMinLightLevel(this.game.currentLocation.getMinLightLevel()-0.1);
    }
    
    
    //----------Player controls-----
    
    public void playerAttack() {
        game.currentLocation.getPlayer().useAction("MeleeAttack");
    }
    
    public void playerMove(Direction direction) {
        //TODO: Current movement lets player move superspeed diagonal. should call moveTowards(Direction.UPRIGHT) etc.
        switch (direction) {
            case UP: game.currentLocation.getPlayer().moveTowards(Direction.UP); break;         
            case DOWN: game.currentLocation.getPlayer().moveTowards(Direction.DOWN); break;
            case LEFT: game.currentLocation.getPlayer().moveTowards(Direction.LEFT); break;
            case RIGHT: game.currentLocation.getPlayer().moveTowards(Direction.RIGHT);break;
            default: break;
        } 
    }
    
    //-------- Mob creation ------
    
    public void addCreature(String mobTemplate) {
        if ("".equals(mobTemplate)) addCreature(); 
        else {
            
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
        Mists.logger.log(Level.INFO, "Creating monster from sprite sheet position {0},{1} at coordinates {2}+{3}x{4}+{5}", new Object[]{startX, startY, x, game.currentLocation.getLastxOffset(), y, game.currentLocation.getLastyOffset()});
        Creature monster = new Creature("Otus", new ImageView("/images/monster_small.png"), 3, startX*3, startY*4, 32, 32);
        monster.getSprite().setCollisionAreaShape(2);
        game.currentLocation.addCreature(monster, x+game.currentLocation.getLastxOffset(), y+game.currentLocation.getLastyOffset());   
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
