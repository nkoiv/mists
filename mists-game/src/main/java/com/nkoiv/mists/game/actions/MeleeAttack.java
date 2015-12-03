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
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.image.ImageView;

/**
 * MeleeAttack is the first (POC) AttackAction
 * TODO: Split this into more specific melees, as per creature perhaps
 * @author daedra
 */
public class MeleeAttack extends Action implements AttackAction {

    private SpriteAnimation attackAnimation;
    private long lastUsed;
    
    public MeleeAttack() {
        super("melee");
        this.setAnimation(new ImageView("/images/attackAnimations.png"), 4, 32, 0, 0, 0, 32, 32);
        this.getSpriteAnimation().setAnimationSpeed(100);
        this.setFlag("range", 0);
        this.setFlag("animationcycles", 1);
        this.setFlag("cooldown", 1000);
        this.setFlag("triggered", 0);
        this.setFlag("damage", 50);
    }
    
    public void setAnimation(ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
        this.attackAnimation = new SpriteAnimation(imageView, frameCount, startX, startY, offsetX, offsetY, frameWidth, frameHeight);
    }
    
    public Sprite getSprite(Creature actor) {
        Sprite attackSprite = new Sprite(this.attackAnimation.getCurrentFrame());
        attackSprite.setAnimation(attackAnimation);
        attackSprite.setPosition(actor.getXPos(), actor.getYPos());
        attackSprite.refreshCollisionBox();
        return attackSprite;
    }
    
    @Override
    public void use(Creature actor, double xDirection, double yDirection) {
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
            this.lastUsed = System.currentTimeMillis();
            double attackPointX = (xDirection * actor.getSprite().getWidth())/2 + actor.getCenterXPos();
            double attackPointY = (yDirection * actor.getSprite().getHeight())/2 + actor.getCenterYPos();
            Effect attackEffect = new Effect(
                    this, "meleeattack",actor.getLocation(),
                    (attackPointX-(this.attackAnimation.getFrameWidth()/2)),
                    (attackPointY-(this.attackAnimation.getFrameHeight()/2)),
                    this.getSprite(actor),400);
            actor.getLocation().addEffect(attackEffect,
                    (attackPointX-(this.attackAnimation.getFrameWidth()/2)),
                    (attackPointY-(this.attackAnimation.getFrameHeight()/2)));
        }
    }
    
    @Override
    public void use(Creature actor) {
        double[] facing = Toolkit.getDirectionXY(actor.getFacing());
        this.use(actor, facing[0], facing[1]);
    }
       
    @Override
    public void hitOn(ArrayList<MapObject> mobs) {
        int damage = this.getFlag("damage");
        if (!mobs.isEmpty() && !this.isFlagged("triggered")) {
            Mists.logger.info(this.toString() + " landed on " + mobs.toString());
            this.setFlag("triggered", 1);
            for (MapObject mob : mobs) {
                if (!mob.equals(this.getOwner())) {
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
                    } else if (mob instanceof Structure) {
                        //TODO: Temp: DESTROY THE STRUCTURES!
                        //this.getOwner().getLocation().removeMapObject(mob);
                        if (this.owner instanceof PlayerCharacter) mob.setFlag("removable", 1);
                    }
                }
            }
        }
    }
    
    @Override
    public Action createFromTemplate() {
        MeleeAttack a = new MeleeAttack();
        for (String flag : this.flags.keySet()) {
            a.setFlag(flag, this.flags.get(flag));
        }
        a.attackAnimation = this.attackAnimation;
        return a;
    }
    
    @Override
    public SpriteAnimation getSpriteAnimation() {
        return this.attackAnimation;
    }

    @Override
    public boolean isOnCooldown() {
        return System.currentTimeMillis() < (this.lastUsed+this.getFlag("cooldown"));
    }
    
}
