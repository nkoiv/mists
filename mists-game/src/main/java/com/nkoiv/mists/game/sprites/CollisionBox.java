/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

/**
 * CollisionBox for finding quick and dirty collisions
 * before more pixel collisions or the like
 * @author nikok
 */
public class CollisionBox {
    public double minX;
    public double minY;
    public double maxX;
    public double maxY;
    
    public double xOffset;
    public double yOffset;
    
    public CollisionBox() {}

    public CollisionBox(double x, double y, double w, double h) {
        this.minX = x;
        this.minY = y;
        this.maxX = x + w -1;
        this.maxY = y + h -1;
    }

    public void refresh(double x, double y, double w, double h) {
        this.minX = x;
        this.minY = y;
        this.maxX = x + w -1;
        this.maxY = y + h -1;
    }
    
    public boolean intersects(CollisionBox r) {
        return this.maxX+xOffset >= r.minX+r.xOffset &&
               this.minX+xOffset <= r.maxX+r.xOffset &&
               this.maxY+yOffset >= r.minY+r.yOffset &&
               this.minY+yOffset <= r.maxY+r.yOffset;              
    }

    public CollisionBox getIntersection(CollisionBox r) {
        CollisionBox i = new CollisionBox();
        if (this.intersects(r)) {
            i.minX = Math.max(this.minX+xOffset, r.minX+r.xOffset);
            i.minY = Math.max(this.minY+yOffset, r.minY+r.yOffset);
            i.maxX = Math.min(this.maxX+xOffset, r.maxX+r.xOffset);
            i.maxY = Math.min(this.maxY+yOffset, r.maxY+r.yOffset);
        }
        return i;       
    }

    public double getWidth() {
       return this.maxX - this.minX + 1;   
    }

    public double getHeight() {
        return this.maxY - this.minY + 1;   
    }
    
    /**
     * Assign x and y offset values for the collisionbox.
     * Because collisionbox position is generally updated  with the 
     * parent, it's sometimes needed to specify x and y offset
     * for when the collisionbox upper left corner doesn't match
     * the graphical representation
     * @param xOffset Offset from upper left (x=0, y=0) corner
     * @param yOffset Offset from upper left (x=0, y=0) corner
     */
    public void setOffset(double xOffset, double yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    public void setPosition(double x, double y) {
        double w = this.getWidth();
        double h= this.getHeight();
        this.minX = x;
        this.minY = y;
        this.maxX = x + w -1;
        this.maxY = y + h -1;
    }
    
}

