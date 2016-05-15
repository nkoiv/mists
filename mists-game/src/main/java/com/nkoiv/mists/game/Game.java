/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game;

import com.nkoiv.mists.game.actions.MeleeWeaponAttack;
import com.nkoiv.mists.game.actions.ProjectileSpell;
import com.nkoiv.mists.game.controls.LocationControls;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.gamestate.LoadingScreen;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.gamestate.MainMenuState;
import com.nkoiv.mists.game.gamestate.WorldMapState;
import com.nkoiv.mists.game.libraries.WorldMapLibrary;
import com.nkoiv.mists.game.quests.QuestManager;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.mapgen.DungeonGenerator;
import com.nkoiv.mists.game.world.worldmap.MapNode;
import com.nkoiv.mists.game.world.worldmap.WorldMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
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
    public final Canvas debugCanvas;
    public double WIDTH; //Dimensions of the screen (render area)
    public double HEIGHT; //Dimensions of the screen (render area)
    public double xOffset; //Offsets are used control which part of the map is drawn
    public double yOffset; //If/when a map is larger than display-area, it should be centered on player
    public boolean toggleScale = false;
    
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
    public Game (Canvas gameCanvas, Canvas uiCanvas, Canvas shadowCanvas, Canvas debugCanvas) {
        //Initialize the screen size
        this.gameCanvas = gameCanvas;
        this.uiCanvas = uiCanvas;
        this.shadowCanvas = shadowCanvas;
        this.debugCanvas = debugCanvas;
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
        
        //POC player:
        PlayerCharacter pocplayer = new PlayerCharacter();
        Creature companion = Mists.creatureLibrary.create("Himmu");
        System.out.println(companion.toString());
        pocplayer.addCompanion(companion);
        pocplayer.addAction(new MeleeWeaponAttack());
        pocplayer.addAction(new ProjectileSpell("Firebolt"));
        setPlayer(pocplayer);
        
        //POC Dialogue
        
        //POC worldmap
        WorldMap wm = new WorldMap("Himmu island", new Image("/images/himmu_island.png"));
        WorldMapLibrary.populateWorldMapWithNodesFromYAML(wm, "libdata/defaultWorldmapNodes.yml");

        wm.setPlayerNode("Beach");
        wm.setPlayerCharacter(player);
        
        this.generatedWorldMaps.put(1, wm);
        this.currentWorldMap = wm;
        this.moveToLocation(5, wm.getPlayerNode());
        this.moveToState(MAINMENU);
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
        if (currentState != null) currentState.exit();
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
     * Retrieve the location tied to the given locationID
     * If the locationID results in a null location, a new
     * location is generated from Mists.LocationLibrary with
     * the given locationID;
     * @param locationID
     * @return Location tied to the given ID
     */
    public Location getLocation(int locationID) {
        Mists.logger.log(Level.INFO, "Retrieving Location with ID {0} from the generated locations table", locationID);
        Location l = this.generatedLocations.get(locationID);
        if (l == null) {
            Mists.logger.info("Generated location was not found, creating a new one from template");
            l = Mists.locationLibrary.create(locationID);
            if (l!= null) {
                this.generatedLocations.put(locationID, l);
                Mists.logger.info(l.getName()+" generated and added to table with ID "+locationID);
            } else {
                Mists.logger.warning("Error generation location "+locationID);
            }
        }
        return l;
    }
    
    
    /**
    * Move the player (and the game) to a new location
    * @param locationID ID of the Location to move to
     * @param entranceNode EntranceNode within the location to zone into
    */
    public void moveToLocation(int locationID, MapNode entranceNode) {
        Location l = getLocation(locationID);
        moveToLocation(l, entranceNode);
    }
    
    public void moveToLocation(int locationID, double xCoor, double yCoor) {
        Location l = getLocation(locationID);
        moveToLocation(l, xCoor, yCoor);
    }
    
    public void moveToLocation(Location l, double xCoor, double yCoor) {
        Mists.logger.info("Moving into location "+l.getName());
        if (currentLocation != null) currentLocation.exitLocation();
        l.enterLocation(player, xCoor, yCoor);
        currentLocation = l;
        GameState s = this.gameStates.get(LOCATION);
        if (s!=null) {
            if (Mists.gameMode == GameMode.CLIENT) ((LocationState)s).getClient().setLocation(l);
            if (Mists.gameMode == GameMode.SERVER) ((LocationState)s).getServer().setLocation(l, null);
        }
    }
    
    /**
     * Move directly to a location without further consulting
     * the generatedLocations. For use in multiplayer fex.
     * @param l Location to move to
     * @param entranceNode EntranceNode within the location to zone into
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
    
    /**
     * Initialize a new game session
     */
    public void newGame() {
        PlayerCharacter pocplayer = new PlayerCharacter();
        pocplayer.addAction(new MeleeWeaponAttack());
        pocplayer.addAction(new ProjectileSpell("Firebolt"));
        setPlayer(pocplayer);
        pocplayer.getInventory().addItem(Mists.itemLibrary.create("Healing potion"));
        pocplayer.getInventory().addItem(Mists.itemLibrary.create("Healing potion"));
        pocplayer.getInventory().addItem(Mists.itemLibrary.create("Healing potion"));
        //Location newLoc = Mists.locationLibrary.create(1);
        //Mists.logger.info("Location "+newLoc.getName()+" generated");
        //game.addLocation(1, newLoc);
        moveToLocation(5, null);
        pocplayer.setPosition(14*Mists.TILESIZE, 8*Mists.TILESIZE);
        moveToState(Game.LOCATION);
        clearLoadingScreen();
        ((LocationState)getGameState(Game.LOCATION)).loadDefaultUI();
    }

    
}
