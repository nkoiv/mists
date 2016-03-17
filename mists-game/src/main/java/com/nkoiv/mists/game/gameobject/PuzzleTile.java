/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Trigger;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.logging.Level;
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
    
    public class PuzzleTrigger implements Trigger {
        private MapObject targetMob;
        
        public PuzzleTrigger(MapObject mob) {
            this.targetMob = mob;
        }
        
        @Override
        public String getDescription() {
            String s = "Trigger to toggle "+targetMob.getName();
            return s;
        }

        @Override
        public boolean toggle(MapObject toggler) {
            if (targetMob instanceof PuzzleTile) {
                ((PuzzleTile)targetMob).toggleLit();
                return true;
            } else {
                Mists.logger.log(Level.WARNING, "PuzzleTrigger set to manipulate a non-PuzzleTile mob: {0}", targetMob.toString());
                return false;
            }
        }
        
        @Override
        public PuzzleTrigger createFromTemplate() {
            PuzzleTrigger tt = new PuzzleTrigger(this.targetMob);
            return tt;
        }

        @Override
        public MapObject getTarget() {
            return this.targetMob;
        }

        @Override
        public void setTarget(MapObject mob) {
            this.targetMob = mob;
        }
        
    }
}
