/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import java.util.ArrayList;
import java.util.Random;

import com.nkoiv.mists.game.gameobject.MapObject;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * CombatPopups are small instances of text or numbers
 * that appear when something gets hit with something
 * in combat.
 * @author nikok
 */
public class CombatPopup {
    
    private static Random rand = new Random();
    private ArrayList<ScrollingPopupText> currentSCT;
    
    
    public CombatPopup() {
        this.currentSCT = new ArrayList<>();
    }
    
    /**
     * Add a combat text popup with random colour
     * @param mob MapObject to pop the text from
     * @param number Number to pop
     */
    public void addNumberPopup(MapObject mob, int number) {
        String text = Integer.toString(number);
        Color c = Color.RED;
        ScrollingPopupText sct = new ScrollingPopupText(text, mob.getCenterXPos()-mob.getLocation().getLastxOffset(), mob.getCenterYPos()-mob.getLocation().getLastyOffset());
        sct.setColour(c);
        //Randomize the direction of the text floating
        double xDir = (rand.nextInt(200))-100;
        double yDir = Math.abs(xDir) - 100;
        sct.setDirection(xDir, yDir);
        this.currentSCT.add(sct);
    }
    
    /**
     * Add a combat text popup with set colour
     * @param mob MapObject to pop text from
     * @param text Text to show
     * @param c Colour for the text
     */
    public void addSCT(MapObject mob, String text, Color c) {
        if (mob == null || text == null || c == null) return;
        if (mob.getLocation() == null) return;
        double lifetime = calculateLifetimeFromTextLength(text);
        ScrollingPopupText sct = new ScrollingPopupText(
                text, mob.getCenterXPos()-mob.getLocation().getLastxOffset(), 
                mob.getYPos()-mob.getLocation().getLastyOffset(),
                lifetime, c);
        this.currentSCT.add(sct);
        //Mists.logger.info("Added SCT on: "+mob.getName()+" text: "+text);
    }
    
    public void addSCT(ScrollingPopupText sct) {
        this.currentSCT.add(sct);
    }
    
    private double calculateLifetimeFromTextLength(String text) {
        double lifetime = 1500;
        if (text.length() >= 15) {
            lifetime = text.length()*100;
            if (lifetime > 5000) lifetime = 5000;
        }
        return lifetime;
    }

    /**
     * Tick the currently active combat popups
     * Each will move and fade accordingly.
     * @param time Number of seconds to tick forward
     */
    public void tick(double time) {
        if (this.currentSCT.isEmpty()) return;
        
        ArrayList<ScrollingPopupText> removables = new ArrayList<>();
        for (int i = 0; i < this.currentSCT.size(); i ++) {
            ScrollingPopupText sct = this.currentSCT.get(i);
            sct.tick(time);
            if (sct.getLifetime() <= 0) removables.add(sct);
        }
        
        for (ScrollingPopupText sct : removables) {
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
        for (ScrollingPopupText sct : this.currentSCT) {
            sct.render(gc);
        }
        gc.restore();
    }
    
    
    
}
