/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * Triggers are small semi-actions
 * that aren't tied to an actor.
 * @author nikok
 */
public interface Trigger {
    public String getDescription();
    public boolean toggle(MapObject toggler);
    public MapObject getTarget();
    public void setTarget(MapObject mob);
    public Trigger createFromTemplate();
}
