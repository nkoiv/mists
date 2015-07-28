/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author daedra
 */
public class Mists extends Application {
     
    private static final Logger logger = Logger.getLogger(Mists.class.getName());
    
    public Scene initGameScene() {
        Text text = new Text (10, 40, "Mists"); 
        text.setFont(new Font(40));
        Scene scene = new Scene(new Group(text));
        return scene;
    }
    
    @Override
    public void start(Stage primaryStage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setWidth(bounds.getWidth() / 2);
        primaryStage.setHeight(bounds.getHeight() / 2);
        primaryStage.setTitle("The Mists");
        primaryStage.setScene(initGameScene());
        primaryStage.show();    	
        logger.info("Mists game started");
    } 
    
    public static void main (String[] args) {
        Application.launch();
    }

}
