/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.Location;
import java.util.logging.Level;

/**
 * GenericTasks is meant to ease sever->client communication.
 * While the creaturetype specific AI-classes handle decisionmaking (serverside),
 * the actual executable tasks can be chopped down to GenericTasks.
 * These GenericTasks can then be transferred over network with simple IDs.
 * @author nikok
 */
public class GenericTasks {
    public static final int ID_IDLE = 0; //no arguments
    public static final int ID_CONTINUE_MOVEMENT = 1; //no arguments
    public static final int ID_MOVE_TOWARDS_DIRECTION = 2; //1 argument: the direction
    public static final int ID_MOVE_TOWARDS_TARGET = 3; // 1 argument: targetID
    public static final int ID_MOVE_TOWARDS_COORDINATES = 4; //2 arguments: x and y coordinates
    public static final int ID_STOP_MOVEMENT = 9; //no arguments
    public static final int ID_TURN_TOWARDS_MOB = 11; //1 argument: MobID
    public static final int ID_USE_MELEE_TOWARDS_MOB = 21; // 1 argument: MobID
    
    public static void performTask(Location l, Task task, double time) {
        Creature actor = (Creature)l.getMapObject(task.actorID);
        switch (task.taskID) {
            case ID_IDLE: break;
            case ID_CONTINUE_MOVEMENT: actor.applyMovement(time);
            case ID_MOVE_TOWARDS_DIRECTION: moveTowardsDirection(actor, task.arguments[0]);
            case ID_MOVE_TOWARDS_TARGET: moveTowardsTarget(actor, task.arguments[0]);
            case ID_MOVE_TOWARDS_COORDINATES: actor.moveTowards(task.arguments[0], task.arguments[1]);
            case ID_STOP_MOVEMENT: actor.stopMovement();
            case ID_TURN_TOWARDS_MOB: turnTowardsMapObject(actor, task.arguments[0]);
            case ID_USE_MELEE_TOWARDS_MOB: useMeleeTowardsMob(actor, task.arguments[0]);
            default: break;
        }
        
    }
    
    public static void turnTowardsMapObject(Creature actor, int targetID) {
        MapObject target = actor.getLocation().getMapObject(targetID);
        if (target == null) return;
        Direction d = AIutil.getDirectionTowards(actor.getCenterXPos(), actor.getCenterYPos(),target.getCenterXPos(), target.getCenterYPos());
        actor.setFacing(d);
    }
    
    public static void stopMovement(Creature creature) {
        creature.moveTowards(Direction.STAY);
    }
    
    public static void moveTowardsDirection(Creature creature, int direction) {
        switch (direction) {
            case 0: creature.moveTowards(Direction.UP); break;
            case 1: creature.moveTowards(Direction.UPRIGHT); break;
            case 2: creature.moveTowards(Direction.RIGHT); break;
            case 3: creature.moveTowards(Direction.DOWNRIGHT); break;
            case 4: creature.moveTowards(Direction.DOWN); break;
            case 5: creature.moveTowards(Direction.DOWNLEFT); break;
            case 6: creature.moveTowards(Direction.LEFT); break;
            case 7: creature.moveTowards(Direction.UPLEFT); break;
            case 8: creature.moveTowards(Direction.STAY); break;
            default: creature.moveTowards(Direction.STAY); break;
        }
    }
    
    public static void moveTowardsDirection(Creature creature, Direction direction) {
        switch (direction) {
            case UP: creature.moveTowards(Direction.UP); break;         
            case DOWN: creature.moveTowards(Direction.DOWN); break;
            case LEFT: creature.moveTowards(Direction.LEFT); break;
            case RIGHT: creature.moveTowards(Direction.RIGHT);break;
            case UPRIGHT: creature.moveTowards(Direction.UPRIGHT);break;
            case UPLEFT: creature.moveTowards(Direction.UPLEFT);break;
            case DOWNRIGHT: creature.moveTowards(Direction.DOWNRIGHT);break;
            case DOWNLEFT: creature.moveTowards(Direction.DOWNLEFT);break;
            default: break;
        } 
    }
    
    public static void moveTowardsTarget(Creature actor, int targetID) {
        MapObject target = actor.getLocation().getMapObject(targetID);
        if (target == null) return;
        actor.moveTowards(target.getCenterXPos(), target.getCenterYPos());
    }
    
    public static void useMeleeTowardsMob(Creature actor, int targetID) {
        //Mists.logger.log(Level.INFO, "{0} uses melee towards {1}", new Object[]{creep.getName(), target.getName()});
        MapObject target = actor.getLocation().getMapObject(targetID);
        if (target == null) return;
        if (actor.getAvailableActions() == null) {
            Mists.logger.log(Level.INFO, "No available actions for {0}", actor.getName());
            return;
        }
        if (!actor.getAvailableActions().isEmpty()) {
            GenericTasks.turnTowardsMapObject(actor, targetID);
            if (actor.getAvailableActionNames().contains("melee")) {
                //Try to use "melee" ability if possible
                actor.useAction("melee");
            } else {
                //If not available, use first available action
                //TODO: Is this necessary?
                Mists.logger.log(Level.INFO, "{0} tried to use melee, but it wasn''t available", actor.getName());
                actor.useAction(actor.getAvailableActionNames().get(0));
            }
        }
    }
    
    
   
}
