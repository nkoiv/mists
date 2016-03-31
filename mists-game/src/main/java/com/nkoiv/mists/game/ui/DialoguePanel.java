/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.dialogue.Link;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * 
 * @author nikok
 */
public class DialoguePanel extends TextPanel {
    private Dialogue dialogue;
    private double maxRowCount;
    private int maxCharWidth = 60;
    private double textHeight = 20.0; //pixelheight for text...
    
    private ArrayList<DialogueLinkButton> linkButtons;
    private String currentCardText;
    
    private static double defaultWidth = Mists.WIDTH - 200;
    private static double defaultHeight = (Mists.HEIGHT/2);
    private static String defaultImageSet = "panelBeige";
    private static double defaultFontSize = 20.0;
    
    public DialoguePanel (GameState parent) {
        this(parent, "Dialogue", defaultWidth, defaultHeight, 100, Mists.HEIGHT/2, Mists.graphLibrary.getImageSet(defaultImageSet));
    }
    
    public DialoguePanel(GameState parent, String name, double width, double height, double xPos, double yPos, Image[] images) {
        super(parent, name, width, height, xPos, yPos, images);
        this.linkButtons = new ArrayList<>();
        
        CloseButton cb = new CloseButton(this, this.width-20, 5);
        this.closeButton = cb;
    }
    
    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
        this.generateLinks();
        this.formatCurrentCardText();
    }
    
    public void moveToCard(int cardNumber) {
        if (this.dialogue.moveToCard(cardNumber) == true)  {
            this.generateLinks();
            this.formatCurrentCardText();
        } else {
            if (this.parent instanceof LocationState) {
                ((LocationState)this.parent).closeDialogue();
            }
        }
        
    }
    
    private void formatCurrentCardText() {
        if (this.dialogue == null) return;
        this.currentCardText = Toolkit.breakLines(this.dialogue.getCurrentCard().getText(), maxCharWidth);
    }
    
    private void generateLinks() {
        if (this.dialogue == null) return;
        this.linkButtons.clear();
        int linkCount = this.dialogue.getCurrentCard().getLinks().size();
        
        for (int i = 0; i < linkCount; i++) {
            DialogueLinkButton dlb = new DialogueLinkButton(this, this.dialogue.getCurrentCard().getLinks().get(i));
            dlb.setPosition(this.xPosition+50, this.yPosition+this.height-((i+1)*50));
            this.linkButtons.add(dlb);
        }
        
        Mists.logger.info("Dialogue links generated");
    }
    
    private void renderCard(GraphicsContext gc, double xOffset, double yOffset) {
        gc.fillText(this.currentCardText, xPosition+this.margin, yPosition+this.margin+15);
        
    }
    
    private void renderDialogueOptions(GraphicsContext gc, double xPosition, double yPosition) {
        int linkCount = this.dialogue.getCurrentCard().getLinks().size();
        //Mists.logger.info("Rendering "+linkCount+" chat options");
        for (int i = 0; i < linkCount; i++) {
            //Mists.logger.info("rendering Link "+i);
            this.linkButtons.get(i).render(gc);
        }
        
    }
    
    @Override
    public void render(GraphicsContext gc, double xOffset, double yOffset) {
        //Mists.logger.info("Rendering dialoguepanel");
        gc.save();
        gc.setGlobalAlpha(this.bgOpacity);
        //Mists.logger.info("Rendering background");
        this.renderBackground(gc);
        //Mists.logger.info("background rendered");
        gc.restore();
        if (this.dialogue != null) {
            //Mists.logger.info("dialogue wasn't null, rendering card");
            this.renderCard(gc, xPosition, yPosition);
            //Mists.logger.info("card rendered, rendering options");
            this.renderDialogueOptions(gc, xPosition, yPosition);   
            //Mists.logger.info("options rendered");
        }        
        if (this.closeButton != null)
            this.closeButton.render(gc, xPosition+closeButton.xPosition, yPosition+closeButton.yPosition);
        //Mists.logger.info("Dialoguepanel rendered");
    }
    
    @Override
    public void handleMouseEvent(MouseEvent me) {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        double clickX = me.getX();
        double clickY = me.getY();
        try {
            for (DialogueLinkButton lb : linkButtons) {
            double uicHeight = lb.getHeight();
            double uicWidth = lb.getWidth();
            double uicX = lb.getXPosition();
            double uicY = lb.getYPosition();
            //Check if the click landed on the ui component
            if (clickX >= uicX && clickX <= (uicX + uicWidth)) {
                if (clickY >= uicY && clickY <= uicY + uicHeight) {
                    lb.handleMouseEvent(me);
                }
            }

            }
        } catch (ConcurrentModificationException e) {
            //Player clicked panel midrender - nothing too bad.
        }
        if (this.closeButton != null) {
            if (clickX >= xPosition+closeButton.getXPosition() && clickX <= (xPosition+closeButton.getXPosition() + closeButton.getWidth())) {
                if (clickY >= yPosition+closeButton.getYPosition() && clickY <= yPosition+closeButton.getYPosition() + closeButton.getHeight()) {
                    Mists.logger.info("CloseButton was pressed");
                    closeButton.handleMouseEvent(me);
                }
            }
        }
        
    }
    
        @Override
    public void handleMouseDrag(MouseEvent me, double lastDragX, double lastDragY) {
        if (this.draggable) {
            Mists.logger.info("Mouse drag: "+me.getX()+","+me.getY());
            this.movePosition(me.getX()-lastDragX, me.getY()-lastDragY);
            for (DialogueLinkButton lb : this.linkButtons) {
                lb.movePosition(me.getX()-lastDragX, me.getY()-lastDragY);
            }
        }
    }
    
    public Dialogue getDialogue() {
        return this.dialogue;
    }
    
    /**
     * DialogueLinkButton is the button
     * that's used to traverse cards in a dialogue
     */
    private class DialogueLinkButton extends UIComponent {
        private DialoguePanel parent;
        private Link link;
        private Image[] bgImages;
        private Image[] bgImagesPressed;
        private boolean pressed;
        private static final double textMargin = 10;
        
        /**
         * Constructor that uses the defaultButtonGraphics(from Mists.graphLibrary)
         * @param parent DialoguePanel this button is housed on
         * @param link Link this button represents
         */
        public DialogueLinkButton(DialoguePanel parent, Link link) {
            this(parent, link, parent.getWidth()-100, Mists.graphLibrary.getImageSet("buttonLongBeige"), Mists.graphLibrary.getImageSet("buttonLongBeigePressed"));
        }
        
        public DialogueLinkButton(DialoguePanel parent, Link link, double width, Image[] bgImages, Image[] bgImagesPressed) {
            this.parent = parent;
            this.link = link;
            this.bgImages = bgImages;
            this.bgImagesPressed = bgImagesPressed;
            this.width = width;
            this.height = bgImages[0].getHeight();
            this.name = "DialogueLink: ["+link.getText()+"]";
        }
        
        
        protected void buttonPress() {
            Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
            if (this.parent.getDialogue().getOwner() == null || this.parent.getDialogue().getTalker() == null) {
                Mists.logger.warning("Talker or Owner lacking at dialogue!");
            } else {
                boolean reqsMet = this.link.LinkRequirementsMet(this.parent.getDialogue().getOwner(), this.parent.getDialogue().getTalker());
                if (reqsMet) {
                    this.parent.moveToCard(this.link.getDestinationCardID());
                } else {
                    Mists.logger.info("Tried to click link in dialogue, but requirements not met.");
                }
            }
        }

        @Override
        public void render (GraphicsContext gc, double xPosition, double yPosition) {
            gc.save();
            //Mists.logger.info("Rendering link button background");
            this.renderBackground(gc, xPosition, yPosition);
            //Mists.logger.info("Rendering link button text");
            this.renderText(gc, xPosition, yPosition);
            gc.restore();
        }

        private void renderText (GraphicsContext gc, double xPosition, double yPosition) {
            gc.setFont(Mists.fonts.get("alagard"));
            gc.setGlobalAlpha(1);
            gc.setFill(Color.BLACK);
            gc.fillText(this.link.getText(), xPosition+textMargin, yPosition+height-textMargin);
        }

        /**
         * Render the background of the button.
         * Buttons Images should be an array of images
         * where the first [0] is the left side, and
         * the [3] is the right side. [1] and [2] are
         * alternating center images
         * @param gc GraphicsContext to render on
         * @param xPosition xPosition of the button
         * @param yPosition yPosition of the button
         */
        private void renderBackground (GraphicsContext gc, double xPosition, double yPosition) {
            double yDifferenceOnPressedButton = (this.bgImages[0].getHeight()-this.bgImagesPressed[0].getHeight());
            //Render first BGtile
            if (pressed) gc.drawImage(this.bgImagesPressed[0], xPosition, yPosition+yDifferenceOnPressedButton);
            else gc.drawImage(this.bgImages[0], xPosition, yPosition);

            //Render the tiles up to the last one
            double currentDrawX = xPosition+bgImages[0].getWidth();
            while (currentDrawX < (this.xPosition+this.width-bgImagesPressed[0].getWidth())) {
                if (pressed) gc.drawImage(bgImagesPressed[1], currentDrawX, yPosition+yDifferenceOnPressedButton);
                else gc.drawImage(bgImages[1], currentDrawX, yPosition);
                currentDrawX = currentDrawX + bgImages[1].getWidth();
                if (bgImages[1].getWidth() < 1) break;
            }

            //Render the last BGtile
            if (pressed) gc.drawImage(this.bgImagesPressed[3], xPosition+this.width-this.bgImagesPressed[3].getWidth(), yPosition+yDifferenceOnPressedButton);
            else gc.drawImage(this.bgImages[3], xPosition+this.width-this.bgImagesPressed[3].getWidth(), yPosition);
        }
        
        public void render(GraphicsContext gc) {
            this.render(gc, this.xPosition, this.yPosition);
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_PRESSED && me.getButton() == MouseButton.PRIMARY) {
                this.pressed = true;
            }
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED && me.getButton() == MouseButton.PRIMARY) {
                this.pressed = false;
            }

            if ((me.getEventType() == MouseEvent.MOUSE_CLICKED) && me.getButton() == MouseButton.PRIMARY) {
                this.buttonPress();
                me.consume();
            }
            
        }

    }
    
}
