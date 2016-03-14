/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

import com.nkoiv.mists.game.actions.MeleeWeaponAttack;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.*;
import com.nkoiv.mists.game.controls.LocationControls;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.libraries.WorldMapLibrary;
import com.nkoiv.mists.game.quests.QuestManager;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.worldmap.WorldMap;
import com.nkoiv.mists.game.world.mapgen.DungeonGenerator;
import com.nkoiv.mists.game.world.worldmap.MapNode;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
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
    private final HashMap<Integer, Location> generatedLocations;
    private final HashMap<Integer, WorldMap> generatedWorldMaps;
    private int nextFreeLocationID; //Free ID:s start from 10000. First 9999 are reserved.
    
    private Location currentLocation;
    private WorldMap currentWorldMap;
    public boolean running = false;
    public final ArrayList<String> inputLog = new ArrayList<>();
    public Scene currentScene;
    private LoadingScreen loadingScreen;
    private boolean loading;
    
    private final Canvas gameCanvas;
    private final Canvas shadowCanvas;
    private final Canvas uiCanvas;
    public double WIDTH; //Dimensions of the screen (render area)
    public double HEIGHT; //Dimensions of the screen (render area)
    public double xOffset; //Offsets are used control which part of the map is drawn
    public double yOffset; //If/when a map is larger than display-area, it should be centered on player
    public boolean toggleScale = true;
    
    public DungeonGenerator mapGen;
    public QuestManager questManager;
    
    private HashMap<Integer,GameState> gameStates;
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
     * @param gameCanvas Canvas to render the game on
     * @param uiCanvas Canvas to render the UI on
     * @param shadowCanvas Canvas for shadow overlay
    */
    public Game (Canvas gameCanvas, Canvas uiCanvas, Canvas shadowCanvas) {
        //Initialize the screen size
        this.gameCanvas = gameCanvas;
        this.uiCanvas = uiCanvas;
        this.shadowCanvas = shadowCanvas;
        WIDTH = gameCanvas.getWidth();
        HEIGHT = uiCanvas.getHeight();
        
        //Setup controls
        this.locControls = new LocationControls(this);
        
        //Initialize GameStates
        this.gameStates = new HashMap<>();
        this.generatedLocations = new HashMap<>();
        this.generatedWorldMaps = new HashMap<>();
        
        //Initialize QuestManager
        this.questManager = new QuestManager();
        //TODO: Stop using this testquest
        //Add TestQuest to openlist:
        questManager.addQuest(QuestManager.generateTestKillQuest());
        questManager.addQuest(QuestManager.generateTestFetchQuest());
        questManager.openQuest(1);
        questManager.openQuest(2);
    }
    
    public void start() {
        gameStates.put(MAINMENU, new MainMenuState(this));
        gameStates.put(WORLDMAP, new WorldMapState(this));
        currentState = gameStates.get(MAINMENU);
        
        //POC player:
        PlayerCharacter pocplayer = new PlayerCharacter();
        Creature companion = Mists.creatureLibrary.create("Himmu");
        System.out.println(companion.toString());
        pocplayer.addCompanion(companion);
        pocplayer.addAction(new MeleeWeaponAttack());
        setPlayer(pocplayer);
        
        //POC Dialogue
        
        //POC worldmap
        WorldMap wm = new WorldMap("Himmu island", new Image("/images/himmu_island.png"));
        WorldMapLibrary.populateWorldMapWithNodesFromYAML(wm, "libdata/defaultWorldmapNodes.yml");
        /*
        LocationNode cave = new LocationNode("Cave", new Image("/images/mountain_cave.png"), 1);
        MapNode boat = new MapNode("Boat", new Image("/images/boat.png"));
        MapNode roadToCave = new MapNode("Road", null);
        LocationNode village = new LocationNode("Village", new Image("/images/village.png"), 2);
        LocationNode byTheWoods = new LocationNode("Road to woods", new Image("/images/woods.png"), 3);
        MapNode sea1 = new MapNode("Out on the sea", null);
        MapNode sea2 = new MapNode("Out on the sea", null);
        
        
        boat.setNeighbour(sea1, Direction.DOWN);
        sea1.setNeighbour(boat, Direction.UP);
        sea1.setNeighbour(sea2, Direction.RIGHT);
        sea2.setNeighbour(sea1, Direction.LEFT);
        
        boat.setNeighbour(roadToCave, Direction.RIGHT);
        boat.setNeighbour(byTheWoods, Direction.UP);
        cave.setNeighbour(roadToCave, Direction.LEFT);
        village.setNeighbour(roadToCave, Direction.DOWN);
        roadToCave.setNeighbour(cave, Direction.RIGHT);
        roadToCave.setNeighbour(village, Direction.UP);
        roadToCave.setNeighbour(boat, Direction.LEFT);
        byTheWoods.setNeighbour(boat, Direction.DOWN);
        
        wm.addNode(sea1, 180, 450);
        wm.addNode(sea2, 550, 550);
        
        wm.addNode(cave, 290, 280);
        wm.addNode(boat, 150, 280);
        wm.addNode(roadToCave, 230, 290);
        wm.addNode(village, 250, 220);
        wm.addNode(byTheWoods, 140, 200);
        */
        wm.setPlayerNode("Boat");
        wm.setPlayerCharacter(player);
        
        this.generatedWorldMaps.put(1, wm);
        this.currentWorldMap = wm;
        currentState.enter();
        this.running = true;
    }
    
    public void setGameMode(GameMode gamemode) {
        Mists.gameMode = gamemode;
    }
    
    public GameState getGameState(int gameStateNumber) {
        return this.gameStates.get(gameStateNumber);
    }
    
    public void moveToState(int gameStateNumber) {
        //TODO: Do some fancy transition?
        currentState.exit();
        if (gameStates.get(gameStateNumber) == null) {
            buildNewState(gameStateNumber);
        } 
        currentState = gameStates.get(gameStateNumber);
        currentState.enter();
        updateUI();
    }
    
    private void buildNewState(int gameStateNumber) {
        switch (gameStateNumber) {
            case MAINMENU: gameStates.put(MAINMENU, new MainMenuState(this)) ;break;
            case LOCATION: gameStates.put(LOCATION, new LocationState(this)); break;
            case WORLDMAP: gameStates.put(WORLDMAP, new WorldMapState(this)); break;
            case LOADSCREEN: Mists.logger.warning("Tried to enter loadscreen!"); break;
            default: Mists.logger.warning("Unknown gamestate!") ;break;
        }
    }
    
    public void moveToWorldMap(int worldMapID) {
        WorldMap wm = getWorldMap(worldMapID);
        moveToWorldMap(wm);
    }
    
    public WorldMap getWorldMap(int worldMapID) {
        return this.generatedWorldMaps.get(worldMapID);
    }
    
    public void moveToWorldMap(WorldMap wm) {
        currentWorldMap = wm;
    }
    
    /**
     * Peek which ID will be given to the next location
     * that's stored in the generatedLocations.
     * @return next free LocationID
     */
    public int peekNextFreeLocationID() {
        return this.nextFreeLocationID;
    }
    
    /**
     * Take the next free LocationID number, and
     * increase the counter by one, reserving the number.
     * @return next free LocationID
     */
    public int takeNextFreeLocationID() {
        int id = this.nextFreeLocationID;
        this.nextFreeLocationID++;
        return id;
    }
    
    /**
     * Get the next free LocationID in the Game and
     * add the Location to generatedLocations with it.
     * @param location The location to store in generatedLocations
     */
    public void addLocation(Location location) {
        this.generatedLocations.put(takeNextFreeLocationID(), location);
    }
    
    /**
     * Add the Location to generatedLocations with a
     * specific location ID
     * @param locationID Location ID to use for the location
     * @param location The location to store in generatedLocations
     */
    public void addLocation(int locationID, Location location) {
        this.generatedLocations.put(locationID, location);
    }
    
    /**
     * Retreive the location tied to the given locationID
     * If the locationID results in a null location, a new
     * location is generated from Mists.LocationLibrary with
     * the given locationID;
     * @param locationID
     * @return 
     */
    public Location getLocation(int locationID) {
        Location l = this.generatedLocations.get(locationID);
        if (l == null) {
            l = Mists.locationLibrary.create(locationID);
            this.generatedLocations.put(locationID, l);
        }
        return l;
    }
    
    
    /**
    * Move the player (and the game) to a new location
    * @param locationID ID of the Location to move to
    */
    public void moveToLocation(int locationID, MapNode entranceNode) {
        Location l = getLocation(locationID);
        moveToLocation(l, entranceNode);
    }
    
    /**
     * Move directly to a location without further consulting
     * the generatedLocations. For use in multiplayer fex.
     * @param l Location to move to
     */
    public void moveToLocation(Location l, MapNode entranceNode) {
        Mists.logger.info("Moving into location "+l.getName());
        if (currentLocation != null) currentLocation.exitLocation();
        l.enterLocation(player, entranceNode); //Don't give entranceNode as a parameter.
        currentLocation = l;
        GameState s = this.gameStates.get(LOCATION);
        if (s!=null) {
            if (Mists.gameMode == GameMode.CLIENT) ((LocationState)s).getClient().setLocation(l);
            if (Mists.gameMode == GameMode.SERVER) ((LocationState)s).getServer().setLocation(l, entranceNode);
        }
    }
    
    public void updateUI() {
        if (this.running) {
            Mists.logger.info("Updating UI");
            currentState.updateUI();
        }
    }
    
    public Canvas getGameCanvas() {
        return this.gameCanvas;
    }
    
    public Canvas getUICanvas() {
        return this.uiCanvas;
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
    
    public WorldMap getCurrentWorldMap() {
        return this.currentWorldMap;
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

    */
    public void render() {
        if (this.loading) {
            try {
                this.loadingScreen.render(gameCanvas, uiCanvas);
                if (this.loadingScreen.isReady()) this.loading=false;
                return;
            } catch (Exception e) {
                Mists.logger.warning("Loading screen got removed mid-render");
                return;
            }
        }
        //TODO: Consider sorting out UI here instead of handing it all to currentState
        currentState.render(gameCanvas, uiCanvas, shadowCanvas);
        //Mists.logger.info("Rendered current state on canvas");
        if (toggleScale) {
            //toggleScale(centerCanvas.getGraphicsContext2D());
            //toggleScale(uiCanvas.getGraphicsContext2D());
            toggleScale=false;
        }
    }
    
    public void handleMouseEvent(MouseEvent me) {
        //Pass the mouse event to the current gamestate
        if (!this.running) return;
        currentState.handleMouseEvent(me);
    }
    
    public void setLoadingScreen(LoadingScreen loadingScreen) {
        this.loadingScreen = loadingScreen;
        this.loading = true;
    }
    
    public void clearLoadingScreen() {
        this.loadingScreen = null;
        this.loading = false;
    }
    
    public LoadingScreen getLoadingScreen() {
        return this.loadingScreen;
    }
    
}
