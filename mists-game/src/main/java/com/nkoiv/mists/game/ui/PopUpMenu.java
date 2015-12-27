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
    private MenuItem[] menuItems;
    
    public PopUpMenu (GameState parent) {
        this.parent = parent;
    }
    
    public void close() {
        this.parent.getUIComponents().remove(this.name);
    }
    
    @Override
    public void render(GraphicsContext gc, double xPosition, double yPosition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void handleMouseEvent(MouseEvent me) {
        double clickX = me.getX();
        double clickY = me.getY();
        for (MenuItem menuitem : this.menuItems) {
            double itemHeight = menuitem.getHeight();
            double itemWidth = menuitem.getWidth();
            double itemX = menuitem.getXPosition();
            double itemY = menuitem.getYPosition();
            //Check if the click landed on the ui component
            if (clickX >= itemX && clickX <= (itemX + itemWidth)) {
                if (clickY >= itemY && clickY <= itemY + itemHeight) {
                    menuitem.handleMouseEvent(me);
                }
            }
            
        }
    }

    @Override
    public String getName() {
        String s = "[PopupMenu:"+this.name+"]";
        return s;
    }
    
    private static class MenuItem  extends UIComponent {
        private String text;
        double textXOffset;
        double textYOffset;
        private double fontSize;
        private PopUpMenu parent;
        
        public MenuItem(PopUpMenu parent) {
            this.parent = parent;
            this.fontSize = -1;
        }

        @Override
        public void render(GraphicsContext gc, double xPosition, double yPosition) {
            //MenuItem text is centered on its area, Scaling text to if the area IF NEEDED
            if (this.fontSize <= 0) {
                this.updateFontSize(gc.getFont());
                this.updateTextOffset(gc.getFont());
            }
            gc.save();
            gc.setFont(Font.font(gc.getFont().getName(), this.fontSize));
            gc.strokeText(this.text, this.xPosition+this.textXOffset, this.yPosition+this.textYOffset);
            gc.restore();
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
