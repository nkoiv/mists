/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.items;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import javafx.scene.image.Image;

/**
 * Weapons are things you hurt someone with
 * @author nikok
 */
public class Weapon extends Item {
    
    private int damageValue;
    //private Enum.WEAPONTYPE wtype; //TODO: Enumerator for weapontypes (sword, axe...)
    //private Enum.DAMAGETYPE dtype; //TODO: Enumerator for damagetypes (piercing, slashing, fire...)
    
    public Weapon() {
    	super();
    }
    
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
        super.write(kryo, output);
        output.writeInt(this.damageValue);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        super.read(kryo, input);
        this.damageValue = input.readInt();
    }
}
