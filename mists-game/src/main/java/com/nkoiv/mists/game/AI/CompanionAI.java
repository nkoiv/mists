/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import java.util.Random;

/**
 * CompanionAI is used for the friendly companions
 * player might have.
 * @author nikok
 */
public class CompanionAI extends CreatureAI {

    public CompanionAI(Creature companion) {
        super(companion);
    }
    
        /**
    * act() is the main loop for AI
    * it's called whenever it's given creatures
    * turn to do things
    * TimeSinceAction governs creature decisionmaking
    * No creature needs to act on every tick!
    * @param time Time passed since the last action
    * @return Returns true if the creature was able to act
    */
    @Override
    public boolean act(double time) {
        //TODO: For now all creatures just want to home in on player
        if (this.timeSinceAction > 0.5) { //Acting twice per second
            //Mists.logger.info(this.getCreature().getName()+" decided to act!");
            this.pickNewAction(time);
            this.timeSinceAction = 0;
        } else {
            this.getCreature().applyMovement(time);
            this.timeSinceAction = this.timeSinceAction+time;
        }
        
        return true;
    }
    
    private void pickNewAction(double time) {
        creep.stopMovement(); //clear old movement
        Random rnd = new Random();
        if (this.distanceToMob(creep.getLocation().getPlayer()) > 3 * Mists.TILESIZE) {
            this.moveTowardsMob(creep.getLocation().getPlayer(), time);
            Mists.logger.info(creep.getName()+" moving towards "+creep.getLocation().getPlayer().getName());
        } else {
            int r = rnd.nextInt(10); //50% chance to move around
            if (r < 5) this.moveRandomly(time);
        }
        
        
    }
    
    
}
