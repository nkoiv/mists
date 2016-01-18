/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;

/**
 *
 * @author nikok
 */
public interface HasInventory {
    
    public Inventory getInventory();
    public boolean addItem(Item item);
    
}
