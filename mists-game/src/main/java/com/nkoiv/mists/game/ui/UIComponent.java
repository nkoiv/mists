/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 * UIComponent is the interface for anything drawn on the UI layer
 * Everything visible on the UI should implement this.
 * @author nikok
 */
public abstract class UIComponent {
    protected String name;
    protected double width;
    protected double height;
    protected double xPosition;
    protected double yPosition;
    
    public void render(GraphicsContext gc, double xPosition, double yPosition) {
        
    }
    
    public double getWidth() {
        return this.width;
    }
    public double getHeight() {
        return this.height;
    }
    public double getXPosition() {
        return this.xPosition;
    }
    public double getYPosition() {
        return this.yPosition;
    }
    
    public void setPosition(double xPos, double yPos) {
        this.xPosition = xPos;
        this.yPosition = yPos;
    }
    public void movePosition(double xChange, double yChange) {
        this.xPosition += xChange;
        this.yPosition += yChange;
    }
    
    public void handleMouseEvent(MouseEvent me) {
        Mists.logger.info(this.name+" was clicked");
    }
    
    public String getName() {
        if (this.name == null) return "Nameless UI component";
        else return this.name;
    }
    
}
