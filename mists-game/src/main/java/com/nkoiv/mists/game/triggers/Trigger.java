/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.esotericsoftware.kryo.KryoSerializable;
import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * Triggers are small semi-actions
 * that aren't tied to an actor.
 * @author nikok
 */
public interface Trigger extends KryoSerializable {
    public String getDescription();
    public boolean toggle(MapObject toggler);
    public MapObject getTarget();
    public void setTarget(MapObject mob);
    public Trigger createFromTemplate();

}
