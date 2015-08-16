/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import java.util.ArrayList;

/**
 *
 * @author nkoiv
 */
public interface AttackAction {
    SpriteAnimation getSpriteAnimation();

    boolean isOnCooldown();
    
}
