/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.ui.UIComponent;
import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

/**
 * GameStates handle various parts of a game.
 * A state handles both the input and the output, coordinating via Game.class.
 * GameState can be considered as the Controller in an MVC model.
 * Planned states include stuff such as:
 * MainMenu
 * WorldMap
 * Location
 * Town
 * @author nikok
 */
public interface GameState {
    
    //public HashMap<String, UIComponent> getUIComponents();
    public void addUIComponent(UIComponent uic);
    public UIComponent getUIComponent(String uicname);
    public boolean removeUIComponent(String uicname);
    public boolean removeUIComponent(UIComponent uic);
    
    public Game getGame();
    
    //Draw things
    public void render(Canvas gameCanvas, Canvas uiCanvas, Canvas shadowCanvas);
    
    //Do things
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons);
    
    //Handle mouse events
    public void handleMouseEvent(MouseEvent me);
    
    //Update UI (screen resize etC)
    public void updateUI();
    
    
    public void closePopUpWindows();
    
    //TODO:
    //Cleanup the stage
    public void exit();
    
    //Initialization
    public void enter();
    
    
    
}
