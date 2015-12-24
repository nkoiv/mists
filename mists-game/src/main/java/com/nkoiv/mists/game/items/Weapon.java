/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.items;

import javafx.scene.image.Image;

/**
 * Weapons are things you hurt someone with
 * @author nikok
 */
public class Weapon extends Item {
    
    private int damageValue;
    //private Enum.WEAPONTYPE wtype; //TODO: Enumerator for weapontypes (sword, axe...)
    //private Enum.DAMAGETYPE dtype; //TODO: Enumerator for damagetypes (piercing, slashing, fire...)
    
    public Weapon(String name, ItemType itype, Image image) {
        super(name, itype, image);
    }
    
    public Weapon(String name, ItemType itype, String description, int damage, Image image) {
        super(name, itype, image);
        this.description = description;
        this.damageValue = damage;
    }
    
    public int getDamageValue() {
        return this.damageValue;
    }
    
    @Override
    public Weapon createFromTemplate() {
        Weapon w = new Weapon(this.name, this.itype, this.description, this.damageValue, this.image);
        
        return w;
    }
    
}
