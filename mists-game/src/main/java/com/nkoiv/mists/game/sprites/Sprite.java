/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

/**
 * Sprite-class handles (movable) images.
 * Both animate (animated=true) and inanimate sprites are handled by this same class.
 * Since the all the render-method does is give an image to a graphics container,
 * only the sprite itself needs to know if it's animated or not.
 * @author nkoiv
 */
public class Sprite extends MovingGraphics
{
    private Image image;
    //private ArrayList<Image> images; // Was considered for animation, but using separate class for now
    private SpriteAnimation animation;

    /** Sprite animation is optional, and every sprite should be usable inanimate.
     *  There is no internal timer for the animation and every next frame is called every time the Sprite gets drawn
     *  To extend the length of a frame, add duplicates to the images-array.
     */
    private boolean animated;
    
    private int collisionArea = 1; // 1=Rectangle, 2=Ellipse, 3=Line
    

    public Sprite()
    {
        positionX = 0;
        positionY = 0;    
        velocityX = 0;
        velocityY = 0;
        radius = new double[4];
        angle = new double[4];
        animated = false;
        collisionBox = new CollisionBox(positionX, positionY, 0, 0);
    }
    
    public Sprite(Image i) {
        this();
        image = i;
        width = i.getWidth();
        height = i.getHeight();
        this.rotatePointX = width/2;
        this.rotatePointY = height/2;
        collisionBox = new CollisionBox(positionX, positionY, width, height);
        this.refreshRotationData();
    }
    
    public Sprite(Image i, double xPosition, double yPosition) {
        this(i);
        positionX = xPosition;
        positionY = yPosition;    
        collisionBox = new CollisionBox(positionX, positionY, width, height);
        this.refreshRotationData();
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
        this.refreshCollisionBox();
        this.refreshRotationData();
        
    }
    
    public void setImage(String filename)
    {
        Image i = new Image(filename);
        setImage(i);
        this.refreshCollisionBox();
        this.refreshRotationData();
    }
    
    public Image getImage() {
        if (this.animated) {
           this.animation.getCurrentFrame();
        } 
        return this.image;
    }
    
    public boolean isAnimated() {
        return this.animated;
    }
    
    public SpriteAnimation getAnimation() {
        return this.animation;
    }
    
    public void setAnimation (SpriteAnimation animation) {
        if (animation == null) return;
        this.animation = animation;
        this.animated = true;
        this.width = animation.getFrameWidth();
        this.height = animation.getFrameHeight();
    }
    
    public void removeAnimation () {
        this.animated = false;
        this.width = this.image.getWidth();
        this.height = this.image.getHeight();
    }
    
    public void setAnimation (ImageView image, int frameCount, int startX, int startY, int offsetX, int offsetY, int width,   int height) {
        this.animation = new SpriteAnimation (image, frameCount, startX, startY, offsetX, offsetY, width, height);
        this.animated = true;
        this.width = animation.getFrameWidth();
        this.height = animation.getFrameHeight();
    }
    
    /**
     * By default sprites rotate around their center (width/2, height/2)
     * setting rotationpoint moves this around.
     * @param x xCoordinate (withing the sprite, from top left corner) to rotate the sprite around
     * @param y yCoordinate (withing the sprite, from top left corner) to rotate the sprite around
     */
    @Override
    public void setRotationPoint(double x, double y) {
        this.rotatePointX = x;
        this.rotatePointY = y;
        this.refreshRotationData();
    }
    
    /**
     * Moves the Sprite (opper left corner) to the set position
     * Also updated the collisionbox to match those coordinates.
     * @param x desired X position
     * @param y desired Y position
     */
    @Override
    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
        this.refreshCollisionBox();
    }
    
    @Override
    public void setCenterPosition(double x, double y) {
        this.setPosition(x-(this.getWidth()/2), y-(this.getHeight()/2));
        this.refreshCollisionBox();
    }
    
    /**
     * getWidth returns the current animation frames width,
     * if the sprite is animated. Otherwise it returns the
     * default width
     * @return (current) width of the sprite
     */
    @Override
    public double getWidth() {
        if (this.animated) {
            return this.animation.getFrameWidth();
        } else {    
            return this.width;
        }
    }   
    
    /**
     * getHeight returns the current animation frames height,
     * if the sprite is animated. Otherwise it returns the
     * default height
     * @return (current) height of the sprite
     */
    @Override
    public double getHeight() {
        if (this.animated) {
            return this.animation.getFrameHeight();
        } else {
            return this.height;
        }
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
        this.collisionBox.setPosition(positionX, positionY);
    }
    /**
     * Update uses the velocity it has and the time given,
     * moving the sprite towards the velocity according to the
     * time it has (velocity * time).     
     * @param time Time spent since last update
     */
    
    @Override
    public void update(double time)
    {
        super.update(time);
        this.collisionBox.setPosition(positionX, positionY);
        if (this.spin!=0) this.refreshCollisionBox();
    }
    
    /**
     * Render sprites at an angle.
     * Performed by rotating canvas at the sprites location
     * TODO: How is this performance wise?
     * @param xOffset xOffset for screen position on the (location) map
     * @param yOffset yOffset for screen position on the (location) map
     * @param degrees Degrees to rotate the sprite around its center.   
     * @param gc GraphicsContext to draw the sprite on
     */
    public void render (double xOffset, double yOffset, double degrees, GraphicsContext gc) {
        gc.save();
        gc.translate((this.positionX+rotatePointX - xOffset), (this.positionY+rotatePointY - yOffset));
        gc.rotate(degrees);
        gc.translate(-(this.positionX+rotatePointX - xOffset), -(this.positionY+rotatePointY - yOffset));
        this.renderOnScreen(xOffset, yOffset, gc);
        gc.restore();
    }
    
    /**
     * Render draws the Sprite on the given GraphicsContext
     * at the coordinates its located at.
     * @param xOffset xOffset for screen position on the (location) map
     * @param yOffset yOffset for screen position on the (location) map
     * @param gc GraphicsContext to draw the sprite on
     */
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc)
    {
        if (this.rotation != 0 ) this.render(xOffset, yOffset, rotation, gc);
        else {
            this.renderOnScreen(xOffset, yOffset, gc);
        }
    }
    
    /**
     * Internal rendering of the sprite
     * @param xOffset xOffset for screen position on the (location) map
     * @param yOffset yOffset for screen position on the (location) map
     * @param gc GraphicsContext to draw the sprite on
     */
    private void renderOnScreen(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.animated) {
                gc.drawImage(this.animation.getCurrentFrame(), positionX-xOffset, positionY-yOffset);
                this.width = this.animation.getCurrentFrame().getWidth();
                this.height = this.animation.getCurrentFrame().getHeight();
            } else if (this.image != null) {
                gc.drawImage(image, positionX-xOffset, positionY-yOffset);
        }
    }
    
    @Override
    public void renderCollisions(double xOffset, double yOffset, GraphicsContext gc) {
        gc.setStroke(Color.RED);
        switch (this.collisionArea) {
            case 1:
                if(this.rotation == 0) {
                    gc.strokeRect(this.getXPos()-xOffset, this.getYPos()-yOffset, this.getWidth(), this.getHeight());
                } else if (this.rotation != 0) {
                    this.renderRotatedCollision(xOffset, yOffset, gc);
                } 
                break;
            case 2:
                gc.strokeOval(this.getXPos()-xOffset, this.getYPos()-yOffset,
                        this.getWidth(), this.getHeight());
                break;
            case 3:
                break;
            default:
                break;
        }
    }
    
    private void renderRotatedCollision(double xOffset, double yOffset, GraphicsContext gc) {
        //Rectangle is four lines
        
        double topleftX = (this.positionX+rotatePointX)-xOffset + (radius[0] * Math.cos(Math.toRadians(rotation+angle[0]+180)));
        double topleftY = (this.positionY+rotatePointY)-yOffset + (radius[0] * Math.sin(Math.toRadians(rotation+angle[0]+180)));
        
        double toprightX = (this.positionX+rotatePointX)-xOffset + (radius[1] * Math.cos(Math.toRadians(rotation-angle[1])));
        double toprightY = (this.positionY+rotatePointY)-yOffset + (radius[1] * Math.sin(Math.toRadians(rotation-angle[1])));
        
        double bottomleftX = (this.positionX+rotatePointX)-xOffset + (radius[2] * Math.cos(Math.toRadians(rotation-angle[2]+180)));
        double bottomleftY = (this.positionY+rotatePointY)-yOffset + (radius[2] * Math.sin(Math.toRadians(rotation-angle[2]+180)));
        
        double bottomrightX = (this.positionX+rotatePointX)-xOffset + (radius[3] * Math.cos(Math.toRadians(rotation+angle[3])));
        double bottomrightY = (this.positionY+rotatePointY)-yOffset + (radius[3] * Math.sin(Math.toRadians(rotation+angle[3])));
        gc.setStroke(Color.RED);
        /*
        gc.strokeText("TL", topleftX, topleftY);
        gc.strokeText("Rotate: "+rotatePointX+","+rotatePointY+" width: "+this.width+" height: "+this.height, this.getCenterXPos()-xOffset, this.getCenterYPos()-yOffset);
        gc.strokeText("TR", toprightX, toprightY);
        gc.strokeText("BL", bottomleftX, bottomleftY);
        gc.strokeText("BR", bottomrightX, bottomrightY);
        */
        gc.strokeLine(topleftX, topleftY, toprightX, toprightY);
        gc.strokeLine(toprightX, toprightY, bottomrightX, bottomrightY);
        gc.strokeLine(bottomrightX, bottomrightY, bottomleftX, bottomleftY);
        gc.strokeLine(bottomleftX, bottomleftY, topleftX, topleftY);
        
    }           
    
    @Override
    public Shape getBoundary() {
    Shape s;
    switch(collisionArea) {
        case 1: s =  new Rectangle(positionX,positionY,width,height); break;
        case 2: s =  new Ellipse((positionX+(width/2)),(positionY+(height/2)),((width)/2),((height)/2)); break;
        case 3: s = new Line(positionX+width/2, positionY+height, positionX+width/2, positionY); break;
        default: s =  new Rectangle(positionX,positionY,width,height); break;
    }
    Rotate r = new Rotate(0, 0, this.rotation);
    if (this.rotation != 0) s.setRotate(this.rotation);
    return s;
    }

    @Override
    public boolean intersects(MovingGraphics m) {
        if (m instanceof Sprite || m instanceof SpriteSkeleton) return this.intersectsInPixels(m);
        return this.intersectsWithShape(m.getBoundary());
    }
    
    /**
     * Check the if the CollisionBoxes intersects
     * (sweep and prune?) before going to pixel detection
     * @param m Sprite to check collisions with
     * @return True if they overlap somewhere
     */    
    private boolean intersectsInPixels(MovingGraphics m) {
        //Rotated objects are happy with intersection, because pixel collision would require rotating the pixel image too...
        
        if (this.rotation!=0 || m.rotation != 0) {
           return this.intersectsWithShape(m.getBoundary());
        }
        
        if (this.collisionBox.intersects(m.collisionBox)) {
            //Check pixel collsion
            return pixelCollision(this.getXPos(), this.getYPos(), this.getImage(), m.getXPos(), m.getYPos(), m.getImage());
            
        }
        else return false;

    }

    /**
     * Take the pixelreaders from two images,
     * and compare them pixel by pixel to see if a collision
     * happens
     * @param x1 x position of the first image
     * @param y1 y position of the first image
     * @param image1 the first image
     * @param x2 x position of the second image
     * @param y2 y position of the second image
     * @param image2 the second image
     * @return true if the images overlap in pixels
     */
    
    public static boolean pixelCollision(double x1, double y1, Image image1, double x2, double y2, Image image2) {
        //Mists.logger.log(Level.INFO, "pixel collision detection at {0}x{1} - {2}x{3}", new Object[]{x1, y2, x2, y2});
        PixelReader pr1 = image1.getPixelReader();
        PixelReader pr2 = image2.getPixelReader();

        // initialization
        double width1 = x1 + image1.getWidth() -1,
               height1 = y1 + image1.getHeight() -1,
               width2 = x2 + image2.getWidth() -1,
               height2 = y2 + image2.getHeight() -1;

        int xstart = (int) Math.max(x1, x2),
            ystart = (int) Math.max(y1, y2),
            xend   = (int) Math.min(width1, width2),
            yend   = (int) Math.min(height1, height2);

        // intersection rect
        int toty = Math.abs(yend - ystart);
        int totx = Math.abs(xend - xstart);

        for (int y=1;y < toty-1;y++){
          int ny = Math.abs(ystart - (int) y1) + y;
          int ny1 = Math.abs(ystart - (int) y2) + y;

          for (int x=1;x < totx-1;x++) {
            int nx = Math.abs(xstart - (int) x1) + x;
            int nx1 = Math.abs(xstart - (int) x2) + x;
            try {
              if (((pr1.getArgb(nx,ny) & 0xFF000000) != 0x00) &&
                  ((pr2.getArgb(nx1,ny1) & 0xFF000000) != 0x00)) {
                 // collide!!
                 //Mists.logger.info("COLLISION!");
                 return true;
              }
            } catch (Exception e) {
            //System.out.println("s1 = "+nx+","+ny+"  -  s2 = "+nx1+","+ny1);
            }
          }
        }
        //Mists.logger.info("Returning false from pixel collision");
        return false;
    }
    
    @Override
    public String toString()
    {
        return " Position: [" + positionX + "," + positionY + "]" 
        + " Velocity: [" + velocityX + "," + velocityY + "]";
    }
}