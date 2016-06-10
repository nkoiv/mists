/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.actions.AttackAction;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * ActionLibrary stores valid generic actions
 * for mobs to use. It's mainly used when
 * spawning creatures from templates.
 * @author nikok
 * @param <E> The type of action stored in the actionlibrary
 */
public class ActionLibrary <E extends Action> {
    private HashMap<String, E> lib;
    private HashMap<Integer, String> libByID;
    
    public ActionLibrary() {
        this.lib = new HashMap<>();
        this.libByID = new HashMap<>();
    }
    
    private int getNextFreeID(int id) {
    	int nextFreeID = id;
    	while (this.libByID.containsKey(nextFreeID)) {
    		nextFreeID++;
    	}
    	if (id != nextFreeID) Mists.logger.warning("ActionLibrary ID conflict. ID "+id+" changed to "+nextFreeID);
    	return nextFreeID;
    }
    
    public E getTemplate(int actionID) {
    	return this.getTemplate(this.libByID.get(actionID));
    }
    
    public E getTemplate(String actionName) {
        String lowercase = actionName.toLowerCase();
        return this.lib.get(lowercase);
    }
    
    public E create(int actionID) {
    	return this.create(this.libByID.get(actionID));
    }
    
    public E create(String actionName) {
        String lowercase = actionName.toLowerCase();
        if (this.lib.keySet().contains(lowercase)) {
            return (E)this.lib.get(lowercase).createFromTemplate();
        }
        else {
            return null;
        }
    }
    
    public void addTemplate(E e) {
        prepareAdd(e);
        String lowercasename = e.getName().toLowerCase();
        this.lib.put(lowercasename, e);
        e.setID(this.getNextFreeID(e.getID()));
        this.libByID.put(e.getID(), lowercasename);
        Mists.logger.log(Level.INFO, "{0} added into library", e.getName());
    }
    
    /**
     * PrepareAdd makes sure no broken stuff gets in the library
     * Also cleans up unneeded values from them. 
     * 
     * @param e 
     */
    private static void prepareAdd(Action e) {
        if (e instanceof AttackAction) {
            prepareAttackAction((AttackAction)e);
        }
        
    } 
    
    private static void prepareAttackAction(AttackAction e) {
        Mists.logger.info("Prepared "+e.toString()+" for library addition");
    }
}
