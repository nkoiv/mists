/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.dialogue.linktriggers;

import com.nkoiv.mists.game.gameobject.MapObject;

/**
 *
 * @author nikok
 */
public class LinkSetFlagTrigger implements LinkTrigger {
    private String flag;
    private int flagValue;
    private boolean flagOwner;
    private boolean flagTalker;
    private boolean flagLocation;
    
    /**
     * On default, the LinkSetFlagTrigger doesn't specify
     * who is getting flagged. That is done separately by the
     * "setFlagNNN()" methods. Everything that is set to be flagged
     * gets flagged on the toggle(), multiple targets are okay.
     * @param flag
     * @param flagValue 
     */
    public LinkSetFlagTrigger(String flag, int flagValue) {
        this.flag = flag;
        this.flagValue = flagValue;
    }
    
    public void setFlagOwner(boolean enableFlagging) {this.flagOwner=enableFlagging;}
    public void setFlagTalker(boolean enableFlagging) {this.flagTalker=enableFlagging;}
    public void setFlagLocation(boolean enableFlagging) {this.flagLocation=enableFlagging;}
    
    @Override
    public boolean toggle(MapObject owner, MapObject talker) {
        if (!flagOwner && !flagTalker && !flagLocation) {
            return false; //return false as nothing was done
        } else {
            if (flagOwner) owner.setFlag(flag, flagValue);
            if (flagTalker) talker.setFlag(flag, flagValue);
            if (flagLocation) owner.getLocation().setFlag(flag, flagValue);
            return true;
        }
    }
    
}
