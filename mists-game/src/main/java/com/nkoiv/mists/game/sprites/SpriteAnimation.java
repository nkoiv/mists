/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import com.nkoiv.mists.game.Mists;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Based on Mikes Sprite Animation guide from
 * http://blog.netopyr.com/2012/03/09/creating-a-sprite-animation-with-javafx/
 * @author nkoiv
 */

public class SpriteAnimation {

	private String animationName;
    private ImageView imageView;
    private int frameCount;
    private int offsetX;
    //private int offsetY; //offsetY is never used, as all frames are assumed to be on same horizontal plane
    private int startX;
    private int startY;
    private double frameWidth;
    private double frameHeight;
    private int frameDurationMs;

    private int currentFrame;
    private long lastFrameChangeMs;
    private final Image[] snapshots;

    public SpriteAnimation(String animationName) {
    	this(animationName, Mists.graphLibrary.getImageSet(animationName));
    }
    
    public SpriteAnimation(String animationName, ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
        this.imageView = imageView;
        this.frameCount = frameCount;
        this.offsetX = offsetX;
        //this.offsetY = offsetY;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameDurationMs = 500;
        this.currentFrame = 0;
        this.lastFrameChangeMs = 0;
        this.startX = startX;
        this.startY = startY;
        this.snapshots = new Image[frameCount];
    }
    
    public SpriteAnimation(String animationName, Image[] frameImages) {
        this.imageView = null;
        this.frameCount = frameImages.length;
        this.frameWidth = frameImages[0].getWidth();
        this.frameHeight = frameImages[1].getHeight();
        this.frameDurationMs = 500;
        this.currentFrame = 0;
        this.lastFrameChangeMs = 0;
        this.snapshots = frameImages;
    }
    
    
    public void setAnimationSpeed (int frameDurationInMilliseconds) {
        this.frameDurationMs = frameDurationInMilliseconds;
    }
    
    public double getFrameWidth() {
        return this.frameWidth;
    }
    
    public double getFrameHeight() {
        return this.frameHeight;
    }
    
    public Image getCurrentFrame () {
        if (this.lastFrameChangeMs == 0) {
            this.lastFrameChangeMs = System.currentTimeMillis();
        }
        if ((System.currentTimeMillis() - lastFrameChangeMs) > this.frameDurationMs) {
            if (this.currentFrame < this.frameCount-1) {
                this.currentFrame++;
            } else {
                this.currentFrame = 0;
            }
            this.lastFrameChangeMs = System.currentTimeMillis();
            //Mists.logger.info("Changed to next frame");
        }
        if (this.snapshots[currentFrame] == null) {
            this.snapshots[currentFrame] = snapshotFrame(currentFrame);
        }
        return this.snapshots[currentFrame];
    }
    
    public static Image[] buildFramesFromImageview(ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
    	Image[] frames = new Image[frameCount];
    	for (int i = 0; i < frameCount; i++) {
    		frames[i] = snapshotFrame(i, imageView, frameWidth, frameHeight, offsetX, startX, startY);
        }
    	return frames;
    }
    
    private static Image snapshotFrame(int frameNumber, ImageView imageView, int frameWidth, int frameHeight, int offsetX, int startX, int startY) {
    	 if (imageView == null) return new WritableImage((int)frameWidth, (int)frameHeight);
         Rectangle2D r = new Rectangle2D((frameNumber * frameWidth)+(frameNumber* offsetX)+startX, 0+startY, frameWidth, frameHeight);
         imageView.setViewport(r);
         WritableImage snapshot = null;
         SnapshotParameters parameters = new SnapshotParameters();
         parameters.setFill(Color.TRANSPARENT);
         return imageView.snapshot(parameters, snapshot);
    }
    
    private Image snapshotFrame(int frameNumber) {
    	return snapshotFrame(frameNumber, this.imageView, (int)this.frameWidth, (int)this.frameHeight, this.offsetX, this.startX, this.startY);
    	/*
        if (this.imageView == null) return new WritableImage((int)frameWidth, (int)frameHeight);
        Rectangle2D r = new Rectangle2D((frameNumber * frameWidth)+(frameNumber* offsetX)+startX, 0+startY, frameWidth, frameHeight);
        imageView.setViewport(r);
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        return imageView.snapshot(parameters, snapshot);
        */
    }
    
    public Image[] getFrames() {
        return this.snapshots;
    }

    public String getAnimationName() {
    	return this.animationName;
    }
    
}