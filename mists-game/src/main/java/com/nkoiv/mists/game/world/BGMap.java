/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author lp35567
 */
public class BGMap implements Map{
    
    Image image;
    
    public BGMap (Image i) {
        this.image = i;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage( image, 0, 0 );
    }
    
}
