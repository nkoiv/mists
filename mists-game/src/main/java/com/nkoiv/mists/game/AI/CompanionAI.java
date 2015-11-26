/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.Random;
import java.util.logging.Level;

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
        distanceBasedFollow(time, creep.getLocation().getPlayer());
    }
    

    
    
}
