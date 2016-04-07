/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

/**
 * Environment gives location the lighting, mood and weather
 * @author nikok
 */
public class LocationEnvironment {
    private Location location;
    private double lightlevel;
    private String defaultMusic;
    
    public LocationEnvironment(Location location) {
        this.location = location;
        this.lightlevel = 1;
        this.defaultMusic = "dungeon";
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
    
    
}
