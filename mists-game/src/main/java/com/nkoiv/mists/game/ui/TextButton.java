/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * TextButton is a simple UI component with only a box and a text.
 * It's going to be phased out mainly by some form of icon-buttons,
 * but it remains a simple button for testing.
 * @author nikok
 */
public class TextButton extends UIComponent{ 
    protected Text text;
    protected double textXOffset;
    protected double textYOffset;
    protected Rectangle background;
    protected Color textColor;

    public TextButton(String name, double width, double height) {
        this(name, width, height, 0, 0);
    }
    
    public TextButton(String name, double width, double height, double xPosition, double yPosition) {
        text = new Text(name);
        text.setFont(Mists.fonts.get("alagard"));
        //text.setFont(Font.font(20));
        text.setStroke(Color.WHITE);
        text.setFill(Color.WHITE);
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
        this.renderBackground(gc, xPosition, yPosition);
        gc.restore();
        
        gc.save();
        this.renderText(gc, xPosition, yPosition);
        gc.restore();
    }
    
    protected void renderText(GraphicsContext gc, double xPosition, double yPosition) {
        gc.setGlobalAlpha(1);
        gc.setFont(text.getFont());
        gc.setFill(text.getFill());
        gc.fillText(text.getText(), xPosition-textXOffset, yPosition+background.getHeight()+textYOffset);
    }
    
    protected void renderBackground(GraphicsContext gc, double xPosition, double yPosition) {
        gc.setGlobalAlpha(background.getOpacity());
        gc.setFill(background.getFill());
        gc.fillRect(xPosition, yPosition, background.getWidth(), background.getHeight());
    }
    
    public void setText(Text newText) {
        this.text = newText;
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        textXOffset = ((textWidth/2)  - (this.getWidth()/2));
        textYOffset = ((textHeight/2) - (this.getHeight()/2));
    }
    
    public void setText(String newTextString) {
        Text newText = new Text(newTextString);
        newText.setFont(this.text.getFont());
        newText.setStroke(this.text.getStroke());
        newText.setFill(this.text.getFill());
        this.text = newText;
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        textXOffset = ((textWidth/2)  - (this.getWidth()/2));
        textYOffset = ((textHeight/2) - (this.getHeight()/2));
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
    public void handleMouseEvent(MouseEvent me) {
        if ((me.getEventType() == MouseEvent.MOUSE_CLICKED) && me.getButton() == MouseButton.PRIMARY) {
            this.buttonPress();
            me.consume();
        }
        
    }
    
    public void buttonPress() {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
    }
    
    @Override
    public String toString() {
        String description = ("Button, ["+this.text.getText()
                +"], size "+this.background.getWidth()+","+this.background.getHeight());
        return description;
    }
    
}
