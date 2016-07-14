/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.GameState;

import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author nikok
 */
public class CharacterPanel extends TextPanel {
    private static double defaultWidth = 300;
    private static double defaultHeight = 300;
    private static String defaultTiles = "panelBlue";
    private static String defaultInnerTiles = "panelInsetBlue";
    private PlayerCharacter character;
    
    private TextPanel attributePanel;
    private TextPanel statsPanel;
    
    public CharacterPanel(GameState parent, PlayerCharacter character) {
        super(parent, "Character Panel", defaultWidth, defaultHeight, 60, 50, Mists.graphLibrary.getImageSet(defaultTiles));
        this.character = character;
        generateStatWindows();
        this.closeButton = new CloseButton(this, this.width-20, 5);
    }
    
    
    private void generateStatWindows() {
        this.attributePanel = new TextPanel(parent, "Attributes", this.width/2, this.height/2, this.width/2, 0, Mists.graphLibrary.getImageSet(defaultInnerTiles));
        this.statsPanel = new TextPanel(parent, "Stats", this.width, this.height/2, 0, this.height/2, Mists.graphLibrary.getImageSet(defaultInnerTiles));
    }
    
    private void updateAttributePanel() {
        attributePanel.setPosition(xPosition +this.width/2, yPosition);
        StringBuilder sb = new StringBuilder();
        sb.append("Strength: ");
        sb.append(character.getAttribute("Strength"));
        sb.append("\n");
        sb.append("Agility: ");
        sb.append(character.getAttribute("Agility"));
        sb.append("\n");
        sb.append("Intelligence: ");
        sb.append(character.getAttribute("Intelligence"));
        attributePanel.setText(sb.toString());
    }
    
    private void updateStatsPanel() {
        statsPanel.setPosition(xPosition, yPosition+this.height/2);
        StringBuilder sb = new StringBuilder();
        sb.append("HP: ");
        sb.append(character.getHealth());
        sb.append(" / ");
        sb.append(character.getMaxHealth());
        sb.append("\n");
        sb.append("Speed: ");
        sb.append(character.getAttribute("Speed"));
        sb.append("\n");
        sb.append("Equipped: ");
        if (character.getWeapon() != null) sb.append(character.getWeapon().getName());
        else sb.append("Nothing");
        statsPanel.setText(sb.toString());
    }
    
    @Override
    public void render(GraphicsContext gc, double xOffset, double yOffset) {
        gc.save();
        gc.setGlobalAlpha(this.bgOpacity);
        super.renderBackground(gc);
        gc.restore();
        updateAttributePanel();
        updateStatsPanel();
        attributePanel.render(gc, xOffset, yOffset);
        statsPanel.render(gc, xOffset, yOffset);
        gc.drawImage(character.getSnapshot(), xPosition+50, yPosition+50);
        if (this.closeButton != null) this.closeButton.render(gc, xPosition+this.width-20, yPosition+5);
    }
}
