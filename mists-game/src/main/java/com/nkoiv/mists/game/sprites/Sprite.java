/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

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
    //private ArrayList<Image> images; // Was considered for animation, but using separate class for now
    private SpriteAnimation animation;
    private double width;
    private double height;
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

    /** Sprite animation is optional, and every sprite should be usable inanimate.
     *  There is no internal timer for the animation and every next frame is called every time the Sprite gets drawn
     *  To extend the length of a frame, add duplicates to the images-array.
     */
    private boolean animated;
    
    private int collisionArea = 1; // 1=Rectangle, 2=Ellipse

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
    
    public Sprite (Image i, double xPosition, double yPosition) {
        positionX = xPosition;
        positionY = yPosition;    
        velocityX = 0;
        velocityY = 0;
        image = i;
        width = i.getWidth();
        height = i.getHeight();
        animated = false;
    }

    public void setCollisionAreaShape (int ShapeNumber) {
        // 1=Rectangle, 2=Ellipse
        this.collisionArea = ShapeNumber;
    }
    
    public int getCollisionAreaType () {
        return this.collisionArea;
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
           this.animation.getCurrentFrame();
        } 
        return this.image;
    }
    
    public void setAnimation (SpriteAnimation animation) {
        this.animation = animation;
        this.animated = true;
        this.width = animation.getFrameWidth();
        this.height = animation.getFrameHeight();
    };
    
    public void removeAnimation () {
        this.animated = false;
        this.width = this.image.getWidth();
        this.height = this.image.getHeight();
    }
    
    public void setAnimation (ImageView image, int frameCount, int offsetX, int offsetY, int width,   int height) {
        this.animation = new SpriteAnimation (image, frameCount, offsetX, offsetY, width, height);
        this.animated = true;
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
    
    public double getXVelocity() {
        return this.velocityX;
    }
    
    public double getYVelocity() {
        return this.velocityY;
    }
    
    public double getWidth() {
        return this.width;
    }
    
    public double getHeight() {
        return this.height;
    }

    public void update(double time)
    {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }

    public void render(double xOffset, double yOffset, GraphicsContext gc)
    {
        if (this.animated) {
            gc.drawImage( this.animation.getCurrentFrame(), positionX-xOffset, positionY-yOffset );
        } else {
            gc.drawImage( image, positionX-xOffset, positionY-yOffset );
        }
        

    }

    public Shape getBoundary()
    {
        //Consider:
        //Collision boxes should be slightly smaller than the Sprite itself
        //Otherwise it will be really hard to move between tiles
        
        switch(collisionArea) {
            case 1: return new Rectangle(positionX+1,positionY+1,width-2,height-2);
            case 2: return new Ellipse(positionX+(width/2),positionY+(height/2),(width)/2,(height)/2);
            default: break;
        }
        
        return new Rectangle(positionX+1,positionY+1,width-2,height-2);
        //return new Rectangle2D(positionX+1,positionY+1,width-2,height-2);
    }

    public boolean intersects(Sprite s)
    {
        Shape shape = Shape.intersect(s.getBoundary(), this.getBoundary());
        return shape.getBoundsInLocal().getWidth() != -1;
    }
    
    public double getXPos() {
        return this.positionX;
    }
    
    public double getYPos() {
        return this.positionY;
    }
    
    @Override
    public String toString()
    {
        return " Position: [" + positionX + "," + positionY + "]" 
        + " Velocity: [" + velocityX + "," + velocityY + "]";
    }
}