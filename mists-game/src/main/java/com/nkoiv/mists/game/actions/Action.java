/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Combatant;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Templatable;
import com.nkoiv.mists.game.world.util.Flags;

/**
 * Action is something that someone triggers on a call.
 * It might be an attack from a creature, or it might be a trap on the floor.
 * TODO: Consider if the allById map gets bloated and if it should get cleaned
 * TODO: Maybe only unique action templates should be stored in allById
 * TODO: (?) All actions are stored in the (static) allById -map
 * @author nkoiv
 */
public class Action extends Flags implements Templatable {
    protected ActionType actionType;
    protected String name;
    protected int id;
    protected MapObject owner;
    protected ArrayList<Effect> effects;
    protected HashMap<String, Integer> flags;
    protected double currentCooldown;
    
    
    public Action(String name) {
        this(name, ActionType.SELF_CAST);
    }
    
    public Action(String name, ActionType actionType) {
        this.name = name;
        this.actionType = actionType;
        this.flags = new HashMap<>();
        this.effects = new ArrayList<>();
    }
    
    /**
    * Actions are owned by the user
    * setOwner gives this action to the MapObject
    * @param o MapObject to own the action
    */
    public void setOwner(MapObject o) {
        this.owner = o;
    }
    
    /**
    * Actions are owned by the user
    * getOwner returns the MapObject(creature?) using the action.
    * @return The owner of the action
    */
    public MapObject getOwner() {
        if (this.owner == null) return new MapObject("MISSING OWNER ON ACTION");
        return this.owner;
    }
    
    /**
    * Use -call is used when the action lands on a target
    * This should trigger the actual effects of the action (damage, slow, teleport...)
    * @param actor The target of the action
    */
    public void use(Creature actor) {
        //Override this to do things
        Mists.logger.log(Level.INFO, "{0} used by {1}", new Object[]{this.toString(), actor.getName()});
    }
    
    public void use(Creature actor, double xCoor, double yCoor) {
        //Override this to do things
        Mists.logger.log(Level.INFO, "{0} used by {1} towards {2}x{3}", new Object[]{this.toString(), actor.getName(), (int)xCoor, (int)yCoor});
    }
    

    public void hitOn(Effect e, ArrayList<MapObject> mobs) {
        Mists.logger.log(Level.INFO, "{0} hit landed {1} targets with {2}", new Object[]{this.name, mobs.size(), e.getName()});
    }
    
    public boolean directDamageHit(ArrayList<MapObject> mobs) {
        int damage = this.getFlag("damage");
        int scalingDamage = 0;
        if (this.owner instanceof Creature) {
            scalingDamage = ((Creature)owner).getAttribute("Strength");
        } //TODO: Customized scaling attribute
        damage = damage+scalingDamage;
        while (mobs.contains(this.owner))  mobs.remove(this.owner);
        if (!mobs.isEmpty() && !this.isFlagged("triggered")) {
            Mists.logger.log(Level.INFO, "{0}s {1} landed on {2}", new Object[]{this.getOwner().getName(),this.toString(), mobs.toString()});
            for (MapObject mob : mobs) {
                if (mob instanceof Combatant) {
                    if (this.getOwner() instanceof PlayerCharacter && mob instanceof Creature) {
                        //Disallow friendly fire
                        //TODO: build this into flags somehow (if mob.faction == getOwner.faction...)
                        PlayerCharacter pc =(PlayerCharacter)this.getOwner();
                        if (!pc.getCompanions().contains((Creature)mob)) {
                            Mists.logger.log(Level.INFO, "{0} Hit {1} for {2} damage", new Object[]{this.getOwner().getName(), mob.getName(), damage});
                            ((Combatant)mob).takeDamage(damage);
                        }
                    } else {
                        Mists.logger.log(Level.INFO, "Hit {0} for {1} damage", new Object[]{mob.getName(), damage});
                        ((Combatant)mob).takeDamage(damage);
                    }
                } 
            }
            this.setFlag("triggered", 1);
            return true;
        }
        return false;
    }
    
    public int getID() {
    	return this.id;
    }
    
    public void setID(int id) {
    	this.id = id;
    }
    
    public String getName() {
        return this.name;
    }

    public ActionType getActionType() {
        return this.actionType;
    }
    
    public void tickCooldown(double time) {
        double time_milliseconds = time * 1000;
        if (currentCooldown == 0) return;
        this.currentCooldown = (currentCooldown - time_milliseconds);
        //Mists.logger.info(this.name+" cooldown ticking. Remaining: "+currentCooldown);
        if (currentCooldown < 0) currentCooldown = 0;
    }
    
    public double getCooldown() {
        return this.getFlag("cooldown");
    }
    
    public double getRemainingCooldown() {
        return currentCooldown;
    }
    
    public boolean isOnCooldown() {
        return (currentCooldown > 0);
    }
    
    public Action createFromTemplate() {
        Action a = new Action(this.name);
        for (String flag : this.flags.keySet()) {
            a.setFlag(flag, this.flags.get(flag));
        }
        return a;
    }
    
	@Override
	public void write(Kryo kryo, Output output) {
		super.write(kryo, output);
		output.writeInt(id);
		output.writeString(this.name);
	}


	@Override
	public void read(Kryo kryo, Input input) {
		super.read(kryo, input);
		this.id = input.readInt();
		this.name = input.readString();
	}
    
    @Override
    public String toString() {
        return name;
    }
    
}
