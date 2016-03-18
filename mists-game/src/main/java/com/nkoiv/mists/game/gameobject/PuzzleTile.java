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
public class PuzzleTile extends Structure implements Shapechanger {
    protected MovingGraphics litUpGraphics;
    protected MovingGraphics unLitGraphics;
    protected boolean isLit;
    protected boolean frozen;
    
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
    
    private boolean litUp() {
        this.isLit = true;
        this.litUpGraphics.setPosition(this.graphics.getXPos(), this.graphics.getYPos());
        this.graphics = this.litUpGraphics;
        return true;
    }
    
    private boolean unlit() {
        this.isLit = false;
        this.unLitGraphics.setPosition(this.graphics.getXPos(), this.graphics.getYPos());
        this.graphics = this.unLitGraphics;
        return true;
    }
    
    public void setLit(boolean litUp) {
        this.isLit = litUp;
        if (this.isLit) this.litUp();
        else this.unlit();
    }
    
    @Override
    public boolean shiftMode() {
        if (this.frozen) return false;
        if (this.isLit) return this.unlit();
        else return this.litUp();
    }
    
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
    public boolean isFrozen() {
        return this.frozen;
    }
    
    @Override
    public PuzzleTile createFromTemplate() {
        PuzzleTile pt = new PuzzleTile(this.name, this.litUpGraphics.getImage(), this.unLitGraphics.getImage());
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getSprite().getXPos();
                double yOffset = s.getYPos() - this.getSprite().getYPos();
                pt.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return pt;
    }
}
