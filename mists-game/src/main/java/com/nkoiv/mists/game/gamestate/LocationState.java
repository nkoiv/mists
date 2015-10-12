/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.ui.ActionButton;
import com.nkoiv.mists.game.ui.AudioControls;
import com.nkoiv.mists.game.ui.AudioControls.MuteMusicButton;
import com.nkoiv.mists.game.ui.GoMainMenuButton;
import com.nkoiv.mists.game.ui.LocationControls;
import com.nkoiv.mists.game.ui.QuitButton;
import com.nkoiv.mists.game.ui.TextButton;
import com.nkoiv.mists.game.ui.UIComponent;
import com.nkoiv.mists.game.ui.TiledWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 * LocationState handles the core of the game: being in Locations.
 * As a GameState it takes in Input from the user via the Game -class,
 * and provides the Game the output of the Location (graphics, etc)
 * @author nikok
 */
public class LocationState implements GameState {
    
    private final Game game;
    private UIComponent currentMenu;
    private boolean gameMenuOpen;
    private final AudioControls audioControls = new AudioControls();
    private final LocationControls locationControls = new LocationControls();
    
    private final HashMap<String, UIComponent> uiComponents;
    
    public LocationState (Game game) {
        this.game = game;
        uiComponents = new HashMap<>();
        this.loadDefaultUI();
    }
    
    @Override
    public void updateUI() {
        //Move the actionbar to where it should be
        Mists.logger.info("Updating UI. Game dimensions: "+game.WIDTH+"x"+game.HEIGHT);
        uiComponents.get("Actionbar").setPosition(0, (game.HEIGHT - 80));
    }
    
    private void loadDefaultUI() {
        TiledWindow actionBar = new TiledWindow(this, "Actionbar", game.WIDTH, 80, 0, (game.HEIGHT - 80));
        TextButton attackButton = new ActionButton(game.player, "Smash!",  80, 60);
        TextButton pathsButton = new LocationControls.DrawPathsButton("Paths Off", 80, 60, this.game);
        TextButton lightenButton = new LocationControls.IncreaseLightlevelButton("Lighten", 80, 60, this.game);
        TextButton darkenButton = new LocationControls.ReduceLightlevelButton("Darken", 80, 60, this.game);
        MuteMusicButton muteMusicButton;
        muteMusicButton = new AudioControls.MuteMusicButton("Mute music", 80, 60);
        
        actionBar.addSubComponent(attackButton);
        actionBar.addSubComponent(pathsButton);
        actionBar.addSubComponent(lightenButton);
        actionBar.addSubComponent(darkenButton);
        actionBar.addSubComponent(muteMusicButton);
        uiComponents.put(actionBar.getName(), actionBar);
    }

    /**
     * The LocationState renderer does things in two layers: Game and UI.
     * Both of these layers are handled with via Canvases. First the Game is rendered on
     * the gameCanvas, then the UI is rendered on the uiCanvas on top of it
     * TODO: Consider separating gameplay into several layers (ground, structures, creatures, (structure)frill, overhead?)
     * @param gameCanvas Canvas to draw the actual gameplay on
     * @param uiCanvas Canvas to draw the UI on
     */
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
    
    
    
    
    /**
     * Toggles open/close the gameMenu, displayed at the middle of the screen.
     * TODO: Consider making a separate class for this, in case other GameStates utilize the same
     */
    public void toggleGameMenu() {
        Mists.logger.info("Game menu toggled");
        if (!gameMenuOpen) {
            gameMenuOpen = true;
            TiledWindow gameMenu = new TiledWindow(this, "GameMenu", 220, 300, (game.WIDTH/2 - 110), 150);
            TextButton resumeButton = new LocationControls.ResumeButton("Resume", 200, 60, this.game);
            TextButton optionsButton = new TextButton("Options", 200, 60);
            GoMainMenuButton mainMenuButton = new GoMainMenuButton(this.game, 200, 60);
            QuitButton quitButton = new QuitButton("Quit game", 200, 60);
            gameMenu.addSubComponent(resumeButton);
            gameMenu.addSubComponent(optionsButton);
            gameMenu.addSubComponent(mainMenuButton);
            gameMenu.addSubComponent(quitButton);
            uiComponents.put(gameMenu.getName(), gameMenu);
            Mists.logger.info("GameMenu opened");
        } else {
            gameMenuOpen = false;
            if (uiComponents.containsKey("GameMenu")) 
                    uiComponents.remove("GameMenu");
            Mists.logger.info("GameMenu closed");
        }
        
    }

    /**
     * The Tick command parses user input and sends an update(time) command to the
     * location game is currently at. These are both done only if game is not inside a menu.
     * In other words, the Location is paused while in a menu (that goes in the menu-stack).
     * @param time Time since last update
     * @param pressedButtons List of buttons pressed down by the user
     * @param releasedButtons  List of buttons released by the user
     */
    @Override
    public void tick(double time, ArrayList<String> pressedButtons, ArrayList<String> releasedButtons) {
        
        if(currentMenu == null) {
            handleLocationKeyPress(pressedButtons, releasedButtons);
            game.currentLocation.update(time);
        } 
    }
    
    @Override
    public void handleMouseEvent(MouseEvent me) {
        //See if there's an UI component to click
        if(!mouseClickOnUI(me)){
            //If not, give the click to the underlying gameLocation
            Mists.logger.info("Click didnt land on an UI button");
        }
    }
    
    /**
     * Check if there's any UI component at the mouse event location.
     * If so, trigger that UI components "onClick". 
     * @param me MouseEvent got from the game user (via Game)
     * @return True if UI component was clicked. False if there was no UI there
     */
    public boolean mouseClickOnUI(MouseEvent me) {
        double clickX = me.getX();
        double clickY = me.getY();
        for (Map.Entry<String, UIComponent> entry : uiComponents.entrySet()) {
            double uicHeight = entry.getValue().getHeight();
            double uicWidth = entry.getValue().getWidth();
            double uicX = entry.getValue().getXPosition();
            double uicY = entry.getValue().getYPosition();
            //Check if the click landed on the ui component
            if (clickX >= uicX && clickX <= (uicX + uicWidth)) {
                if (clickY >= uicY && clickY <= uicY + uicHeight) {
                    entry.getValue().onClick(me);
                    return true;
                }
            }
            
        }
        //Click landed on area without UI component
        Mists.logger.info("Clicked "+clickX+","+clickY+" - moving player there");
        double xOffset = this.game.currentLocation.getLastxOffset();
        double yOffset = this.game.currentLocation.getLastyOffset();
        this.game.currentLocation.getPlayer().setCenterPosition(clickX+xOffset, clickY+yOffset);
        return false;
    }

    /**
     * HandleLocationKeyPresses takes in the arraylists of keypresses and releases from the Game,
     * and does location-appropriate things with them.
     * TODO: Load these keybindings from an external file.
     * @param pressedButtons
     * @param releasedButtons 
     */
    
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
            game.currentLocation.toggleFlag("testFlag");
        
        }
        
        if (releasedButtons.contains("ESCAPE")) {
            this.toggleGameMenu();
        }
        
        releasedButtons.clear(); //Button releases are handled only once
    }
    

    
    @Override
    public Game getGame() {
        return this.game;
    }
    
    @Override
    public void exit() {
        Mists.soundManager.stopMusic();
    }

    @Override
    public void enter() {
        Mists.soundManager.playMusic("dungeon");
    }

    @Override
    public HashMap<String, UIComponent> getUIComponents() {
        return this.uiComponents;
    }
    
    
    
}
