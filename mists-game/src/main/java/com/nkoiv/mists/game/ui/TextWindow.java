/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.gamestate.GameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * TextWindow is a simple UI component with just text in it.
 * @author nikok
 */
public class TextWindow extends UIComponent {

    protected GameState parent;
    protected Color bgColor;
    protected double bgOpacity;
    protected double margin;
    
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
        this.bgOpacity = 0.3;
        this.bgColor = Color.BLACK;
    }
    
    @Override
    public void render(GraphicsContext gc, double xPosition, double yPosition) {
        //Draw the background window
        gc.save();
        gc.setGlobalAlpha(this.bgOpacity);
        gc.setFill(bgColor);
        gc.fillRect(this.xPosition, this.yPosition,this.width, this.height);
        gc.restore();
        //Draw the text
        this.renderText(gc, xPosition, yPosition);
    }

    protected void renderText(GraphicsContext gc, double xPosition, double yPosition) {
        gc.save();
        //gc.setFont(Font.font("Verdana"));
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));
        gc.fillText(this.text, xPosition+this.margin, yPosition+this.margin+15);
        gc.restore();
    }
    
    public void setText(String string) {
        this.text = string;
    }
    
    public String getText() {
        return this.text;
    }

    @Override
    public void handleMouseEvent(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected GameState getParent() {
        return this.parent;
    }
    
    protected Game getGame() {
        return this.parent.getGame();
    }

}
