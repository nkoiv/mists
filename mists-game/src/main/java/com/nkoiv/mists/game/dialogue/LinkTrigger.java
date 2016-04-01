/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.dialogue;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.HasInventory;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.quests.Quest;

/**
 * LinkTriggers are something that manipulate the game world
 * when a link is clicked.
 * @author nikok
 */
public interface LinkTrigger {
    
    public boolean toggle(MapObject owner, MapObject talker);
    
}

class LinkGiveQuestTrigger implements LinkTrigger {
    private int questID;

    public LinkGiveQuestTrigger(Quest quest) {
        this.questID = quest.getID();
    }
    
    public LinkGiveQuestTrigger(int questID) {
        this.questID = questID;
    }
    
    @Override
    public boolean toggle(MapObject owner, MapObject talker) {
        return Mists.MistsGame.questManager.openQuest(questID);
    }
    
    
}

class LinkConsumeItemFromTalkerTrigger implements LinkTrigger {
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

class LinkGiveItemToTalkerTrigger implements LinkTrigger {
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

class LinkSetFlagTrigger implements LinkTrigger {
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

