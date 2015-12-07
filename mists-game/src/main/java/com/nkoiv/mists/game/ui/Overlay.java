/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Overlay contains (mostly location-tied) UI components
 * that are drawn on top of the game-layer. Good examples
 * of this are targeting indicators, HP bars and other such
 * things that rely on location-specific info to function.
 * @author nikok
 */
public class Overlay {
    
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
        drawHPBar(gc, xPosition, yPosition, barWidth*hpPercentage, barHeight);
        gc.restore();
    }
    
    private static void drawHPBar(GraphicsContext gc, double xCoor, double yCoor, double barWidth, double barHeight) {
        gc.fillRect(xCoor, yCoor, barWidth, barHeight);
    }
    
    
}
