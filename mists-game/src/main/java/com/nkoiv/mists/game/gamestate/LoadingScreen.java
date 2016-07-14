/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.gamestate;

import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author daedra
 */
public class LoadingScreen {
    private String title;
    private double maxProgress;
    private double currentProgress;
    private String currentText;
    private boolean ready;
    
    public LoadingScreen(String loadingScreenTitle, double maxProgress) {
        this.title = loadingScreenTitle;
        this.maxProgress = maxProgress;
    }
    
    public void updateProgress(double progress, String loadingText) {
        this.addProgress(progress);
        this.currentText = loadingText;
        Mists.logger.log(Level.INFO, "Loading screen progress at {0}/{1}: ''{2}''", new Object[]{currentProgress, maxProgress, currentText});
    }
    
    public void addProgress(double progress) {
        this.currentProgress+=progress;
        if (this.currentProgress>=this.maxProgress) {
            this.currentProgress = this.maxProgress;
            this.ready = true;
        }
    }
    
    public void setLoadingText(String loadingText) {
        this.currentText = loadingText;
    }
    
    public String getLoadingText() {
        return this.currentText;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public boolean isReady() {
        return this.ready;
    }
    
    public void render(Canvas gameCanvas, Canvas uiCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        GraphicsContext uigc = uiCanvas.getGraphicsContext2D();
        double screenWidth = gameCanvas.getWidth();
        double screenHeight = gameCanvas.getHeight();        
        gc.clearRect(0, 0, screenWidth, screenHeight);
        uigc.clearRect(0, 0, screenWidth, screenHeight);
        uigc.save();
        uigc.setGlobalAlpha(1);
        uigc.setFill(Color.WHITESMOKE);
        uigc.fillRect(0, 0, screenWidth, screenHeight);
        this.drawTitleText(uigc, screenWidth, screenHeight);
        this.drawLoadingText(uigc, screenWidth, screenHeight);
        this.drawLoadingBar(uigc, screenWidth, screenHeight);
        uigc.restore();
    }
    
    private void drawTitleText(GraphicsContext gc, double screenWidth, double screenHeight) {
        if (this.title == null) return;
        gc.setFont(Font.font("Verdana", 50));
        gc.setFill(Color.CADETBLUE);
        gc.fillText(this.title, 100, 100);
        
    }
    
    private void drawLoadingText(GraphicsContext gc, double screenWidth, double screenHeight) {
        if (this.currentText == null) return;
        gc.setFont(Mists.fonts.get("alagard"));
        gc.setFill(Color.CADETBLUE);
        gc.fillText(this.currentText, 50, screenHeight-210);
        
    }

    private void drawLoadingBar(GraphicsContext gc, double screenWidth, double screenHeight) {
        //TODO:Actually make a progressbar
        double barStart = 100;
        double barWidth = screenWidth-200;
        gc.setFill(Color.BLUEVIOLET);
        gc.fillRect(barStart, screenHeight-200, barWidth, 50);
        gc.setFill(Color.DARKCYAN);
        double barCurrent = barWidth*(this.currentProgress/this.maxProgress);
        gc.fillRect(barStart, screenHeight-200, barCurrent, 50);
    }
    
    
}
