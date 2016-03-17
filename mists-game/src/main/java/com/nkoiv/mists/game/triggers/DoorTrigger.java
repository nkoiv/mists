/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * Open/Close door on triggering
 * @author nikok
 */
public class DoorTrigger implements Trigger {
    private Door door;

    public DoorTrigger(Door d) {
        this.door = d;
    }

    @Override
    public boolean toggle(MapObject toggler) {
        this.door.toggle();
        return true;
    }

    @Override
    public MapObject getTarget() {
        return this.door;
    }

    @Override
    public void setTarget(MapObject mob) {
        if (mob instanceof Door) this.door = (Door)mob;
    }

    @Override
    public String getDescription() {
        if (this.door.isOpen()) return "Close door";
        else return "Open door";
    }

    @Override
    public DoorTrigger createFromTemplate() {
        DoorTrigger d = new DoorTrigger(this.door);
        return d;
    }

}



