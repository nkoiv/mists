/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

import static com.nkoiv.mists.game.Mists.logger;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author lp35567
 */
public class Game {
    
    public Location currentLocation;
    public boolean running = false;
    public final ArrayList<String> inputLog = new ArrayList<>();
    public Scene currentScene;
    public double xOffset; //Offsets are used control which part of the map is drawn
    public double yOffset; //If/when a map is larger than display-area, it should be centered on player
    
    boolean inMenu = false;
    
    public Game () {
        currentLocation = new Location();
    }
    
    public void moveToLocation (Location l) {
        currentLocation = l;
    }
 
    public void tick(double time, ArrayList<String> inputLog) {
        /*
        * Tick checks keybuffer, initiates actions and does just about everything.
        * Tick needs to know how much time has passed since the last tick, so it can
        * even out actions and avoid rollercoaster game speed. 
        */
        handleKeyPress(inputLog);
        currentLocation.update(time);

    }
    
    public void render(Canvas canvas) {
        /*
        * Render handles updating the game window, and should be called every time something needs refreshed.
        * By default render is called 60 times per second (or as close to as possible) by AnimationTimer -thread.
        */
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 800, 600);
        currentLocation.render(gc);
        //logger.info("Rendered on canvas");
    }
    
    private void handleKeyPress(ArrayList<String> inputLog) {
        /*
        * TODO: External loadable configfile for keybindings
        * (probably via a class of its own)
        */
        
        currentLocation.getPlayer().stopMovement();
        
        if (inputLog.isEmpty()) {
            return;
        }

        
        if (inputLog.contains("UP")) {
            //Mists.logger.log(Level.INFO, "Moving {0} UP", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.UP);            
        }
        if (inputLog.contains("DOWN")) {
            //Mists.logger.log(Level.INFO, "Moving {0} DOWN", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.DOWN);
        }
        if (inputLog.contains("LEFT")) {
            //Mists.logger.log(Level.INFO, "Moving {0} LEFT", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.LEFT);
        }
        if (inputLog.contains("RIGHT")) {
            //Mists.logger.log(Level.INFO, "Moving {0} RIGHT", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.RIGHT);
        }
        
        //TODO: These should be directed to the UI-layer, which knows which abilities player has bound where
        if (inputLog.contains("SPACE")) {
            Mists.logger.log(Level.INFO, "{0} TRIED USING ABILITY 0", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().useAction("MeleeAttack");
        }

        
    }
}
