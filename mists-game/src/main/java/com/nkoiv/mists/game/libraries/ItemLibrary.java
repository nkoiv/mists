/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.actions.AttackAction;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.items.Weapon;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author nikok
 * @param <E> Type of items stored in the library
 */
public class ItemLibrary <E extends Item> {
    private HashMap<String, E> lib;
    
    public ItemLibrary() {
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
    private static void prepareAdd(Item e) {
        if (e instanceof Weapon) {
            prepareWeapon((Weapon)e);
        }
        
    }
    
    private static void prepareWeapon(Weapon w) {
        
    }
    
}
