/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.items;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import javafx.scene.image.Image;

/**
 * Weapons are things you hurt someone with
 * @author nikok
 */
public class Weapon extends Item {
    
    private int damageValue;
    //private Enum.WEAPONTYPE wtype; //TODO: Enumerator for weapontypes (sword, axe...)
    //private Enum.DAMAGETYPE dtype; //TODO: Enumerator for damagetypes (piercing, slashing, fire...)
    
    public Weapon(int baseID, String name, ItemType itype, Image image) {
        super(baseID, name, itype, image);
    }
    
    public Weapon(int baseID, String name, ItemType itype, String description, int damage, Image image) {
        super(baseID, name, itype, image);
        this.description = description;
        this.damageValue = damage;
    }
    
    public int getDamageValue() {
        return this.damageValue;
    }
    
    @Override
    public Weapon createFromTemplate() {
        Weapon w = new Weapon(this.baseID, this.name, this.itype, this.description, this.damageValue, this.image);
        
        return w;
    }
    
    @Override
    public void write(Kryo kryo, Output output) {
        output.writeInt(baseID);
        output.writeString(name);
        output.writeString(description);
        output.writeInt(weight);
        output.writeInt(damageValue);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        int id = input.readInt();
        String n = input.readString();
        String d = input.readString();
        int w = input.readInt();
        int dv = input.readInt();

        //-----
        this.baseID = id;
        this.name = n;
        this.image = Mists.itemLibrary.getTemplate(id).getImage();
        this.description = d;
        this.weight = w;
        this.damageValue = dv;
    }
}
