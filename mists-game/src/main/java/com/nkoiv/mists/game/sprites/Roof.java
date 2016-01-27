/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Roof is a type of graphic object that's placed
 * over other graphics when no player is below it.
 * Roofs maybe also be rendered translucent in various situations.
 * 
 * @author nikok
 */
public class Roof {
    private Image image;
    private boolean visible;
    private double transparency;
    private double xCoordinate;
    private double yCoordinate;
    
    public Roof() {

    }
     
    
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        gc.drawImage(image, xCoordinate - xOffset, yCoordinate - yOffset);
    }
       
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double getTransparency() {
        return transparency;
    }

    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }
       
}
