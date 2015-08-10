/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import com.nkoiv.mists.game.Mists;
import java.util.ArrayList;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Based on Mikes Sprite Animation guide from
 * http://blog.netopyr.com/2012/03/09/creating-a-sprite-animation-with-javafx/
 * @author nkoiv
 */

public class SpriteAnimation {

    private final ImageView imageView;
    private final int frameCount;
    private final int offsetX;
    private final int offsetY;
    private final int startX;
    private final int startY;
    private final double frameWidth;
    private final double frameHeight;
    private int frameDurationMs;

    private int currentFrame;
    private long lastFrameChangeMs;

    public SpriteAnimation(
            ImageView imageView, int frameCount, int startX, int startY, int offsetX, int offsetY, int frameWidth, int frameHeight) {
        this.imageView = imageView;
        this.frameCount = frameCount;
        this.offsetX   = offsetX;
        this.offsetY   = offsetY;
        this.frameWidth     = frameWidth;
        this.frameHeight    = frameHeight;
        this.frameDurationMs = 500;
        this.currentFrame = 0;
        this.lastFrameChangeMs = 0;
        this.startX = startX;
        this.startY = startY;
        
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
            imageView.setViewport(new Rectangle2D((currentFrame * frameWidth)+(currentFrame* offsetX)+startX, 0+startY, frameWidth, frameHeight));
        }
        if ((System.currentTimeMillis() - lastFrameChangeMs) > this.frameDurationMs) {
            if (this.currentFrame < this.frameCount-1) {
                this.currentFrame++;
            } else {
                this.currentFrame = 0;
            }
            this.lastFrameChangeMs = System.currentTimeMillis();
            //Mists.logger.info("Changed to next frame");
            imageView.setViewport(new Rectangle2D((currentFrame * frameWidth)+(currentFrame* offsetX)+startX, 0+startY, frameWidth, frameHeight));
        }
         
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        snapshot = imageView.snapshot(parameters, snapshot);
        return snapshot;
    }
    
    
    // TODO: Rip images from ImageView
    // Currently unusable
    public ArrayList<Image> getImages() {
       ArrayList<Image> imageList = new ArrayList<>();
       for (int i=0; i<frameCount;i++) {
           //imageList.add(new Image(imageView.getImage().));
       }
        
       return imageList;
    }


}