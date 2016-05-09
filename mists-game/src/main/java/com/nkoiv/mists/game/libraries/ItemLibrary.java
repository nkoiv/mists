/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.items.ItemType;
import com.nkoiv.mists.game.items.Potion;
import com.nkoiv.mists.game.items.Weapon;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javafx.scene.image.Image;

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
    
    public static Item generateFromYAML(Map itemData) {
        String mobtype = (String)itemData.get("type");
        switch (mobtype) {
            case "Misc": Mists.logger.info("Generating MISC ITEM");
                return generateMiscItemFromYAML(itemData);
            case "Weapon 1h": Mists.logger.info("Generating 1H WEAPON");
                return generateWeaponFromYAML(itemData);
            case "Potion": Mists.logger.info("Generating POTION");
                return generatePotionFromYAML(itemData);
            default: break;
        }        
        return null;
    }
    
    private static Weapon generateWeaponFromYAML(Map weaponData) {
        Weapon w;
        int itemID = Integer.parseInt((String)weaponData.get("id"));
        ItemType weaponType;
        String type = (String)weaponData.get("type");
        switch (type) {
            case "Weapon 1h": weaponType = ItemType.WEAPON_1H_MELEE; break;
            case "Weapon 2h": weaponType = ItemType.WEAPON_2H_MELEE; break;
            case "Ranged 1h": weaponType = ItemType.WEAPON_1H_RANGED; break;
            case "Ranged 2h": weaponType = ItemType.WEAPON_2H_RANGED; break;
            default: weaponType = ItemType.WEAPON_1H_MELEE; break;
        }
        String name = (String)weaponData.get("name");
        String description = (String)weaponData.get("description");
        Image image = new Image((String)weaponData.get("image"));
        int damage = Integer.parseInt((String)weaponData.get("damage"));
        w = new Weapon(itemID, name, weaponType, description, damage, image);
        return w;
    }
    
    private static Item generateMiscItemFromYAML(Map itemData) {
        Item i;
        int itemID = Integer.parseInt((String)itemData.get("id"));
        String name = (String)itemData.get("name");
        String description = (String)itemData.get("description");
        Image image = new Image((String)itemData.get("image"));
        i = new Item(itemID, name, ItemType.MISC, image);
        i.setDescription(description);
        return i;
    }
    
    private static Potion generatePotionFromYAML(Map itemData) {
        Potion p;
        int itemID = Integer.parseInt((String)itemData.get("id"));
        String name = (String)itemData.get("name");
        String description = (String)itemData.get("description");
        Image image = new Image((String)itemData.get("image"));
        p = new Potion(itemID, name, image);
        p.setDescription(description);
        
        if (itemData.containsKey("healing")) {
            int healing = Integer.parseInt((String)itemData.get("healing"));
            p.setHealingDone(healing);
        }
        
        return p;
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
