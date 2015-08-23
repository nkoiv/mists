/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

import static com.nkoiv.mists.game.Global.HEIGHT;
import static com.nkoiv.mists.game.Global.WIDTH;
import static com.nkoiv.mists.game.Mists.logger;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.*;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.MapGenerator;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    public double xOffset; //Offsets are used control which part of the map is drawn
    public double yOffset; //If/when a map is larger than display-area, it should be centered on player
    public MapGenerator mapGen;

    private ArrayList<GameState> gameStates;
    private GameState currentState;
    
    /**
    * Initialize a new game
    * Call in the character generator, set the location to start.
    * TODO: Game states
    */
    public Game () {
        //POC player:
        PlayerCharacter himmu = new PlayerCharacter();
        himmu.getSprite().setCollisionAreaShape(2);
        himmu.addAction(new MeleeAttack());
        this.player = himmu;
        //Initialize GameStates
        this.gameStates = new ArrayList<>();
        gameStates.add(new LocationState(this));
        currentState = gameStates.get(0);
        //Temp TODO:
        currentLocation = new Location(player);
        
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
        currentState.tick(time, pressedButtons, releasedButtons);
    }
    
    /**
    * Render handles updating the game window, and should be called every time something needs refreshed.
    * By default render is called 60 times per second (or as close to as possible) by AnimationTimer -thread.
    * @param centerCanvas The Canvas to draw the game on
   
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
