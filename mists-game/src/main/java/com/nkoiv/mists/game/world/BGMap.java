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
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Structure;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nkoiv
 */
public class BGMap implements GameMap, KryoSerializable{
    
    private Image image;
    private double width;
    private double height;
    
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
    public ArrayList<Structure> getStaticStructures() {
        //BGmaps have no static structures 
        //So empty list is returned (TODO: at least yet)
        ArrayList<Structure> staticStructures = new ArrayList<>();
        return staticStructures;
    }
    
    @Override
    public double getWidth() {
        return this.width;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void read(Kryo kryo, Input input) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
}
