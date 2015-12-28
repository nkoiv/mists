/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * IconButton is a button with an image on it.
 * Normal use of IconButton is to extend it and
 * overwrite the onClick() with whatever the button should do
 * @author nikok
 */
public class IconButton extends UIComponent {
    protected long lastClickTime;
    protected Image icon;
    protected Image altIcon;
    protected boolean drawAlt;
    protected boolean pressed;
    private Rectangle background;
    private Image bgImage;
    private Image bgImagePressed;
    protected double alpha;

    public IconButton(String name, double xPosition, double yPosition, Image icon, Image altIcon, Image bgImage, Image bgImagePressed) {
        this(name, bgImage.getWidth(), bgImage.getHeight(), xPosition, yPosition, icon, altIcon);
        this.bgImage = bgImage;
        this.bgImagePressed = bgImagePressed;
    }
    
    public IconButton(String name, double width, double height, double xPosition, double yPosition, Image icon, Image altIcon) {
        this.name = name;
        background = new Rectangle(width, height);
        background.setOpacity(0.6);
        background.setFill(Color.BLACK);
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.icon = icon;
        this.altIcon = altIcon; 
    }
        
    @Override
    public void render (GraphicsContext gc, double xPosition, double yPosition) {
         if (this.alpha != 0) {
            gc.save();
            gc.setGlobalAlpha(alpha);
        }
        this.renderBackground(gc, xPosition, yPosition);
        this.renderCenteredIcon(gc, xPosition, yPosition);
        if (this.alpha != 0) gc.restore();
    }
    
    private void renderBackground (GraphicsContext gc, double xPosition, double yPosition) {
       
        if (this.bgImage == null || this.bgImagePressed == null) {
            gc.save();
            gc.setGlobalAlpha(background.getOpacity());
            gc.setFill(background.getFill());
            gc.fillRect(xPosition, yPosition, background.getWidth(), background.getHeight());
            gc.restore();
        } else {
            if (this.pressed) gc.drawImage(this.bgImagePressed, xPosition, yPosition+(this.bgImage.getHeight()-this.bgImagePressed.getHeight()));
            else gc.drawImage(this.bgImage, xPosition, yPosition);
        }
    }
    
    private void renderCenteredIcon (GraphicsContext gc, double xPosition, double yPosition) {
        double xOffset = 0;
        double yOffset = 0;
        Image img;
        if (this.drawAlt) img = this.altIcon;
        else img = this.icon;
        
        if (this.background.getWidth() != img.getWidth()) xOffset = (this.background.getWidth() - img.getWidth())/2;
        if (this.background.getHeight() != img.getHeight()) yOffset = (this.background.getHeight() - img.getHeight())/2;
        
        gc.drawImage(img, xPosition+xOffset, yPosition+yOffset);
        
    }
    
    public void setIcon(Image i) {
        this.icon = i;
    }
    
    public void setIcons(Image icon, Image altIcon) {
        this.icon = icon;
        this.altIcon = altIcon;
        background = new Rectangle(icon.getWidth(), icon.getHeight());
    }
    
    public void gainFocus() {
        this.background.setFill(Color.ORANGERED);
        this.drawAlt = true;
    }
    
    public void loseFocus() {
        this.background.setFill(Color.BLACK);
        this.drawAlt = false;
    }
    

    
    @Override
    public double getWidth() {
        return this.background.getWidth();
    }
    
    @Override
    public double getHeight() {
        return this.background.getHeight();
    }
    
    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
    
    @Override
    public void handleMouseEvent(MouseEvent me) {
        if (me.getEventType() == MouseEvent.MOUSE_RELEASED && me.getButton() == MouseButton.PRIMARY) {
            this.lastClickTime = System.currentTimeMillis();
            this.buttonPress();
        }
    }
    
    private void buttonPress() {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
    }
    
    @Override
    public String toString() {
        String description = ("Button, ["+this.name
                +"], size "+this.background.getWidth()+","+this.background.getHeight());
        return description;
    }


    
}
