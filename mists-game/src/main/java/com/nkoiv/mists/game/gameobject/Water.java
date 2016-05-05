/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.MovingGraphics;

/**
 * Water is a type of terrain obstacle that needs
 * to be tiled to look nice. Not much unlike Walls.
 * @author nikok
 */
public class Water extends MapObject {
    
    public Water(String name, MovingGraphics graphics) {
        super(name, graphics);
    }
    
}
