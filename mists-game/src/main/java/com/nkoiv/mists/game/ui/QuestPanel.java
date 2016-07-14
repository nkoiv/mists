/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import java.util.HashMap;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.quests.Quest;
import com.nkoiv.mists.game.quests.QuestManager;
import com.nkoiv.mists.game.quests.QuestTask;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *
 * @author daedra
 */
public class QuestPanel extends TextPanel {

    private static double topMargin = 20;
    private static double defaultWidth = 300;
    private static double defaultHeight= 200;
    private static String defaultPanelImages = "panelBlue";
    private final QuestManager questManager;
    private double rowHeight = 20; //TODO: Scale this from font
    
    public QuestPanel(GameState parent, String name, QuestManager questManager, double width, double height, double xPos, double yPos, Image[] images) {
        super(parent, name, width, height, xPos, yPos, images);
        this.questManager = questManager;
        this.bgOpacity = 0.5;
        this.renderZ = 5;
        CloseButton cb = new CloseButton(this, this.width-20, 5);
        this.closeButton = cb;
    }
    
    public QuestPanel(GameState parent) {
        this(parent, "QuestPanel", Mists.MistsGame.questManager, defaultWidth, defaultHeight, Mists.WIDTH/2, Mists.HEIGHT/2, Mists.graphLibrary.getImageSet(defaultPanelImages));
    }
    
    private void renderQuestText(GraphicsContext gc, double xPosition, double yPosition) {
        int maxRowCount = (int)(this.height / this.rowHeight);
        int currentRow = 1;
        StringBuilder sb = new StringBuilder();
        if (!questManager.getOpenQuests().isEmpty()) {
            gc.setFont(Mists.fonts.get("alagard20"));
            gc.setFill(Color.BISQUE);
            renderTextLine("Open quests", gc, xPosition, yPosition, currentRow, maxRowCount);
            currentRow++;
            currentRow = renderQuests(this.questManager.getOpenQuests(), sb, gc, currentRow, maxRowCount);
        } 
        if (!questManager.getClosedQuests().isEmpty()) {
            gc.setFont(Mists.fonts.get("alagard20"));
            gc.setFill(Color.BISQUE);
            renderTextLine("Closed quests", gc, xPosition, yPosition, currentRow, maxRowCount);
            currentRow++;
            renderQuests(this.questManager.getClosedQuests(), sb, gc, currentRow, maxRowCount);
        }
    }
    
    private int renderQuests(HashMap<Integer, Quest> quests, StringBuilder sb, GraphicsContext gc, int currentRow, int maxRowCount) {
        for (int questID : quests.keySet()) {
            gc.setFill(Color.DARKBLUE);
            gc.setFont(Mists.fonts.get("alagard20"));
            String title = quests.get(questID).getTitle();
            renderTextLine(title, gc, xPosition, yPosition, currentRow, maxRowCount);
            currentRow++;
            for (QuestTask qt : quests.get(questID).getTasks()) {
                if (qt.isDone()) gc.setFill(Color.LIME);
                else gc.setFill(Color.CYAN);
                gc.setFont(Mists.fonts.get("alagard12"));
                sb = new StringBuilder();
                sb.append(qt.getDescription());
                sb.append(": ");
                sb.append(qt.getCurrentCompletion());
                sb.append(" / ");
                sb.append(qt.getRequiredCompletion());
                renderTextLine(sb.toString(), gc, xPosition, yPosition, currentRow, maxRowCount);
                currentRow++;
                if (currentRow>=maxRowCount) break;
            }
            if (currentRow>=maxRowCount) break;
        }
        return currentRow;
    }
    
    private boolean renderTextLine(String text, GraphicsContext gc, double xPosition, double yPosition, int currentRow, int maxRow) {
        if (currentRow > maxRow) return false;
        gc.fillText(text, xPosition+this.margin, yPosition+this.margin+(currentRow*this.rowHeight));
        return true;
    }
    
    @Override
    protected void renderText(GraphicsContext gc, double xPosition, double yPosition) {
        gc.save();
        //gc.setFont(Font.font("Verdana"));
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));
        this.renderQuestText(gc, xPosition, yPosition);
        //gc.fillText(this.text, xPosition+this.margin, yPosition+this.margin+15);
        gc.restore();
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof QuestPanel)) return false;
        return ((QuestPanel)object).getName().equals(this.name);
    }
    
}
