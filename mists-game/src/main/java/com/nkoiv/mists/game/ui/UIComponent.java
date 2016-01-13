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
 * UIComponent is the abstract class for anything drawn on the UI layer
 * Everything visible on the UI should implement this.
 * @author nikok
 */
public abstract class UIComponent implements Comparable<UIComponent>{
    protected int renderZ;
    protected String name;
    protected double width;
    protected double height;
    protected double xPosition;
    protected double yPosition;
    
    protected boolean draggable;
    protected double lastDragStartX;
    protected double lastDragStartY;
    
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
    
    /**
     * RenderZ signifies "how front" the component is drawn.
     * Generally components are drawn in descending order, 
     * with renderZ of less than zero being ignored (not drawn)
     * @return the component depth on screen.
     */
    public int getRenderZ() {
        return this.renderZ;
    }
    
    public boolean isDraggable() {
        return this.draggable;
    }
    
    /**
     * RenderZ signifies "how front" the component is drawn.
     * Generally components are drawn in descending order, 
     * with renderZ of less than zero being ignored (not drawn)
     * @param renderZ How front the component is placed .
     */
    public void setRenderZ(int renderZ) {
        this.renderZ = renderZ;
    }
    
    public void setPosition(double xPos, double yPos) {
        this.xPosition = xPos;
        this.yPosition = yPos;
    }
    public void movePosition(double xChange, double yChange) {
        this.xPosition += xChange;
        this.yPosition += yChange;
    }
    
    public void handleMouseDrag(MouseEvent me, double lastDragX, double lastDragY) {
        if (this.draggable) {
            Mists.logger.info("Mouse drag: "+me.getX()+","+me.getY());
            this.movePosition(me.getX()-lastDragX, me.getY()-lastDragY);
        }
    }
    
    public void handleMouseEvent(MouseEvent me) {
        Mists.logger.info(this.name+" was clicked");
    }
    
    public String getName() {
        if (this.name == null) return "Nameless UI component";
        else return this.name;
    }
    
    @Override
    public int compareTo(UIComponent uic) {
        return (this.renderZ-uic.renderZ);
    }

    
}
