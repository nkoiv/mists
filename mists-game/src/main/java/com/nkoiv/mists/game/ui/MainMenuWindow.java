/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.world.Location;
import java.util.logging.Level;
import javafx.scene.input.MouseEvent;

/**
 * MainMenuWindow is the actual Main Menu displayed by
 * the MainMenuState.
 * @author nikok
 */
public class MainMenuWindow extends TiledPanel {

    public MainMenuWindow(GameState parent) {
        super(parent, "MainMenu", 220, 300, (parent.getGame().WIDTH/2 - 110), 250, Mists.graphLibrary.getImageSet("panelBeige"));
        initializeMenuButtons();
        super.setInteractive(true);
    }
    
    private void initializeMenuButtons() {
        NewGameButton menubutton1 = new NewGameButton(super.getParent().getGame());
        ResumeGameButton menubutton2 = new ResumeGameButton(super.getParent().getGame());
        OptionsButton menubutton3 = new OptionsButton();
        QuitButton menubutton4 = new QuitButton("Quit game", 200, 60);
        super.addSubComponent(menubutton1);
        super.addSubComponent(menubutton2);
        super.addSubComponent(menubutton3);
        super.addSubComponent(menubutton4);
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
            //For now just generate a new location
            PlayerCharacter pocplayer = new PlayerCharacter();
            Creature companion = Mists.creatureLibrary.create("Himmu");
            System.out.println(companion.toString());
            pocplayer.addCompanion(companion);
            pocplayer.addAction(new MeleeAttack());
            game.setPlayer(pocplayer);
            Location newLoc = new Location(this.game.getPlayer());
            this.game.moveToLocation(newLoc);
            this.game.moveToState(Game.LOCATION);
        }
        
    }
    
    private class ResumeGameButton extends TextButton {
        private final Game game;
        
        public ResumeGameButton(Game game) {
            super("Resume game", 200, 60);
            this.game = game;
        }
        
        @Override
        public void onClick(MouseEvent me) {
            Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
            if (this.game.getCurrentLocation() == null) {
                //No game to resume
            } else {
                this.game.moveToState(Game.LOCATION);
            }
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
