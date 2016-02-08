/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Trigger;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.ArrayList;

/**
 * TriggerPlate is an invisible Effect that can
 * be placed on the ground to have something happen
 * when it's touched.
 * @author nikok
 */
public class TriggerPlate extends Effect {
    private Trigger touchTrigger;
    private double triggerCooldown;
    private double onCooldown;
    
    public TriggerPlate(String name, double width, double height, double triggerCooldown) {
        super(null, name, new Sprite(Mists.graphLibrary.getImage("blank")), -1);
        super.getSprite().setWidth(width);
        super.getSprite().setHeight(height);
        this.triggerCooldown = triggerCooldown;
    }
    
    @Override
    public void update(double time) {
        if (this.touchTrigger == null) return;
        if (this.onCooldown>0) this.onCooldown = onCooldown - (time*1000);
        if (onCooldown <=0) touch(getLocation().checkCollisions(this));
    }

    private void touch(ArrayList<MapObject> touchedMobs) {
        for (MapObject mob : touchedMobs) {
            if (touchTrigger.toggle(mob)) {
                this.onCooldown = this.triggerCooldown;
            }
        }
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
    
    public void setTouchTrigger(Trigger trigger) {
        this.touchTrigger = trigger;
    }
    
    public Trigger getTouchTrigger() {
        return this.touchTrigger;
    }
    
    public class ToggleTrigger implements Trigger {

        private MapObject targetMob;
        
        public ToggleTrigger(MapObject mob) {
            this.targetMob = mob;
        }
        
        public void setTargetMob(MapObject target) {
            this.targetMob = target;
        }
        
        @Override
        public String getDescription() {
            String s = "Trigger to toggle "+targetMob.getName();
            return s;
        }

        @Override
        public boolean toggle(MapObject toggler) {
            Trigger[] targetTriggers = targetMob.getTriggers();
            if (targetTriggers.length > 0) { 
                targetTriggers[0].toggle(toggler);
                return true;
            }
            return false;
        }

        @Override
        public MapObject getTarget() {
            return targetMob;
        }
        
    }
}
