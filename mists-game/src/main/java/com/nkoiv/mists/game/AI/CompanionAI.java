/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.AI;

import java.util.ArrayList;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.gameobject.Creature;

/**
 * CompanionAI is used for the friendly companions
 * player might have.
 * @author nikok
 */
public class CompanionAI extends CreatureAI {

    public CompanionAI(Creature companion) {
        super(companion);
    }
    
    
    
    @Override
    protected Task pickNewAction() {
        Task t = this.attackNearbyEnemies();
        if (t.taskID == GenericTasks.ID_IDLE && creep.getLocation().getPlayer()!=null) {
            Task followTask = distanceBasedFollow(creep.getLocation().getPlayer());
            return followTask;
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
    private Task attackNearbyEnemies() {
        ArrayList<Creature> nearbyMobs = this.surroundingCreatures(creep.getCenterXPos(), creep.getCenterYPos(), Mists.TILESIZE*8);
        //TODO: Filter only hostile mobs from the list. For now remove itself and player.
        if (nearbyMobs.isEmpty()) return new Task(GenericTasks.ID_IDLE, creep.getID(), null);
        nearbyMobs.remove(creep); //Creature is always near itself
        if (nearbyMobs.contains(creep.getLocation().getPlayer())) nearbyMobs.remove(creep.getLocation().getPlayer());
        //Pick the first (effectively random) mob from the list
        return this.attackNearest(nearbyMobs);
    }
    
    
}
