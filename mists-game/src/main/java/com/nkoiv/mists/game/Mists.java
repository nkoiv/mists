/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.audio.SoundManager;
import com.nkoiv.mists.game.audio.SoundManagerJavaFX;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gamestate.LoadingScreen;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.libraries.ActionLibrary;
import com.nkoiv.mists.game.libraries.CreatureLibrary;
import com.nkoiv.mists.game.libraries.DialogueLibrary;
import com.nkoiv.mists.game.libraries.GraphLibrary;
import com.nkoiv.mists.game.libraries.ItemLibrary;
import com.nkoiv.mists.game.libraries.LibLoader;
import com.nkoiv.mists.game.libraries.LocationLibrary;
import com.nkoiv.mists.game.libraries.StructureLibrary;
import com.nkoiv.mists.game.libraries.WorldMapLibrary;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Mists Class is used to initialize the window and launch the game.
 * This class is intended to be easily rewritten when porting game to other platforms.
 * Mists is the View of the game.
 * @author nkoiv
 */
public class Mists extends Application implements Global {
	public static final String gameVersion = "Version-0.7-Pandarin_Pomelo";
    public static final Logger logger = Logger.getLogger(Mists.class.getName());
    
    public static Game MistsGame;
    public static GameMode gameMode = GameMode.SINGLEPLAYER;
    
    public static int graphicScale = 1;
    //public static Image pixel = new Image("/images/blackpixel.png");
    
    public boolean running = false;
    public final ArrayList<KeyCode> pressedButtons = new ArrayList<>();
    public final ArrayList<KeyCode> releasedButtons = new ArrayList<>();
    public final double[] mouseClickCoordinates = new double[2];
    public Scene currentScene;
    
    public static SoundManager soundManager;
    public static GraphLibrary graphLibrary;
    public static HashMap<String, Font> fonts;
    public static HashMap<String, ImageCursor> cursors;
    public static ActionLibrary<Action> actionLibrary;
    public static ItemLibrary<Item> itemLibrary;
    public static StructureLibrary<Structure> structureLibrary;
    public static CreatureLibrary<Creature> creatureLibrary;
    public static LocationLibrary locationLibrary;
    public static WorldMapLibrary worldmapLibrary;
    public static DialogueLibrary dialogueLibrary;
    public static Stage primaryStage;
    /**
    * start(), coming from the Application that Mists extends, is the call to launch the game.
    * The main loop of the game is executed inside the start, with an AnimationTimer();
    * Everything shown on the screen is displayed via the priamaryStage, given to start by the Application.launch();
    * @param primaryStage Comes from the Application class, as called for in the main(): Application.launch();
    */
    @Override
    public void start(Stage primaryStage) {
        Mists.primaryStage = primaryStage;
        primaryStage.setTitle("The Mists");
        primaryStage.setMinHeight(Global.HEIGHT);
        primaryStage.setMinWidth(Global.WIDTH);
        primaryStage.setWidth(Global.WIDTH);
        primaryStage.setHeight(Global.HEIGHT);
        Group root = new Group();
        Scene launchScene = new Scene(root);
        final Canvas gameCanvas = new Canvas(Global.WIDTH, Global.HEIGHT);
        gameCanvas.widthProperty().bind(primaryStage.widthProperty());
        gameCanvas.heightProperty().bind(primaryStage.heightProperty());
        final Canvas uiCanvas = new Canvas(Global.WIDTH, Global.HEIGHT);
        uiCanvas.widthProperty().bind(primaryStage.widthProperty());
        uiCanvas.heightProperty().bind(primaryStage.heightProperty());
        final Canvas shadowCanvas = new Canvas(Global.WIDTH, Global.HEIGHT);
        shadowCanvas.widthProperty().bind(primaryStage.widthProperty());
        shadowCanvas.heightProperty().bind(primaryStage.heightProperty());
        shadowCanvas.setBlendMode(BlendMode.MULTIPLY);
        final Canvas debugCanvas = new Canvas(Global.WIDTH, Global.HEIGHT);
        debugCanvas.widthProperty().bind(primaryStage.widthProperty());
        debugCanvas.heightProperty().bind(primaryStage.heightProperty());
        root.getChildren().add(gameCanvas);
        root.getChildren().add(shadowCanvas);
        root.getChildren().add(uiCanvas);
        root.getChildren().add(debugCanvas);
        logger.info("Scene initialized");
        setupSoundManager();
        logger.info("SoundManager initialized");
        loadFonts();
        logger.info("Fonts loaded");
        primaryStage.setScene(launchScene);
        setupKeyHandlers(primaryStage);
        setupMouseHandles(root);
        setupWindowResizeListeners(launchScene);
        logger.info("Game set up");
        MistsGame = new Game(gameCanvas, uiCanvas, shadowCanvas, debugCanvas);
        MistsGame.WIDTH = primaryStage.getWidth();
        MistsGame.HEIGHT = primaryStage.getHeight();
        primaryStage.show();
        this.loadLibraries(gameCanvas, uiCanvas);
        MistsGame.start();
        running = true;
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        logger.info("Mists game started");
        
        /*
        * Game main loop
        */
        new AnimationTimer() //Logic loop
        {
            final double startNanoTime = System.nanoTime();
            double previousNanoTime = 0;
            
            @Override
            public void handle(long currentNanoTime)
            {
                if (previousNanoTime == 0) {
                   previousNanoTime = currentNanoTime;
                return;
                }
                
                double elapsedSeconds = (currentNanoTime - previousNanoTime) / 1000000000.0;
                previousNanoTime = currentNanoTime;
                //Do things:
                MistsGame.tick(elapsedSeconds, pressedButtons, releasedButtons); 
                releasedButtons.clear();
                MistsGame.render();
                //System.out.println("FPS : " + (int)(1/elapsedSeconds));
                uiCanvas.getGraphicsContext2D().setFill(Color.DARKRED);
                uiCanvas.getGraphicsContext2D().fillText("FPS : " + (int)(1/elapsedSeconds), 0, 20);
            } 
        }.start();
        
    }
    
    
    public static void main (String[] args) {
        logger.info("Mists game launching...");
        Application.launch();
        logger.info("Mists game ended");
    }
    
    
    private void setupWindowResizeListeners(Scene launchScene) {
                
        launchScene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                Mists.logger.log(Level.INFO, "Width: {0}", newSceneWidth);
                MistsGame.WIDTH = (Double)newSceneWidth;
                MistsGame.updateUI();
            }
        });
        launchScene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                Mists.logger.log(Level.INFO, "Height: {0}", newSceneHeight);
                MistsGame.HEIGHT = (Double)newSceneHeight;
                MistsGame.updateUI();
            }
        });
    }
    
    public static void loadLibraries() {
        setupGraphLibrary();
        setupDialogueLibrary();
        setupActionLibrary();
        setupItemLibrary();
        setupStructureLibrary();
        setupCreatureLibrary();
        setupLocationLibrary();
    }
    
    private void loadLibraries(Canvas gameCanvas, Canvas uiCanvas) {
        LoadingScreen loadingScreen = new LoadingScreen("Loading game assets",8);
        MistsGame.setLoadingScreen(loadingScreen);
        loadingScreen.updateProgress(1, "Initializing graphics");   
        setupGraphLibrary();
        logger.info("Graphics library initialized");
        loadingScreen.render(gameCanvas, uiCanvas);
        loadingScreen.updateProgress(1, "Creating actions");
        loadingScreen.render(gameCanvas, uiCanvas);
        setupActionLibrary();
        loadingScreen.updateProgress(1, "Generating dialogue library");
        loadingScreen.render(gameCanvas, uiCanvas);
        setupDialogueLibrary();
        logger.info("Action library initialized");
        loadingScreen.updateProgress(1, "Loading items");
        loadingScreen.render(gameCanvas, uiCanvas);
        setupItemLibrary();
        logger.info("Item library initialized");
        loadingScreen.updateProgress(1, "Generating structures");
        loadingScreen.render(gameCanvas, uiCanvas);
        setupStructureLibrary();
        logger.info("Structure library initialized");
        loadingScreen.updateProgress(1, "Spawning creatures");
        loadingScreen.render(gameCanvas, uiCanvas);
        setupCreatureLibrary();
        logger.info("Creature library initialized");
        loadingScreen.updateProgress(1, "Generating location templates");
        loadingScreen.render(gameCanvas, uiCanvas);
        setupLocationLibrary();
        logger.info("Location templates initialized");
        loadingScreen.updateProgress(1, "Generating worldmaps");
        loadingScreen.render(gameCanvas, uiCanvas);
        setupWorldmapLibrary();
        logger.info("Worldmaps initialized");
        loadingScreen.updateProgress(1, "Loading cursors");
        loadingScreen.render(gameCanvas, uiCanvas);
        loadCursors();
        loadingScreen.updateProgress(1, "Done");
        MistsGame.clearLoadingScreen();   
    }
       
    private static void setupSoundManager() {
        Mists.soundManager = new SoundManagerJavaFX(5);
        //Mists.soundManager = new SoundManagerOgg();
    }
    
    private static void setupGraphLibrary() {
        Mists.graphLibrary = new GraphLibrary();
        LibLoader.initializeGraphLibrary(graphLibrary);
    }
    
    private static void setupStructureLibrary() {
        Mists.structureLibrary = new StructureLibrary<>();
        LibLoader.initializeStructureLibrary(structureLibrary);
    }
    
    private static void setupActionLibrary() {
        Mists.actionLibrary = new ActionLibrary<>();
        LibLoader.initializeActionLibrary(actionLibrary);
    }
    
    private static void setupItemLibrary() {
        Mists.itemLibrary = new ItemLibrary<>();
        LibLoader.initializeItemLibrary(itemLibrary);
    }
    
    private static void setupCreatureLibrary() {
        Mists.creatureLibrary = new CreatureLibrary<>();
        LibLoader.initializeCreatureLibrary(creatureLibrary);
    }
    
    private static void setupLocationLibrary() {
        Mists.locationLibrary = new LocationLibrary();
        LibLoader.initializeLocationLibrary(locationLibrary);
    }
    
    private static void setupWorldmapLibrary() {
    	Mists.worldmapLibrary = new WorldMapLibrary();
    	LibLoader.initializeWorldMapLibrary(worldmapLibrary);
    }
    
    private static void setupDialogueLibrary() {
        Mists.dialogueLibrary = new DialogueLibrary();
        LibLoader.initializeDialogueLibrary(dialogueLibrary);
    }
    
    private void loadFonts() {
        fonts = new HashMap<>();
        Font alagard = Font.loadFont(getClass().getResourceAsStream("/fonts/alagard.ttf"), 20);
        fonts.put("alagard", alagard);
        Font alagard20 = Font.loadFont(getClass().getResourceAsStream("/fonts/alagard.ttf"), 20);
        fonts.put("alagard20", alagard20);
        Font alagard12 = Font.loadFont(getClass().getResourceAsStream("/fonts/alagard.ttf"), 12);
        fonts.put("alagard12", alagard12);
        Font romulus = Font.loadFont(getClass().getResourceAsStream("/fonts/romulus.ttf"), 20);
        fonts.put("romulus", romulus);   
        Font romulus20 = Font.loadFont(getClass().getResourceAsStream("/fonts/romulus.ttf"), 20);
        fonts.put("romulus20", romulus20); 
        Font romulus12 = Font.loadFont(getClass().getResourceAsStream("/fonts/romulus.ttf"), 12);
        fonts.put("romulus12", romulus12); 
    }
    
    private void loadCursors() {
    	this.cursors = new HashMap<>();
    	cursors.put("handblue",new ImageCursor(graphLibrary.getImage("cursorHandblue")));
    	cursors.put("handbeige", new ImageCursor(graphLibrary.getImage("cursorHandbeige")));
		cursors.put("handgrey", new ImageCursor(graphLibrary.getImage("cursorHandgrey")));
    	
		cursors.put("gauntletblue",new ImageCursor(graphLibrary.getImage("cursorGauntletblue")));
    	cursors.put("gauntletbronze", new ImageCursor(graphLibrary.getImage("cursorGauntletbronze")));
		cursors.put("gauntletgrey", new ImageCursor(graphLibrary.getImage("cursorGauntletgrey")));
    	
		cursors.put("swordGold",new ImageCursor(graphLibrary.getImage("cursorSwordgold")));
    	cursors.put("swordSilver", new ImageCursor(graphLibrary.getImage("cursorSwordsilver")));
		cursors.put("swordBronze", new ImageCursor(graphLibrary.getImage("cursorSwordbronze")));
    	
    }
    
    public void setCursor(String cursorName) {
    	String lowercasename = cursorName.toLowerCase();
    	if (cursors.containsKey(lowercasename)) {
    		currentScene.setCursor(cursors.get(cursorName));
    	} else {
    		//If specified cursor wasn't found, change to default cursor
    		currentScene.setCursor(Cursor.DEFAULT);
    	}
    }
    
    private void setupMouseHandles(Group root) {
        //Pass the event over to the game
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MistsGame.handleMouseEvent(me);
            }
        });
        root.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MistsGame.handleMouseEvent(me);
            }
        });
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MistsGame.handleMouseEvent(me);
            }
        });
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MistsGame.handleMouseEvent(me);
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                MistsGame.handleMouseEvent(me);
            }
        });
        
    }
    
    private void setupKeyHandlers(Stage primaryStage) {
        /** KeyPresses and releases are stored separately, so that holding down a button continues to execute commands **/
        primaryStage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    KeyCode code = e.getCode();
                    if ( !pressedButtons.contains(code) )
                        pressedButtons.add( code );
                        //logger.log(Level.INFO, "{0} pressed", code);
                }
            });

        primaryStage.getScene().setOnKeyReleased(new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    KeyCode code = e.getCode();
                    pressedButtons.remove( code );
                    if (!releasedButtons.contains(code)) 
                        releasedButtons.add( code );
                    //logger.log(Level.INFO, "{0} released", code);
                }
            });
    }


}
