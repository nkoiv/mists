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
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
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
        super("MeleeAttack");
        this.setAnimation(new ImageView("/images/attackAnimations.png"), 4, 32, 0, 0, 0, 32, 32);
        this.getSpriteAnimation().setAnimationSpeed(100);
        this.setFlag("range", 0);
        this.setFlag("animationcycles", 1);
        this.setFlag("cooldown", 1000);
        this.setFlag("triggered", 0);
    }
    
    public void setAnimation(ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
        this.attackAnimation = new SpriteAnimation(imageView, frameCount, startX, startY, offsetX, offsetY, frameWidth, frameHeight);
    }
    
    public Sprite getSprite(Creature actor) {
        Sprite attackSprite = new Sprite(this.attackAnimation.getCurrentFrame());
        attackSprite.setAnimation(attackAnimation);
        attackSprite.setPosition(actor.getXPos(), actor.getYPos());
        return attackSprite;
    }
    @Override
    public void use(Creature actor) {
        if (this.isOnCooldown()) {
            //Mists.logger.log(Level.INFO, "{0} tried to use {1}, but it was on cooldown", new Object[]{actor.getName(), this.toString()});
        } else {
            Mists.soundManager.playSound("attack");
            this.setFlag("triggered", 0);
            Mists.logger.log(Level.INFO, "{0} used by {1} towards {2}", new Object[]{this.toString(), actor.getName(), actor.getFacing()});
            this.lastUsed = System.currentTimeMillis();
            Double[] attackPoint = actor.getSprite().getCorner(actor.getFacing());
            Effect attackEffect = new Effect(
                    this, "meleeattack",actor.getLocation(),
                    (attackPoint[0]-(this.attackAnimation.getFrameWidth()/2)),
                    (attackPoint[1]-(this.attackAnimation.getFrameHeight()/2)),
                    this.getSprite(actor),400);
            actor.getLocation().addEffect(attackEffect,
                    (attackPoint[0]-(this.attackAnimation.getFrameWidth()/2)),
                    (attackPoint[1]-(this.attackAnimation.getFrameHeight()/2)));
        }
    }
       
    @Override
    public void hitOn(ArrayList<MapObject> mobs) {
        if (!mobs.isEmpty() && !this.isFlagged("triggered")) {
            //Mists.logger.info(this.toString() + " landed on " + mobs.toString());
            this.setFlag("triggered", 1);
            for (MapObject mob : mobs) {
                if (!mob.equals(this.getOwner())) {
                    if (mob instanceof Combatant) {
                        Mists.logger.info("Hit "+mob.getName()+" for 50 damage");
                        ((Combatant)mob).takeDamage(50);
                    } else if (mob instanceof Structure) {
                        //TODO: Temp: DESTROY THE STRUCTURES!
                        this.getOwner().getLocation().removeMapObject(mob);
                    }
                }
            }
        }
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
