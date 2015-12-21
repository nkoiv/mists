/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

/**
 * CollisionBox for finding quick and dirty collisions
 * without (before?) Javas shape.intersects.
 * @author nikok
 */
public class CollisionBox {
    public double minX;
    public double minY;
    public double maxX;
    public double maxY;

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
    
    public boolean Intersect(CollisionBox r) {
        return this.maxX >= r.minX &&
               this.minX <= r.maxX &&
               this.maxY >= r.minY &&
               this.minY <= r.maxY;              
    }

    public CollisionBox GetIntersection(CollisionBox r) {
        CollisionBox i = new CollisionBox();
        if (this.Intersect(r)) {
            i.minX = Math.max(this.minX, r.minX);
            i.minY = Math.max(this.minY, r.minY);
            i.maxX = Math.min(this.maxX, r.maxX);
            i.maxY = Math.min(this.maxY, r.maxY);
        }
        return i;       
   }

   public double GetWidth() {
       return this.maxX - this.minX + 1;   
   }

    public double GetHeight() {
        return this.maxY - this.minY + 1;   
    }

    public void SetPosition(double x, double y) {
        double w = this.GetWidth();
        double h= this.GetHeight();
        this.minX = x;
        this.minY = y;
        this.maxX = x + w -1;
        this.maxY = y + h -1;
    }
    
}

