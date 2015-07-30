/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.world.Location;
import javafx.scene.image.Image;

/**
 *
 * @author lp35567
 */
public class Structure extends MapObject {

    public Structure(String name, Image image, int collisionLevel) {
        super(name, image);
        super.setCollisionLevel(collisionLevel);
    }
    
    public Structure(String name, Image image, Location location, int xCoor, int yCoor) {
        super(name, image, location, xCoor, yCoor);
        this.setCollisionLevel(100);
    }
    
}
