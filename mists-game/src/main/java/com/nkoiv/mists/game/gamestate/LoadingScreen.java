/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import javafx.scene.canvas.Canvas;

/**
 *
 * @author nikok
 */
public interface LoadingScreen {
    
    public void render(Canvas canvas, Canvas uiCanvas);
    
    public void enter();
    public void exit();
    
}
