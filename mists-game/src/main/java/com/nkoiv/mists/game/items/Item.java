/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.items;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Items are something that's stored in inventories.
 * Item may be represented by a map object, but item itself is not one.
 * Items are generally always generated via subclasses (?)
 * @author nikok
 */
public class Item {
    protected String name;
    protected String description;
    protected ItemType itype;
    protected int weight; //TODO: Probably pointless
    protected Image image;
    protected Image[] equippedImages;
    
    public Item(String name, ItemType itype, Image image) {
        this.name = name;
        this.image = image;
        this.description = "";
        this.weight = 1;
    }
    
    public void setEquippedImages(Image[] images) {
        this.equippedImages = images;
    }
    
    /**
     * Create the set of equipped images from an ImageView,
     * framed by given parameters
     * @param images The ImageView to be snapshotted for individual equipped-images
     * @param startX X-Coordinate to start the snapshotting from
     * @param startY Y-Coordinate to start the snapshotting from
     * @param imageWidth Width of an individual frame
     * @param imageHeight Height of an individual frame
     * @param imageCount Amount of snapshots to capture
     */
    public void setEquippedImages(ImageView images, int startX, int startY, int imageWidth, int imageHeight, int imageCount) {
        this.equippedImages = new Image[imageCount];
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        for (int i = 0; i < imageCount; i++) {
            images.setViewport(new Rectangle2D(startX+(i*imageWidth),startY,imageWidth,imageHeight));
            WritableImage snapshottedImage = images.snapshot(parameters, snapshot);
            this.equippedImages[i] = snapshottedImage;
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Image getImage() {
        return this.image;
    }
    
    public Image[] getEquippedImages() {
        return this.equippedImages;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public boolean use() {
        Mists.logger.info(this.getName() + " can't be used");
        return false;
    }
    
    public boolean use(MapObject target) {
        Mists.logger.info(this.getName() + " was used on "+target.getName()+" but can't be used");
        return false;
    }
    
    @Override
    public String toString() {
        String s = "["+this.itype+"|"+this.name+"]";
        return s;
    }
    
    public Item createFromTemplate() {
        Item i = new Item(this.name, this.itype, this.image);
        i.description = this.description;
        i.weight = this.weight;
        
        return i;
    }
    
}
