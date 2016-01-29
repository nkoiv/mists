/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.worldmap;

import com.nkoiv.mists.game.world.Location;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
 
public class LocationNode extends MapNode {
        
        private int locationID; //if already created
        private int locationSeed; //if random generated
        
        
        public LocationNode(String name, Image image, int locationID) {
            super(name, image);
            this.locationID = locationID;
        }
        
        public void setLocationID(int locationID) {
            this.locationID = locationID;
        }
        
        public int getLocationID() {
            return this.locationID;
        }
}