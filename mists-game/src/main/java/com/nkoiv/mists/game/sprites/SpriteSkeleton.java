/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import java.util.HashSet;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * SpriteSkeleton is a collection of sprites moving in uniform
 * Class is mainly made for creatures composed of several sprites (head, arms, legs...)
 * @author nikok
 */
public class SpriteSkeleton extends MovingGraphics {
    HashSet<Sprite> sprites;
    CollisionBox collisionBox;
    
    public SpriteSkeleton() {
        this.sprites = new HashSet<>();
        this.collisionBox = new CollisionBox();
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        for (Sprite s : this.sprites) {
            s.render(this.positionX-xOffset, this.positionY-yOffset, gc);
        }
    }

    
}
