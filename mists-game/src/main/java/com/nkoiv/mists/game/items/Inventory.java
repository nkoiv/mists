/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.items;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.Arrays;
import java.util.Stack;

/**
 * Inventory is a storage container for items
 * @author nikok
 */
public class Inventory implements KryoSerializable {
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
     * Force an item into given slot, expanding inventory if needed.
     * Whatever was in that slot gets overwritten.
     */
    public void forceItemIntoSlot(Item i, int slot) {
        if (slot == -1) return;
        if (items.length < slot) expand(slot-items.length);
        if (items[slot] == null) itemSize++;
        items[slot] = i;
    }
    
    public Item[] getAllItems() {
        Stack<Integer> itemIDs = new Stack<>();
        for (int i = 0; i < this.items.length; i++) {
            if (items[i] != null) itemIDs.add(i);
        }
        Item[] newItems = new Item[itemIDs.size()];
        int spot = 0;
        while (!itemIDs.isEmpty()) {
            newItems[spot] = items[itemIDs.pop()];
            spot++;
        }
        return newItems;
    }
    
    public Item[] takeAllItems() {
        Item[] newItems = getAllItems();
        for (Item i : this.items) {
            i = null;
        }
        return newItems;
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
    
    public int getItemCount(int itemID) {
        if (this.items == null) return 0;
        int itemCount = 0;
        for (Item item : this.items) {
            if (item != null) {
                if (item.baseID == itemID) itemCount++;
            }
        }
        return itemCount;
    }
    
    public Item getItemFromSlot(String slotname) {
        int id = -1;
        for (int i = 0; i < this.slotnames.length; i ++) {
            if (this.slotnames[i].equals(slotname)) id =i;
        }
        return this.getItem(id);
    }
    
    public Item getItemByID(int itemID) {
        for (Item i : this.items) {
            if (i != null && i.baseID == itemID) return i;
        }
        return null;
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
    
    public Item removeItemByID(int itemID) {
        for (int i = 0; i< this.items.length; i++) {
            if (this.items[i] != null && this.items[i].getBaseID() == itemID) return this.removeItem(i);
        }
        return null;
    }
    
    public Item removeItemByName(String itemname) {
        for (int i = 0; i< this.items.length; i++) {
            if (this.items[i] != null && this.items[i].getName().equals(itemname)) return this.removeItem(i);
        }
        return null;
    }
    
    public boolean containsItem(int itemID) {
        for (Item i : this.items) {
            if (i !=null && i.baseID == itemID) return true;
        }
        return false;
    }
    
    public boolean containsItem(String itemName) {
        for (Item i : this.items) {
            if (i !=null && i.name.equals(itemName)) return true;
        }
        return false;
    }
    
    public boolean isFull() {
        return this.itemSize >= this.items.length;
    }
    
    public boolean isEmpty() {
        return this.itemSize == 0;
    }
    
    public int getFreeSpace() {
        return (this.items.length - this.itemSize);
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
        //Mists.logger.info("Trying to use item "+inv.getItem(slot).getName());
        boolean usedSuccesfully;
        
        if (inv.owner != null) usedSuccesfully = inv.getItem(slot).use(inv.owner);
        else usedSuccesfully = inv.getItem(slot).use();
        
        if (usedSuccesfully && inv.getItem(slot).isConsumedOnUse()) inv.removeItem(slot);
        //Mists.logger.info("Itemuse: "+usedSuccesfully);
        return usedSuccesfully;
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
            Mists.logger.info("Spawning itempile with "+droppedItem.getName()+" on the ground at "+inv.getOwner().getXPos()+", "+inv.getOwner().getYPos());
            ItemContainer itemPile = new ItemContainer(droppedItem.getName(), new Sprite(Mists.graphLibrary.getImage("blank")));
            itemPile.setPermanency(true);
            itemPile.addItem(droppedItem);
            itemPile.setRenderContent(true);
            inv.owner.getLocation().addMapObject(itemPile, inv.owner.getCenterXPos(), inv.owner.getCenterYPos());
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

    @Override
    public void write(Kryo kryo, Output output) {
    	
        output.writeInt(this.itemSize);
        for (int i = 0; i < this.itemSize; i++) {
            if (this.items[i] == null) output.writeInt(-1);
            else output.write(items[i].baseID);
        }
        
    }

    @Override
    public void read(Kryo kryo, Input input) {
    	
        this.itemSize = input.readInt();
        this.items = new Item[itemSize];
        this.slotnames = new String[itemSize];
        this.prepareInventory();
        for (int i = 0; i < itemSize; i++) {
            int itemID = input.readInt();
            if (itemID != -1) {
                this.items[i] =  Mists.itemLibrary.create(itemID);
            }
        }
        
    }
    
}
