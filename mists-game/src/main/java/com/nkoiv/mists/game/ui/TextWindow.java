/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * TextWindow is a simple UI component with just text in it.
 * @author nikok
 */
public class TextWindow extends UIComponent {

    protected GameState parent;
    protected Color bgColor;
    protected double bgOpacity;
    protected double margin;
    
    protected CloseButton closeButton;
    
    protected Font font;
    protected String text;
    
    public TextWindow(GameState parent, String name, double width, double height, double xPos, double yPos){
        this.parent = parent;
        this.name = name;
        this.width = width;
        this.height = height;
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.margin = 10;
        this.bgOpacity = 0.8;
        this.bgColor = Color.BLACK;
    }
    
    @Override
    public void render(GraphicsContext gc, double xPosition, double yPosition) {
        //Draw the background window
        gc.save();
        gc.setGlobalAlpha(this.bgOpacity);
        gc.setFill(bgColor);
        gc.fillRect(this.xPosition, this.yPosition,this.width, this.height);
        //Draw the text
        gc.restore();
        this.renderText(gc, xPosition, yPosition);
        if (this.closeButton != null) this.closeButton.render(gc, xPosition+closeButton.xPosition, yPosition+closeButton.yPosition);
    }

    protected void renderText(GraphicsContext gc, double xPosition, double yPosition) {
        gc.save();
        //gc.setFont(Font.font("Verdana"));
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));
        gc.fillText(this.text, xPosition+this.margin, yPosition+this.margin+15);
        gc.restore();
    }
    
    public void close() {
        this.parent.removeUIComponent(this.name);
    }
    
    public void addCloseButton() {
        CloseButton cb = new CloseButton(this, this.width-20, 5);
        this.closeButton = cb;
    }
    
    public void removeCloseButton() {
        this.closeButton = null;
    }
    
    public void setText(String string) {
        this.text = string;
    }
    
    public String getText() {
        return this.text;
    }

    @Override
    public void handleMouseEvent(MouseEvent me) {
        if ((me.getEventType() == MouseEvent.MOUSE_CLICKED) && me.getButton() == MouseButton.PRIMARY) {
            //Mists.logger.info("Click event at "+this.getName()+", "+me.getX()+"x"+me.getY());
            //Mists.logger.info("X: "+(xPosition+closeButton.xPosition)+" Y:"+(yPosition+closeButton.yPosition));
            if (closeButton == null) return;
            if (me.getX() > xPosition+closeButton.xPosition && me.getX() < xPosition+closeButton.xPosition+closeButton.width
                    && me.getY() > yPosition+closeButton.yPosition && me.getY() < yPosition+closeButton.yPosition+closeButton.height) {
                closeButton.buttonPress();
                Mists.logger.info("Raa");
                me.consume();
            }
            
        }
    }
    
    protected GameState getParent() {
        return this.parent;
    }
    
    protected Game getGame() {
        return this.parent.getGame();
    }
    
    protected class CloseButton extends IconButton {
        private TextWindow tw;
        
        public CloseButton(TextWindow tw, double xPosition, double yPosition) {
            super("CloseButton", 15, 15, xPosition, yPosition, Mists.graphLibrary.getImage("iconCrossBlue"), Mists.graphLibrary.getImage("iconCrossBlue"));
            this.tw = tw;
            
        }
        
        @Override
        protected void buttonPress() {
            this.tw.close();
        }
        
    }

}
