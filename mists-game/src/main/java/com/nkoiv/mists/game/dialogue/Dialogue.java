/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.dialogue;

import com.nkoiv.mists.game.actions.Trigger;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.HashMap;

/**
 * Dialogue is a conversation between participants.
 * Generally Dialogue is owned by one party (Non-PlayerCharacter) and
 * participated by another party (PlayerCharacter).
 * Dialogue is traversed by choosing Dialogue options, which either
 * lead to further Dialogue or end it.
 * 
 * @author nikok
 */
public class Dialogue {
    private int startingCard;
    private int currentCard;
    private HashMap<Integer, Card> cards;
    
    private MapObject owner;
    private MapObject talker;
    
    public Dialogue() {
        this.startingCard = 1;
        this.currentCard = startingCard;
        this.cards = new HashMap<>();
    }
    
    public void addCard(int cardID, Card card) {
        this.cards.put(cardID, card);
    }
    
    public void initiateDialogue(MapObject owner, MapObject talker) {
        //Reset the localization if some participants changed
        if (this.owner != owner || this.talker != talker) {
            for (int id : this.cards.keySet()) {
                this.cards.get(id).setLocalized(false);
            }
        }
        this.owner = owner;
        this.talker = talker;
        this.moveToCard(startingCard);
    }
    
    /**
     * Move to a different part of conversation.
     * If the cardID specified is not found in the conversation,
     * the conversation is ended.
     * By default all negative ID's end conversation.
     * @param cardID
     * @return True if conversation continues, False if it's terminated
     */
    public boolean moveToCard(int cardID) {
        if (cardID < 0) return false;
        if (!this.cards.containsKey(cardID)) return false;
        Card c = this.cards.get(cardID);
        if (!c.isLocalized()) c.localizeText(owner, talker);
        this.currentCard = cardID;
        return true;
    }
    
    public void setStartingCard(int cardID) {
        this.startingCard = cardID;
    }
    
    public int getCardNumber() {
        return this.currentCard;
    }
    
    public Card getCurrentCard() {
        return this.cards.get(currentCard);
    }

    public void setCurrentCard(int cardID) {
        this.currentCard = cardID;
    }
    
    public void reset() {
        this.initiateDialogue(this.owner, this.talker);
    }
    
    
    public Dialogue createFromTemplate() {
        Dialogue d = new Dialogue();
        d.setStartingCard(this.startingCard);
        for (int i : this.cards.keySet()) {
            Card c = this.cards.get(i);
            Card cn = c.createFromTemplate();
            d.addCard(i, cn);
        }
        return d;
    }
    
    class DialogueTrigger implements Trigger {
        private Dialogue dialogue;
        private MapObject owner;
        
        public DialogueTrigger(MapObject owner, Dialogue dialogue) {
            this.owner = owner;
            this.dialogue = dialogue;
        }
        
        @Override
        public String getDescription() {
            String s = "Talk with " + owner.getName();
            return s;
        }

        @Override
        public boolean toggle(MapObject toggler) {
            dialogue.initiateDialogue(owner, toggler);
            //TODO: Open the UI window for dialogue
            throw new UnsupportedOperationException("This should open the UI window for dialogue");
        }

        @Override
        public MapObject getTarget() {
            return this.owner;
        }

        @Override
        public void setTarget(MapObject mob) {
            this.owner = mob;
        }

        @Override
        public Trigger createFromTemplate() {
            DialogueTrigger t = new DialogueTrigger(this.owner, this.dialogue);
            return t;
        }
        
    }
}
