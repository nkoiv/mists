/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author nkoiv
 */
public interface GameMap {
    
    void render(double xOffset, double yOffset, GraphicsContext gc);
    
    double getWidth();
    double getHeight();
    
}