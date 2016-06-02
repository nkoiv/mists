/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * FlaggerTrigger flags a target with a given flag
 * upon triggering
 * @author nikok
 */
public class FlaggerTrigger  implements Trigger {
    private MapObject target;
    private String flag;
    private int flagValue;

    public FlaggerTrigger(MapObject target, String flag, int flagValue) {
        this.target = target;
        this.flag = flag;
        this.flagValue = flagValue;
    }
    
    @Override
    public String getDescription() {
        return "Flagger: "+flag+":"+flagValue;
    }

    @Override
    public boolean toggle(MapObject toggler) {
        target.setFlag(flag, flagValue);
        return true;
    }

    @Override
    public MapObject getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.target = mob;
    }

    @Override
    public Trigger createFromTemplate() {
        return new FlaggerTrigger(this.target, this.flag, this.flagValue);
    }
    
}
