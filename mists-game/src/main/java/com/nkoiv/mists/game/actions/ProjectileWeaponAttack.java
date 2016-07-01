/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Projectile;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Water;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import javafx.scene.image.ImageView;

/**
 * ProjectileWeaponAttack is for shooting with bows and whatnot.
 * Every weapon should have a projectile tied to it. 
 * TODO: Should this come from the consumed projectile instead?
 * @author nikok
 */
public class ProjectileWeaponAttack  extends Action implements AttackAction {
    
    protected SpriteAnimation projectileAnimation;
    protected SpriteAnimation impactAnimation;
    
    public ProjectileWeaponAttack(String name, SpriteAnimation projectileAnimation, SpriteAnimation impactAnimation) {
        super(name, ActionType.RANGED_ATTACK);
        this.projectileAnimation = projectileAnimation;
        this.impactAnimation = impactAnimation;
        this.setFlag("range", 0);
        this.setFlag("animationcycles", 1);
        this.setFlag("cooldown", 3000);
        this.setFlag("triggered", 0);
        this.setFlag("projectilespeed", 300);
        this.setFlag("projectilerange", 3000);
    }
    
    public ProjectileWeaponAttack() {
        this("Shoot", new SpriteAnimation("arrowAnimation"), null);
    }
    
    private Sprite createSprite(SpriteAnimation anim, double xPos, double yPos) {
        Sprite attackSprite = new Sprite(anim.getCurrentFrame());
        //attackSprite.setAnimation(anim);
        attackSprite.setPosition(xPos, yPos);
        attackSprite.setRotationPoint(attackSprite.getWidth()/2, attackSprite.getHeight()/2); //Rotate around the center
        return attackSprite;
    }
    
    private void use(Creature actor, double[] directionXY) {
        if (this.isOnCooldown()) {
            //Mists.logger.log(Level.INFO, "{0} tried to use {1}, but it was on cooldown", new Object[]{actor.getName(), this.toString()});
        } else {
            try {
                Mists.soundManager.playSound("bow_shoot");
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
            Projectile attackProjectile = new Projectile(
                    this, "projectile",
                    this.createSprite(projectileAnimation,
                     actor.getXPos(), actor.getYPos()), duration,
                    directionXY[0]*projectileSpeed, directionXY[1]*projectileSpeed);
            //Put the effect on the list for keeping tabs on it
            attackProjectile.getSprite().setRotation(Math.toDegrees(Math.atan2(directionXY[1], directionXY[0])));
            this.effects.add(attackProjectile);
            //Put the effect on the actor
            actor.getLocation().addEffect(attackProjectile,
                    (attackPointX-(this.projectileAnimation.getFrameWidth()/2)),
                    (attackPointY-(this.projectileAnimation.getFrameHeight()/2)));
        }
    }
    
    @Override
    public void use(Creature actor, double xTarget, double yTarget) {
        Mists.logger.log(Level.INFO, "{0} using {1} from {2}x{3} towards {4}x{5}", new Object[]{actor.getName(), this.name, actor.getCenterXPos(), actor.getCenterYPos(), xTarget, yTarget});
        double[] directionXY = Toolkit.getDirectionXY(actor.getCenterXPos(), actor.getCenterYPos(), xTarget, yTarget);
        Mists.logger.info("Giving directionXY: "+directionXY[0]+"x"+directionXY[1]);
        this.use(actor, directionXY);
    }
    
    @Override
    public void use(Creature actor) {
        //Mists.logger.log(Level.INFO, "{0} using {1} towards {2}", new Object[]{actor.getName(), this.getName(), actor.getFacing()});
        double[] directionXY = Toolkit.getDirectionXY(actor.getFacing());
        this.use(actor, directionXY);
    }
    
    
    @Override
    public void hitOn(Effect e, ArrayList<MapObject> mobs) {
        while (mobs.contains(owner)) {
            mobs.remove(owner);
        }
        
        //Spells should fly over frills and water
        Iterator mobIterator = mobs.iterator();
        while (mobIterator.hasNext()) {
            MapObject mob = (MapObject)mobIterator.next();
            if ((mob.getCollisionLevel() == 0 && mob instanceof Structure) || mob instanceof Water) mobIterator.remove();
        }
        
        if (mobs.isEmpty()) return;
        
        if ("projectile".equals(e.getName())) {
            //Trigger the on impact effect (animation changes etc)
            for (MapObject mob : mobs) {
                if (mob instanceof Structure) {
                    //TODO: Temp: DESTROY THE STRUCTURES!
                    //this.getOwner().getLocation().removeMapObject(mob);
                    //mob.remove();
                }
                if (mob instanceof Creature) {
                    ((Creature)mob).takeDamage(10);
                }
            }
            e.remove();
        }
    }
    
    @Override
    public ProjectileWeaponAttack createFromTemplate() {
        ProjectileWeaponAttack a = new ProjectileWeaponAttack(this.name, this.projectileAnimation, this.impactAnimation);
        for (String flag : this.flags.keySet()) {
            a.setFlag(flag, this.flags.get(flag));
        }
        return a;
    }
    
}
