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
 * Text Panel is a simple window with text in it, with the addition
 * of background image, based on an image array.
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
public class TextPanel extends TextWindow {
    private Image[] images;
    
    public TextPanel(GameState parent, String name, double width, double height, double xPos, double yPos, Image[] images) {
        super(parent, name, width, height, xPos, yPos);
        this.images = images;
        this.draggable = true;
    }
    
    
    public void setImageSet(Image[] images) {
        this.images = images;
    }
    
    @Override
    public void render(GraphicsContext gc, double xOffset, double yOffset) {
        gc.save();
        gc.setGlobalAlpha(this.bgOpacity);
        this.renderBackground(gc);
        gc.restore();
        this.renderText(gc, xPosition, yPosition);
        if (this.closeButton != null) this.closeButton.render(gc, xPosition+closeButton.xPosition, yPosition+closeButton.yPosition);
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
