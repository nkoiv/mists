/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Structures are MapObjects with varying collision boxes
 * A single structure may have some parts of it causing collision, others not
 * TODO: utilize ImageView viewport to show pieces from same file
 * @author nkoiv
 */
public class Structure extends MapObject {
    
    //Extra sprites are used as a non-collision part of the structure
    protected ArrayList<Sprite> extraSprites;

    public Structure(String name, Image image, int collisionLevel) {
        super(name, image);
        this.collisionLevel = collisionLevel;
        this.setFlag("visible", 1);
        this.extraSprites = new ArrayList<>();
    }
    
    public Structure(String name, MovingGraphics graphics, int collisionLevel) {
        super(name, graphics);
        this.collisionLevel = collisionLevel;
        this.setFlag("visible", 1);
        this.extraSprites = new ArrayList<>();
    }
    
    public void addExtra (Sprite sprite, double xOffset, double yOffset) {
        sprite.setPosition(this.getXPos()+xOffset, this.getYPos()+yOffset);
        this.extraSprites.add(sprite);
    }
    
    public void addExtra (Image image, double xOffset, double yOffset) {
        this.extraSprites.add(new Sprite(
        image, this.getXPos()+xOffset, this.getYPos()+yOffset ));
    }
    
    public ArrayList<Sprite> getExtras() {
        return this.extraSprites;
    }
    
    public void removeExtras() {
        this.extraSprites.clear();
    }

    public void renderExtras (double xOffset, double yOffset, GraphicsContext gc) {
        if (!this.extraSprites.isEmpty()) {
            for (Sprite extraSprite : this.extraSprites) {
                //Mists.logger.info("Rendering extra for " +this.getName());
                extraSprite.render(xOffset, yOffset, gc);
            }
        }
    }
    
    public Sprite getSprite() {
        return (Sprite)this.graphics;
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.isFlagged("visible")) {
            super.render(xOffset, yOffset, gc);
            this.renderExtras(xOffset, yOffset, gc);
        }
        
    }
    
    //setPosition is overwritten to move extras along with the main sprite
    @Override
    public void setPosition (double xPos, double yPos) {
        if (!this.extraSprites.isEmpty()) {
            for (Sprite extraSprite : extraSprites) { //All extras are moved keeping the same relation to the main sprite
                double xOffset = this.getSprite().getXPos() - extraSprite.getXPos();
                double yOffset = this.getSprite().getYPos() - extraSprite.getYPos();
                extraSprite.setPosition(xPos-xOffset, yPos-yOffset);
            }
        }
        this.getSprite().setPosition(xPos, yPos);
    }
    
    @Override
    public void setCenterPosition (double xPos, double yPos) {
        this.setPosition(xPos+(this.getSprite().getWidth()/2), yPos+(this.getSprite().getHeight()/2));
    }
    
    @Override
    public String[] getInfoText() {
        double lightlevel = 0;
        if (this.location != null) lightlevel = location.getLightLevel(this.getCenterXPos(), this.getCenterYPos());
        String[] s = new String[]{
            this.name,
            "ID "+this.IDinLocation+" @ "+this.location.getName(),
            "X:"+((int)this.getXPos())+" Y:"+((int)this.getYPos()),
            "LightLevel:"+lightlevel
        };
        return s;
    }
    
    @Override
    public Structure createFromTemplate() {
        Structure ns = new Structure(this.name, this.getSprite().getImage(), this.collisionLevel);
        if (this.getSprite().isAnimated()) {
            ns.getSprite().setAnimation(this.getSprite().getAnimation());
        }
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getSprite().getXPos();
                double yOffset = s.getYPos() - this.getSprite().getYPos();
                ns.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return ns;
    }
    
}
