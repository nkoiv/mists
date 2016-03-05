/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.dialogue.Card;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.dialogue.Link;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class DialoguePanel extends TextPanel {
    private final Dialogue dialogue;
    private double maxRowCount;
    private int maxCharWidth;
    private double textHeight = 20.0; //pixelheight for text...
    
    protected ArrayList<DialogueLinkButton> linkButtons;
    
    private static double defaultWidth = Mists.WIDTH - 200;
    private static double defaultHeight = (Mists.HEIGHT/2)-100;
    private static String defaultImageSet = "panelBeige";
    private static double defaultFontSize = 20.0;
    
    public DialoguePanel (GameState parent, Dialogue dialogue) {
        this(parent, dialogue, "Dialogue", defaultWidth, defaultHeight, 100, Mists.HEIGHT/2, Mists.graphLibrary.getImageSet(defaultImageSet));
    }
    
    public DialoguePanel(GameState parent, Dialogue dialogue, String name, double width, double height, double xPos, double yPos, Image[] images) {
        super(parent, name, width, height, xPos, yPos, images);
        this.dialogue = dialogue;
    }
    
    private void renderCard(GraphicsContext gc, double xOffset, double yOffset) {
        Card c = dialogue.getCurrentCard();
        String newTextLine = c.getText();
        if (newTextLine.length() > maxCharWidth) {
            String[] newText = Toolkit.splitStringIntoLines(newTextLine, maxCharWidth);
            int y = 15;
            for (int i = 0; i < newText.length; i++) {
                if (i>=this.maxRowCount) break;
                gc.fillText(newText[i], xPosition+this.margin, yPosition+this.margin+y);
                y+=15;
            }
        } else {
            gc.fillText(newTextLine, xPosition+this.margin, yPosition+this.margin+15);
        }
        
    }
    
    private void renderDialogueOptions(GraphicsContext gc, double xPosition, double yPosition) {
        int linkCount = this.dialogue.getCurrentCard().getLinks().size();
        double yOffset = margin;
        margin+= (this.linkButtons.get(0).getHeight() * linkCount);
        for (int i = 0; i < linkCount; i++) {
            this.linkButtons.get(i).render(gc, xPosition, yPosition+this.height-yOffset);
            margin-=this.linkButtons.get(0).getHeight();
        }
        
    }
    
    @Override
    public void render(GraphicsContext gc, double xOffset, double yOffset) {
        gc.save();
        gc.setGlobalAlpha(this.bgOpacity);
        this.renderBackground(gc);
        gc.restore();
        this.renderCard(gc, xPosition, yPosition);
        this.renderDialogueOptions(gc, xPosition, yPosition);
        if (this.closeButton != null) this.closeButton.render(gc, xPosition+closeButton.xPosition, yPosition+closeButton.yPosition);
        
    }
    
    private class DialogueLinkButton extends UIComponent {
    private Link link;
    private Image[] bgImages;
    private Image[] bgImagesPressed;
    private boolean pressed;
    
    private double width;
    
    public DialogueLinkButton(Link link, double width, Image[] bgImages, Image[] bgImagesPressed) {
        this.link = link;
        this.bgImages = bgImages;
        this.bgImagesPressed = bgImagesPressed;
        this.width = width;
    }
    
    @Override
    public void render (GraphicsContext gc, double xPosition, double yPosition) {
        this.renderBackground(gc, xPosition, yPosition);
        //this.renderText(gc, xPosition, yPosition);
    }
    
    private void renderBackground (GraphicsContext gc, double xPosition, double yPosition) {
        Image[] currentImages;
        //Render first BGtile
        if (pressed) gc.drawImage(this.bgImagesPressed[0], xPosition, yPosition+(this.bgImages[0].getHeight()-this.bgImagesPressed[0].getHeight()));
        else gc.drawImage(this.bgImages[0], xPosition, yPosition);
        double currentDrawX = xPosition+bgImages[0].getWidth();
        while (currentDrawX < this.width) {
            //Draw the tiles up to the last one
            if (pressed) gc.drawImage(bgImagesPressed[1], currentDrawX, yPosition);
            else gc.drawImage(bgImages[1], currentDrawX, yPosition);
            currentDrawX=+bgImages[0].getWidth();
        }
        
        
    }
    
    }
    
}
