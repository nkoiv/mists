/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.world.Location;

import java.util.logging.Level;

/**
 *
 * @author nikok
 */
public class DialogueTrigger implements Trigger {
    private Dialogue dialogue;
    private MapObject owner;
    private int ownerID;
    private int dialogueID;
    
    /**
     * Construct the trigger using ID's instead
     * of direct object links. Useful when the objects
     * in question havent been generated yet.
     * @param ownerID LocationID of the to-be owner of this trigger
     * @param dialogueID LibraryID of the Dialogue to be loaded
     */
    public DialogueTrigger(int ownerID, int dialogueID) {
    	this.ownerID = ownerID;
    	this.dialogueID = dialogueID;
    }
    
    public DialogueTrigger(MapObject owner, Dialogue dialogue) {
        this.owner = owner;
        this.dialogue = dialogue;
    }
    
    @Override
    public String getDescription() {
    	if (this.owner == null) updateOwner(Mists.MistsGame.getCurrentLocation());
        String s = "Talk with " + owner.getName();
        return s;
    }

    private void updateOwner(Location loc) {
    	this.owner = loc.getMapObject(ownerID);
    }
    
    private void updateDialogue() {
    	this.dialogue = Mists.dialogueLibrary.getDialogue(dialogueID);
    }
    
    @Override
    public boolean toggle(MapObject toggler) {
    	if (this.owner == null) updateOwner(toggler.getLocation());
    	if (this.dialogue == null) updateDialogue();
        Mists.logger.log(Level.INFO, "Initiating dialogue between {0} and {1}", new Object[]{owner.getName(), toggler.getName()});
        dialogue.initiateDialogue(owner, toggler);
        //TODO:Fix this bubblegum thing...
        if (Mists.MistsGame.currentState instanceof LocationState) {
            ((LocationState)Mists.MistsGame.currentState).openDialogue(this.dialogue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public MapObject getTarget() {
    	if (this.owner == null) updateOwner(Mists.MistsGame.getCurrentLocation());
        return this.owner;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.owner = mob;
    }

    @Override
    public Trigger createFromTemplate() {
        DialogueTrigger t = new DialogueTrigger(this.owner, this.dialogue);
        t.ownerID = this.ownerID;
        t.dialogueID = this.dialogueID;
        return t;
    }

	@Override
	public void write(Kryo kryo, Output output) {
		if (this.owner != null) this.ownerID = owner.getID();
		//TODO: Dialogue into ID?
		output.writeInt(this.dialogueID);
		output.writeInt(this.ownerID);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.dialogueID = input.readInt();
		this.ownerID = input.readInt();
	}
    
}
