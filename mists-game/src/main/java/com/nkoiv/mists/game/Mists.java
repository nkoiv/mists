/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;


import static com.nkoiv.mists.game.Mists.logger;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author nkoiv
 */
public class Mists extends Application implements Global {
     
    public static final Logger logger = Logger.getLogger(Mists.class.getName());
    
    public Location currentLocation;
    public boolean running = false;
    public final ArrayList<String> inputLog = new ArrayList<>();

    
    public Scene initGameScene() { 
        Group root = new Group();
        Scene launchScene = new Scene(root);
        Canvas canvas = new Canvas( 512, 512 );
        root.getChildren().add( canvas );
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        Text titleText = new Text (200, 200, "Mists"); 
        titleText.setFont(new Font(40));
        root.getChildren().add(titleText);
        
        logger.info("Scene initialized");
        return launchScene;
    }
    
    public void moveToLocation (Location l) {
        currentLocation = l;
    }
    
    
    
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Mists");
        Scene currentScene = initGameScene();
        primaryStage.setScene(currentScene);
        setupKeyHandlers(primaryStage);
        primaryStage.show();
        running = true;
        logger.info("Mists game started");
        
         new AnimationTimer() {
             @Override
            public void handle(long now) {
                tick();
            }
             
         }.start();
        
        
    } 
    
    /*
    * Tick checks keybuffer, initiates actions and does just about everything.
    * Tick needs to know how much time has passed since the last tick, so it can
    * even out actions and avoid rollercoaster game speed. 
    */

    public void tick() {
        
    }

    
    public static void main (String[] args) {
        logger.info("Mists game launching...");
        Application.launch();
        logger.info("Mists game ended");
    }
    
    private void setupKeyHandlers(Stage primaryStage) {
        /** Keypresses and releases are stored separately, so that holding down a button continues to execute commands **/
        primaryStage.getScene().setOnKeyPressed(
            new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    if ( !inputLog.contains(code) )
                        inputLog.add( code );
                }
            });

        primaryStage.getScene().setOnKeyReleased(
            new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    inputLog.remove( code );
                }
            });
    }
}
