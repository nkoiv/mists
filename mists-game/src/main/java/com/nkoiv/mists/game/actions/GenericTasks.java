/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.AI.AIutil;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.HasInventory;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.MoveState;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * GenericTasks is meant to ease server-client communication.
 * While the creaturetype specific AI-classes handle decision making (serverside),
 * the actual executable tasks can be chopped down to GenericTasks.
 * These GenericTasks can then be transferred over network with simple IDs.
 * 
 * When adding new tasks, remember to add the description at Tasks.java static String[]
 * for debugging use.
 * @author nikok
 */
public abstract class GenericTasks {
    public static final int ID_IDLE = 0; //no arguments
    public static final int ID_CONTINUE_MOVEMENT = 1; //no arguments
    public static final int ID_MOVE_TOWARDS_DIRECTION = 2; //1 argument: the direction
    public static final int ID_MOVE_TOWARDS_TARGET = 3; // 1 argument: targetID
    public static final int ID_MOVE_TOWARDS_COORDINATES = 4; //2 arguments: x and y coordinates
    public static final int ID_DASH_TOWARDS_DIRECTION = 5; //1 argument: the direction
    public static final int ID_DASH_TOWARDS_TARGET = 6; // 1 argument: targetID
    public static final int ID_DASH_TOWARDS_COORDINATES = 7; //2 arguments: x and y coordinates
    public static final int ID_CHECK_COORDINATES = 8; //2 arguments: x and y coordinates
    public static final int ID_STOP_MOVEMENT = 9; //no arguments
    public static final int ID_TURN_TOWARDS_MOB = 11; //1 argument: MobID
    public static final int ID_USE_MELEE_TOWARDS_MOB = 21; // 1 argument: MobID
    public static final int ID_USE_MELEE_TOWARDS_COORDINATES = 22; // 2 argument: Mob X and Y
    public static final int ID_USE_MELEE_TOWARDS_DIRECTION = 23; // 1 argument: direction number
    public static final int ID_USE_RANGED_TOWARDS_MOB = 24; // 1 argument: MobID
    public static final int ID_USE_RANGED_TOWARDS_COORDINATES = 25; // 2 argument: Mob X and Y
    public static final int ID_USE_RANGED_TOWARDS_DIRECTION = 26; // 1 argument: direction number
    public static final int ID_DROP_ITEM = 31; //1 argument: inventoryslotID of the actor dropping the item
    public static final int ID_TAKE_ITEM = 32; //2 arguments: inventoryholder ID and inventoryslotID
    public static final int ID_EQUIP_ITEM = 33; //1 arguments: inventoryslotID
    public static final int ID_USE_ITEM = 34; //1 arguments: inventoryslotID
    public static final int ID_USE_TRIGGER = 41; //1 argument: id of the mapobject to toggle (TODO: and ID of the trigger)
    /**
     * PerformTask is the core of task-processing.
     * It takes the Task object and processes it into
     * actual (Creature) commands.
     * @param l Location the task is performed at (MobID's are based on this)
     * @param task Task-object to parse
     * @param time Time available for performing the task
     * @return Returns true if task was performed successfully. False means object IDs might be out of sync.
     */
    public static boolean performTask(Location l, Task task, double time) {
        if (task == null || l == null) return false;
        MapObject mob = l.getMapObject(task.actorID);
        if (!(mob instanceof Creature)) return false;
        Creature actor = (Creature)mob;
        switch (task.taskID) {
            case ID_IDLE: break;
            case ID_CONTINUE_MOVEMENT: actor.applyMovement(time); break;
            case ID_MOVE_TOWARDS_DIRECTION: moveTowardsDirection(actor, (int)task.arguments[0]); actor.applyMovement(time);break;
            case ID_MOVE_TOWARDS_TARGET: moveTowardsTarget(actor, task.arguments[0]); actor.applyMovement(time); break;
            case ID_MOVE_TOWARDS_COORDINATES: moveTowardsCoordinates(actor, task.arguments[0], task.arguments[1]); actor.applyMovement(time); break;
            case ID_DASH_TOWARDS_DIRECTION: dashTowardsDirection(actor, (int)task.arguments[0]); actor.applyMovement(time);break;
            case ID_DASH_TOWARDS_TARGET: dashTowardsTarget(actor, task.arguments[0]); actor.applyMovement(time); break;
            case ID_DASH_TOWARDS_COORDINATES: dashTowardsCoordinates(actor, task.arguments[0], task.arguments[1]); actor.applyMovement(time); break;
            case ID_CHECK_COORDINATES: checkCoordinates(actor, task.arguments[0], task.arguments[1]); break;
            case ID_STOP_MOVEMENT: actor.stopMovement(); break;
            case ID_TURN_TOWARDS_MOB: turnTowardsMapObject(actor, (int)task.arguments[0]); break;
            case ID_USE_MELEE_TOWARDS_MOB: useAttackTowardsMob(actor, (int)task.arguments[0], ActionType.MELEE_ATTACK); break;
            case ID_USE_MELEE_TOWARDS_COORDINATES: useAttackTowardsCoordinates(actor, task.arguments[0], task.arguments[1], ActionType.MELEE_ATTACK); break;
            case ID_USE_MELEE_TOWARDS_DIRECTION: useAttackTowardsDirection(actor, (int)task.arguments[0], ActionType.MELEE_ATTACK); break;
            case ID_USE_RANGED_TOWARDS_MOB: useAttackTowardsMob(actor, (int)task.arguments[0], ActionType.RANGED_ATTACK); break;
            case ID_USE_RANGED_TOWARDS_COORDINATES: useAttackTowardsCoordinates(actor, task.arguments[0], task.arguments[1], ActionType.RANGED_ATTACK); break;
            case ID_USE_RANGED_TOWARDS_DIRECTION: useAttackTowardsDirection(actor, (int)task.arguments[0], ActionType.RANGED_ATTACK); break;
            case ID_DROP_ITEM: dropItem(actor, (int)task.arguments[0]); break;
            case ID_TAKE_ITEM: takeItem(actor, (int)task.arguments[0], (int)task.arguments[1]); break;
            case ID_EQUIP_ITEM: equipItem(actor, (int)task.arguments[0]); break;
            case ID_USE_ITEM: useItem(actor, (int)task.arguments[0]); break;
            case ID_USE_TRIGGER: useMapObjectTrigger(actor, (int)task.arguments[0], (int)task.arguments[1]); break;
            default: break;
        }
        return true;
    }
    
    public static void checkCoordinates (MapObject actor, double xCoordinate, double yCoordinate) {
        if (Math.abs(actor.getXPos()-xCoordinate) > 5 || Math.abs(actor.getYPos()-yCoordinate) > 5) {
            actor.setPosition(xCoordinate, yCoordinate);
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
        switch (creature.getMoveState()) {
            case WALK: creature.setMoveState(MoveState.STAND); break;
            case RUN: creature.setMoveState(MoveState.STAND); break;
            default: break;
        }
    }
    
    
    public static void dashTowardsDirection(Creature creature, int direction) {
        switch (direction) {
            case 1: creature.dashTowards(Direction.UP); break;
            case 2: creature.dashTowards(Direction.UPRIGHT); break;
            case 3: creature.dashTowards(Direction.RIGHT); break;
            case 4: creature.dashTowards(Direction.DOWNRIGHT); break;
            case 5: creature.dashTowards(Direction.DOWN); break;
            case 6: creature.dashTowards(Direction.DOWNLEFT); break;
            case 7: creature.dashTowards(Direction.LEFT); break;
            case 8: creature.dashTowards(Direction.UPLEFT); break;
            case 0: creature.dashTowards(Direction.STAY); break;
            default: creature.dashTowards(Direction.STAY); break;
        }
    }
    
    public static void moveTowardsDirection(Creature creature, int direction) {
        switch (direction) {
            case 1: creature.moveTowards(Direction.UP); break;
            case 2: creature.moveTowards(Direction.UPRIGHT); break;
            case 3: creature.moveTowards(Direction.RIGHT); break;
            case 4: creature.moveTowards(Direction.DOWNRIGHT); break;
            case 5: creature.moveTowards(Direction.DOWN); break;
            case 6: creature.moveTowards(Direction.DOWNLEFT); break;
            case 7: creature.moveTowards(Direction.LEFT); break;
            case 8: creature.moveTowards(Direction.UPLEFT); break;
            case 0: creature.moveTowards(Direction.STAY); break;
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
            case STAY: creature.moveTowards(Direction.STAY); break;
            default: creature.moveTowards(Direction.STAY); break;
        } 
    }
    
    public static void moveTowardsFacing(Creature creature) {
        creature.moveTowards(creature.getFacing());
    }
    
    public static void turnTowardsDirection(Creature creature, int direction)  {
        switch (direction) {
            case 1: creature.setFacing(Direction.UP); break;
            case 2: creature.setFacing(Direction.UPRIGHT); break;
            case 3: creature.setFacing(Direction.RIGHT); break;
            case 4: creature.setFacing(Direction.DOWNRIGHT); break;
            case 5: creature.setFacing(Direction.DOWN); break;
            case 6: creature.setFacing(Direction.DOWNLEFT); break;
            case 7: creature.setFacing(Direction.LEFT); break;
            case 8: creature.setFacing(Direction.UPLEFT); break;
            case 0: creature.setFacing(Direction.STAY); break;
            default: creature.setFacing(Direction.STAY); break;
        }
    }
    
    public static void dashTowardsCoordinates(Creature actor, double xCoor, double yCoor) {
        actor.dashTowards(xCoor, yCoor);
    }
    
    public static void moveTowardsCoordinates(Creature actor, double xCoor, double yCoor) {
        actor.moveTowards(xCoor, yCoor);
    }
    
    public static void dashTowardsTarget(Creature actor, double targetID) {
        MapObject target = actor.getLocation().getMapObject((int)targetID);
        if (target == null) return;
        actor.dashTowards(target.getCenterXPos(), target.getCenterYPos());
    }
    
    public static void moveTowardsTarget(Creature actor, double targetID) {
        MapObject target = actor.getLocation().getMapObject((int)targetID);
        if (target == null) return;
        actor.moveTowards(target.getCenterXPos(), target.getCenterYPos());
    }
    
    /*
    //Old UseMelee code, replaced with new useAttack(attacktype)
    public static void useMeleeTowardsDirection(Creature actor, int direction) {
        Action meleeAttack = actor.getAttack(ActionType.MELEE_ATTACK);
        if (meleeAttack != null) {
            turnTowardsDirection(actor, direction);
            actor.useAction(meleeAttack.getName());
        } else {
            Mists.logger.log(Level.INFO, "{0} tried to use melee, but it wasn''t available", actor.getName());
        }
    }
    
    public static void useMeleeTowardsCoordinates(Creature actor, double xCoor, double yCoor) {
        //Mists.logger.log(Level.INFO, "{0} uses melee towards {1}", new Object[]{creep.getName(), target.getName()});
        Action meleeAttack = actor.getAttack(ActionType.MELEE_ATTACK);
        if (meleeAttack != null) {
            actor.setFacing(Toolkit.getDirection(actor.getCenterXPos(), actor.getCenterYPos(), xCoor, yCoor));
            actor.useAction(meleeAttack.getName());
        } else {
            Mists.logger.log(Level.INFO, "{0} tried to use melee, but it wasn''t available", actor.getName());
        }
    }
    
    public static void useMeleeTowardsMob(Creature actor, int targetID) {
        //Mists.logger.log(Level.INFO, "{0} uses melee towards {1}", new Object[]{creep.getName(), target.getName()});
        MapObject target = actor.getLocation().getMapObject(targetID);
        if (target == null) return;
        int xCoor = (int)target.getCenterXPos();
        int yCoor = (int)target.getCenterYPos();
        useMeleeTowardsCoordinates(actor, xCoor, yCoor);
    }
    */
    public static void useAttackTowardsDirection(Creature actor, int direction, ActionType attackType) {
        Action attack = actor.getAttack(attackType);
        if (attack != null) {
            turnTowardsDirection(actor, direction);
            actor.useAction(attack.getName());
        } else {
            Mists.logger.log(Level.INFO, "{0} tried to use "+attackType.toString()+", but it wasn''t available", actor.getName());
        }
    }
    
    public static void useAttackTowardsCoordinates(Creature actor, double xCoor, double yCoor, ActionType attackType) {
        //Mists.logger.log(Level.INFO, "{0} uses melee towards {1}", new Object[]{creep.getName(), target.getName()});
        Action attack = actor.getAttack(attackType);
        if (attack != null) {
            actor.setFacing(Toolkit.getDirection(actor.getCenterXPos(), actor.getCenterYPos(), xCoor, yCoor));
            actor.useAction(attack.getName(), xCoor, yCoor);
        } else {
            Mists.logger.log(Level.INFO, "{0} tried to use "+attackType.toString()+", but it wasn''t available", actor.getName());
        }
    }
    
    public static void useAttackTowardsMob(Creature actor, int targetID, ActionType attackType) {
        MapObject target = actor.getLocation().getMapObject(targetID);
        if (target == null) return;
        int xCoor = (int)target.getCenterXPos();
        int yCoor = (int)target.getCenterYPos();
        useAttackTowardsCoordinates(actor, xCoor, yCoor, attackType);
    }
    
    
    public static void dropItem (Creature actor, int itemID) {
        Inventory.dropItem(actor.getInventory(), itemID);
    }
    
    public static void equipItem (Creature actor, int itemID) {
        Inventory.equipItem(actor.getInventory(), itemID);
    }
    
    public static void useItem (Creature actor, int itemID) {
        Inventory.useItem(actor.getInventory(), itemID);
    }
    
    public static void takeItem (Creature actor, int itemContainerID, int itemID) {
        MapObject mob = actor.getLocation().getMapObject(itemContainerID);
        if (mob instanceof HasInventory) {
            Item i = ((HasInventory) mob).getInventory().removeItem(itemID);
            actor.addItem(i);
        }
    }
    
    public static void useMapObjectTrigger(Creature actor, int targetMobID, int triggerID) {
        MapObject mob = actor.getLocation().getMapObject(targetMobID);
        if (mob != null) {
            Mists.logger.info(actor.getName()+" used trigger on "+mob.getName());
            Trigger[] triggers = mob.getTriggers();
            Mists.logger.info("Got triggers: "+Arrays.toString(triggers));
            if (triggerID < 0 || triggerID >= triggers.length) return;
            Trigger t = triggers[triggerID];
            if (t != null) t.toggle(actor);
            Mists.logger.log(Level.INFO, "Trigger {0} toggled", triggerID);
        }
    }
}
