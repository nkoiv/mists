/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
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
    }
    
    public void setAnimation(ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
        this.attackAnimation = new SpriteAnimation(imageView, frameCount, startX, startY, offsetX, offsetY, frameWidth, frameHeight);
    }
    
    
    @Override
    public SpriteAnimation getSpriteAnimation() {
        return this.attackAnimation;
    }
    
}
