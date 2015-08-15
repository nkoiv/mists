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
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Mists Class is used to initialize the window and launch the game
 * This class is intended to be easily rewritten when porting game to other platforms
 * @author nkoiv
 */
public class Mists extends Application implements Global {
     
    public static final Logger logger = Logger.getLogger(Mists.class.getName());
    
    public Game MistsGame;
    
    public boolean running = false;
    public final ArrayList<String> pressedButtons = new ArrayList<>();
    public final ArrayList<String> releasedButtons = new ArrayList<>();
    public Scene currentScene;
    

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Mists");
        Group root = new Group();
        Scene launchScene = new Scene(root);
        final Canvas locationCanvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add( locationCanvas );
                
        
        logger.info("Scene initialized");
        
        primaryStage.setScene(launchScene);
        setupKeyHandlers(primaryStage);
        
        MistsGame = new Game();
        
        primaryStage.show();
        running = true;
        logger.info("Mists game started");
        
       
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
            
                double elapsedNanoTime = (currentNanoTime - previousNanoTime) / 1000000000.0;
                previousNanoTime = currentNanoTime;
                MistsGame.tick(elapsedNanoTime, pressedButtons, releasedButtons);
                MistsGame.render(locationCanvas);          
            } 
         }.start();
    } 
 
    
    public static void main (String[] args) {
        logger.info("Mists game launching...");
        Application.launch();
        logger.info("Mists game ended");
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
