/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.dialogue;

import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * Localizable Texts are essentially strings with variables in them.
 * Most common instance is when the player (or some other non-static 
 * map object) is referred by name.
 * @author nikok
 */
public abstract class LocalizableText {
    public static String OWNER_NAME = "OWNER_NAME";
    public static String TALKER_NAME = "TALKER_NAME";
    public static String LOCATION_NAME = "LOCATION_NAME";
    
    protected String originalText;
    protected String localizedText;
    protected boolean localized;
    
    public String getText() {
        if (!this.localized) return originalText;
        else return localizedText;
    }
    
    /**
     * Localize changes the variables in the text into
     * actual in-game names for things.
     * @param owner The MapObject holding the card
     * @param talker The MapObject (probably player) talking to the card holder
     */
    public void localizeText(MapObject owner, MapObject talker) {
        localizedText = originalText;
        if (owner!=null) {
            localizedText = localizedText.replaceAll("OWNER_NAME", owner.getName());
            if (owner.getLocation()!=null) localizedText = localizedText.replaceAll("LOCATION_NAME", owner.getLocation().getName());
        }
        if (talker!=null) {
            localizedText = localizedText.replaceAll("TALKER_NAME", talker.getName());
        }
        this.localized = true;
    }
    
    public boolean isLocalized() {
        return this.localized;
    }
    
    public void setLocalized(boolean localized) {
        this.localized = localized;
    }
}
