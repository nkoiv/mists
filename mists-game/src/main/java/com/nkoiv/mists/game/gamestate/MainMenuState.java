/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * MainMenuState controls and manages the main menu.
 * Because of the simplicity of the main menu, there is no separate class for it.
 * Main menu is really only composed of UI-components, like buttons.
 * @author nikok
 */
public class MainMenuState implements GameState {

    @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        double screenWidth = gameCanvas.getWidth();
        double screenHeight = gameCanvas.getHeight();
        
        
    }

    @Override
    public void tick(double time, ArrayList<String> pressedButtons, ArrayList<String> releasedButtons) {
        
    }

    @Override
    public void exit() {
        
    }

    @Override
    public void enter() {
        
    }
    
}
