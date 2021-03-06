/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.controls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.nkoiv.mists.game.GameMode;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.networking.LocationClient;
import com.nkoiv.mists.game.triggers.Trigger;

import javafx.scene.image.Image;

/**
 * ContextAction looks around a player and shows up what can
 * be done at a given location. When next to a closed door, player
 * can open it - opened door can be closed. 
 * 
 * @author nikok
 */
public class ContextAction {
    private final ArrayList<Trigger> availableTriggers;
    private final HashMap<Trigger, MapObject> triggerSource;
    private ArrayList<MapObject> nearbyObjects;
    private MapObject triggerRadius;
    private int currentTrigger;
    private Creature actor; //Usually the player?
    //private LocationServer server;
    private LocationClient client;
    
    public ContextAction(Creature actor) {
        this.actor = actor;
        this.availableTriggers = new ArrayList<>();
        this.triggerSource = new HashMap<>();
        this.generateTriggerRange();
    }
    /*
    public void setLocationServer(LocationServer server) {
        this.server = server;
    }
    */
    public void setLocationClient(LocationClient client) {
        this.client = client;
    }
    
    /**
     * Refresh the list of available actions
     */
    public void update() {
        this.refreshNearbyObjects();
        this.refreshObjectTriggers();
    }
    
    /**
     * Do a collision detection on the actionRadius to
     * see what's within toggle range
     */
    private void refreshNearbyObjects() {
        if (this.triggerRadius == null) generateTriggerRange();
        triggerRadius.setCenterPosition(actor.getCenterXPos(), actor.getCenterYPos());
        this.nearbyObjects = actor.getLocation().checkCollisions(triggerRadius);
        this.nearbyObjects.remove(this.actor);
    }
    
    /**
     * Take triggers from nearby objects and add them
     * to the available triggers -list.
     */
    private void refreshObjectTriggers() {
        this.availableTriggers.clear();
        this.triggerSource.clear();
        if (this.nearbyObjects == null) refreshNearbyObjects();
        if (this.nearbyObjects.isEmpty()) return;
        for (MapObject mob : this.nearbyObjects) {
            List<Trigger> triggers = Arrays.asList(mob.getTriggers());
            if (!triggers.isEmpty()) {
                this.availableTriggers.addAll(triggers);
                for (Trigger t : triggers) {
                    this.triggerSource.put(t, mob);
                }
            }
            
        }
    }
    
    private void generateTriggerRange() {
        //TODO: is 64 pixel size circle always the one we want to use?
        if (this.triggerRadius == null) {
            this.triggerRadius = new MapObject("trigger radius",new Image("/images/circle.png"));
            //this.triggerRadius.setFlag("visible", 0); //TriggerRadius doesnt even need to be invisible, as its never placed in a location.
        }
    }
    
    public int getSelectedTriggerSourceID() {
        if (currentTrigger<0 || currentTrigger>=availableTriggers.size()) return -1;
        int sourceID = -1;
        Trigger t = this.availableTriggers.get(currentTrigger);
        if (t!=null) sourceID = this.triggerSource.get(t).getID();
        Mists.logger.info("Returning ID "+sourceID+" for trigger "+currentTrigger);
        return sourceID;
    }
    
    /**
     * Use the currently selected action
     * NOTE: This is currently unused, as triggers
     * handled via the Tasks, at LocationControls
     * @return True if action was performed
     */
    private boolean useTrigger() {
        Mists.logger.log(Level.INFO, "Context trigger used. Trigger list size: {0}. Nearby objects: {1}", new Object[]{this.availableTriggers.size(), this.nearbyObjects.size()});
        for (MapObject mob : this.nearbyObjects) {
            Mists.logger.info(mob.toString());
        }
        if (availableTriggers.isEmpty()) {
            Mists.logger.info("No triggers found");
            return false;
        }
        if (currentTrigger < 0 || currentTrigger >= availableTriggers.size()) currentTrigger = 0;
        //
        Trigger t = this.availableTriggers.get(currentTrigger);
        Task task = new Task(GenericTasks.ID_USE_TRIGGER, actor.getID(), new double[]{this.triggerSource.get(t).getID(), 0});
        actor.setNextTask(task);
        if (Mists.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(task);
        return true;
    }
    
    public List<MapObject> getTriggerObjects() {
        ArrayList<MapObject> mobs = new ArrayList<>();
        if (this.availableTriggers.isEmpty()) return mobs;
        for (Trigger t : this.availableTriggers) {
            if (t == null) return mobs;
            if (t.getTarget()!=null) {
                mobs.add(t.getTarget());
            }
        }
        return mobs;
    }
    
    public boolean setTriggerOnMobIfInRange(MapObject mob) {
        for (int i = 0; i < this.availableTriggers.size(); i++) {
            if (availableTriggers.get(i) == null) continue;
            if (availableTriggers.get(i).getTarget() == null) {
                //Do nothing as the mob was lost to void
                //TODO: Maybe log this? Dig to the bottom of it at some point
                Mists.logger.warning("Context Action tried to access a null mob");
            } else if (availableTriggers.get(i).getTarget().equals(mob)) {
                this.currentTrigger = i;
                return true;
            }
        }
        return false;
    }
    
    public Trigger getCurrentTrigger() {
        if (this.availableTriggers == null) return null;
        if (this.availableTriggers.size() < 1) return null;
        if (this.currentTrigger>=this.availableTriggers.size()) return this.availableTriggers.get(this.availableTriggers.size()-1);
        return this.availableTriggers.get(currentTrigger);
    }
    
    /**
     * Shift to the next action on the list
     * Return to first action if going over the list
     */
    public void nextAction() {
        currentTrigger++;
        if (currentTrigger < 0 || currentTrigger >= availableTriggers.size()) currentTrigger = 0;
    }
    
}