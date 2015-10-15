/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;


import static com.nkoiv.mists.game.Mists.logger;
import com.nkoiv.mists.game.audio.SoundManager;
import java.util.ArrayList;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    
    public Game MistsGame;
    
    public boolean running = false;
    public final ArrayList<KeyCode> pressedButtons = new ArrayList<>();
    public final ArrayList<KeyCode> releasedButtons = new ArrayList<>();
    public final double[] mouseClickCoordinates = new double[2];
    public Scene currentScene;
    public static SoundManager soundManager;
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
        root.getChildren().add(gameCanvas);
        root.getChildren().add(uiCanvas);
        logger.info("Scene initialized");
        setupSoundManager();
        logger.info("SoundManager initialized");
        primaryStage.setScene(launchScene);
        setupKeyHandlers(primaryStage);
        setupMouseHandles(root);
        setupWindowResizeListeners(launchScene);
        MistsGame = new Game();
        MistsGame.WIDTH = primaryStage.getWidth();
        MistsGame.HEIGHT = primaryStage.getHeight();
        logger.info("Game set up");
        primaryStage.show();
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
        new AnimationTimer()
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
                //Show things:
                MistsGame.render(gameCanvas, uiCanvas);
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
       
    private void setupSoundManager() {
                    
        this.soundManager = new SoundManager(5);
    }
    
    private void setupMouseHandles(Group root) {
        //Pass the event over to the game
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
