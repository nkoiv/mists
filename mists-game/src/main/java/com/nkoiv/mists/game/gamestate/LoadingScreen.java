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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author daedra
 */
public class LoadingScreen implements GameState {

    private double maxProgress;
    private double currentProgress;
    private boolean ready;
    
    public LoadingScreen() {
        
    }
    
    public void addProgress(double progress) {
        this.currentProgress+=progress;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public boolean isReady() {
        return this.ready;
    }
    
    @Override
    public void addUIComponent(UIComponent uic) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UIComponent getUIComponent(String uicname) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeUIComponent(String uicname) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeUIComponent(UIComponent uic) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Game getGame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        GraphicsContext uigc = uiCanvas.getGraphicsContext2D();
        double screenWidth = gameCanvas.getWidth();
        double screenHeight = gameCanvas.getHeight();
        gc.clearRect(0, 0, screenWidth, screenHeight);
        uigc.clearRect(0, 0, screenWidth, screenWidth);
        gc.save();
        
        this.drawLoadingBar(gc, screenWidth, screenHeight);
        gc.restore();
    }

    private void drawLoadingBar(GraphicsContext gc, double screenWidth, double screenHeight) {
        //TODO:Actually make a progressbar
        double barStart = 50;
        double barEnd = screenWidth-50;
        gc.setFill(Color.BLUEVIOLET);
        gc.fillRect(barStart, screenHeight-80, barEnd, screenHeight-50);
        gc.setFill(Color.DARKMAGENTA);
        double barCurrent = 50+((barEnd-barStart)*(currentProgress/maxProgress));
        gc.fillRect(barStart, screenHeight-80, barCurrent, screenHeight-50);
    }
    
    @Override
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        
    }

    @Override
    public void handleMouseEvent(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateUI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
