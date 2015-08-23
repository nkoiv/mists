/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;

/**
 * GameStates handle various parts of a game.
 * A state handles both the input and the output, coordinating via Game.class.
 * Planned states include stuff such as:
 * MainMenu
 * WorldMap
 * Location
 * Town
 * @author nikok
 */
public interface GameState {
    
    //Draw things
    public void render(Canvas gameCanvas, Canvas uiCanvas);
    
    //Do things
    public void tick(double time, ArrayList<String> pressedButtons, ArrayList<String> releasedButtons);
    
    //TODO:
    //Cleanup the stage
    public void exit();
    
    //Initialization
    public void enter();
    
    
    
}
