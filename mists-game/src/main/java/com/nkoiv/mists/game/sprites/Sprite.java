/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import java.util.ArrayList;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Sprite-class handles (movable) images.
 * Both animate (animated=true) and inanimate sprites are handled by this same class.
 * Since the all the render-method does is give an image to a graphics container,
 * only the sprite itself needs to know if it's animated or not.
 * @author nkoiv
 */
public class Sprite
{
    private Image image;
    private ArrayList<Image> images;
    /** Position for Sprite is pixel coordinates on the screen 
     *   Not to be confused with position for game objects (they're at a location)
     */
    private double positionX;
    private double positionY;    
    /** Velocity indicates the speed and direction a Sprite is heading.
     *  It's noted "per tick" for the game and added to position by calling update(time) 
     */
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    /**
     * 
     */
    private boolean animated;
    private int currentFrame;

    public Sprite()
    {
        positionX = 0;
        positionY = 0;    
        velocityX = 0;
        velocityY = 0;
        animated = false;
    }
    
    public Sprite(Image i) {
        positionX = 0;
        positionY = 0;    
        velocityX = 0;
        velocityY = 0;
        image = i;
        width = i.getWidth();
        height = i.getHeight();
        animated = false;
    }

    public void setImage(Image i)
    {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
        animated = false;
    }

    public void setImage(String filename)
    {
        Image i = new Image(filename);
        setImage(i);
    }
    
    public Image getImage() {
        if (this.animated) {
           this.image = this.images.get(currentFrame);
           this.nextFrame();
        } 
        return this.image;
    }
    
    public void setAnimation (ArrayList<Image> images) {
        this.images = new ArrayList<>();
        for (Image animationFrame : images) {
            this.images.add(animationFrame);
        }
        this.image = this.images.get(0);
        this.animated = true;
        this.currentFrame = 0;
    };
    
    private void nextFrame () {
        if (this.animated) {
            if (this.currentFrame < this.images.size()) {
                this.currentFrame++;
            } else {
                this.currentFrame = 0;
            }
        }
        
    }
    
    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    public void setVelocity(double x, double y)
    {
        velocityX = x;
        velocityY = y;
    }

    public void addVelocity(double x, double y)
    {
        velocityX += x;
        velocityY += y;
    }

    public void update(double time)
    {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }

    public void render(GraphicsContext gc)
    {
        gc.drawImage( image, positionX, positionY );

    }

    public Rectangle2D getBoundary()
    {
        return new Rectangle2D(positionX,positionY,width,height);
    }

    public boolean intersects(Sprite s)
    {
        return s.getBoundary().intersects( this.getBoundary() );
    }
    
    @Override
    public String toString()
    {
        return " Position: [" + positionX + "," + positionY + "]" 
        + " Velocity: [" + velocityX + "," + velocityY + "]";
    }
}