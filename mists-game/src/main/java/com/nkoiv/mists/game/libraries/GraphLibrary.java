/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.libraries;

import java.util.HashMap;
import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;

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
    private final HashMap<String, Image[]> setgallery;
 
    public GraphLibrary() {
        this.gallery = new HashMap<>();
        this.setgallery = new HashMap<>();
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
    

    public void addImageSet(String name, Image... images) {
        String lowercasename = name.toLowerCase();
        if (this.containsImage(lowercasename)) {
            Mists.logger.log(Level.WARNING, "Graphics Set Library already contains {0}", name);
            return;
        }
        this.setgallery.put(lowercasename, images);
    }
    
    public void addImageSet(String name, ImageView iw, int columns, int rows, int width, int height) {
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        Image[] images = new Image[columns * rows];
        int counter = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                iw.setViewport(new Rectangle2D(x*width, y*height, width, height));
                WritableImage image = iw.snapshot(parameters, snapshot);
                images[counter] = image;
                counter++;
            }
        }
        
        this.addImageSet(name, images);
    }
    
    public Image getImage(String name) {
        String lowercasename = name.toLowerCase();
        Image i = this.gallery.get(lowercasename);
        if (i == null) Mists.logger.warning("GraphLibrary couldn't find: '"+name+"'");
        return i;
    }
    
    public Image[] getImageSet(String name) {
        String lowercasename = name.toLowerCase();
        return this.setgallery.get(lowercasename);
    }
    
    public boolean containsImage(String name) {
        String lowercasename = name.toLowerCase();
        return this.gallery.containsKey(lowercasename);
    }
    public boolean containsImageSet(String name) {
        String lowercasename = name.toLowerCase();
        return this.setgallery.containsKey(lowercasename);
    }
    
}
