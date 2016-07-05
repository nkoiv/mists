/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.dialogue;

import java.util.HashMap;
import java.util.Stack;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.Location;

/**
 * DialogueManager maintains the active dialogue states, ensuring
 * each dialogue owner continues from intended spot.
 * 
 * Dialogues are stored in two ways: Foremost dialogue storage is
 * done by LocationID-MobID-Dialogue -chain. However since mobs without
 * location lack the forementioned identifiers, secondary dialoguestorage
 * happens in "waitingDialogues", where dialogues are keyed directly
 * to a map object. When a dialogue is called from there, it's moved to the
 * normal storage.
 */
public class DialogueManager implements KryoSerializable {
	private TreeMap<Integer, HashMap<Integer, Dialogue>> openDialogues;
	private HashMap<String, Dialogue> waitingDialogues;
	
	public DialogueManager() {
		this.openDialogues = new TreeMap<>();
		this.waitingDialogues = new HashMap<>();
	}
	
	public void setDialogue(String mobName, int dialogueID) {
		Dialogue d = Mists.dialogueLibrary.getDialogue(dialogueID);
		if (d != null) waitingDialogues.put(mobName, d);
		
	}
	
	public void setDialogue(MapObject mob, int dialogueID) {
		Dialogue d = Mists.dialogueLibrary.getDialogue(dialogueID);
		if (d != null) setDialogue(mob, d);
	}
	
	public void setDialogue(MapObject mob, Dialogue dialogue) {
		if (mob.getID() != 0 && mob.getLocation() != null) {
			setDialogue(mob.getLocation().getBaseID(), mob.getID(), dialogue);
		} else {
			waitingDialogues.put(mob.getName(), dialogue);
		}
		
	}
	
	public void setDialogue(int locationID, int mobID, Dialogue dialogue) {
		if (!openDialogues.containsKey(locationID)) {
			openDialogues.put(locationID, new HashMap<Integer, Dialogue>());
		}
		openDialogues.get(locationID).put(mobID, dialogue);
	}
	
	public Dialogue getDialogue(MapObject mob) {
		if (mob.getID() != 0 && mob.getLocation() != null && getDialogue(mob.getLocation().getBaseID(), mob.getID()) != null) {
			return getDialogue(mob.getLocation().getBaseID(), mob.getID());
		} 
		if (waitingDialogues.containsKey(mob.getName())) {
			Dialogue d = waitingDialogues.get(mob.getName());
			waitingDialogues.remove(mob.getName());
			//Note that this remove/add can simply re-add the dialogue in waitingDialogues if the mob remains unplaced.
			setDialogue(mob, d);
			return d;
		} 
		return null;
	}
	
	public Dialogue getDialogue(int locationID, int mobID) {
		if (openDialogues.containsKey(locationID)) {
			return openDialogues.get(locationID).get(mobID);
		}
		return null;
	}
	
	/**
	 * Purge unneeded dialogues from given location.
	 * Any dialogue lacking linked MapObject is removed.
	 * @param locationID ID of the Location to perform the purge on
	 */
	public void cleanup(int locationID) {
		Location loc = Mists.MistsGame.getLocation(locationID);
		if (loc == null) return;
		Stack<Integer> cleanable = new Stack<>();
		for (Integer mobID : openDialogues.get(locationID).keySet()) {
			if (loc.getMapObject(mobID) == null) cleanable.push(mobID);
		}
		while (!cleanable.isEmpty()) {
			int id = cleanable.pop();
			openDialogues.get(locationID).remove(id);
		}
	}

	@Override
	public void write(Kryo kryo, Output output) {
		//Write the amount of locations dialogue is stored for
		output.writeInt(this.openDialogues.keySet().size());
		for (int key : openDialogues.keySet()) {
			output.writeInt(key);
			HashMap<Integer, Dialogue> d = openDialogues.get(key);
			//Write the amount of dialogue tied to this particular location
			int dialogueCount = d.keySet().size();
			output.writeInt(dialogueCount);
			for (int i : d.keySet()) {
				//Write each dialogue along with the mobID it's tied to
				output.writeInt(i);
				kryo.writeClassAndObject(output, d.get(i));
			}
		}
		
	}

	@Override
	public void read(Kryo kryo, Input input) {
		int locationCount = input.readInt();
		Mists.logger.info("Reading dialogues for "+locationCount+" locations");
		for (int l = 0; l < locationCount; l++) {
			int locationID = input.readInt();
			int dialogues = input.readInt();
			Mists.logger.info("LocationID: "+locationID+" has "+dialogues+" dialogues...");
			for (int d = 0; d < dialogues; d++) {
				int mobID = input.readInt();
				Dialogue dialogue = (Dialogue)kryo.readClassAndObject(input);
				this.setDialogue(locationID, mobID, dialogue);
				Mists.logger.info("Dialogue for mobID "+mobID+" set to "+dialogue.toString());
			}
		}
	}
	
}
