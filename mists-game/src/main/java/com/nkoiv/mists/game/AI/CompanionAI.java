/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.nkoiv.mists.game.gameobject.PlayerCharacter;

/**
 * CompanionAI is used for the friendly companions
 * player might have.
 * @author nikok
 */
public class CompanionAI extends CreatureAI {

    public CompanionAI(PlayerCharacter companion) {
        super(companion);
    }
    
}
