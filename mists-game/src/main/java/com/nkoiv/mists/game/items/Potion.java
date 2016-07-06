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
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.HashMap;
import javafx.scene.image.Image;

/**
 * Potions are consumable items that affect
 * the user in some way. Good examples of
 * potions are Healing Potions, which increase
 * user health by given amount.
 * @author nikok
 */
public class Potion extends Item {
    private HashMap<String, Integer> givesFlags;
    private int healing;
    
    public Potion() {
    	super();
    	this.givesFlags = new HashMap<>();
    }
    
    public Potion(int baseID, String name, Image image) {
        super(baseID, name, ItemType.CONSUMABLE, image);
        this.weight = 1;
        this.consumedOnUse = true;
        this.givesFlags = new HashMap<>();
    }
    
    protected boolean drink(Creature drinker) {
        if (this.healing >= 0) {
            if (drinker.getHealth() < drinker.getMaxHealth()) {
                drinker.healHealth(healing);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean use(MapObject target) {
        //Mists.logger.info("Potion being used by "+target.getName());
        if (target instanceof Creature) {
            return this.drink((Creature)target);
        }
        return false;
    }
    
    protected void causeFlags(Creature target) {
        if (givesFlags != null && !givesFlags.isEmpty()) {
            for (String f : this.givesFlags.keySet()) {
                target.setFlag(f, givesFlags.get(f));
            }
        }
    }
    
    public void addGivenFlag(String flag, int value) {
        this.givesFlags.put(flag, value);
    }
    
    public void setHealingDone(int healing) {
        this.healing = healing;
    }
    
    @Override
    public Potion createFromTemplate() {
        Potion p = new Potion(this.baseID,this.name, this.image);
        p.description = this.description;
        p.weight = this.weight;
        p.healing = this.healing;
        if (givesFlags != null && !givesFlags.isEmpty()) {
            for (String f : this.givesFlags.keySet()) {
                p.givesFlags.put(f, givesFlags.get(f));
            } 
        }
        
        return p;
    }
    
    @Override
    public void write(Kryo kryo, Output output) {
        super.write(kryo, output);
        output.writeInt(this.healing);
        output.writeInt(this.givesFlags.keySet().size());
        for (String s : this.givesFlags.keySet()) {
        	output.writeString(s);
        	output.writeInt(this.givesFlags.get(s));
        }
    }

    @Override
    public void read(Kryo kryo, Input input) {
        super.read(kryo, input);
        this.healing = input.readInt();
        int flagKeys = input.readInt();
        for (int b = 0; b < flagKeys; b++) {
        	String s = input.readString();
        	int i = input.readInt();
        	this.givesFlags.put(s, i);
        }
    }
}
