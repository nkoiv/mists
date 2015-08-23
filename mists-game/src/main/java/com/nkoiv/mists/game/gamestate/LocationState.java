/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.ui.TextButton;
import com.nkoiv.mists.game.ui.UIComponent;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * LocationState handles the core of the game: being in Locations.
 * 
 * @author nikok
 */
public class LocationState implements GameState {
    
    private final Game game;
    private boolean inMenu;
    
    private ArrayList<UIComponent> uiComponents;
    
    public LocationState (Game game) {
        this.game = game;
        uiComponents = new ArrayList<>();
        TextButton testButton = new TextButton("Testbutton", 200, 50);
        uiComponents.add(testButton);
    }

    @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        GraphicsContext uigc = uiCanvas.getGraphicsContext2D();
        double screenWidth = gameCanvas.getWidth();
        double screenHeight = gameCanvas.getHeight();
        //Render the current Location
        gc.clearRect(0, 0, screenWidth, screenHeight);
        if (game.currentLocation != null) {
            game.currentLocation.render(gc);
        }
        //Render the UI
        uigc.clearRect(0, 0, screenWidth, screenHeight);
        if (uiComponents != null) {
            for (UIComponent uc : uiComponents) {
                uc.render(uigc, 0, 0);
            }
        }
        
    }

    @Override
    public void tick(double time, ArrayList<String> pressedButtons, ArrayList<String> releasedButtons) {
        if(!inMenu) {
            handleLocationKeyPress(pressedButtons, releasedButtons);
            game.currentLocation.update(time);
        }
    }

    private void handleLocationKeyPress(ArrayList<String> pressedButtons, ArrayList<String> releasedButtons) {
        
        //TODO: External loadable configfile for keybindings
        //(probably via a class of its own)     
        game.currentLocation.getPlayer().stopMovement();
        
        if (pressedButtons.isEmpty() && releasedButtons.isEmpty()) {
            return;
        }

        //TODO: Current movement lets player move superspeed diagonal. should call moveTowards(Direction.UPRIGHT) etc.
        if (pressedButtons.contains("UP")) {
            //Mists.logger.log(Level.INFO, "Moving {0} UP", currentLocation.getPlayer().getName());
            game.currentLocation.getPlayer().moveTowards(Direction.UP);            
        }
        if (pressedButtons.contains("DOWN")) {
            //Mists.logger.log(Level.INFO, "Moving {0} DOWN", currentLocation.getPlayer().getName());
            game.currentLocation.getPlayer().moveTowards(Direction.DOWN);
        }
        if (pressedButtons.contains("LEFT")) {
            //Mists.logger.log(Level.INFO, "Moving {0} LEFT", currentLocation.getPlayer().getName());
            game.currentLocation.getPlayer().moveTowards(Direction.LEFT);
        }
        if (pressedButtons.contains("RIGHT")) {
            //Mists.logger.log(Level.INFO, "Moving {0} RIGHT", currentLocation.getPlayer().getName());
            game.currentLocation.getPlayer().moveTowards(Direction.RIGHT);
        }
        
        //TODO: These should be directed to the UI-layer, which knows which abilities player has bound where
        if (pressedButtons.contains("SPACE")) {
            //Mists.logger.log(Level.INFO, "{0} TRIED USING ABILITY 0", currentLocation.getPlayer().getName());
            game.currentLocation.getPlayer().useAction("MeleeAttack");
        }
        
        if (releasedButtons.contains("ENTER")) {
            game.currentLocation.getPathFinder().printCollisionMapIntoConsole();
            game.currentLocation.getPathFinder().printClearanceMapIntoConsole(0);
            
        }
        
        if (releasedButtons.contains("SHIFT")) {
            game.currentLocation.getCreatureByName("Otus").toggleFlag("testFlag");
        
        }
        
        releasedButtons.clear(); //Button releases are handled only once
    }
    
    @Override
    public void exit() {
        this.inMenu = false;
    }

    @Override
    public void enter() {
        this.inMenu = false;
    }
    
    
    
}
