/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import java.util.ArrayList;

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
    * @return Returns the task the creature decided to perform
    */
    @Override
    public Task act(double time) {
        //TODO: For now all creatures just want to home in on player
        if (this.timeSinceAction > 0.5) { //Acting twice per second
            //Mists.logger.info(this.getCreature().getName()+" decided to act!");
            this.timeSinceAction = 0;
            return this.pickNewAction(time);
        } else {
            //this.getCreature().applyMovement(time);
            this.timeSinceAction = this.timeSinceAction+time;
            return new Task(GenericTasks.ID_CONTINUE_MOVEMENT, creep.getID(), null);
        }
    }
    
    private Task pickNewAction(double time) {
        Task t = this.attackNearbyEnemies(time);
        if (t.taskID == GenericTasks.ID_IDLE) {
            return distanceBasedFollow(time, creep.getLocation().getPlayer());
        }
        else {
            return t;
        }
    }
    
    /**
     * AttackNearbyEnemies checks if there's a hostile enemy nearby,
     * and attacks it if that is the case.
     * @param time Time given for acting
     * @return Return true if there was something to go and attack
     */
    private Task attackNearbyEnemies(double time) {
        ArrayList<Creature> nearbyMobs = this.surroundingCreatures(creep.getCenterXPos(), creep.getCenterYPos(), Mists.TILESIZE*8);
        //TODO: Filter only hostile mobs from the list. For now remove itself and player.
        nearbyMobs.remove(creep); //Creature is always near itself
        if (nearbyMobs.contains(creep.getLocation().getPlayer())) nearbyMobs.remove(creep.getLocation().getPlayer());
        //Pick the first (effectively random) mob from the list
        return this.attackNearest(nearbyMobs, time);
    }
    
    
}
