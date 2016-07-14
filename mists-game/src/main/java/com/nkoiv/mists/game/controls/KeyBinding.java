/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.controls;

import java.util.ArrayList;

import com.nkoiv.mists.game.Mists;

import javafx.scene.input.KeyCode;

/**
 * Commands are bound to keybindings and executed on keypress
 * @author nikok
 */
public abstract class KeyBinding {
    protected boolean enableExecuteOnKeyDown;
    protected KeyCode[] primaryKey;
    protected KeyCode[] secondaryKey;
    
    public void setPrimaryKey(KeyCode[] keys) {
        this.primaryKey = keys;
    }
    
    public void setSecondaryKey(KeyCode[] keys) {
        this.secondaryKey = keys;
    }
    
    public boolean matchingKeyPress(ArrayList<KeyCode> keyPress, ArrayList<KeyCode> keyRelease) {
        boolean primaryPressed = checkForKeyPress(keyPress, keyRelease, primaryKey);
        boolean secondaryPressed = checkForKeyPress(keyPress, keyRelease, secondaryKey);
        return (primaryPressed || secondaryPressed);
    }
    
    private boolean checkForKeyPress(ArrayList<KeyCode> keyPress, ArrayList<KeyCode> keyRelease, KeyCode[] binds) {
        if (keyPress == null || keyRelease == null || binds == null) return false;
        boolean keyPressed = true;
        for (KeyCode p : binds) {
            //All the bound keypresses need to be in either Press or Release
            if (!keyPress.contains(p) && !keyRelease.contains(p)) keyPressed = false;
            //If the key is currently held down, but enableExecuteOnKeyDown is not enabled, return false
            if (keyPress.contains(p) && !enableExecuteOnKeyDown) keyPressed = false;
        }
        return keyPressed;
    }
    
    public boolean execute() {
        Mists.logger.info("Empty command executed");
        return false;
    }
}
