/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javafx.scene.paint.Color;

/**
 * Environment gives location the lighting, mood and weather
 * @author nikok
 */
public class LocationEnvironment implements KryoSerializable{
    
    //TODO: Implement time of day by rotating shadow colours (red for dusk, etc)
    private Color shadowcolor;
    private double shadowdepth;
    private double lightlevel;
    private String defaultMusic;
    
    public LocationEnvironment() {
        this.lightlevel = 1;
        this.defaultMusic = "dungeon";
        this.shadowcolor = Color.MIDNIGHTBLUE;
        this.shadowdepth = 1;
    }
        
    public double getLightlevel() {
        return lightlevel;
    }
    
    public String getDefaultMusic() {
        return this.defaultMusic;
    }
    
    public void setDefaultMusic(String musicTitle) {
        this.defaultMusic = musicTitle;
    }
    
    public void setLightlevel(double lightlevel) {
        this.lightlevel = lightlevel;
    }

    public Color getShadowColor() {
        return shadowcolor;
    }

    public void setShadowColor(Color shadowcolor) {
        this.shadowcolor = shadowcolor;
    }

    /**
     * Shadow Depth is the opacity of the default
     * shadow layer. If set to 1, everything not in
     * light is totally hidden. 0 means no darkness
     * at all.
     * @return current depth of shadows
    */
    public double getShadowDepth() {
        return shadowdepth;
    }

    /**
     * Shadow Depth is the opacity of the default
     * shadow layer. If set to 1, everything not in
     * light is totally hidden. 0 means no darkness
     * at all.
     * @param shadowdepth depth of shadows
     */
    public void setShadowDepth(double shadowdepth) {
        this.shadowdepth = shadowdepth;
    }

	@Override
	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, this.shadowcolor);
	    output.writeDouble(shadowdepth);
	    output.writeDouble(lightlevel);
	    output.writeString(defaultMusic);
		
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.shadowcolor = (Color)kryo.readClassAndObject(input);
		this.shadowdepth = input.readDouble();
		this.lightlevel = input.readDouble();
		this.defaultMusic = input.readString();
	}
    
    
}
