/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Window is the basic UI component. It acts as a collection of other UI stuff.
 * Windows can be interactive (reserving input) or simply static.
 * @author nikok
 */
public class Window implements UIComponent{
    
    private boolean interactive;
    private ArrayList<UIComponent> subComponents;
    private int currentButton;
    private double xPosition;
    private double yPosition;
    private double width;
    private double height;
    
    public Window (double width, double height, double xPos, double yPos) {
        this.width = width;
        this.height = height;
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.subComponents = new ArrayList<>();
    }
    
    public void addMenuButton(TextButton mb) {
        if (mb.getWidth() <= this.getWidth()) {
            this.subComponents.add(mb);
        } else {
            Mists.logger.warning("Tried to add button ["+mb.toString()+"] to Window, but it wouldnt fit");
        }
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
    
    public void setWidth(double width) {
        this.width = width;
    }
    public void setHeight(double height) {
        this.height = height;
    }
    
    @Override
    public double getWidth() {
        return this.width;
    }
    @Override
    public double getHeight() {
        return this.height;
    }
    
    /**
     * Resizes the window to fit in the given GC.
     * First the window is moved left/up if it would clip outside the canvas (at most to 0,0).
     * If the window is still too large, it's downsized to the width and height of the canvas.
     * @param gc GraphicsContext to resize to
     */
    private void resizeToFit(GraphicsContext gc) {
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
    * Render the window on the given graphics context
    * The Subcomponents are drawn in turn, tiled to new row whenever needed
    * @param  gc GraphicsContext to render the window on 
    * @param xPos xPosition offset for the window
    * @param yPos yPosition offset for the window
    */
    
    @Override
    public void render(GraphicsContext gc, double xPos, double yPos) {
        //Optional resize
        this.resizeToFit(gc);
        
        //Render all the subcomponents so that they are tiled in the window area
        double currentXPos = this.xPosition + xPos;
        double currentYPos = this.yPosition + yPos;
        double widthOfRow = 0;
        double lastComponentHeight = 0;
        for (UIComponent sc : this.subComponents) {
            widthOfRow = widthOfRow + sc.getWidth();
            lastComponentHeight = sc.getHeight();
            sc.render(gc, currentXPos, currentYPos);            
        }
        
    }
    
    /**
     * handleKeyPress is called if the window is interactive.
     * The keyboard input is directed to this frame, and possibly away from elsewhere.
     * @param pressedButtons
     * @param releasedButtons 
     */
    public void handleKeyPress(ArrayList<String> pressedButtons, ArrayList<String> releasedButtons) {
        if(this.interactive) {
            
        }
    }
    
    @Override
    public void onClick() {
        
    }


    
  
}
