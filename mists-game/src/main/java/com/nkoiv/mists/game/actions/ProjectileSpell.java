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
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.image.ImageView;

/**
 *
 * @author nikok
 */
public class ProjectileSpell extends Action implements AttackAction {

    private SpriteAnimation projectileAnimation;
    private SpriteAnimation explosionAnimation;
    private ArrayList damagedMobs = new ArrayList<>();
    
    public ProjectileSpell(String name) {
        super(name, ActionType.RANGED_ATTACK);
        this.projectileAnimation = new SpriteAnimation(new ImageView("/images/environment/torch_flame.png"), 4, 0, 0, 0, 0, 32, 32);
        this.projectileAnimation.setAnimationSpeed(100);
        this.explosionAnimation = new SpriteAnimation(new ImageView("/images/effects/explosion-4.png"), 12, 0, 0, 0, 0, 128, 128);
        this.explosionAnimation.setAnimationSpeed(50);
        this.setFlag("range", 0);
        this.setFlag("animationcycles", 1);
        this.setFlag("cooldown", 2500);
        this.setFlag("triggered", 0);
        this.setFlag("damage", 10);
        this.setFlag("projectilespeed", 200);
        this.setFlag("projectilerange", 2000);
    }

    public void setAnimation(ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
        this.projectileAnimation = new SpriteAnimation(imageView, frameCount, startX, startY, offsetX, offsetY, frameWidth, frameHeight);
    }
    
    public Sprite createSprite(SpriteAnimation anim, double xPos, double yPos) {
        Sprite attackSprite = new Sprite(anim.getCurrentFrame());
        attackSprite.setAnimation(anim);
        attackSprite.setPosition(xPos, yPos);
        return attackSprite;
    }
    
    
    private void use(Creature actor, double[] directionXY) {
        Mists.logger.log(Level.INFO, "{0} tried to use projectile spell (owner {1})", new Object[]{actor.getName(), this.owner});
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
            this.currentCooldown = this.getFlag("cooldown");
            //Build the effect
            double attackPointX = (directionXY[0] * actor.getWidth())/2 + actor.getCenterXPos();
            double attackPointY = (directionXY[1] * actor.getHeight())/2 + actor.getCenterYPos();
            double projectileSpeed = this.getFlag("projectilespeed");
            int duration = (int)(this.getFlag("projectilerange") / projectileSpeed * 100);
            Effect attackEffect = new Effect(
                    this, "projectile",
                    this.createSprite(projectileAnimation, actor.getXPos(), actor.getYPos()),duration);
            attackEffect.getSprite().setVelocity(directionXY[0]*projectileSpeed, directionXY[1]*projectileSpeed);
            //Put the effect on the list for keeping tabs on it
            this.effects.add(attackEffect);
            //Put the effect on the actor
            actor.getLocation().addEffect(attackEffect,
                    (attackPointX-(this.projectileAnimation.getFrameWidth()/2)),
                    (attackPointY-(this.projectileAnimation.getFrameHeight()/2)));
        }
    }
    
    private void onImpact() {
        //Only trigger once
        for (Effect e : this.effects) {
            e.setRemovable();
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
        double[] directionXY = Toolkit.getDirectionXY(actor.getFacing());
        this.use(actor, directionXY);
    }
       
    private void explode(String effectName) {
        damagedMobs.clear();
        int projectileID = 0;
        for (int i = 0; i < effects.size(); i++) {
            if (effectName.equals(effects.get(i).getName())) projectileID = i;
        }
        double xSpot = this.effects.get(projectileID).getXPos()-54;
        double ySpot = this.effects.get(projectileID).getYPos()-80;
        Effect explosionEffect = new Effect(
                    this, "explosion",
                    this.createSprite(explosionAnimation, xSpot, ySpot),350);
        //Put the effect on the list for keeping tabs on it
        this.effects.add(explosionEffect);
        //Put the effect on the actor
        this.effects.get(projectileID).getLocation().addEffectThreadSafe(explosionEffect,(xSpot),(ySpot));
    }
    
    @Override
    public void hitOn(Effect e, ArrayList<MapObject> mobs) {
        while (mobs.contains(owner)) {
            mobs.remove(owner);
        }
        if (mobs.isEmpty()) return;
        
        if ("projectile".equals(e.getName())) {
            explode("projectile");
            e.setRemovable();
        }
        if ("explosion".equals(e.getName())) {
            //Trigger the on impact effect (animation changes etc)
            for (MapObject mob : mobs) {
                if (mob instanceof Structure) {
                    //TODO: Temp: DESTROY THE STRUCTURES!
                    //this.getOwner().getLocation().removeMapObject(mob);
                    mob.setRemovable();
                }
                if (mob instanceof Creature) {
                    if (damagedMobs.contains(((Creature)mob).getID())) continue;
                    damagedMobs.add(((Creature)mob).getID());
                    ((Creature)mob).takeDamage(50);
                }
            }
        }
    }
    
    @Override
    public Action createFromTemplate() {
        ProjectileSpell a = new ProjectileSpell(this.name);
        for (String flag : this.flags.keySet()) {
            a.setFlag(flag, this.flags.get(flag));
        }
        a.projectileAnimation = this.projectileAnimation;
        return a;
    }
}
