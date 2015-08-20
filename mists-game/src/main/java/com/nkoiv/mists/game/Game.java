/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

import static com.nkoiv.mists.game.Mists.logger;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.MapGenerator;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Game hosts the main loop of the game.
 * The Game class handles swapping between locations.
 * @author nkoiv
 */
public class Game {
    
    public Location currentLocation;
    public boolean running = false;
    public final ArrayList<String> inputLog = new ArrayList<>();
    public Scene currentScene;
    public double xOffset; //Offsets are used control which part of the map is drawn
    public double yOffset; //If/when a map is larger than display-area, it should be centered on player
    public MapGenerator mapGen;
    
    boolean inMenu = false;
    
    /**
    * Initialize a new game
    * Call in the character generator, set the location to start.
    *
    */
    public Game () {
        currentLocation = new Location();
    }
    
    /**
    * Move the player (and the game) to a new location
    * @param l The location to be moved to
    */
    public void moveToLocation (Location l) {
        currentLocation = l;
    }

    /**
    * Tick checks keybuffer, initiates actions and does just about everything.
    * Tick needs to know how much time has passed since the last tick, so it can
    * even out actions and avoid rollercoaster game speed. 
    * @param time Time passed since last time 
    * @param pressedButtons Buttons currently pressed down
    * @param releasedButtons Buttons recently released
    */
    public void tick(double time, ArrayList<String> pressedButtons, ArrayList<String> releasedButtons) {

        handleKeyPress(pressedButtons, releasedButtons);
        currentLocation.update(time);

    }
    
    /**
    * Render handles updating the game window, and should be called every time something needs refreshed.
    * By default render is called 60 times per second (or as close to as possible) by AnimationTimer -thread.
    * @param canvas The Canvas to draw the game on
    */
    public void render(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double screenWidth = canvas.getWidth();
        double screenHeight = canvas.getHeight();
        //Clear old stuff from the screen
        gc.clearRect(0, 0, screenWidth, screenHeight);
        currentLocation.render(gc);
        //logger.info("Rendered on canvas");
    }
    
    private void handleKeyPress(ArrayList<String> pressedButtons, ArrayList<String> releasedButtons) {
        
        //TODO: External loadable configfile for keybindings
        //(probably via a class of its own)
        
        
        currentLocation.getPlayer().stopMovement();
        
        if (pressedButtons.isEmpty() && releasedButtons.isEmpty()) {
            return;
        }

        //TODO: Current movement lets player move superspeed diagonal. should call moveTowards(Direction.UPRIGHT) etc.
        if (pressedButtons.contains("UP")) {
            //Mists.logger.log(Level.INFO, "Moving {0} UP", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.UP);            
        }
        if (pressedButtons.contains("DOWN")) {
            //Mists.logger.log(Level.INFO, "Moving {0} DOWN", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.DOWN);
        }
        if (pressedButtons.contains("LEFT")) {
            //Mists.logger.log(Level.INFO, "Moving {0} LEFT", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.LEFT);
        }
        if (pressedButtons.contains("RIGHT")) {
            //Mists.logger.log(Level.INFO, "Moving {0} RIGHT", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.RIGHT);
        }
        
        //TODO: These should be directed to the UI-layer, which knows which abilities player has bound where
        if (pressedButtons.contains("SPACE")) {
            //Mists.logger.log(Level.INFO, "{0} TRIED USING ABILITY 0", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().useAction("MeleeAttack");
        }
        
        if (releasedButtons.contains("ENTER")) {
            currentLocation.getPathFinder().printCollisionMapIntoConsole();
            currentLocation.getPathFinder().printClearanceMapIntoConsole(0);
            
        }
        
        if (releasedButtons.contains("SHIFT")) {
            currentLocation.getCreatureByName("Otus").toggleFlag("testFlag");
        
        }
        
        releasedButtons.clear(); //Button releases are handled only once
    }
}
