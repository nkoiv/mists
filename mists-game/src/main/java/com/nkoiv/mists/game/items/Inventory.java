/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.items;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import java.util.Arrays;

/**
 * Inventory is a storage container for items
 * @author nikok
 */
public class Inventory {
    private Creature owner;
    private Item[] items;
    private String[] slotnames;
    //private int maxSize;
    private static int defaultCapacity = 10;
    private int itemSize;
    
    public Inventory(int size) {
        this.items = new Item[size];
        this.slotnames= new String[size];
        this.prepareInventory();
    }
    
    public Inventory() {
        this(defaultCapacity);
    }
    
    public Inventory(Creature c) {
        this(c, defaultCapacity);
    }
    
    public Inventory(Creature c, int size) {
        this(size);
        this.owner = c;
    }
    
    private void prepareInventory() {
        for (int i = 0; i < items.length; i++) {
            this.slotnames[i] = Integer.toString(i);
        }
    }
    
    /**
     * Add item to the inventory
     * @param i Item to add
     * @return True if item was added successfully, False if there was no room
     */
    public boolean addItem(Item i) {
        int slot = nextFreeSlot();
        if (slot == -1) return false;
        this.items[slot] = i;
        this.itemSize++;
        return true;
    }
    
    private int nextFreeSlot() {
        for (int i = 0; i < this.items.length; i++) {
            if (items[i] == null) return i;
        }
        return -1;
    }
    
    /**
     * Add an item to a slot. If slot already contains an item,
     * return the item in the slot. If slot was empty, return null.
     * If Item was unable to be placed at all, return the item itself.
     * @param i Item to add to a slot
     * @param slotname Slot to add the item to
     * @return Item that was already in the selected slot, Item itself if slot was unavailable
     */
    public Item addItem(Item i, String slotname) {
        for (int n = 0; n < this.slotnames.length; n++) {
            if (this.slotnames[n].equals(slotname)) {
                this.addItem(i, n);
            }
        }
        return i;
    }
    
    /**
     * Add an item to the specified slot number
     * Return the item in that slot.
     * @param i Item to add
     * @param slotnumber Slot to add the item into
     * @return Item that was in the slot. Return the given item "i" if specified slot was not found.
     */
    public Item addItem(Item i, int slotnumber) {
        if (slotnumber < 0 || slotnumber >= this.items.length) return i;
        Item overflow = this.items[slotnumber];
        this.items[slotnumber] = i;
        return overflow;
    }
    
    /**
     * Set names for the slots of the inventory
     * The size of the list of names must correspond
     * to the size of the inventory.
     * @param names New names for inventory slots
     */
    public void setSlotNames(String[] names) {
        if (names.length != this.slotnames.length) return;
        this.slotnames = names;
    }
    
    public Item getItem(int slotnumber) {
        if (slotnumber < 0 || slotnumber >= this.items.length) return null;
        return this.items[slotnumber];
    }
    
    public Item getItemFromSlot(String slotname) {
        int id = -1;
        for (int i = 0; i < this.slotnames.length; i ++) {
            if (this.slotnames[i].equals(slotname)) id =i;
        }
        return this.getItem(id);
    }
    
    public Item getItemByName(String itemname) {
        for (Item i : this.items) {
            if (i != null) {
                if (i.name.equals(itemname)) return i;
            }
        }
        return null;
    }
    
    public Item removeItem(int slotnumber) {
        if (slotnumber < 0 || slotnumber >= this.items.length) return null;
        Item i = this.items[slotnumber];
        this.items[slotnumber] = null;
        this.itemSize--;
        return i;
    }
    
    public Item removeItemFromSlot(String slotname) {
        int id = -1;
        for (int i = 0; i < this.slotnames.length; i ++) {
            if (this.slotnames[i].equals(slotname)) id =i;
        }
        if (id == -1) return null;
        return removeItem(id);
    }
    
    public Item removeItemByName(String itemname) {
        int id = -1;
        for (int i = 0; i< this.items.length; i++) {
            if (this.items[i] != null) {
                if (this.items[i].getName().equals(itemname)) return this.removeItem(i);
            }
        }
        return null;
    }
    
    public boolean isFull() {
        return this.itemSize >= this.items.length;
    }
    
    public int getSize() {
        return itemSize;
    }
    
    public int getCapacity() {
        return this.items.length;
    }
    
    public void setOwner(Creature c) {
        this.owner = c;
    }
    
    public Creature getOwner() {
        return this.owner;
    }
    
    public static boolean useItem(Inventory inv, int slot) {
        if (inv.owner != null) return inv.getItem(slot).use(inv.owner);
        else return inv.getItem(slot).use();
    }
    
    public static boolean equipItem(Inventory inv, int slot) {
        //TODO: Currently just equipping weapon
        if (inv.getOwner() == null) return false;
        Mists.logger.info("Equipping item on "+inv.getOwner().getName());
        if (inv.getItem(slot) instanceof Weapon) {
            Item currentWeapon = inv.getOwner().getWeapon();
            Weapon newWeapon = (Weapon)inv.removeItem(slot);
            inv.getOwner().equipWeapon(newWeapon);
            if (currentWeapon != null) inv.addItem(currentWeapon);
            Mists.logger.info("Equipped "+newWeapon.getName());
            return true;
        }
        return false;
    }
    
    public static boolean dropItem(Inventory inv, int slot) {
            if (inv.getOwner() == null) return false;
            if (inv.getItem(slot) == null) return false;
            Item droppedItem = inv.removeItem(slot);
            //TODO: Spawn the item as a mapobject on the location
            return true;
        }
    
    public void expand(int extraSlots) {
        Item[] newInventory = Arrays.copyOf(this.items, this.items.length + extraSlots);
        String[] newNames = Arrays.copyOf(this.slotnames, this.slotnames.length + extraSlots);
        this.items = newInventory;
        this.slotnames = newNames;
    }
    
    public Item[] shrink(int slotsToReduce) {
        if (slotsToReduce > items.length) return null; //Cant reduce the size of inventory to negative
        Item[] overflow = Arrays.copyOfRange(items, items.length-slotsToReduce, items.length);
        this.expand(-slotsToReduce);
        return overflow;
    }
    
    public int contentHash() {
        int hash = 1;
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i]!=null) hash += this.items[i].getName().hashCode()/(i+17);
        }
        //Mists.logger.info("Generated inv content hash: "+hash);
        return hash;
    }
    
}
