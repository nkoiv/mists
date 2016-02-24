/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        if (this.localized) return originalText;
        else return localizedText;
    }
    
    /**
     * Localize changes the variables in the text into
     * actual in-game names for things.
     * @param owner The MapObject holding the card
     * @param talker The MapObject (probably player) talking to the card holder
     */
    public void localizeText(MapObject owner, MapObject talker) {
        if (owner!=null) {
            localizedText = originalText.replaceAll("OWNER_NAME", owner.getName());
            localizedText = originalText.replaceAll("LOCATION_NAME", owner.getLocation().getName());
        }
        if (talker!=null) {
            localizedText = originalText.replaceAll("TALKER_NAME", talker.getName());
        }
        this.localized = true;
    }
    
    public boolean isLocalized() {
        return this.localized;
    }
}
