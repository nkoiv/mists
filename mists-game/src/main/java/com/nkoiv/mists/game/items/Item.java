/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.items;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
public class Item implements KryoSerializable {
    protected int baseID;
    protected String name;
    protected String description;
    protected ItemType itype;
    protected int weight; //TODO: Probably pointless
    protected Image image;
    protected Image[] equippedImages;
    protected boolean consumedOnUse;
    
    public Item(int baseID, String name, ItemType itype, Image image) {
        this.baseID = baseID;
        this.name = name;
        this.image = image;
        this.description = "";
        this.weight = 1;
        this.consumedOnUse = false;
    }
    
    public void setEquippedImages(Image[] images) {
        this.equippedImages = images;
    }
    
    public ItemType getType() {
        return this.itype;
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
    
    public int getBaseID() {
        return this.baseID;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setDescription(String desc) {
        this.description = desc;
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
    
    public boolean isConsumedOnUse() {
        return this.consumedOnUse;
    }
    
    public boolean use() {
        Mists.logger.info(this.getName() + " can't be used without a target");
        return false;
    }
    
    public boolean use(MapObject target) {
        Mists.logger.info(this.getName() + " was used on "+target.getName()+" but can't be used on another target");
        return false;
    }
    
    @Override
    public String toString() {
        String s = "["+this.itype+"|"+this.name+"]";
        return s;
    }
    
    public Item createFromTemplate() {
        Item i = new Item(this.baseID,this.name, this.itype, this.image);
        i.description = this.description;
        i.weight = this.weight;
        
        return i;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeInt(baseID);
        output.writeString(name);
        output.writeString(description);
        output.writeInt(weight);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        int id = input.readInt();
        String n = input.readString();
        String d = input.readString();
        int w = input.readInt();

        //-----
        this.baseID = id;
        this.name = n;
        this.image = Mists.itemLibrary.getTemplate(id).getImage();
        this.description = d;
        this.weight = w;
    }
}
