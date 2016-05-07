/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.dialogue.linktriggers;

import com.nkoiv.mists.game.gameobject.HasInventory;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.items.Inventory;

/**
 *
 * @author nikok
 */
public class LinkConsumeItemFromTalkerTrigger implements LinkTrigger {
    private int itemID;
    private int itemCount;
    
    public LinkConsumeItemFromTalkerTrigger(int itemID, int itemCount) {
        this.itemID = itemID;
        this.itemCount = itemCount;
    }
    
    /**
     * Return true if the items were consumed succesfully.
     * False if items couldn't be consumed (there werent enough etc).
     * If talker has less than required amount of items, no
     * items are consumed.
     * @param owner MapObject hosting the Dialogue
     * @param talker MapObject initiating the Dialogue
     * @return 
     */
    @Override
    public boolean toggle(MapObject owner, MapObject talker) {
        if (talker instanceof HasInventory) {
            if (talkerHasRequireNumberOfItems(talker, itemID, itemCount)) {
                //TODO: Potential concurrency-problem: Amount of items might change between checking and removing?
                for (int i = 0; i < itemCount; i++) {
                    ((HasInventory)talker).getInventory().removeItemByID(itemID);
                }
            } else {
                //Talker didn't have enough of the required items
                return false;
            }
        }
        //Return false if talker doesn't even have an inventory
        return false;
    }
    
    public boolean talkerHasRequireNumberOfItems(MapObject talker, int itemID, int itemCount) {
        Inventory inv = ((HasInventory)talker).getInventory();
        int itemsFound = 0;
        for (int i = 0; i < inv.getCapacity(); i ++) {
            if (inv.getItem(i).getBaseID() == itemID) itemsFound++;
        }
        return (itemsFound >= itemCount);
    }
    
}