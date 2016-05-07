/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.dialogue.linktriggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.HasInventory;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.items.Item;

/**
 *
 * @author nikok
 */
public class LinkGiveItemToTalkerTrigger implements LinkTrigger {
    private int itemID;
    private int itemCount;
    
    public LinkGiveItemToTalkerTrigger(int itemID, int itemCount) {
        this.itemID = itemID;
        this.itemCount = itemCount;
    }
    
    
    /**
     * Return false if the item couldn't be
     * given to the talker for whatever reason
     * (inventory full, no inventory...)
     * @param owner MapObject hosting the Dialogue
     * @param talker MapObject initiating the Dialogue
     * @return 
     */
    @Override
    public boolean toggle(MapObject owner, MapObject talker) {
        if (talker instanceof HasInventory) {
            int space = ((HasInventory)talker).getInventory().getFreeSpace();
            if (space < itemCount) return false;
            else {
                //Give the items to the talker
                for (int i = 0; i < itemCount; i++) {
                    Item itemToGive = Mists.itemLibrary.create(itemID);
                    ((HasInventory)talker).addItem(itemToGive);
                }
                return true;
            }
        }else {
            //Can't give item to someone who has no inventory
            return false;
        }
    }
    
}
