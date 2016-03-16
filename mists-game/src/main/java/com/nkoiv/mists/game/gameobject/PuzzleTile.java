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
    private boolean isLit;
    
    public PuzzleTile(String name, MovingGraphics litUpGraphics, MovingGraphics unLitGraphics) {
        super(name, unLitGraphics, 0);
        this.litUpGraphics = litUpGraphics;
        this.unLitGraphics = unLitGraphics;
        this.isLit = false;
    }
    
    public PuzzleTile(String name, Image litUpImage, Image unLitImage) {
        this(name, new Sprite(litUpImage), new Sprite(unLitImage));
    }
    
    public boolean isLit() {
        return this.isLit;
    }
    
    private void litUp() {
        this.isLit = true;
        this.litUpGraphics.setPosition(this.graphics.getXPos(), this.graphics.getYPos());
        this.graphics = this.litUpGraphics;
    }
    
    private void unlit() {
        this.isLit = false;
        this.unLitGraphics.setPosition(this.graphics.getXPos(), this.graphics.getYPos());
        this.graphics = this.unLitGraphics;
    }
    
    public void setLit(boolean litUp) {
        this.isLit = litUp;
        if (this.isLit) this.litUp();
        else this.unlit();
    }
    
    public void toggleLit() {
        if (this.isLit) this.unlit();
        else this.litUp();
    }
    
}
