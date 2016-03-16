/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import javafx.scene.image.Image;

/**
 * PuzzleTile is a structure with 0 CollisionLevel
 * and two distinctive graphics: Lit up and un lit.
 * @author nikok
 */
public class PuzzleTile extends Structure {
    private MovingGraphics litUpGraphics;
    private MovingGraphics unLitGraphics;
    
    public PuzzleTile(String name, MovingGraphics litUpGraphics, MovingGraphics unLitGraphics) {
        super(name, unLitGraphics, 0);
        this.litUpGraphics = litUpGraphics;
        this.unLitGraphics = unLitGraphics;
        
    }
    
    public PuzzleTile(String name, Image litUpImage, Image unLitImage) {
        this(name, new Sprite(litUpImage), new Sprite(unLitImage));
    }
    
}
