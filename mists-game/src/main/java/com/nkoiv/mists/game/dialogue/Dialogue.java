/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.dialogue;

import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * Dialogue is a conversation between participants.
 * Generally Dialogue is owned by one party (Non-PlayerCharacter) and
 * participated by another party (PlayerCharacter).
 * Dialogue is traversed by choosing Dialogue options, which either
 * lead to further Dialogue or end it.
 * 
 * @author nikok
 */
public class Dialogue implements KryoSerializable {
	private int id;
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
        if (!c.isLocalized()) {
            c.localizeText(owner, talker);
            for (Link l : c.getLinks()) {
                l.localizeText(owner, talker);
            }
        }
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
    
    public MapObject getOwner() {
        return this.owner;
    }
    public MapObject getTalker() {
        return this.talker;
    }
    
    public void setID(int id) {
    	this.id = id;
    }
    
    public int getID() {
    	return this.id;
    }
    
    public Dialogue createFromTemplate() {
        Dialogue d = new Dialogue();
        d.id = this.id;
        d.setStartingCard(this.startingCard);
        for (int i : this.cards.keySet()) {
            Card c = this.cards.get(i);
            Card cn = c.createFromTemplate();
            d.addCard(i, cn);
        }
        return d;
    }
    
    @Override
    public String toString() {
        return this.cards.size()+" cards in dialogue";
    }

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeInt(this.id);
		output.writeInt(this.currentCard);
		
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.id = input.readInt();
		this.currentCard = input.readInt();
		this.startingCard = Mists.dialogueLibrary.getDialogue(this.id).startingCard;
		this.cards = Mists.dialogueLibrary.getDialogue(this.id).cards;
	}

    
}
