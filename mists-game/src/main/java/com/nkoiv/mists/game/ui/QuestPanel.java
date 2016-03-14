/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
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
    }
    
    public QuestPanel(GameState parent) {
        this(parent, "QuestPanel", Mists.MistsGame.questManager, defaultWidth, defaultHeight, Mists.WIDTH/2, Mists.HEIGHT/2, Mists.graphLibrary.getImageSet(defaultPanelImages));
    }
    
    private void renderQuestText(GraphicsContext gc, double xPosition, double yPosition) {
        gc.setFill(Color.STEELBLUE);
        int maxRowCount = (int)(this.height / this.rowHeight);
        int currentRow = 1;
        StringBuilder sb;
        for (int questID : this.questManager.getOpenQuests().keySet()) {
            gc.setFont(Mists.fonts.get("alagard20"));
            String title = this.questManager.getOpenQuests().get(questID).getTitle();
            renderTextLine(title, gc, xPosition, yPosition, currentRow, maxRowCount);
            currentRow++;
            for (QuestTask qt : this.questManager.getOpenQuests().get(questID).getTasks()) {
                if (qt.isDone()) gc.setFill(Color.LIME);
                else gc.setFill(Color.STEELBLUE);
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
    
    
}
