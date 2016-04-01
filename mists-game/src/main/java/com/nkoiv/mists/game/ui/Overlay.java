/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.triggers.DialogueTrigger;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Overlay contains (mostly location-tied) UI components
 * that are drawn on top of the game-layer. Good examples
 * of this are targeting indicators, HP bars and other such
 * things that rely on location-specific info to function.
 * @author nikok
 */
public abstract class Overlay {
    
    private Overlay() {
        //Dont subclass this static class
    }
    
    /**
     * Takes in a list of mobs and draws HP bars on them.
     * HP bars are drawn only on Creatures and only on
     * Creatures that are missing at least some HP.
     * @param gc GraphicsContext to draw the HP bars on
     * @param mobs List of mobs to draw the HP bars on
    */
    public static void drawAllHPBars(GraphicsContext gc, List<MapObject> mobs) {
        for (MapObject mob : mobs) {
            if (mob instanceof Creature) { //Only draw HP bars on Creatures
                Creature c = (Creature)mob;
                if (c.getHealth() < c.getMaxHealth()) { //Only draw bar on those missing HPs
                    drawHPBar(gc, c);
                }
            }
        }
    }
    
    /**
     * Draw HP bars on a single target
     * @param gc GraphicsContext to draw the bar on
     * @param mob Creature to draw the bar on
     */
    public static void drawHPBar(GraphicsContext gc, Creature mob) {
        int maxHP = mob.getMaxHealth();
        int currentHP = mob.getHealth();
        double hpPercentage = (double)currentHP/maxHP;
        //Mists.logger.info("HP/Max:"+currentHP+"/"+maxHP+" HP bar: "+hpPercentage);
        double barWidth = mob.getWidth();
        double barHeight = 5;
        double xPosition = mob.getXPos()-mob.getLocation().getLastxOffset();
        double yPosition = mob.getYPos()-mob.getLocation().getLastyOffset()-barHeight;
        gc.save();
        //gc.setFill(Color.BLACK);
        //drawHPBar(gc, xPosition, yPosition, barWidth, barHeight);
        gc.setFill(Color.GREEN); //TODO: Change colour depending on HP amount
        //TODO: Use creature affiliation here instead of instanceof
        if (mob instanceof PlayerCharacter) drawHPBar(gc, xPosition, yPosition, barWidth*hpPercentage, barHeight, Color.RED);
        else drawHPBar(gc, xPosition, yPosition, barWidth*hpPercentage, barHeight, Color.GREEN);
        gc.restore();
    }
    
    /**
     * Actually paint the bar.
     * TODO: Consider if the right end of the bar is needed at all
     * @param gc GraphicsContext to draw the bar on
     * @param xCoor xCoor on where to draw the bar
     * @param yCoor yCoor on where to draw the bar
     * @param barWidth Size of the bar (width of creature?)
     * @param barHeight Size of the bar (static height?)
     */
    private static void drawHPBar(GraphicsContext gc, double xCoor, double yCoor, double barWidth, double barHeight, Color colour) {
        //Set the bar as Green, and colour it differently if the colour is valid
        Image barLeft = Mists.graphLibrary.getImage("barGreenHorizontalLeft");
        Image barMid = Mists.graphLibrary.getImage("barGreenHorizontalMid");
        Image barRight = Mists.graphLibrary.getImage("barGreenHorizontalRight");
        if (colour == Color.RED) {
            barLeft = Mists.graphLibrary.getImage("barRedHorizontalLeft");
            barMid = Mists.graphLibrary.getImage("barRedHorizontalMid");
            barRight = Mists.graphLibrary.getImage("barRedHorizontalRight");
        } 
        gc.drawImage(barLeft, xCoor, yCoor);
        if (barWidth <= barLeft.getWidth()) return;
        double currentX = barLeft.getWidth();
        while (currentX < (barWidth-barMid.getWidth())) {
            gc.drawImage(barMid, xCoor+currentX, yCoor);
            currentX = currentX + barMid.getWidth();
        }
        gc.drawImage(barMid, (xCoor+barWidth)-barMid.getWidth(), yCoor);
        //gc.drawImage(barRight, (xCoor+barWidth)-barRight.getWidth(), yCoor);
        //gc.fillRect(xCoor, yCoor, barWidth, barHeight);
    }
    
    /**
     * Draw the targetting circle on a location
     * @param gc GraphicsContext to draw the bar on
     * @param xCoor (center) xCoor on where to draw the marker on
     * @param yCoor (center) yCoor on where to draw the marker on
     */
    public static void drawTargettingCircle (GraphicsContext gc, double xCoor, double yCoor) {
        drawTargettingCircle(gc, xCoor, yCoor, Mists.TILESIZE, Mists.TILESIZE);
    }
    
    /**
     * Draw a targetting circle on a specified target MapObject
     * @param gc GraphicsContext to draw the marker on
     * @param mob MapObject that's being targeted
     */
    public static void drawTargettingCircle(GraphicsContext gc, MapObject mob) {
        drawTargettingCircle(gc, mob.getXPos()-mob.getLocation().getLastxOffset(), mob.getYPos()-mob.getLocation().getLastyOffset(), mob.getWidth(), mob.getHeight());
    }
    
    /**
     * Actually draw the targetting circle
     * TODO: Replace with fancy graphics
     * @param gc GraphicsContext to draw the marker on
     * @param xCoor xCoor on where to draw the marker on
     * @param yCoor yCoor on where to draw the marker on
     * @param width
     * @param height 
     */
    private static void drawTargettingCircle(GraphicsContext gc, double xCoor, double yCoor, double width, double height) {
        gc.save();
        gc.setLineDashes(4, 6);
        gc.setStroke(Color.MAGENTA);
        gc.strokeOval(xCoor, yCoor, width, height);
        gc.restore();
    }
    
    public static void drawHighlightRectangle(GraphicsContext gc, List<MapObject> mobs) {
        for (MapObject mob : mobs) {
            drawHighlightRectangle(gc, mob);
        }
    }
    
    public static void drawHighlightRectangle(GraphicsContext gc, MapObject mob) {
        drawHighlightRectangle(gc, mob.getXPos()-mob.getLocation().getLastxOffset(), mob.getYPos()-mob.getLocation().getLastyOffset(), mob.getWidth(), mob.getHeight());
    }
    
    private static void drawHighlightRectangle (GraphicsContext gc, double xCoor, double yCoor, double width, double height) {
        gc.save();
        gc.setLineDashes(4, 6);
        gc.setStroke(Color.BLUE);
        gc.strokeRect(xCoor*Mists.graphicScale, yCoor*Mists.graphicScale, width*Mists.graphicScale, height*Mists.graphicScale);
        gc.restore();
    }
    
    public static void drawTriggerCursor(GraphicsContext gc, Trigger t) {
        MapObject mob = t.getTarget();
        Image triggerImage;
        double xPos;
        double yPos;
        if (t instanceof DialogueTrigger) {
            triggerImage = Mists.graphLibrary.getImage("buttonSelectSmallbeige");
            xPos = mob.getCenterXPos()-mob.getLocation().getLastxOffset()-(triggerImage.getWidth()/2);
            yPos = mob.getCenterYPos()-mob.getLocation().getLastyOffset()-triggerImage.getHeight();
        } else {
            triggerImage = Mists.graphLibrary.getImage("cursorGauntletSmallbronze");
            xPos = mob.getCenterXPos()-mob.getLocation().getLastxOffset();
            yPos = mob.getCenterYPos()-mob.getLocation().getLastyOffset();
        }
        gc.drawImage(triggerImage, xPos*Mists.graphicScale, yPos*Mists.graphicScale);
    }
    
    public static void drawToggleIcon(GraphicsContext gc, MapObject mob) {
            gc.drawImage(Mists.graphLibrary.getImage("buttonSelectSmallbeige"), mob.getXPos()-mob.getLocation().getLastxOffset(), mob.getYPos()-mob.getLocation().getLastyOffset());
    }
        
    public static String generateInfoBoxText(MapObject mob) {
        StringBuilder sb = new StringBuilder();
        for (String s : mob.getInfoText()) {
            sb.append(s);
            sb.append("\n");
        }
        String text = sb.toString();
        return text;
    }
}
