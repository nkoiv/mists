/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
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
    private final HashMap<String, E> libByName;
    private final HashMap<Integer, E> lib;
    
    public ItemLibrary() {
        this.lib = new HashMap<>();
        this.libByName = new HashMap<>();
    }
    
    public E getTemplate(int itemID) {
        return this.lib.get(itemID);
    }
    
    public E getTemplate(String itemname) {
        String lowercase = itemname.toLowerCase();
        return this.libByName.get(lowercase);
    }
    
    public E create(String itemname) {
        String lowercase = itemname.toLowerCase();
        if (this.libByName.keySet().contains(lowercase)) {
            return (E)this.libByName.get(lowercase).createFromTemplate();
        }
        else {
            return null;
        }
    }
    
    public E create(int itemID) {
        if (this.lib.keySet().contains(itemID)) {
            return (E)this.lib.get(itemID).createFromTemplate();
        }
        else {
            return null;
        }
    }
    
    public void addTemplate(E e) {
        prepareAdd(e);
        String lowercasename = e.getName().toLowerCase();
        int itemID = e.getBaseID();
        this.libByName.put(lowercasename, e);
        this.lib.put(itemID, e);
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
