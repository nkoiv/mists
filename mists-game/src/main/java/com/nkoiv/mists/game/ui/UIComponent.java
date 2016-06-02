/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import java.util.Objects;
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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UIComponent)) return false;
        return ((UIComponent)object).getName().equals(this.name);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.height) ^ (Double.doubleToLongBits(this.height) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.xPosition) ^ (Double.doubleToLongBits(this.xPosition) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.yPosition) ^ (Double.doubleToLongBits(this.yPosition) >>> 32));
        return hash;
    }
    
    @Override
    public String toString() {
        return "UIC: "+this.name;
    }
    
}
