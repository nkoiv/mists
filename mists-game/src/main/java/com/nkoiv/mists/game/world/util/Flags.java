/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.util;

import java.util.HashMap;

/**
 * Abstract class used for tracking various
 * misc variables, mainly within the MapObjects.
 * @author nikok
 */
public abstract class Flags {
    protected HashMap<String, Integer> flags = new HashMap<>();
    /**
    * Flags store any soft information for the object
    * @param flag The name of the flag
    * @param value Value for the flag (0 or less is not flagged)
    */
    public void setFlag(String flag, int value) {
        if (this.flags.containsKey(flag)) {
            this.flags.replace(flag, value);
        } else {
            this.flags.put(flag, value);
        }   
    }
    
    /**
    * Toggle flag on or off. If Flag was more than 0, it's now 0.
    * If it was less or equal to 0 or didnt exist, it's now 1
    * @param flag Flag to toggle
    */
    public void toggleFlag(String flag) {
        if (this.isFlagged(flag)) {
            this.setFlag(flag, 0);
        } else {
            this.setFlag(flag, 1);
        }
        
    }
    
    /**
    * Return the value for the given flag
    * @param flag Desired flag
    * @return Returns the value of the flag
    */
    public int getFlag(String flag) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag);
        } else {
            return 0;
        }
    }
    
    /**
    * Check if the MapObject has the given flag
    * @param flag Flag to check
    * @return returns true if MapObject has given flag at more than 0
    */
    public boolean isFlagged (String flag) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag) > 0;
        } else {
            return false;
        }
    }
}
