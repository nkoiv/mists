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
import com.nkoiv.mists.game.GameMode;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeWeaponAttack;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.gamestate.LoadingScreen;
import com.nkoiv.mists.game.world.Location;

import javafx.concurrent.Task;

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
        NewGameButton menubutton1 = new NewGameButton(super.getParent().getGame(), this.parent);
        //ResumeGameButton menubutton2 = new ResumeGameButton(super.getParent().getGame());
        TextButton menubutton2 = new LocationButtons.LoadButton("Load game", 200, 60, Mists.MistsGame);
        OptionsButton menubutton3 = new OptionsButton();
        QuitButton menubutton4 = new QuitButton("Quit game", 200, 60);
        super.addSubComponent(menubutton1);
        super.addSubComponent(menubutton2);
        super.addSubComponent(menubutton3);
        super.addSubComponent(menubutton4);
    }
    
    private class NewGameButton extends TextButton {
        private final Game game;
        private final GameState state;
        
        public NewGameButton(Game game, GameState state) {
            super("New game", 200, 60);
            this.game = game;
            this.state = state;
        }
        
        private void popUpGameModeWindow() {
            GameModeSubMenu menu = new GameModeSubMenu(this.state);
            this.state.addUIComponent(menu);
        }
        
        @Override
        public void buttonPress() {
            Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
            this.popUpGameModeWindow();
        }
        
    }
    
    private class GameModeSubMenu extends TiledPanel {
        
        public GameModeSubMenu(GameState parent) {
            super(parent, "GameModeWindow", 260, 280, (parent.getGame().WIDTH/2 - 130), 250, Mists.graphLibrary.getImageSet("panelBeige"));
            this.generateButtons();
            this.margin = 30;
            this.renderZ = 10;
        }
        
        private void generateButtons() {
            StartSinglePlayerButton sp = new StartSinglePlayerButton(this.parent.getGame(), this);
            StartServerButton ss = new StartServerButton(this.parent.getGame(), this);
            StartClientButton sc = new StartClientButton(this.parent.getGame(), this);
            this.addSubComponent(sp);
            this.addSubComponent(ss);
            this.addSubComponent(sc);
            
        }
        
        private class StartSinglePlayerButton extends TextButton {
            private Game game;
            private GameModeSubMenu parent;
            public StartSinglePlayerButton(Game game, GameModeSubMenu parent) {
                super("Single Player", 200, 60);
                this.game = game;
                this.parent = parent;
            }
            
            private void newGame() {
                game.setLoadingScreen(new LoadingScreen("Starting game", 10));
                Task task = new Task<Void>() {
                    @Override public Void call() {
                        game.newGame();
                        return null;
                    }
                };
                new Thread(task).start();
                
            }
            
            @Override
            public void buttonPress() {
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                this.game.setGameMode(GameMode.SINGLEPLAYER);
                parent.close();
                this.newGame();
            }
            
        }
        
        private class StartServerButton extends TextButton {
            private Game game;
            private GameModeSubMenu parent;
            public StartServerButton(Game game, GameModeSubMenu parent) {
                super("Host Multiplayer", 200, 60);
                this.game = game;
                this.parent = parent;
            }
            
            private void newGame() {
                game.setLoadingScreen(new LoadingScreen("Starting server", 10));
                Task task = new Task<Void>() {
                    @Override public Void call() {
                    PlayerCharacter pocplayer = new PlayerCharacter();
                    //Creature companion = Mists.creatureLibrary.create("Himmu");
                    //System.out.println(companion.toString());
                    //pocplayer.addCompanion(companion);
                    pocplayer.addAction(new MeleeWeaponAttack());
                    game.setPlayer(pocplayer);
                    Location newLoc = Mists.locationLibrary.create(1);
                    game.addLocation(1, newLoc);
                    Mists.logger.info("Starting Location added");
                    game.moveToLocation(1, null);
                    Mists.logger.info("Starting Location added");
                    game.moveToState(Game.LOCATION);
                    game.clearLoadingScreen();
                    Mists.logger.info("Game loading done - loading screen removed");
                    return null;
                }
                };
                new Thread(task).start();
            }
            
            @Override
            public void buttonPress() {
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                this.game.setGameMode(GameMode.SERVER);
                parent.close();
                this.newGame();
            }
            
        }
        
        private class StartClientButton extends TextButton {
            private Game game;
            private GameModeSubMenu parent;
            public StartClientButton(Game game, GameModeSubMenu parent) {
                super("Join Multiplayer", 200, 60);
                this.game = game;
                this.parent = parent;
            }
            
            private void joinGame() {
                game.setLoadingScreen(new LoadingScreen("Connecting to server", 10));
                Task task = new Task<Void>() {
                    @Override public Void call() {
                    PlayerCharacter secondPlayer = new PlayerCharacter("Himmu");
                    secondPlayer.addAction(new MeleeWeaponAttack());
                    game.setPlayer(secondPlayer);
                    /*
                    Location newLoc = new Location(this.game.getPlayer());
                    this.game.moveToLocation(newLoc);
                    */
                    game.moveToState(Game.LOCATION);
                    return null;
                    }
                };
                new Thread(task).start();
            }
            
            @Override
            public void buttonPress() {
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                this.game.setGameMode(GameMode.CLIENT);
                parent.close();
                this.joinGame();
            }
        }
        
    }
    
    private class ResumeGameButton extends TextButton {
        private final Game game;
        
        public ResumeGameButton(Game game) {
            super("Resume game", 200, 60);
            this.game = game;
        }
        
        @Override
        public void buttonPress() {
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
            super("TODO: Options", 200, 60);
        }
        
        @Override
        public void buttonPress() {
            Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        }
        
    }
    
}
