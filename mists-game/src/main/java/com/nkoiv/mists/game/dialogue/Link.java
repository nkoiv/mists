/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.dialogue;

import com.nkoiv.mists.game.gameobject.HasInventory;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.ArrayList;

/**
 * Dialogue Links take Dialogue from one Card
 * to another, assuming it's what player chooses.
 * Links might or might not have requirements
 * to appear visible and/or selectable.
 * @author nikok
 */
public class Link extends LocalizableText{
    int destinationCardID;
    ArrayList<LinkRequirement> ownerRequirements;
    ArrayList<LinkRequirement> talkerRequirements;
    
    public Link(String optionText, int destinationCardID) {
        this.destinationCardID = destinationCardID;
        this.originalText = optionText;
        this.ownerRequirements = new ArrayList<>();
        this.talkerRequirements = new ArrayList<>();
    }
    
    public int getDestinationCardID() {
        return this.destinationCardID;
    }
    
    public void addOwnerRequirement(LinkRequirement lr) {
        this.ownerRequirements.add(lr);
    }
    
    public void addTalkerRequirement(LinkRequirement lr) {
        this.talkerRequirements.add(lr);
    }
    
    /**
     * Check that all the requirements are met
     * @param owner MapObject the Dialogue is initiated on
     * @param talker MapObject initiating the dialogue
     * @return 
     */
    public boolean LinkRequirementsMet(MapObject owner, MapObject talker) {
        for (LinkRequirement lr : ownerRequirements) {
            if (lr.requirementsMet(owner)) return false;
        }
        
        for (LinkRequirement lr : talkerRequirements) {
            if (lr.requirementsMet(talker)) return false;
        }
        
        return true;
    }
    
    public Link createFromTemplate() {
        Link ln = new Link(this.originalText, this.destinationCardID);
        //TODO: Is it actually necessary or beneficial to spawn extra LinkRequirements? Why not reuse old?
        for (LinkRequirement lr : this.ownerRequirements) {
            ln.addOwnerRequirement(lr.createFromTemplate());
        }
        for (LinkRequirement lr : this.talkerRequirements) {
            ln.addTalkerRequirement(lr.createFromTemplate());
        }
        
        return ln;
    }
    
    
    public interface LinkRequirement {
        public boolean requirementsMet(MapObject mob);
        public LinkRequirement createFromTemplate();
    }
    
    public class ItemRequirement implements LinkRequirement{
        private int requiredItemID;
        
        public ItemRequirement(int requiredItemID) {
            this.requiredItemID = requiredItemID;
        }
        
        @Override
        public boolean requirementsMet(MapObject mob) {
            if (!(mob instanceof HasInventory)) return false;
            return ((HasInventory)mob).getInventory().containsItem(requiredItemID);
        }
        
        @Override
        public ItemRequirement createFromTemplate() {
            ItemRequirement ir = new ItemRequirement(this.requiredItemID);
            return ir;
        }
        
    }
    
    public class AttributeRequirement implements LinkRequirement{
        String attribute;
        int value;
        
        public AttributeRequirement(String attributeName, int attributeValue) {
            this.attribute = attributeName;
            this.value = attributeValue;
        }
        
        @Override
        public boolean requirementsMet(MapObject mob) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        @Override
        public AttributeRequirement createFromTemplate() {
            AttributeRequirement ar = new AttributeRequirement(this.attribute, this.value);
            return ar;
        }
        
    }
    
    public class FlagRequirement implements LinkRequirement{
        private String flagName;
        private int value;
        private boolean requirement;
        
        public FlagRequirement(String flag, int value, boolean requirement) {
            this.flagName = flag; this.value = value; this.requirement = requirement;
        }
        
        public void addProhibitedFlag(String flag, int value) {
            this.flagName = flag;
            this.value = value;
            this.requirement = false;
        }
        
        public void addRequiredFlag(String flag, int value) {
            this.flagName = flag;
            this.value = value;
            this.requirement = true;
        }
        
        /**
         * Check if the mob is flagged with the set flag.
         * If "requirement" is set to true, return true when
         * mob has equal or higher value on the flag.
         * If "requirement" is set to false, return true when
         * mob is below required value.
         * @param mob MapObject to check for requirements
         * @return True if mob meets the requirements, False otherwise
         */
        @Override
        public boolean requirementsMet(MapObject mob) {
            if (this.requirement) {
                if (mob.getFlag(flagName) >= value) return true;
            } else {
                if (mob.getFlag(flagName) < value) return true;
            }
            return false;
        }
        
        @Override
        public FlagRequirement createFromTemplate() {
            FlagRequirement fr = new FlagRequirement(this.flagName, this.value, this.requirement);
            
            return fr;
        }
        
    }
    
}
