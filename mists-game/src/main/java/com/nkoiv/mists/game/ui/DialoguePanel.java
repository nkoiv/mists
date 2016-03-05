/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.gamestate.GameState;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class DialoguePanel extends TextPanel {
    private Dialogue dialogue;
    private static double defaultWidth;
    private static double defaultHeight;
    private static String defaultImageSet = "panelBeige";
    
    public DialoguePanel (GameState parent, Dialogue dialogue) {
        super(parent, "Dialogue", defaultWidth, defaultHeight, 100, Mists.HEIGHT/2, Mists.graphLibrary.getImageSet(defaultImageSet));
        this.dialogue = dialogue;
    }
    
    public DialoguePanel(GameState parent, Dialogue dialogue, String name, double width, double height, double xPos, double yPos, Image[] images) {
        super(parent, name, width, height, xPos, yPos, images);
        this.dialogue = dialogue;
    }
    
}
