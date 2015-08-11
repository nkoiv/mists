/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.Direction;
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
    
    private ArrayList<Double> getAttackPoint(Creature actor) {
        Mists.logger.log(Level.INFO, "Size of {0}: {1}/{2}", new Object[]{actor.getName(), actor.getSprite().getWidth(), actor.getSprite().getHeight()});
        ArrayList<Double> attackPoint = new ArrayList<>();
        Direction actorFacing = actor.getFacing();
        double actorLeftX = actor.getxPos();
        double actorRightX = (actor.getxPos()+actor.getSprite().getWidth());
        double actorTopY = actor.getyPos();
        double actorBottomY = (actor.getyPos()+actor.getSprite().getHeight());
        switch(actorFacing) {
            case UP: attackPoint.add(actorLeftX+(actor.getSprite().getWidth()/2));
                     attackPoint.add(actorTopY) ; break;
            case DOWN: attackPoint.add(actorLeftX+(actor.getSprite().getWidth())/2);
                     attackPoint.add(actorBottomY) ; break;
            case LEFT: attackPoint.add(actorLeftX);
                     attackPoint.add(actorTopY+(actor.getSprite().getHeight()/2)) ; break;
            case RIGHT: attackPoint.add(actorRightX);
                     attackPoint.add(actorTopY+(actor.getSprite().getHeight()/2));break;
            case UPRIGHT: break;
            case UPLEFT: break;
            case DOWNRIGHT: break;
            case DOWNLEFT: break;
        default: break;
        }
        
        if (attackPoint.isEmpty()) {
            attackPoint.add(actorTopY);
            attackPoint.add(actorLeftX);
        }
        return attackPoint;
    }
    
    @Override
    public void use(Creature actor) {
        Mists.logger.log(Level.INFO, "{0} used by {1}", new Object[]{this.toString(), actor.getName()});
        ArrayList<Double> attackPoint = this.getAttackPoint(actor);
        Effect attackEffect = new Effect(
                "meleeattack",actor.getLocation(), attackPoint.get(0), attackPoint.get(1),this.getSprite(actor),400);
    }
    
    @Override
    public SpriteAnimation getSpriteAnimation() {
        return this.attackAnimation;
    }
    
}
