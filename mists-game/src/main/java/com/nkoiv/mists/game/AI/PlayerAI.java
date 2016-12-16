/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.world.pathfinding.Path;
import java.util.ArrayDeque;

/**
 * PlayerAI is the support for semi-automated player actions
 * such as queueing up actions or moving on a predetermined path
 * @author nikok
 */
public class PlayerAI {
    protected PlayerCharacter player;
    protected ArrayDeque<Task> taskQueue;
    private Task idleTask;
    
    public PlayerAI(PlayerCharacter player) {
        this.player = player;
        this.taskQueue = new ArrayDeque<>();
        idleTask = new Task(GenericTasks.ID_IDLE, player.getID(), null);
    }
    
    public Task getTask() {
        Task t = taskQueue.getFirst();
        if (t.taskID != GenericTasks.ID_MOVE_TOWARDS_COORDINATES) taskQueue.pop();
        return t;
    }
    
    public boolean hasTasks() {
        return !this.taskQueue.isEmpty();
    }
    
    public void clearTasks() {
        this.taskQueue.clear();
    }
    
    public void addTask(Task t) {
        this.taskQueue.addLast(t);
    }
    
    public void orderMovement(double xTarget, double yTarget) {
        Path movementPath = player.getLocation().getPathFinder().findPath(32, player.getCrossableTerrain(), player.getXPos(), player.getYPos(), xTarget, yTarget);
        Mists.logger.info("Setting movement to:" +movementPath.toString());
        for (int i = 1; i < movementPath.getLength(); i++) {
            int nextTileX = movementPath.getNode(i).getX();//*pathToMob.getNode(0).getSize();
            int nextTileY = movementPath.getNode(i).getY();//*pathToMob.getNode(0).getSize();
            int nodeSize = player.getLocation().getCollisionMap().getNodeSize();            
            Task t = new Task(GenericTasks.ID_MOVE_TOWARDS_COORDINATES, player.getID(), new double[]{nextTileX*nodeSize+nodeSize/2, nextTileY*nodeSize+nodeSize/2});
            taskQueue.add(t);
        }
        //Finally add a task to move to the actual given coordinates
        Task t = new Task(GenericTasks.ID_MOVE_TOWARDS_COORDINATES, player.getID(), new double[]{xTarget, yTarget});
        taskQueue.add(t);
        
    }
    
    public void updateTasks() {
        //update movement
        if (hasTasks() && taskQueue.getFirst().taskID == GenericTasks.ID_MOVE_TOWARDS_COORDINATES ){
            Task t = taskQueue.getFirst();
            if (Math.abs(t.arguments[0] - player.getXPos()) <= 20 &&
                    Math.abs(t.arguments[1] - player.getYPos()) <=20) { //We're really close to our target
                taskQueue.pollFirst(); //Remove the first element
                Mists.logger.info("Player movement task completed");
            }
        }
    }
}
