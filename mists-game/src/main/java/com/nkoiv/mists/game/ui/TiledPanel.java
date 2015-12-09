/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.gamestate.GameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class TiledPanel extends TiledWindow {
    private Image[] images;
    
    
    public TiledPanel(GameState parent, String name, double width, double height, double xPos, double yPos, Image[] images) {
        super(parent, name, width, height, xPos, yPos);
        this.images = images;
    }
    
    
    public void setImageSet(Image[] images) {
        this.images = images;
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
        
        //Draw the background
        double currentX = 0;
        double currentY = 0;
        //First row
        gc.drawImage(this.images[0], this.xPosition, this.yPosition+currentY);
        currentX = currentX + this.images[0].getWidth();
        while (currentX < (this.width-this.images[3].getWidth())) {
            gc.drawImage(this.images[1], this.xPosition+currentX, this.yPosition+currentY);
            currentX = currentX + this.images[1].getWidth();
        }
        gc.drawImage(this.images[3], this.xPosition+(this.width-this.images[3].getWidth()), this.yPosition+currentY);
        currentY = currentY + this.images[0].getHeight();
        //Middle sections
        while (currentY < (this.height-this.images[0].getHeight())) {
            currentX = 0;
            gc.drawImage(this.images[4], this.xPosition, this.yPosition+currentY);
            currentX = currentX + this.images[0].getWidth();
            while (currentX < (this.width-this.images[5].getWidth())) {
                gc.drawImage(this.images[5], this.xPosition+currentX, this.yPosition+currentY);
                currentX = currentX + this.images[5].getWidth();
            }
            gc.drawImage(this.images[7], this.xPosition+(this.width-this.images[7].getWidth()), this.yPosition+currentY);
            currentY = currentY + this.images[4].getHeight();
        }
        //Last row
        currentX = 0;
        gc.drawImage(this.images[12], this.xPosition, this.yPosition+currentY);
        currentX = currentX + this.images[12].getWidth();
        while (currentX < (this.width-this.images[3].getWidth())) {
            gc.drawImage(this.images[13], this.xPosition+currentX, this.yPosition+currentY);
            currentX = currentX + this.images[13].getWidth();
        }
        gc.drawImage(this.images[15], this.xPosition+(this.width-this.images[15].getWidth()), this.yPosition+currentY);
        
        
        //Render all the subcomponents so that they are tiled in the window area
        tileSubComponentPositions(xOffset, yOffset);
        for (UIComponent sc : this.subComponents) {
            sc.render(gc, sc.getXPosition(), sc.getYPosition());
        }
        
    }
    
}
