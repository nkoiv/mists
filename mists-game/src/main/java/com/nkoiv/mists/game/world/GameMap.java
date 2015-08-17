/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.gameobject.Structure;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author nkoiv
 */
public interface GameMap {
    
    //Render the map background
    void render(double xOffset, double yOffset, GraphicsContext gc);
    
    //If the map has static stuctures (walls etc), get them for the location
    ArrayList<Structure> getStaticStructures(Location l);
    
    //Map size
    double getWidth();
    double getHeight();
    
}
