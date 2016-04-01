/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.GameState;

/**
 *
 * @author nikok
 */
public class CharacterPanel extends TextPanel {
    private static double defaultWidth = 300;
    private static double defaultHeight = 300;
    private static String defaultTiles = "panelBlue";
    private PlayerCharacter character;
    
    public CharacterPanel(GameState parent, PlayerCharacter character) {
        super(parent, "Character Panel", defaultWidth, defaultHeight, 50, 50, Mists.graphLibrary.getImageSet(defaultTiles));
        this.character = character;
    }
    
}
