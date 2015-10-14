/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.ui.MainMenuWindow;
import com.nkoiv.mists.game.ui.UIComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * MainMenuState controls and manages the main menu.
 * Because of the simplicity of the main menu, there is no separate class for it.
 * Main menu is really only composed of UI-components, like buttons.
 * @author nikok
 */
public class MainMenuState implements GameState {

    private HashMap<String, UIComponent> uiComponents;
    private final Game game;
    private UIComponent currentMenu;
    private boolean gameMenuOpen;
    
    
    public MainMenuState (Game game) {
        this.game = game;
        this.uiComponents = new HashMap<>();
        MainMenuWindow mainMenuWindow = new MainMenuWindow(this);
        this.uiComponents.put(mainMenuWindow.getName(), mainMenuWindow);
        this.currentMenu = uiComponents.get(mainMenuWindow.getName());
        
    }
    
    @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas) {
        GraphicsContext uigc = uiCanvas.getGraphicsContext2D();
        double screenWidth = uiCanvas.getWidth();
        double screenHeight = uiCanvas.getHeight();
        //TODO: Add a background to render
        
        //Render the UI
        uigc.clearRect(0, 0, screenWidth, screenHeight);
        this.drawLogo(uiCanvas);
        this.drawVersion(uiCanvas);
        if (uiComponents != null) {
            for (Map.Entry<String, UIComponent> entry : uiComponents.entrySet()) {
                entry.getValue().render(uigc, 0, 0);
                //Mists.logger.info("Rendering UIC " + entry.getKey());
            }
        }   
    }
    
    private void drawLogo(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double screenWidth = game.WIDTH;
        double screenHeight = game.HEIGHT;
        Image logo = new Image("/images/mists_logo.png");
        gc.drawImage(logo, (screenWidth/2)-(logo.getWidth()/2), 50);
        
    }
    
    private void drawVersion(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.save();
        gc.setStroke(Color.ORANGERED);
        gc.strokeText("Version 0.1-Pandarin_Orange", game.WIDTH-270, game.HEIGHT-20);
        gc.restore();
    }

    @Override
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        
    }

    @Override
    public void handleMouseEvent(MouseEvent me) {
        //See if there's an UI component to click
        if(!mouseClickOnUI(me)){
            //If not, give the click to the underlying gameLocation
            Mists.logger.info("Click didnt land on an UI button");
        }
    }
    
    public boolean mouseClickOnUI(MouseEvent me) {
        double clickX = me.getX();
        double clickY = me.getY();
        for (Map.Entry<String, UIComponent> entry : uiComponents.entrySet()) {
            double uicHeight = entry.getValue().getHeight();
            double uicWidth = entry.getValue().getWidth();
            double uicX = entry.getValue().getXPosition();
            double uicY = entry.getValue().getYPosition();
            //Check if the click landed on the ui component
            if (clickX >= uicX && clickX <= (uicX + uicWidth)) {
                if (clickY >= uicY && clickY <= uicY + uicHeight) {
                    entry.getValue().onClick(me);
                    return true;
                }
            }
            
        }
        
        return false;
    }
    
    @Override
    public Game getGame() {
        return this.game;
    }
    
    @Override
    public void exit() {
        Mists.soundManager.stopMusic();
    }

    @Override
    public void enter() {
        try {
            Mists.soundManager.playMusic("menu");
        }catch (Exception e) {
            
        }
    }

    @Override
    public HashMap<String, UIComponent> getUIComponents() {
        return this.uiComponents;
    }

    @Override
    public void updateUI() {
        this.uiComponents.get("MainMenu").setPosition((game.WIDTH/2 - 110), 250);
    }
    
}
