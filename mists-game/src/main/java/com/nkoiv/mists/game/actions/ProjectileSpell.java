/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.sprites.SpriteAnimation;

/**
 *
 * @author nikok
 */
public class ProjectileSpell extends Action implements AttackAction {

    private SpriteAnimation projectileAnimation;
    private long lastUsed;
    
    public ProjectileSpell(String name) {
        super("firebolt");
        this.setFlag("range", 0);
        this.setFlag("animationcycles", 1);
        this.setFlag("cooldown", 1000);
        this.setFlag("triggered", 0);
        this.setFlag("damage", 10);
    }

    @Override
    public boolean isOnCooldown() {
        return System.currentTimeMillis() < (this.lastUsed+this.getFlag("cooldown"));
    }
    
}
