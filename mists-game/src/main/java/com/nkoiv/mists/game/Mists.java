/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;


import static com.nkoiv.mists.game.Mists.logger;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.audio.SoundManager;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gamestate.LoadingScreen;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.libraries.ActionLibrary;
import com.nkoiv.mists.game.libraries.GraphLibrary;
import com.nkoiv.mists.game.libraries.ItemLibrary;
import com.nkoiv.mists.game.libraries.LibLoader;
import com.nkoiv.mists.game.libraries.LocationLibrary;
import com.nkoiv.mists.game.libraries.MobLibrary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
     
    public static final Logger logger = Logger.getLogger(Mists.class.getName());
    
    public static Game MistsGame;
    public static GameMode gameMode = GameMode.SINGLEPLAYER;
    
    public static int graphicScale = 1;
    public static Image pixel = new Image("/images/blackpixel.png");
    
    public boolean running = false;
    public final ArrayList<KeyCode> pressedButtons = new ArrayList<>();
    public final ArrayList<KeyCode> releasedButtons = new ArrayList<>();
    public final double[] mouseClickCoordinates = new double[2];
    public Scene currentScene;
    
    public static SoundManager soundManager;
    public static GraphLibrary graphLibrary;
    public static HashMap<String, Font> fonts;
    public static ActionLibrary<Action> actionLibrary;
    public static ItemLibrary<Item> itemLibrary;
    public static MobLibrary<Structure> structureLibrary;
    public static MobLibrary<Creature> creatureLibrary;
    public static LocationLibrary locationLibrary;
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
        root.getChildren().add(gameCanvas);
        root.getChildren().add(shadowCanvas);
        root.getChildren().add(uiCanvas);
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
        MistsGame = new Game(gameCanvas, uiCanvas, shadowCanvas);
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
                //System.out.println("FPS : " + (int)(1/elapsedSeconds));
                previousNanoTime = currentNanoTime;
                //Do things:
                MistsGame.tick(elapsedSeconds, pressedButtons, releasedButtons); 
                releasedButtons.clear();
                MistsGame.render();
            } 
        }.start();
        
    }
    
    
    public static void main (String[] args) {
        logger.info("Mists game launching...");
        Application.launch();
        logger.info("Mists game ended");
    }
    
    public Game getGame() {
        return this.MistsGame;
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
        setupActionLibrary();
        setupItemLibrary();
        setupStructureLibrary();
        setupCreatureLibrary();
        setupLocationLibrary();
    }
    
    private void loadLibraries(Canvas gameCanvas, Canvas uiCanvas) {
        LoadingScreen loadingScreen = new LoadingScreen("Loading game assets",6);
        MistsGame.setLoadingScreen(loadingScreen);
        loadingScreen.updateProgress(1, "Initializing graphics");   
        setupGraphLibrary();
        logger.info("Graphics library initialized");
        loadingScreen.render(gameCanvas, uiCanvas);
        loadingScreen.updateProgress(1, "Creating actions");
        loadingScreen.render(gameCanvas, uiCanvas);
        setupActionLibrary();
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
        loadingScreen.updateProgress(1, "Done");
        MistsGame.clearLoadingScreen();   
    }
       
    private static void setupSoundManager() {
        Mists.soundManager = new SoundManager(5);
    }
    
    private static void setupGraphLibrary() {
        Mists.graphLibrary = new GraphLibrary();
        LibLoader.initializeGraphLibrary(graphLibrary);
    }
    
    private static void setupStructureLibrary() {
        Mists.structureLibrary = new MobLibrary<>();
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
        Mists.creatureLibrary = new MobLibrary<>();
        LibLoader.initializeCreatureLibrary(creatureLibrary);
    }
    
    private static void setupLocationLibrary() {
        Mists.locationLibrary = new LocationLibrary();
        LibLoader.initializeLocationLibrary(locationLibrary);
    }
    
    private void loadFonts() {
        fonts = new HashMap<>();
        Font alagard = Font.loadFont(getClass().getResourceAsStream("/fonts/alagard.ttf"), 20);
        fonts.put("alagard", alagard);
        Font romulus = Font.loadFont(getClass().getResourceAsStream("/fonts/romulus.ttf"), 20);
        fonts.put("romulus", romulus);   
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
