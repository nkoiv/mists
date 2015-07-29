/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;


import static com.nkoiv.mists.game.Mists.logger;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.logging.Level;
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
import javafx.stage.Screen;
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
    public Scene currentScene;
    public double xOffset; //Offsets are used control which part of the map is drawn
    public double yOffset; //If/when a map is larger than display-area, it should be centered on player
    
    boolean inMenu = false;
    
    public void moveToLocation (Location l) {
        currentLocation = l;
    }

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
        
        currentLocation = new Location();
        
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
                tick(elapsedNanoTime);
                render(locationCanvas);
                
            } 
         }.start();
        
        
    } 
    
    
    public void tick(double time) {
        /*
        * Tick checks keybuffer, initiates actions and does just about everything.
        * Tick needs to know how much time has passed since the last tick, so it can
        * even out actions and avoid rollercoaster game speed. 
        */
        
        handleKeyPress(inputLog);
        currentLocation.update(time);

    }
    
    public void render(Canvas canvas) {
        /*
        * Render handles updating the game window, and should be called every time something needs refreshed.
        * By default render is called 60 times per second (or as close to as possible) by AnimationTimer -thread.
        */
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 800, 600);
        currentLocation.render(gc);
        //logger.info("Rendered on canvas");
    }

    
    public static void main (String[] args) {
        logger.info("Mists game launching...");
        Application.launch();
        logger.info("Mists game ended");
    }
    
    private void handleKeyPress(ArrayList<String> inputLog) {
        /*
        * TODO: External loadable configfile for keybindings
        * (probably via a class of its own)
        */
        
        currentLocation.getPlayer().stopMovement();
        
        if (inputLog.isEmpty()) {
            return;
        }

        
        if (inputLog.contains("UP")) {
            logger.log(Level.INFO, "Moving {0} UP", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.UP);            
        }
        if (inputLog.contains("DOWN")) {
            logger.log(Level.INFO, "Moving {0} DOWN", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.DOWN);
        }
        if (inputLog.contains("LEFT")) {
            logger.log(Level.INFO, "Moving {0} LEFT", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.LEFT);
        }
        if (inputLog.contains("RIGHT")) {
            logger.log(Level.INFO, "Moving {0} RIGHT", currentLocation.getPlayer().getName());
            currentLocation.getPlayer().moveTowards(Direction.RIGHT);
        }
        
        
        
    }
    
    private void setupKeyHandlers(Stage primaryStage) {
        /** KeyPresses and releases are stored separately, so that holding down a button continues to execute commands **/
        primaryStage.getScene().setOnKeyPressed(
            new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    if ( !inputLog.contains(code) )
                        inputLog.add( code );
                        logger.log(Level.INFO, "{0} pressed", code);
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
                    logger.log(Level.INFO, "{0} released", code);
                }
            });
    }
}
