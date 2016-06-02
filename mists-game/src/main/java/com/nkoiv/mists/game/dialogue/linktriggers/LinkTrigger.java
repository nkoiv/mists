/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
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






