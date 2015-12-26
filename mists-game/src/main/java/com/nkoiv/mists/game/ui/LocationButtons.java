/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author nikok
 */
public class LocationButtons {
    public static int DISPLAY_PATHS = 1;
    
    
    public LocationButtons() {
        
    }
    
    
    public  static class ResumeButton extends TextButton {
        private final Game game;
        
        public ResumeButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                Mists.logger.info("Trying to toggle game menu");
                this.game.locControls.toggleLocationMenu();
            }
        }
        
    }
    
    public static class ToggleScaleButton extends TextButton {
        private final Game game;
        public ToggleScaleButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                this.game.toggleScale = true;
            }
        }
        
    }
    
    public static class DrawPathsButton extends TextButton {
        private final Game game;
        
        public DrawPathsButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                this.game.locControls.toggleFlag("drawPaths");
            }
        }
        
        @Override
        public void render(GraphicsContext gc, double xPosition, double yPosition) {
            if (this.game.getCurrentLocation().isFlagged("drawPaths")) {
                this.setText("Paths On");
                this.gainFocus();
            } else {
                this.setText("Paths Off");
                this.loseFocus();
            }
            
            super.render(gc, xPosition, yPosition);
        }
        
    }
    
    public static class IncreaseLightlevelButton extends TextButton {
        private final Game game;
        
        public IncreaseLightlevelButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                this.game.locControls.increseLightLevel();
            }
        }
        
    }
    public static class ReduceLightlevelButton extends TextButton {
        private final Game game;
        
        public ReduceLightlevelButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
           if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
               Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
               this.game.locControls.reduceLightLevel();
           }
        }
        
    }
}
