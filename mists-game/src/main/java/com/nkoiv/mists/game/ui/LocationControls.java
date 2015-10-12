/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.Location;
import java.util.logging.Level;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author nikok
 */
public class LocationControls {
    public static int DISPLAY_PATHS = 1;
    
    
    public LocationControls() {
        
    }
    
    public static class DrawPathsButton extends TextButton {
        private Game game;
        
        public DrawPathsButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
        
        @Override
        public void onClick(MouseEvent me) {
            Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
            this.game.currentLocation.toggleFlag("drawPaths");
            if (this.game.currentLocation.isFlagged("drawPaths")) {
                this.setText("Paths On");
            } else {
                this.setText("Paths Off");
            }
        }
        
    }
}
