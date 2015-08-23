/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.ui.TextButton;
import com.nkoiv.mists.game.ui.UIComponent;
import com.nkoiv.mists.game.ui.Window;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private boolean gameMenuOpen;
    
    private HashMap<String, UIComponent> uiComponents;
    
    public LocationState (Game game) {
        this.game = game;
        uiComponents = new HashMap<>();
        this.loadDefaultUI();
    }
    
    private void loadDefaultUI() {
        Window actionBar = new Window(Global.WIDTH, 80, 0, (Global.HEIGHT - 80));
        TextButton testButton1 = new TextButton("Test", 80, 60);
        TextButton testButton2 = new TextButton("Button", 80, 60);
        TextButton testButton3 = new TextButton("Foo", 80, 60);
        TextButton testButton4 = new TextButton("Bar", 80, 60);
        TextButton testButton5 = new TextButton("Himmu", 80, 60);
        
        actionBar.addMenuButton(testButton1);
        actionBar.addMenuButton(testButton2);
        actionBar.addMenuButton(testButton3);
        actionBar.addMenuButton(testButton4);
        actionBar.addMenuButton(testButton5);
        uiComponents.put("Actionbar", actionBar);
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
            for (Map.Entry<String, UIComponent> entry : uiComponents.entrySet()) {
                entry.getValue().render(uigc, 0, 0);
                //Mists.logger.info("Rendering UIC " + entry.getKey());
            }
        }
        
    }
    
    private void toggleGameMenu() {
        if (!gameMenuOpen) {
            gameMenuOpen = true;
            Window gameMenu = new Window(220, 220, (Global.WIDTH/2 - 110), 150);
            TextButton testButton1 = new TextButton("Testbutton", 200, 60);
            TextButton testButton2 = new TextButton("Options", 200, 60);
            TextButton testButton3 = new TextButton("Quit game", 200, 60);
            gameMenu.addMenuButton(testButton1);
            gameMenu.addMenuButton(testButton2);
            gameMenu.addMenuButton(testButton3);
            uiComponents.put("GameMenu", gameMenu);
            Mists.logger.info("GameMenu opened");
        } else {
            gameMenuOpen = false;
            if (uiComponents.containsKey("GameMenu")) 
                    uiComponents.remove("GameMenu");
            Mists.logger.info("GameMenu closed");
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
        
        if (releasedButtons.contains("ESCAPE")) {
            this.toggleGameMenu();
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
