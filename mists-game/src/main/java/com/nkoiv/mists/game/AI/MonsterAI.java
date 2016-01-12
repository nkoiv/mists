/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.world.util.Toolkit;

/**
 *
 * @author nikok
 */
public class MonsterAI extends CreatureAI{

    public MonsterAI(Creature monster) {
        super(monster);
    }
    
 /**
    * act() is the main loop for AI
    * it's called whenever it's given creatures
    * turn to do things
    * TimeSinceAction governs creature decision making
    * No creature needs to act on every tick!
    * @param time Time passed since the last action
    * @return Returns the task the creature decided to perform
    */
    @Override
    public Task act(double time) {
        //TODO: For now all creatures just want to home in on player
        //TODO: Adjust the action-picking speed dynamically based on AI load?
        if (this.timeSinceAction > 0.3 || creep.getLastTask() == null) { //Acting thrice per second
            //Mists.logger.info(this.getCreature().getName()+" decided to act!");
            this.timeSinceAction = 0;
            return this.pickNewAction(time);
        } else {
            //TODO: Continue current chosen action. Atm this means just movement
            this.timeSinceAction = this.timeSinceAction+time;
            return creep.getLastTask();
        }
    }
    
    private Task pickNewAction(double time) {
        if (!this.active) {
            if (Toolkit.distance(creep.getCenterXPos(), creep.getCenterYPos(), creep.getLocation().getPlayer().getCenterXPos(), creep.getLocation().getPlayer().getCenterYPos())
                    < 10 * 32) {
                if (this.isInLineOfSight(creep.getLocation().getPlayer())) {
                    this.active = true;
                }
            }
            return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
        }
        else {
            return this.goMelee(creep.getLocation().getPlayer(), time);
        }
    }
    

    
}
