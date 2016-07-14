/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.actions.ActionType;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.gameobject.Creature;

/**
 *
 * @author nikok
 */
public class MonsterAI extends CreatureAI{

    public MonsterAI(Creature monster) {
        super(monster);
    }
    
    @Override
    protected Task pickNewAction() {
        if (!this.active) {
            this.active = activateByLineOfSight(creep.getLocation().getPlayer());
            return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
        }
        else {
            Task t;
            //Mists.logger.info(creep.getName()+" is acting");
            //Mists.logger.info(creep.getName()+" has actions: "+creep.getAvailableActionNames());
            if (creep.getAttack(ActionType.RANGED_ATTACK) != null && this.isInLineOfSight(creep.getLocation().getPlayer())) {
                //Mists.logger.info(creep.getName()+" wants to go shoot player");
                t = this.goShoot(creep.getLocation().getPlayer());
            }
            else t = this.goMelee(creep.getLocation().getPlayer());
            if (t.taskID == GenericTasks.ID_MOVE_TOWARDS_COORDINATES && this.pathToMoveOn == null) {
                //Not in melee range and no path found to target;
                this.active = false;
                return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
            } else return t;
        }
    }
    

    
}
