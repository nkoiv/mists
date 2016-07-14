/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.sprites.Sprite;

import javafx.scene.canvas.GraphicsContext;

/**
 * Tile is variable sized part of a TileMap
 * The ID of a tile tells its "type"
 * @author nikok
 */
public class Tile {
    
    private int id;
    private int size;
    private Sprite tileSprite;
    private String name;
    
    public Tile(int id, String name, int size, Sprite tileSprite) {
        this.id = id;
        this.name =name;
        this.size = size;
        this.tileSprite = tileSprite;
    }
    
    public Sprite getSprite() {
        return this.tileSprite;
    }
    public String getName() {
        return this.name;
    }
    public int getId(){
        return this.id;
    }
    public int getSize() {
        return this.size;
    }
    public double getX() {
        return this.getSprite().getXPos();
    }
    public double getY() {
        return this.getSprite().getYPos();
    }
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        this.getSprite().render(xOffset, yOffset, gc);
    }
    
    
    @Override
    public String toString() {
        String description = ("Tile id" + this.getId() + ", "+this.getName());
        return description;
    }
    
}
