/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.dialogue.linktriggers;

import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * LinkTriggers are something that manipulate the game world
 * when a link is clicked.
 * @author nikok
 */
public interface LinkTrigger {
    
    public boolean toggle(MapObject owner, MapObject talker);
    
}






