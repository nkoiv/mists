/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;


import static com.nkoiv.mists.game.Mists.logger;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
    public final ArrayList<String> pressedButtons = new ArrayList<>();
    public final ArrayList<String> releasedButtons = new ArrayList<>();
    public final double[] mouseClickCoordinates = new double[2];
    public Scene currentScene;
    
    /**
    * start(), coming from the Application that Mists extends, is the call to launch the game.
    * The main loop of the game is executed inside the start, with an AnimationTimer();
    * Everything shown on the screen is displayed via the priamaryStage, given to start by the Application.launch();
    * @param primaryStage Comes from the Application class, as called for in the main(): Application.launch();
    */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Mists");
        Group root = new Group();
        Scene launchScene = new Scene(root);
        final Canvas gameCanvas = new Canvas(WIDTH, HEIGHT);
        final Canvas uiCanvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(gameCanvas);
        root.getChildren().add(uiCanvas);
        logger.info("Scene initialized");
        
        primaryStage.setScene(launchScene);
        setupKeyHandlers(primaryStage);
        setupMouseHandles(root);
        MistsGame = new Game();
        
        primaryStage.show();
        running = true;
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
                    String code = e.getCode().toString();
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
                    String code = e.getCode().toString();
                    pressedButtons.remove( code );
                    if (!releasedButtons.contains(code)) 
                        releasedButtons.add( code );
                    //logger.log(Level.INFO, "{0} released", code);
                }
            });
    }
}
