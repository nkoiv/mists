/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Mists;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author nikok
 */
public class SplashScreen implements LoadingScreen {

    @Override
    public void render(Canvas centerCanvas, Canvas uiCanvas) {
        GraphicsContext gc = centerCanvas.getGraphicsContext2D();
        GraphicsContext uigc = uiCanvas.getGraphicsContext2D();
        gc.save();
        double screenWidth = centerCanvas.getWidth();
        double screenHeight = centerCanvas.getHeight();
        uigc.clearRect(0, 0, screenWidth, screenHeight);
        gc.setFill(Color.WHITESMOKE);
        //gc.clearRect(0, 0, screenWidth, screenHeight);
        gc.fillRect(0, 0, screenWidth, screenWidth);
        this.drawLoadingBar(gc, screenWidth, screenHeight);        
        gc.restore();
        Mists.logger.info("SplashScreen Render was called!");
    }
    
    private void drawLoadingBar(GraphicsContext gc, double screenWidth, double screenHeight) {
        gc.setStroke(Color.CADETBLUE);
        gc.strokeRect(50, screenHeight-100, screenWidth-50, screenHeight-60);
    }

    @Override
    public void enter() {
        
    }

    @Override
    public void exit() {
        
    }
    
    
    
}
