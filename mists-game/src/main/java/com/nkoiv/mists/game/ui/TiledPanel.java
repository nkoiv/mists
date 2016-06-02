/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.gamestate.GameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Derivative of the Tiled Window, Tiled Panel groups together
 * a bunch of UI components with a background image composed of recurring images.
 * The default Image[] Tiled Panel takes should be arranged as follows:
 *                  [0], [3], [12] and [15] are corners
 * [**][**][**][**] [1] and [2] are top sides
 * [* ][  ][  ][ *] [4] and [8] are left sides
 * [* ][  ][  ][ *] [7] and [11] are right sides
 * [**][**][**][**] [13] and [14] are bottom sides
 *                  [5], [6], [9] and [10] are central area.
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
        gc.save();
        //Draw the background
        gc.setGlobalAlpha(this.bgOpacity);
        this.renderBackground(gc);
        //Render all the subcomponents so that they are tiled in the window area
        gc.setGlobalAlpha(1);
        tileSubComponentPositions(xOffset, yOffset);
        for (UIComponent sc : this.subComponents) {
            sc.render(gc, sc.getXPosition(), sc.getYPosition());
        }       
        gc.restore();
    }
    
    protected void renderBackground(GraphicsContext gc) {
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
    }
    
}
