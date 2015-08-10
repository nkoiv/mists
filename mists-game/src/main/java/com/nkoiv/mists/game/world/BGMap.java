/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Mists;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author lp35567
 */
public class BGMap implements GameMap{
    
    Image image;
    double width;
    double height;
    
    public BGMap (Image i) {
        this.image = i;
        this.width = i.getWidth();
        this.height = i.getHeight();
        Mists.logger.info("Generated a BGMap");
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        gc.drawImage( image, xOffset, yOffset );
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    @Override
    public double getHeight() {
        return this.height;
    }
    
}
