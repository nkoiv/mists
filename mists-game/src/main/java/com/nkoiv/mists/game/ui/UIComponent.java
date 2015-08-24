/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 * UIComponent is the interface for anything drawn on the UI layer
 * Everything visible on the UI should implement this.
 * @author nikok
 */
public interface UIComponent {
 
    public void render(GraphicsContext gc, double xPosition, double yPosition);
    
    public double getWidth();
    public double getHeight();
    public double getXPosition();
    public double getYPosition();
    
    public void setPosition(double xPos, double yPos);
    
    public void onClick(MouseEvent me);
    
    public String getName();
    
}
