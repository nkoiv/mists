/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.items;

import javafx.scene.image.Image;

/**
 * Items are something that's stored in inventories.
 * Item may be represented by a map object, but item itself is not one.
 * Items are generally always generated via subclasses (?)
 * @author nikok
 */
public class Item {
    protected String name;
    protected String description;
    //private Enum.ITEMTYPE itype; //TODO: Enumerator for item types (pants, boots, weapon, book...)
    protected int weight; //TODO: Probably pointless
    protected Image image;
    
    public Item(String name, Image image) {
        this.name = name;
        this.image = image;
        this.description = "";
        this.weight = 1;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Image getImage() {
        return this.image;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public Item createFromTemplate() {
        Item i = new Item(this.name, this.image);
        i.description = this.description;
        i.weight = this.weight;
        
        return i;
    }
    
}
