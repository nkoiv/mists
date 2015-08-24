/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

/**
 * MainMenuWindow is the actual Main Menu displayed by
 * the MainMenuState.
 * @author nikok
 */
public class MainMenuWindow extends TiledWindow {

    public MainMenuWindow(GameState parent) {
        super(parent, "MainMenu", 220, 220, (Global.WIDTH/2 - 110), 250);
        initializeMenuButtons();
        super.setInteractive(true);
    }
    
    private void initializeMenuButtons() {
        NewGameButton menubutton1 = new NewGameButton(super.getParent().getGame());
        OptionsButton menubutton2 = new OptionsButton();
        QuitButton menubutton3 = new QuitButton("Quit game", 200, 60);
        super.addSubComponent(menubutton1);
        super.addSubComponent(menubutton2);
        super.addSubComponent(menubutton3);
    }
    
    private class NewGameButton extends TextButton {
        private final Game game;
        
        public NewGameButton(Game game) {
            super("New game", 200, 60);
            this.game = game;
        }
        
        @Override
        public void onClick(MouseEvent me) {
            //TODO: Move the gamestate to character creator
            Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
            this.game.moveToState(Game.LOCATION);
            
        }
        
    }
    
    private class OptionsButton extends TextButton {

        public OptionsButton() {
            super("Options", 200, 60);
        }
        
        @Override
        public void onClick(MouseEvent me) {
            //TODO: Open options menu (window?)
            Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());

        }
        
    }
    
}
