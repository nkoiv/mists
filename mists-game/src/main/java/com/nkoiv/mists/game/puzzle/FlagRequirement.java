/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.world.util.Flags;

/**
 * Monitor a Flaggable object (Mob, location...)
 * and return true if the set flag requirements are met
 * @author nikok
 */
public class FlagRequirement {
    private Flags target;
    private String flag;
    private int requiredValue;
    private boolean largerValuesAllowed;
    private boolean smallerValuesAllowed;
    
    /**
    * Constructor with default (false) values on LargerValuesAllowed
    * and SmallerValuesAllowed
     * @param target Target to monitor for the flag values
     * @param flag Flag to monitor
     * @param requiredValue Value that return true
    */
    public FlagRequirement(Flags target, String flag, int requiredValue) {
        this(target, flag, requiredValue, false, false);
    }
    
    public FlagRequirement(Flags target, String flag, int requiredValue, boolean largerValuesAllowed, boolean smallerValuesAllowed) {
        this.target = target;
        this.flag = flag;
        this.requiredValue = requiredValue;
        this.largerValuesAllowed = largerValuesAllowed;
        this.smallerValuesAllowed = smallerValuesAllowed;
    }
    
    protected boolean isComplete() {
        if (target == null) return false;
        if (target.isFlagged(flag)) {
            int flagValue = target.getFlag(flag);
            if (flagValue == this.requiredValue) return true;
            if (flagValue > this.requiredValue && this.largerValuesAllowed) return true;
            if (flagValue < this.requiredValue && this.smallerValuesAllowed) return true;
        }
        return false;
    }
    
    public void setLargerValuesAllowed(boolean largerValuesAllowed) {
        this.largerValuesAllowed = largerValuesAllowed;
    }
    
    public void setSmallerValuesAllowed(boolean smallerValuesAllowed) {
        this.smallerValuesAllowed = smallerValuesAllowed;
    }
    
}
