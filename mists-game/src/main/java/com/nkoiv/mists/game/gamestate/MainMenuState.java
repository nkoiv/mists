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
import java.util.TreeSet;
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
    private final TreeSet<UIComponent> drawOrder;
    private final Game game;
    private UIComponent currentMenu;
    private boolean gameMenuOpen;
    
    
    public MainMenuState (Game game) {
        this.game = game;
        this.uiComponents = new HashMap<>();
        this.drawOrder = new TreeSet<>();
        MainMenuWindow mainMenuWindow = new MainMenuWindow(this);
        this.addUIComponent(mainMenuWindow);
        //this.uiComponents.put(mainMenuWindow.getName(), mainMenuWindow);
        this.currentMenu = uiComponents.get(mainMenuWindow.getName());
        
    }
    
    @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas) {
        GraphicsContext uigc = uiCanvas.getGraphicsContext2D();
        uigc.setFont(Mists.fonts.get("alagard"));
        double screenWidth = uiCanvas.getWidth();
        double screenHeight = uiCanvas.getHeight();
        //TODO: Add a background to render
        
        //Render the UI
        uigc.clearRect(0, 0, screenWidth, screenHeight);
        this.drawLogo(uiCanvas);
        this.drawVersion(uiCanvas);
        if (this.drawOrder != null) {
            for (UIComponent uic : this.drawOrder) {
                uic.render(uigc, 0, 0);
            }
        }
        /*
        if (this.uiComponents != null) {
            for (String s: this.uiComponents.keySet()) {
                this.uiComponents.get(s).render(uigc, 0, 0);
            }
        }
        */
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
        gc.strokeText("Version 0.3-Pandarin_Vines", game.WIDTH-300, game.HEIGHT-20);
        gc.restore();
    }

    @Override
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        
    }
 

    @Override
    public void handleMouseEvent(MouseEvent me) {
        //See if there's an UI component to click
        if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if(!mouseClickOnUI(me)){
            //If not, give the click to the underlying gameLocation
            Mists.logger.info("Click didnt land on an UI button");
        }
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
                    entry.getValue().handleMouseEvent(me);
                    me.consume();
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
    public void addUIComponent(UIComponent uic) {
        this.uiComponents.put(uic.getName(), uic);
        this.drawOrder.add(uic);
    }
    
    @Override
    public boolean removeUIComponent(String uicName) {
        if (this.uiComponents.keySet().contains(uicName)) {
            //Mists.logger.info("Removing UIC "+uicName);
            this.drawOrder.remove(this.uiComponents.get(uicName));
            this.uiComponents.remove(uicName);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeUIComponent(UIComponent uic) {
        if (this.uiComponents.containsValue(uic)) {
            this.drawOrder.remove(uic);
            this.uiComponents.remove(uic.getName());
            return true;
        }
        return false;
    }
    
    @Override
    public UIComponent getUIComponent(String uicName) {
        return this.uiComponents.get(uicName);
    }
    @Override
    public void updateUI() {
        this.uiComponents.get("MainMenu").setPosition((game.WIDTH/2 - 110), 250);
    }
    
}
