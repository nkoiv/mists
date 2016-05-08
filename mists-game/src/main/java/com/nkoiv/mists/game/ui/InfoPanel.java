/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author nikok
 */
public class InfoPanel extends TextPanel {
    
    public InfoPanel(GameState parent, String name, double width, double height, double xPos, double yPos, Image[] images) {
        super(parent, name, width, height, xPos, yPos, images);
        CloseButton cb = new CloseButton(this, this.width-20, 5);
        this.closeButton = cb;
    }
    
    
    @Override
    public void render(GraphicsContext gc, double xOffset, double yOffset) {
        gc.save();
        gc.setGlobalAlpha(this.bgOpacity);
        this.renderBackground(gc);
        gc.restore();
        
        this.renderText(gc, xPosition, yPosition);
        if (this.closeButton != null) this.closeButton.render(gc, xPosition+closeButton.xPosition, yPosition+closeButton.yPosition);
        gc.restore();
    }
    
    @Override
    protected void renderText(GraphicsContext gc, double xPosition, double yPosition) {
        gc.save();
        gc.setFont(Mists.fonts.get("alagard"));
        gc.setFill(Color.LIGHTGREEN);
        gc.fillText(this.text, xPosition+this.margin, yPosition+this.margin+15);
        gc.restore();
    }
    
}
