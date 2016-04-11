/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
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
        super("melee", ActionType.MELEE_ATTACK);
        this.attackAnimation = new SpriteAnimation(new ImageView("/images/attackAnimations.png"), 4, 32, 0, 0, 0, 32, 32);
        this.attackAnimation.setAnimationSpeed(100);
        this.setFlag("range", 0);
        this.setFlag("animationcycles", 1);
        this.setFlag("cooldown", 1000);
        this.setFlag("triggered", 0);
        this.setFlag("damage", 10);
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
    
    
    private void use(Creature actor, double[] directionXY) {
        if (this.isOnCooldown()) {
            //Mists.logger.log(Level.INFO, "{0} tried to use {1}, but it was on cooldown", new Object[]{actor.getName(), this.toString()});
        } else {
            try {
                Mists.soundManager.playSound("weapon_blow");
            } catch (Exception e){
                Mists.logger.warning("Sounds not available");
            }
            this.setFlag("triggered", 0);
            Mists.logger.log(Level.INFO, "{0} used by {1} towards {2} ({3},{4})",
                    new Object[]{this.toString(), actor.getName(), actor.getFacing(), directionXY[0], directionXY[1]});
            this.lastUsed = System.currentTimeMillis(); //In case the ability has a cooldown
            //Build the effect
            double attackPointX = (directionXY[0] * actor.getWidth())/2 + actor.getCenterXPos();
            double attackPointY = (directionXY[1] * actor.getHeight())/2 + actor.getCenterYPos();
            Effect attackEffect = new Effect(
                    this, "meleeattack",
                    this.getSprite(actor),400);
            //Put the effect on the actor
            actor.getLocation().addEffect(attackEffect,
                    (attackPointX-(this.attackAnimation.getFrameWidth()/2)),
                    (attackPointY-(this.attackAnimation.getFrameHeight()/2)));
            //Link the effect so it moves with actor
            attackEffect.setLinkedObject(actor);
        }
    }
    @Override
    public void use(Creature actor, double xTarget, double yTarget) {
        double[] directionXY = Toolkit.getDirectionXY(actor.getCenterXPos(), actor.getCenterYPos(), xTarget, yTarget);
        this.use(actor, directionXY);
    }
    
    @Override
    public void use(Creature actor) {
        //Mists.logger.log(Level.INFO, "{0} using {1} towards {2}", new Object[]{actor.getName(), this.getName(), actor.getFacing()});
        double[] facing = Toolkit.getDirectionXY(actor.getFacing());
        this.use(actor, facing);
    }
       
    @Override
    public void hitOn(ArrayList<MapObject> mobs) {
        this.directDamageHit(mobs);
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
    public boolean isOnCooldown() {
        return System.currentTimeMillis() < (this.lastUsed+this.getFlag("cooldown"));
    }
    
}
