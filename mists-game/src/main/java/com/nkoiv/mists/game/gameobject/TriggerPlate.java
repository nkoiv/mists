/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.ToggleTrigger;
import com.nkoiv.mists.game.triggers.Trigger;
import java.util.ArrayList;

/**
 * TriggerPlate is an invisible MapObject that can
 * be placed on the ground to have something happen
 * when it's touched.
 * @author nikok
 */
public class TriggerPlate extends MapObject {
    private ArrayList<Trigger> touchTriggers;
    private double triggerCooldown;
    private double onCooldown;
    private boolean requireReEntry;
    private boolean clear;
    
    public TriggerPlate(String name, double width, double height, double triggerCooldown, MapObject target) {
        this(name,width, height, triggerCooldown);
        this.touchTriggers = new ArrayList<>();
        this.touchTriggers.add(new ToggleTrigger(target));
    }
    
    public TriggerPlate(String name, double width, double height, double triggerCooldown) {
        super(name, Mists.graphLibrary.getImage("circle32"));
        super.getGraphics().setWidth(width);
        super.getGraphics().setHeight(height);
        this.touchTriggers = new ArrayList<>();
        this.triggerCooldown = triggerCooldown;
    }
    
    @Override
    public void update(double time) {
        //Mists.logger.info("Triggerplate updating at "+this.getXPos()+"x"+this.getYPos());
        if (this.touchTriggers.isEmpty()) return;
        if (this.onCooldown>0) {
            this.onCooldown = onCooldown - (time*1000);
            //Mists.logger.info(name+" cooldown "+onCooldown);
        }
        if (onCooldown <=0) {
            touch(getLocation().checkCreatureCollisions(this));
        }
    }

    private void touch(ArrayList<Creature> touchedMobs) {
        if (touchedMobs.isEmpty()) this.clear = true;
        if (this.requireReEntry && !this.clear) return;
        for (MapObject mob : touchedMobs) {
            //Mists.logger.info("Standing on triggerplate: "+touchedMobs.toString());
            for (Trigger touchTrigger : this.touchTriggers) {
                if (touchTrigger.toggle(mob)) {
                //Mists.logger.info("Wasn't on cooldown, toggling "+name);
                this.onCooldown = this.triggerCooldown;
                this.clear = false;
            }
            }   
        }
    }
    
    /**
     * Force the Trigger effect for the the triggerplate,
     * for for example initiation of locations and puzzles.
     */
    public void forceTrigger() {
        MapObject tempForcer = new MapObject("TempForcer");
        for (Trigger touchTrigger : this.touchTriggers) {
            touchTrigger.toggle(tempForcer);
        }
    }
    
    /**
     * If requireReEntry is set, a mob standing
     * on trigger will no re-trigger it before moving
     * in and out again
     * @param requireReEntry Value to set requireReEntry to
     */
    public void setRequireReEntry(boolean requireReEntry) {
        this.requireReEntry = requireReEntry;
    }
    
    public void setCooldown(double cooldown) {
        this.triggerCooldown = cooldown;
    }
    
    public double getCooldown() {
        return this.triggerCooldown;
    }
    
    public boolean isOnCooldown() {
        return (this.onCooldown>0);
    }
    
    public void addTouchTrigger(Trigger trigger) {
        this.touchTriggers.add(trigger);
    }
    
    public ArrayList<Trigger> getTouchTriggers() {
        return this.touchTriggers;
    }
    
    
    @Override
    public TriggerPlate createFromTemplate() {
        TriggerPlate tp = new TriggerPlate(this.name, this.getWidth(), this.getHeight(), this.triggerCooldown);
        tp.setSprite(new Sprite(this.getGraphics().getImage()));
        tp.setRequireReEntry(this.requireReEntry);
        if (!this.touchTriggers.isEmpty()) {
                for (Trigger touchTrigger : this.touchTriggers) {
                Trigger tr = touchTrigger.createFromTemplate();
                tr.setTarget(tp);
                tp.addTouchTrigger(tr);
            }
        }
        return tp;
    }
    
}
