/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.*;
import com.nkoiv.mists.game.controls.LocationControls;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.MapGenerator;
import java.util.ArrayList;
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
    public PlayerCharacter player;
    public Location currentLocation;
    public boolean running = false;
    public final ArrayList<String> inputLog = new ArrayList<>();
    public Scene currentScene;
    
    public double WIDTH; //Dimensions of the screen (render area)
    public double HEIGHT; //Dimensions of the screen (render area)
    public double xOffset; //Offsets are used control which part of the map is drawn
    public double yOffset; //If/when a map is larger than display-area, it should be centered on player
    public MapGenerator mapGen;

    private ArrayList<GameState> gameStates;
    public GameState currentState;
    public static final int MAINMENU = 0;
    public static final int LOCATION = 1;
    public static final int WORLDMAP = 2;
    public static final int LOADSCREEN = 9;
    
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
        PlayerCharacter player = new PlayerCharacter();
        player.getSprite().setCollisionAreaShape(2);
        player.addAction(new MeleeAttack());
        this.player = player;
        //Initialize GameStates
        this.gameStates = new ArrayList<>();
        gameStates.add(new MainMenuState(this));
        gameStates.add(new LocationState(this));
        currentState = gameStates.get(MAINMENU);
        currentState.enter();
        //Temp TODO:
        currentLocation = new Location(player);
        
    }
    
    public void moveToState(int gameStateNumber) {
        //TODO: Do some fancy transition?
        currentState.exit();
        currentState = gameStates.get(gameStateNumber);
        currentState.enter();
        updateUI();
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
    }
    
    public void handleMouseEvent(MouseEvent me) {
        //Pass the mouse event to the current gamestate
        currentState.handleMouseEvent(me);
    }
    
}
