/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * TextButton is a simple UI component with only a box and a text.
 * It's going to be phased out mainly by some form of icon-buttons,
 * but it remains a simple button for testing.
 * @author nikok
 */
public class TextButton implements UIComponent{ 
    private Text text;
    private double textXOffset;
    private double textYOffset;
    private Rectangle background;
    private double xPosition;
    private double yPosition;

    public TextButton(String name, double width, double height) {
        this(name, width, height, 0, 0);
    }
    
    public TextButton(String name, double width, double height, double xPosition, double yPosition) {
        text = new Text(name);
        text.setFont(Font.font(20));
        text.setStroke(Color.WHITE);
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
        
        gc.setGlobalAlpha(1);
        gc.setFont(text.getFont());
        gc.setStroke(text.getStroke());
        gc.strokeText(text.getText(), xPosition-textXOffset, yPosition+background.getHeight()+textYOffset);
        gc.restore();
    }

    public void gainFocus() {
        this.background.setFill(Color.ORANGERED);
    }
    
    public void loseFocus() {
        this.background.setFill(Color.BLACK);
    }
    
    @Override
    public double getWidth() {
        return this.background.getWidth();
    }
    
    @Override
    public double getHeight() {
        return this.background.getHeight();
    }
    
    @Override
    public String getName() {
        return this.text.getText();
    }
    
    @Override
    public void setPosition(double xPos, double yPos) {
        this.xPosition = xPos;
        this.yPosition = yPos;
    }
    
    @Override
    public double getXPosition() {
        return this.xPosition;
    }

    @Override
    public double getYPosition() {
        return this.yPosition;
    }
    
    @Override
    public void onClick(MouseEvent me) {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
    }
    
    @Override
    public String toString() {
        String description = ("Button, ["+this.text.getText()
                +"], size "+this.background.getWidth()+","+this.background.getHeight());
        return description;
    }
    
}
