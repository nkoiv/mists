/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * CombatPopups are small instances of text or numbers
 * that appear when something gets hit with something
 * in combat.
 * @author nikok
 */
public class CombatPopup {
    
    private static Random rand = new Random();
    ArrayList<ScrollingCombatText> currentSCT;
    
    
    public CombatPopup() {
        this.currentSCT = new ArrayList<>();
    }
    
    /**
     * Add a combat text popup with random colour
     * @param mob MapObject to pop the text from
     * @param number Number to pop
     */
    public void addNumberPopup(MapObject mob, int number) {
        //Generate a (reddish) random color
        /*
        float r = rand.nextFloat();
        float g = rand.nextFloat() / 2f;
        float b = rand.nextFloat() / 2f;
        Color c = new Color(r, g, b, 1);
        */
        String text = Integer.toString(number);
        Color c = Color.RED;
        this.addSCT(mob, text, c);
    }
    
    /**
     * Add a combat text popup with set colour
     * @param mob MapObject to pop text from
     * @param text Text to show
     * @param c Colour for the text
     */
    public void addSCT(MapObject mob, String text, Color c) {
        ScrollingCombatText sct = new ScrollingCombatText(text, mob.getCenterXPos()-mob.getLocation().getLastxOffset(), mob.getCenterYPos()-mob.getLocation().getLastyOffset());
        sct.setColour(c);
        //Randomize the direction of the text floating
        double xDir = (rand.nextInt(200))-100;
        double yDir = Math.abs(xDir) - 100;
        sct.setDirection(xDir, yDir);
        this.currentSCT.add(sct);
        //Mists.logger.info("Added SCT on: "+mob.getName()+" text: "+text);
    }
    
    /**
     * Tick the currently active combat popups
     * Each will move and fade accordingly.
     * @param time Number of seconds to tick forward
     */
    public void tick(double time) {
        if (this.currentSCT.isEmpty()) return;
        
        ArrayList<ScrollingCombatText> removables = new ArrayList<>();
        for (int i = 0; i < this.currentSCT.size(); i ++) {
            ScrollingCombatText sct = this.currentSCT.get(i);
            sct.tick(time);
            if (sct.getLifetime() <= 0) removables.add(sct);
        }
        
        for (ScrollingCombatText sct : removables) {
            this.currentSCT.remove(sct);
        }
        
    }
    
    /**
     * Render all the active combat popups on a
     * given graphics context
     * @param gc GraphicsContext to render the combat texts on
     */
    public void render(GraphicsContext gc) {
        if (this.currentSCT.isEmpty()) return;
        gc.save();
        for (ScrollingCombatText sct : this.currentSCT) {
            sct.render(gc);
        }
        gc.restore();
    }
    
    private class ScrollingCombatText {
        private String text;
        private double xCoor;
        private double yCoor;
        private Paint colour;
        private double xDirection;
        private double yDirection;
        private double lifetime;
        
        
        public ScrollingCombatText(String text, double xCoor, double yCoor) {
            this.text = text;
            this.colour = Color.RED;
            this.xCoor = xCoor;
            this.yCoor = yCoor;
            
            this.lifetime = 1500; //text duration on screen in milliseconds
        }
        
        public void setDirection(double xDirection, double yDirection) {
            this.xDirection = xDirection;
            this.yDirection = yDirection;
        }
        
        public void setColour(Paint color) {
            this.colour = color;
        }
        
        public void render(GraphicsContext gc) {
            //Mists.logger.info("Rendering SCT");
            if (lifetime < 1000) gc.setGlobalAlpha(lifetime / 1000);
            gc.setFont(Mists.fonts.get("romulus"));
            gc.setFill(colour);
            gc.fillText(text, xCoor, yCoor);
        }
        
        public double getLifetime() {
            return this.lifetime;
        }
        
        /**
         * SCT tick moves the text towards its float
         * direction as well as decays the object.
         * @param time Seconds that have elapsed since last tick
         */
        public void tick(double time) {
            double timeInMS = time * 1000;
            this.xCoor = this.xCoor +  (this.xDirection * time);
            this.yCoor = this.yCoor +  (this.yDirection * time);
            this.lifetime = this.lifetime - (timeInMS);
        }
        
    }
    
}
