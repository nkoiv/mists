/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
import java.util.HashMap;
import java.util.logging.Level;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * GraphLibrary stores loaded graphics data
 * The main use is to reduce the overhead done
 * by accidentally loading same file over and over
 * @author nikok
 */
public class GraphLibrary {
    private final HashMap<String, Image> gallery;
    
    
    public GraphLibrary() {
        this.gallery = new HashMap();
    }
    
    public void addImage(String name, Image i) {
        String lowercasename = name.toLowerCase();
        if (this.containsImage(lowercasename)) {
            Mists.logger.log(Level.WARNING, "Graphics Library already contains {0}", name);
            return;
        }
        this.gallery.put(lowercasename, i);
    }
    
    public void addImage(String name, ImageView iw, int xCoor, int yCoor, int width, int height) {
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        iw.setViewport(new Rectangle2D(xCoor, yCoor, width, height));
        WritableImage image = iw.snapshot(parameters, snapshot);
        this.addImage(name, image);
    }
    
    public Image getImage(String name) {
        String lowercasename = name.toLowerCase();
        return this.gallery.get(lowercasename);
    }
    
    public boolean containsImage(String name) {
        String lowercasename = name.toLowerCase();
        return this.gallery.containsKey(lowercasename);
    }
    
}
