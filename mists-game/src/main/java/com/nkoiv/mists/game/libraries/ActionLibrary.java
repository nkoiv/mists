/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    public ActionLibrary() {
        this.lib = new HashMap<>();
    }
    
    public E getTemplate(String actionName) {
        String lowercase = actionName.toLowerCase();
        return this.lib.get(lowercase);
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
