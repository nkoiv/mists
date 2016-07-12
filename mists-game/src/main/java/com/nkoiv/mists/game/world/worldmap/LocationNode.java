/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.worldmap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

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

    public int getLocationSeed() {
        return locationSeed;
    }

    public void setLocationSeed(int locationSeed) {
        this.locationSeed = locationSeed;
    }

	@Override
	public void write(Kryo kryo, Output output) {
		super.write(kryo, output);
		output.writeInt(locationID);
		output.writeInt(locationSeed);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		super.read(kryo, input);
		this.locationID = input.readInt();
		this.locationSeed = input.readInt();
	}

	@Override
	public MapNode createFromTemplate() {
		LocationNode ln = new LocationNode(this.name, this.imageOnMap, this.locationID);
		ln.id = this.id;
		ln.imageName = this.imageName;
		ln.bigNode = this.bigNode;
		ln.locationID = this.locationID;
		ln.locationSeed = this.locationSeed;
		return ln;
	}
    
  
        
}