/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.scene.image.ImageView;

/**
 *
 * @author daedra
 */
public class MeleeAttack extends Action implements AttackAction {

    private SpriteAnimation attackAnimation;
    
    public MeleeAttack() {
        super("MeleeAttack");
        this.setAnimation(new ImageView("/images/attackAnimations.png"), 4, 32, 0, 0, 0, 32, 32);
        this.getSpriteAnimation().setAnimationSpeed(100);
        this.setFlag("range", 0);
        this.setFlag("animationcycles", 1);
    }
    
    public void setAnimation(ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
        this.attackAnimation = new SpriteAnimation(imageView, frameCount, startX, startY, offsetX, offsetY, frameWidth, frameHeight);
    }
    
    public Sprite getSprite(Creature actor) {
        Sprite attackSprite = new Sprite(this.attackAnimation.getCurrentFrame());
        attackSprite.setAnimation(attackAnimation);
        attackSprite.setPosition(actor.getxPos(), actor.getyPos());
        return attackSprite;
    }
    @Override
    public void use(Creature actor) {
        Mists.logger.log(Level.INFO, "{0} used by {1} towards {2}", new Object[]{this.toString(), actor.getName(), actor.getFacing()});
        ArrayList<Double> attackPoint = actor.getSprite().getCorner(actor.getFacing());
        Effect attackEffect = new Effect(
                "meleeattack",actor.getLocation(),
                (attackPoint.get(0)-(this.attackAnimation.getFrameWidth()/2)),
                (attackPoint.get(1)-(this.attackAnimation.getFrameHeight()/2)),
                this.getSprite(actor),400);
    }
    
    @Override
    public SpriteAnimation getSpriteAnimation() {
        return this.attackAnimation;
    }
    
}
