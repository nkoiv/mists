/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import com.nkoiv.mists.game.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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
    private CollisionBox collisionBox;

    public Sprite()
    {
        positionX = 0;
        positionY = 0;    
        velocityX = 0;
        velocityY = 0;
        animated = false;
        collisionBox = new CollisionBox(positionX, positionY, 0, 0);
    }
    
    public Sprite(Image i) {
        this();
        image = i;
        width = i.getWidth();
        height = i.getHeight();
        collisionBox = new CollisionBox(positionX, positionY, width, height);
    }
    
    public Sprite (Image i, double xPosition, double yPosition) {
        this(i);
        positionX = xPosition;
        positionY = yPosition;    
        collisionBox = new CollisionBox(positionX, positionY, width, height);
    }

    public void setCollisionAreaShape (int ShapeNumber) {
        // 1=Rectangle, 2=Ellipse
        this.collisionArea = ShapeNumber;
    }
    
    public int getCollisionAreaType () {
        return this.collisionArea;
    }
    
    public void refreshCollisionBox() {
        this.collisionBox = new CollisionBox(positionX, positionY, width, height);
        //Mists.logger.log(Level.INFO, "{0}Refreshed new collisionbox with values {1}x{2}:{3}x{4}", new Object[]{height, positionX, positionY, width, height});
    }
    
    public CollisionBox getCollisionBox() {
        if (this.collisionBox.GetWidth()<=1 || this.collisionBox.GetHeight() <=1) {
            this.collisionBox = new CollisionBox(positionX, positionY, width, height);
        }
        return this.collisionBox;
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
    
    public void setAnimation (ImageView image, int frameCount, int startX, int startY, int offsetX, int offsetY, int width,   int height) {
        this.animation = new SpriteAnimation (image, frameCount, startX, startY, offsetX, offsetY, width, height);
        this.animated = true;
    }
    
    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
        this.refreshCollisionBox();
    }

    public void setVelocity(double x, double y)
    {
        velocityX = x;
        velocityY = y;
    }
    
    public void setYVelocity (double y) {
        this.velocityY = y;
    }
    public void setXVelocity (double x) {
        this.velocityX = x;
    }
    
    public void setXPosition (double x) {
        this.positionX = x;
    }
    public void setYPosition (double y) {
        this.positionY = y;
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
        if (this.animated) {
            return this.animation.getFrameWidth();
        } else {    
            return this.width;
        }
    }   
    
    public double getHeight() {
        if (this.animated) {
            return this.animation.getFrameHeight();
        } else {
            return this.height;
        }
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Update() with targetX and targetY moves the sprite towards the target
     * with the velocity it has, but never past the target. This can be used
     * for fine manoeuvring.
     * @param time Time since last update
     * @param targetX X coordinates to move towards
     * @param targetY  Y coordinates to move towards
     */
    public void update(double time, double targetX, double targetY) {
        if ((positionX + velocityX * time) > targetX) {
            positionX = targetX;
        } else {
            positionX += velocityX * time;
        }           
        if ((positionY + velocityY * time) > targetY) {
            positionY = targetY;
        } else {
            positionY += velocityY * time;
        }                   
        this.collisionBox.SetPosition(positionX, positionY);
    }
    /**
     * Update uses the velocity it has and the time given,
     * moving the sprite towards the velocity according to the
     * time it has (velocity * time).     
     * @param time Time spent since last update
     */
    
    public void update(double time)
    {
        positionX += velocityX * time;
        positionY += velocityY * time;
        this.collisionBox.SetPosition(positionX, positionY);
    }

    public void render(double xOffset, double yOffset, GraphicsContext gc)
    {
        if (this.animated) {
            gc.drawImage( this.animation.getCurrentFrame(), positionX-xOffset, positionY-yOffset );
            this.width = this.animation.getCurrentFrame().getWidth();
            this.height = this.animation.getCurrentFrame().getHeight();
        } else if (this.image != null) {
            gc.drawImage( image, positionX-xOffset, positionY-yOffset );
        }
    }
    
    
    public Double[] getCenter() {
        double xCenter = this.positionX + (this.width/2);
        double yCenter = this.positionY + (this.height/2);
        Double[] center = new Double[2];
        center[0] = xCenter;
        center[1] = yCenter;
        return center;
    }
    
    public Double[] getCorner(Direction d) {
        Double[] corner = new Double[2];
         switch(d) {
            case UP: corner[0] = (this.positionX+(this.width/2));
                    corner[1] = (this.positionY);
                    break;
            case DOWN: corner[0] = (this.positionX+(this.width/2));
                    corner[1] = (this.positionY+this.height);
                    break;
            case LEFT: corner[0] = (this.positionX);
                    corner[1] = (this.positionY+(this.height/2));
                    break;
            case RIGHT: corner[0] = (this.positionX+(this.width));
                    corner[1] = (this.positionY+(this.height/2));
                    break;
            case UPRIGHT: corner[0] = (this.positionX+(this.width));
                    corner[1] = (this.positionY);
                    break;
            case UPLEFT: corner[0] = (this.positionX);
                    corner[1] = (this.positionY);
                    break;
            case DOWNRIGHT: corner[0] = (this.positionX+(this.width));
                    corner[1] = (this.positionY+this.height);
                    break;
            case DOWNLEFT: corner[0] = (this.positionX);
                    corner[1] = (this.positionY+this.height);
                    break;
        default: corner[0] = (this.positionX);corner[1] = (this.positionY);break;
         }
        
        return corner;
    }

    public Shape getBoundary()
    {
        //Consider:
        //Collision boxes should be slightly smaller than the Sprite itself
        //Otherwise it will be really hard to move between tiles
        
    switch(collisionArea) {
        case 1: return new Rectangle(positionX+1,positionY+1,width-2,height-2);
        case 2: return new Ellipse((positionX+(width/2))+2,(positionY+(height/2))+2,((width)/2)-1,((height)/2)-1);
        default: break;
    }
   
        return new Rectangle(positionX+1,positionY+1,width-2,height-2);
        //return new Rectangle2D(positionX+1,positionY+1,width-2,height-2);
    }

    private boolean pixelCollision (Image i) {
        
        /*TODO: Pixel based collisions
        * This is checked if the collision boxes intersect
        * For now returns always true
        */
        return true;
    }
    
    /**
     * TODO: Javas intersects() from Shapes is really effin slow
     * New way: just check corners
     * @param s Sprite to check collisions with
     * @return True if they overlap somewhere
     */
    
    public boolean intersects(Sprite s) {
        return this.intersectsWithShape(s);
        /*
        if(this.getCollisionBox().Intersect(s.getCollisionBox())) {
            //System.out.println("Intersection with the colBox - testing shape");
            return this.intersectsWithShape(s);
        } else {
            return false;
        }
        */
    }

    
    
    //Old intersects, with Java shape.intersect
    public boolean intersectsWithShape(Sprite s)
    {
        //Shape shape = Shape.intersect(s.getBoundary(), this.getBoundary());
        return this.getBoundary().intersects(s.getBoundary().getBoundsInLocal());
        /*
        if (shape.getBoundsInLocal().getWidth() != -1) {
            return pixelCollision(s.getImage());
        } else {
            return false;
        }
        */
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