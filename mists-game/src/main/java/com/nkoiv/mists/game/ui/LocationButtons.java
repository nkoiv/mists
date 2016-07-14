/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import java.util.logging.Level;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.SaveManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author nikok
 */
public class LocationButtons {
    public static int DISPLAY_PATHS = 1;
    
    public static class SaveButton extends TextButton {
    	private final Game game;
    	
    	public SaveButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
    	
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
                Mists.logger.info("Trying to save the game");
                SaveManager.saveGame();
            }
        }
        
    }
    
    public static class LoadButton extends TextButton {
    	private final Game game;
    	
    	public LoadButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
    	
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
                Mists.logger.info("Trying to load the game");
               SaveManager.loadGame();
            }
        }
        
    }
    
    public  static class ResumeButton extends TextButton {
        private final Game game;
        
        public ResumeButton(String name, double width, double height, Game game) {
            super(name, width, height);
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
                Mists.logger.info("Trying to toggle game menu");
                this.game.locControls.toggleSystemMenu();
            }
        }
        
    }
    
    public static class ToggleInventoryButton extends IconButton {
        private final Game game;
        private final InventoryPanel invPanel;
        
        public ToggleInventoryButton(Game game, InventoryPanel invPanel) {
            super("Inventory", 0, 0, Mists.graphLibrary.getImage("inventoryIcon"), Mists.graphLibrary.getImage("inventoryIcon"), Mists.graphLibrary.getImage("buttonSquareBeige"), Mists.graphLibrary.getImage("buttonSquareBeigePressed"));
            this.game = game;
            this.invPanel = invPanel;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_PRESSED) this.pressed = true;
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                this.pressed = false;
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                game.locControls.toggleInventory(this.invPanel);
            }
        }
        
    }
    
    public static class ToggleQuestLogButton extends IconButton {
        private final Game game;
        
        public ToggleQuestLogButton(Game game) {
            super("QuestLog", 0, 0, Mists.graphLibrary.getImage("questlogIcon"), Mists.graphLibrary.getImage("questlogIcon"), Mists.graphLibrary.getImage("buttonSquareBeige"), Mists.graphLibrary.getImage("buttonSquareBeigePressed"));
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_PRESSED) this.pressed = true;
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                this.pressed = false;
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                game.locControls.toggleQuestPanel();
            }
        }
    }
    
        public static class ToggleLocationMenuButton extends IconButton {
        private final Game game;
        
        public ToggleLocationMenuButton(Game game) {
            super("Menu", 0, 0, Mists.graphLibrary.getImage("locationmenuIcon"), Mists.graphLibrary.getImage("locationmenuIcon"), Mists.graphLibrary.getImage("buttonSquareBeige"), Mists.graphLibrary.getImage("buttonSquareBeigePressed"));
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_PRESSED) this.pressed = true;
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                this.pressed = false;
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                game.locControls.toggleSystemMenu();
            }
        }
    }
    
    public static class ToggleCharacterPanelButton extends IconButton {
        private final Game game;
        
        public ToggleCharacterPanelButton(Game game) {
            super("CharacterSheet", 0, 0, Mists.graphLibrary.getImage("charsheetIcon"), Mists.graphLibrary.getImage("charsheetIcon"), Mists.graphLibrary.getImage("buttonSquareBeige"), Mists.graphLibrary.getImage("buttonSquareBeigePressed"));
            this.game = game;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_PRESSED) this.pressed = true;
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                this.pressed = false;
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                game.locControls.toggleCharacterPanel();
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
            if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
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
            if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                this.game.locControls.toggleFlag("drawPaths");
            }
        }
        
        @Override
        public void render(GraphicsContext gc, double xPosition, double yPosition) {
            if (this.game.getCurrentLocation() == null) this.setText("Null");
            else {
                if (this.game.getCurrentLocation().isFlagged("drawPaths")) {
                    this.setText("Paths On");
                    this.gainFocus();
                } else {
                    this.setText("Paths Off");
                    this.loseFocus();
                }
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
            if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
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
           if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
               Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
               this.game.locControls.reduceLightLevel();
           }
        }
        
    }
}
