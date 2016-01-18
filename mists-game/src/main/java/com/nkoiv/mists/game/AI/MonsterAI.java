/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.actions.GenericTasks;
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
    
    @Override
    protected Task pickNewAction(double time) {
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
