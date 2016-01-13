/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.actions.MeleeWeaponAttack;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.*;
import com.nkoiv.mists.game.controls.LocationControls;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.items.Weapon;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.mapgen.DungeonGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

/**
 * Game handles the main loop of the game. The View of the game (Mists-class)
 * Tick and Render from Game, without knowing anything. Game.class knows everything.
 * The Game class handles swapping between locations etc.
 * @author nkoiv
 */
public class Game {
    private PlayerCharacter player;
    private Location currentLocation;
    public boolean running = false;
    public final ArrayList<String> inputLog = new ArrayList<>();
    public Scene currentScene;
    
    public double WIDTH; //Dimensions of the screen (render area)
    public double HEIGHT; //Dimensions of the screen (render area)
    public double xOffset; //Offsets are used control which part of the map is drawn
    public double yOffset; //If/when a map is larger than display-area, it should be centered on player
    public boolean toggleScale = true;
    
    public DungeonGenerator mapGen;

    private HashMap<Integer,GameState> gameStates;
    public GameState currentState;
    public static final int MAINMENU = 0;
    public static final int LOCATION = 1;
    public static final int WORLDMAP = 2;
    public static final int LOADSCREEN = 9;
    
    private GameMode currentGameMode = GameMode.SINGLEPLAYER;
    
    public LocationControls locControls;
    
    /**
    * Initialize a new game
    * Call in the character generator, set the location to start.
    * TODO: Game states
    */
    public Game () {
        //Initialize the screen size
        WIDTH = Global.WIDTH;
        HEIGHT = Global.HEIGHT;
        
        //Setup controls
        this.locControls = new LocationControls(this);
        
        //POC player:
        PlayerCharacter pocplayer = new PlayerCharacter();
        Creature companion = Mists.creatureLibrary.create("Himmu");
        System.out.println(companion.toString());
        pocplayer.addCompanion(companion);
        pocplayer.addAction(new MeleeWeaponAttack());
        this.player = pocplayer;
        //Initialize GameStates
        this.gameStates = new HashMap<>();
        gameStates.put(MAINMENU, new MainMenuState(this));
        //gameStates.add(new LocationState(this, GameMode.SINGLEPLAYER));
        currentState = gameStates.get(MAINMENU);
        currentState.enter();
        //Temp TODO:
        currentLocation = new Location(pocplayer);
        currentLocation.enterLocation(player);
    }
    
    public void setGameMode(GameMode gamemode) {
        this.currentGameMode = gamemode;
    }
    
    public GameMode getGameMode() {
        return this.currentGameMode;
    }
    
    public void moveToState(int gameStateNumber) {
        //TODO: Do some fancy transition?
        currentState.exit();
        if (gameStates.get(gameStateNumber) == null) {
            buildNewState(gameStateNumber);
        } else if (gameStateNumber == LOCATION) {
            if (((LocationState)gameStates.get(LOCATION)).gamemode != this.currentGameMode) buildNewState(LOCATION);
        }
        currentState = gameStates.get(gameStateNumber);
        currentState.enter();
        updateUI();
    }
    
    private void buildNewState(int gameStateNumber) {
        switch (gameStateNumber) {
            case MAINMENU: gameStates.put(MAINMENU, new MainMenuState(this)) ;break;
            case LOCATION: gameStates.put(LOCATION, new LocationState(this, this.currentGameMode)); break;
            case WORLDMAP: Mists.logger.warning("Tried to enter worldmap!"); break;
            case LOADSCREEN: Mists.logger.warning("Tried to enter loadscreen!"); break;
            default: Mists.logger.warning("Unknown gamestate!") ;break;
        }
    }
    
    /**
    * Move the player (and the game) to a new location
    * @param l The location to be moved to
    */

    public void moveToLocation(Location l) {
        currentLocation.exitLocation();
        l.enterLocation(player);
        currentLocation = l;
    }
    
    public void updateUI() {
        Mists.logger.info("Updating UI");
        currentState.updateUI();
    }
    
    public PlayerCharacter getPlayer() {
        return this.player;
    }
    
    public void setPlayer(PlayerCharacter p) {
        this.player = p;
    }
    
    public Location getCurrentLocation() {
        return this.currentLocation;
    }
    
    /*
    public void toggleScale(GraphicsContext gc) {
        if (Mists.scale == 1) {
            gc.scale(2, 2);
            Mists.scale = 2;
        } else {
            gc.scale(0.5, 0.5);
            Mists.scale = 1;
        }
        
    }
    */
    /**
    * Tick checks keybuffer, initiates actions and does just about everything.
    * Tick needs to know how much time has passed since the last tick, so it can
    * even out actions and avoid rollercoaster game speed. 
    * @param time Time passed since last time 
    * @param pressedButtons Buttons currently pressed down
    * @param releasedButtons Buttons recently released
    */
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        currentState.tick(time, pressedButtons, releasedButtons);
    }
    
    /**
    * Render handles updating the game window, and should be called every time something needs refreshed.
    * By default render is called 60 times per second (or as close to as possible) by AnimationTimer -thread.
    * @param centerCanvas The Canvas to draw the game on
    * @param uiCanvas the Canvas to draw UI on 
    */
    public void render(Canvas centerCanvas, Canvas uiCanvas) {
        //TODO: Consider sorting out UI here instead of handing it all to currentState
        currentState.render(centerCanvas, uiCanvas);
        //Mists.logger.info("Rendered current state on canvas");
        if (toggleScale) {
            //toggleScale(centerCanvas.getGraphicsContext2D());
            //toggleScale(uiCanvas.getGraphicsContext2D());
            toggleScale=false;
        }
    }
    
    public void handleMouseEvent(MouseEvent me) {
        //Pass the mouse event to the current gamestate
        currentState.handleMouseEvent(me);
    }

}
