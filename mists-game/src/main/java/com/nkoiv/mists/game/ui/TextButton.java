/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author nikok
 */
public class TextButton implements UIComponent{ 
    private Text text;
    private double textXOffset;
    private double textYOffset;
    private Rectangle background;

    public TextButton(String name, int width, int height) {
        text = new Text(name);
        text.setFont(Font.font(20));
        text.setFill(Color.RED);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        textXOffset = ((textWidth/2)  - (width/2));
        textYOffset = ((textHeight/2) - (height/2));
        background = new Rectangle(width, height);
        background.setOpacity(0.6);
        background.setFill(Color.BLACK);
    }
        
    @Override
    public void render (GraphicsContext gc, double xPosition, double yPosition) {
        gc.save();
        
        gc.setGlobalAlpha(background.getOpacity());
        gc.setFill(background.getFill());
        gc.fillRect(xPosition, yPosition, background.getWidth(), background.getHeight());
        gc.restore();
        
        gc.setFont(text.getFont());
        gc.setStroke(text.getFill());
        gc.strokeText(text.getText(), xPosition-textXOffset, yPosition+background.getHeight()+textYOffset);
        gc.restore();
    }

    @Override
    public double getWidth() {
        return this.background.getWidth();
    }
    
    @Override
    public double getHeight() {
        return this.background.getWidth();
    }
    
    @Override
    public void onClick() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String toString() {
        String description = ("Button, ["+this.text.getText()
                +"], size "+this.background.getWidth()+","+this.background.getHeight());
        return description;
    }
    
}
