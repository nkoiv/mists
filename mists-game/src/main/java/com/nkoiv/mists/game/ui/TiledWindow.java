/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Window is the basic UI component. It acts as a collection of other UI stuff.
 * Windows can be interactive (reserving input) or simply static.
 * @author nikok
 */
public class TiledWindow extends UIComponent{
    
    protected GameState parent;
    protected boolean interactive;
    protected ArrayList<UIComponent> subComponents;
    protected int currentButton;
    protected Color bgColor;
    protected double bgOpacity;
    protected double margin;
    
    public TiledWindow (GameState parent, String name, double width, double height, double xPos, double yPos) {
        this.parent = parent;
        this.name = name;
        this.width = width;
        this.height = height;
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.subComponents = new ArrayList<>();
        this.margin = 10;
        this.bgOpacity = 0.3;
        this.bgColor = Color.BLACK;
                
    }
    
    public void close() {
        this.parent.getUIComponents().remove(this.name);
    }
    
    public void addSubComponent(UIComponent uiComp) {
        this.subComponents.add(uiComp);
    }
    
    public ArrayList<UIComponent> getSubComponents() {
        return this.subComponents;
    }
    
    public void setInteractive(boolean i) {
       this.interactive = i;
    }
    
    public boolean isInteractive() {
        return this.interactive;
    }
    
    public void setMargin(double margin) {
        this.margin = margin;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    public void setHeight(double height) {
        this.height = height;
    }
    
    public GameState getParent() {
        return this.parent;
    }
    
    /**
     * Resizes the window to fit in the given GC.
     * First the window is moved left/up if it would clip outside the canvas (at most to 0,0).
     * If the window is still too large, it's downsized to the width and height of the canvas.
     * @param gc GraphicsContext to resize to
     */
    protected void resizeToFit(GraphicsContext gc) {
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        if ((this.width + this.xPosition) > canvasWidth) {
            this.xPosition = xPosition+ (canvasWidth - this.width);
            if (this.xPosition < 0) this.xPosition = 0;
        }
        if ((this.height + this.xPosition) > canvasHeight) {
            this.yPosition = yPosition+ (canvasHeight - this.height);
            if (this.yPosition < 0) this.yPosition = 0;
        }
        if (this.width > canvasWidth) this.width = canvasWidth;
        if (this.height > canvasHeight) this.height = canvasHeight;
            
        
    }
    
    /**
     * Tile the subcomponents of this window to
     * fit inside the window, reserving Offset amounts
     * of padding on the sides of the window
     * @param xOffset upper left corner of the first subcomponent
     * @param yOffset upper left corner of the first subcomponent
     */
    protected void tileSubComponentPositions (double xOffset, double yOffset) {
        double currentXPos = this.xPosition + xOffset;
        double currentYPos = this.yPosition + this.margin + yOffset;
        double widthOfRow = 0;
        double rowHeight = 0;
        for (UIComponent sc : this.subComponents) {
            widthOfRow = widthOfRow + sc.getWidth() + this.margin;
            rowHeight = sc.getHeight();
            if (widthOfRow > this.getWidth()) {
                //Move a row down
                currentYPos = currentYPos+rowHeight+this.margin;
                //Start from the beginning of the row again
                currentXPos = this.xPosition + this.margin + xOffset;
                //Row width is now just this component
                widthOfRow = sc.getWidth();
                }
            sc.setPosition(currentXPos+(widthOfRow-sc.getWidth()), currentYPos);
        }
    }
    /**
    * Render the window on the given graphics context
    * The Subcomponents are drawn in turn, tiled to new row whenever needed
    * @param  gc GraphicsContext to render the window on 
    * @param xOffset xPosition offset for the window
    * @param yOffset yPosition offset for the window
    */
    
    @Override
    public void render(GraphicsContext gc, double xOffset, double yOffset) {
        //Optional resize
        //this.resizeToFit(gc);
        
        //Draw the background window
        gc.save();
        gc.setGlobalAlpha(this.bgOpacity);
        gc.setFill(bgColor);
        gc.fillRect(this.xPosition, this.yPosition,this.width, this.height);
        gc.restore();
        
        //Render all the subcomponents so that they are tiled in the window area
        tileSubComponentPositions(xOffset, yOffset);
        for (UIComponent sc : this.subComponents) {
            sc.render(gc, sc.getXPosition(), sc.getYPosition());
        }
        
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * handleKeyPress is called if the window is interactive.
     * The keyboard input is directed to this frame, and possibly away from elsewhere.
     * @param pressedButtons Buttons the user has pressed
     * @param releasedButtons Buttons the user has released
     */
    public void handleKeyPress(ArrayList<String> pressedButtons, ArrayList<String> releasedButtons) {
        if(this.interactive) {
            
        }
    }
    
    @Override
    public void handleMouseEvent(MouseEvent me) {
        //Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        double clickX = me.getX();
        double clickY = me.getY();
        for (UIComponent uic : subComponents) {
            double uicHeight = uic.getHeight();
            double uicWidth = uic.getWidth();
            double uicX = uic.getXPosition();
            double uicY = uic.getYPosition();
            //Check if the click landed on the ui component
            if (clickX >= uicX && clickX <= (uicX + uicWidth)) {
                if (clickY >= uicY && clickY <= uicY + uicHeight) {
                    uic.handleMouseEvent(me);
                }
            }
            
        }
    }
    
}
