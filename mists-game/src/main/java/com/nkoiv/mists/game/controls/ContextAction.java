/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.actions.Trigger;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
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
    private ArrayList<MapObject> nearbyObjects;
    private Sprite triggerRadius;
    int currentTrigger;
    MapObject actor; //Usually the player?
    
    public ContextAction(MapObject actor) {
        this.actor = actor;
        this.availableTriggers = new ArrayList<>();
        this.generateTriggerRange();
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
        if (this.nearbyObjects == null) refreshNearbyObjects();
        if (this.nearbyObjects.isEmpty()) return;
        for (MapObject mob : this.nearbyObjects) {
            this.availableTriggers.addAll(Arrays.asList(mob.getTriggers()));
        }
    }
    
    private void generateTriggerRange() {
        //TODO: is 64 pixel size circle always the one we want to use?
        if (this.triggerRadius == null) this.triggerRadius = new Sprite(new Image("/images/circle.png"));
    }
    
    /**
     * Use the currently selected action
     * @return True if action was performed
     */
    public boolean useTrigger() {
        Mists.logger.log(Level.INFO, "Context trigger used. Trigger list size: {0}. Nearby objects: {1}", new Object[]{this.availableTriggers.size(), this.nearbyObjects.size()});
        for (MapObject mob : this.nearbyObjects) {
            Mists.logger.info(mob.toString());
        }
        if (availableTriggers.isEmpty()) return false;
        if (currentTrigger < 0 || currentTrigger >= availableTriggers.size()) currentTrigger = 0;
        this.availableTriggers.get(currentTrigger).toggle();
        return true;
    }
    
    public List<MapObject> getTriggerObjects() {
        ArrayList<MapObject> mobs = new ArrayList<>();
        for (Trigger t : this.availableTriggers) {
            mobs.add(t.getTarget());
        }
        return mobs;
    }
    
    public Trigger getCurrentTrigger() {
        if (this.availableTriggers == null) return null;
        if (this.availableTriggers.size() < 1) return null;
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