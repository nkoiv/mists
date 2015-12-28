/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.world.util.Toolkit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A context-sensitive PopUp menu, usually
 * drawn at the location of a mouse click.
 * MenuItems are stacked atop one another, and
 * the menu is cleared when one is clicked.
 * 
 * @author nikok
 */
public class PopUpMenu extends UIComponent{
    private GameState parent;
    private MenuButton[] menuButtons;
    private int buttonCount;
    private boolean openUpwards = false;
    
    public PopUpMenu (GameState parent) {
        this.menuButtons = new MenuButton[10];
        this.buttonCount = 0;
        this.parent = parent;
    }
    
    public void close() {
        this.parent.getUIComponents().remove(this.name);
    }
    
    public void addMenuButton(MenuButton mb) {
        if (this.buttonCount >= 10) return;
        this.menuButtons[buttonCount] = mb;
        this.buttonCount++;
        this.updatePositions();
    }
    
    @Override
    public void setPosition(double xCoor, double yCoor) {
        super.setPosition(xCoor, yCoor);
        this.updatePositions();
    }
    
    @Override
    public void movePosition(double xChange, double yChange) {
        super.setPosition(xChange, yChange);
        this.updatePositions();
    }
    
    @Override
    public void render(GraphicsContext gc, double xPosition, double yPosition) {
        for (int i = 0; i < this.buttonCount; i++) {
            this.menuButtons[i].render(gc);
        }
    }
    
    public void setOpenUpwards(boolean s) {
        this.openUpwards = s;
    }
    
    public boolean isOpenUpwards() {
        return this.openUpwards;
    }
    
    /**
     * Update the menu items to expand up or down from
     * the menu position, depending on if openUpwards is
     * true or false 
    */
    private void updatePositions() {
        if (this.menuButtons[0] == null) return;
        this.menuButtons[0].setPosition(this.xPosition, this.yPosition);
        for (int i = 1; i < this.buttonCount; i++) {
            if (this.menuButtons[i] == null) return;
            this.menuButtons[i].setPosition(this.menuButtons[i-1].getXPosition(), this.menuButtons[i-1].getYPosition());
            if (this.openUpwards) this.menuButtons[i].movePosition(0, -(this.menuButtons[i].height+this.menuButtons[i-1].height));
            else this.menuButtons[i].movePosition(0, this.menuButtons[i-1].height);
        }
    }

    @Override
    public void handleMouseEvent(MouseEvent me) {
        double clickX = me.getX();
        double clickY = me.getY();
        for (int i = 0; i < this.buttonCount; i++) {
            double itemHeight = menuButtons[i].getHeight();
            double itemWidth = menuButtons[i].getWidth();
            double itemX = menuButtons[i].getXPosition();
            double itemY = menuButtons[i].getYPosition();
            //Check if the click landed on the ui component
            if (clickX >= itemX && clickX <= (itemX + itemWidth)) {
                if (clickY >= itemY && clickY <= itemY + itemHeight) {
                    menuButtons[i].handleMouseEvent(me);
                }
            }
            
        }
    }

    @Override
    public String getName() {
        String s = "[PopupMenu:"+this.name+"]";
        return s;
    }
    
    public static class MenuButton  extends UIComponent {
        private static Font defaultFont = Mists.fonts.get("alagard");
        protected String text;
        double textXOffset;
        double textYOffset;
        protected double fontSize;
        protected PopUpMenu parent;
        protected boolean displayOnlyWhenAvailable;
        
        public MenuButton(PopUpMenu parent) {
            this.parent = parent;
            this.fontSize = -1;
            this.width = 100;
            this.height = 20;
        }

        @Override
        public void render(GraphicsContext gc, double xPosition, double yPosition) {
            //MenuItem text is centered on its area, Scaling text to if the area IF NEEDED
            //Mists.logger.info("Rendering menubutton "+text+" at "+xPosition+","+yPosition);
            if (this.fontSize <= 0) {
                this.updateFontSize(defaultFont);
                this.updateTextOffset(defaultFont);
            }
            gc.save();
            gc.setGlobalAlpha(0.5);
            gc.setFill(Color.BLACK);
            gc.fillRect(xPosition, yPosition, this.width, this.height);
            gc.setGlobalAlpha(0.5);
            gc.setStroke(Color.WHITE);
            gc.setFont(Font.font(gc.getFont().getName(), this.fontSize));
            gc.strokeText(this.text, xPosition, yPosition);
            gc.restore();
        }
        
        public void render(GraphicsContext gc) {
            this.render(gc, this.xPosition+this.textXOffset, this.yPosition+this.textYOffset);
        }
        
        private void updateFontSize(Font currentFont) {
            double currentFontSize = currentFont.getSize();
            Font scaledFont = Toolkit.scaleFont(this.text, this.width, currentFont);
            this.fontSize = Math.min(currentFontSize, scaledFont.getSize());
        }
        
        private void updateTextOffset(Font currentFont) {
            Text t = new Text(this.text);
            t.setFont(Font.font(currentFont.getName(), this.fontSize));
            this.textXOffset = (this.width - t.getLayoutBounds().getWidth())/2;
            this.textYOffset = (this.height - t.getLayoutBounds().getHeight())/2;
        }


        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED && me.getButton() == MouseButton.PRIMARY) {
                Mists.logger.info(this.getName()+" was clicked");
                if (this.click()) this.parent.close();
            }
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED && me.getButton() == MouseButton.SECONDARY) {
                
            }
        }
        
        /**
         * click() should be overwritten with whatever the
         * button needs to do.
         * @return True if action was successful
         */
        protected boolean click() {
            
            return false;
        }

        @Override
        public String getName() {
            String s = "[MenuItem:"+this.text+"]";
            return s;
        }
    
    }
    
}
