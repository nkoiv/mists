/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Combatant;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Action for swinging whichever (melee)weapon
 * the actor has at hand.
 * @author nikok
 */
public class MeleeWeaponAttack extends Action implements AttackAction {
    
    //private double swingPosition;
    
    public MeleeWeaponAttack() {
        super("weaponmelee", ActionType.MELEE_ATTACK);
        this.setFlag("range", 0);
        this.setFlag("animationcycles", 1);
        this.setFlag("cooldown", 500);
        this.setFlag("triggered", 0);
    }
    
    public Sprite getSprite(Creature actor) {
        Sprite attackSprite = new Sprite(actor.getWeapon().getImage());
        attackSprite.setPosition(actor.getXPos(), actor.getYPos());
        //attackSprite.setRotationPoint(5, 10);   
        attackSprite.setRotationPoint(attackSprite.getWidth()/2, attackSprite.getHeight()); //Rotate around bottom center - "the handle"
        return attackSprite;
    }
    @Override
    public void use(Creature actor) {
        if (actor.getWeapon() == null) return; //Cant attack with weapon without weapon
        if (this.isOnCooldown()) {
            //Mists.logger.log(Level.INFO, "{0} tried to use {1}, but it was on cooldown", new Object[]{actor.getName(), this.toString()});
        } else {
            try {
                Mists.soundManager.playSound("weapon_blow");
            } catch (Exception e){
                Mists.logger.warning("Sounds not available");
            }
            this.setFlag("triggered", 0);
            Mists.logger.log(Level.INFO, "{0} used by {1} towards {2}", new Object[]{this.toString(), actor.getName(), actor.getFacing()});
            this.currentCooldown = this.getFlag("cooldown");
            Double[] attackPoint = actor.getCorner(actor.getFacing());
            Effect attackEffect = new Effect(
                    this, "weaponattack",
                    this.getSprite(actor),400);
            attackEffect.getSprite().setRotation(Toolkit.getRotation(Toolkit.counterClockwise(actor.getFacing(),2)));
            //double[] swingTarget = Toolkit.getDirectionXY(Toolkit.clockwise(actor.getFacing(),2));
            //int speed = 60;
            //attackEffect.getSprite().addVelocity(swingTarget[0]*speed, swingTarget[1]*speed);
            attackEffect.getSprite().setSpin(400);
            Mists.logger.info("Swinging towards: "+Toolkit.clockwise(actor.getFacing()));
            actor.getLocation().addEffect(attackEffect,
                    (attackPoint[0]-attackEffect.getSprite().getRotationPointX()),
                    (attackPoint[1]-attackEffect.getSprite().getRotationPointY()));
            attackEffect.setLinkedObject(actor);
        }
    }
       
    @Override
    public void hitOn(Effect e, ArrayList<MapObject> mobs) {
        int damage = 0;
        if (this.owner instanceof Creature) {
            damage += ((Creature) this.owner).getWeapon().getDamageValue();
            damage += ((Creature) this.owner).getAttribute("Strength");
        }
        
        if (!mobs.isEmpty() && !this.isFlagged("triggered")) {
            Mists.logger.log(Level.INFO, "{0} landed on {1}", new Object[]{this.toString(), mobs.toString()});
            for (MapObject mob : mobs) {
                if (!mob.equals(this.getOwner())) {
                    this.setFlag("triggered", 1);
                    if (mob instanceof Combatant) {
                        if (this.getOwner() instanceof PlayerCharacter && mob instanceof Creature) {
                            //Disallow friendly fire
                            //TODO: build this into flags somehow (if mob.faction == getOwner.faction...)
                            PlayerCharacter pc =(PlayerCharacter)this.getOwner();
                            if (!pc.getCompanions().contains((Creature)mob)) {
                                Mists.logger.log(Level.INFO, "Hit {0} for {1} damage", new Object[]{mob.getName(), damage});
                                ((Combatant)mob).takeDamage(damage);
                            }
                        } else {
                            Mists.logger.log(Level.INFO, "Hit {0} for {1} damage", new Object[]{mob.getName(), damage});
                            ((Combatant)mob).takeDamage(damage);
                        }
                    } 
                }
            }
        }
    }
}
