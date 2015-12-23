/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import com.nkoiv.mists.game.Direction;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

/**
 * MovingGraphics holds all the basic simple tools
 * Sprites and such need to control their position on the screen
 * @author nikok
 */
public abstract class MovingGraphics {
    protected double width;
    protected double height;
    protected double rotation;
    protected double rotatePointX; //point of rotation
    protected double rotatePointY; //point of rotation
    //corners: [up left][up right][down right][down left]
    protected double[] angle; //Angles to the corners from point of rotation
    protected double[] radius; //Pythagoras is expensive to calc, so do it only once.
    protected double spin; //rotation per timeframe


    /** Position for Sprite is pixel coordinates on the screen 
     *   Not to be confused with position for game objects (they're at a location)
     */
    protected double positionX;
    protected double positionY;    
    /** Velocity indicates the speed and direction a Sprite is heading.
     *  It's noted "per tick" for the game and added to position by calling update(time) 
     */
    protected double velocityX;
    protected double velocityY;
    
    /**
     * By default sprites rotate around their center (width/2, height/2)
     * setting rotationpoint moves this around.
     * @param x xCoordinate (withing the sprite, from top left corner) to rotate the sprite around
     * @param y yCoordinate (withing the sprite, from top left corner) to rotate the sprite around
     */
    public void setRotationPoint(double x, double y) {
        this.rotatePointX = x;
        this.rotatePointY = y;
        this.refreshRotationData();
    }
    
    protected void refreshRotationData() {
        this.angle[0] = Math.toDegrees(Math.atan2(rotatePointY,rotatePointX));
        this.angle[1] = Math.toDegrees(Math.atan2(rotatePointY,width-rotatePointX));
        this.angle[2] = Math.toDegrees(Math.atan2(height-rotatePointY,rotatePointX));
        this.angle[3] = Math.toDegrees(Math.atan2(height-rotatePointY,width-rotatePointX));
        
        this.radius[0] = Math.pow(Math.pow(rotatePointX, 2) + Math.pow(rotatePointY, 2), 0.5);
        this.radius[1] = Math.pow(Math.pow(width-rotatePointX, 2) + Math.pow(rotatePointY, 2), 0.5);
        this.radius[2] = Math.pow(Math.pow(rotatePointX, 2) + Math.pow(height-rotatePointY, 2), 0.5);
        this.radius[3] = Math.pow(Math.pow(width-rotatePointX, 2) + Math.pow(height-rotatePointY, 2), 0.5);
    }
    
    public double getRotationPointX() {
        return this.rotatePointX;
    }
    
    public double getRotationPointY() {
        return this.rotatePointY;
    }
    
    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
    
    public double getSpin() {
        return spin;
    }
    
    public void setSpin(double spin) {
        this.spin = spin;
    }
    
    
    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }
    
    public void setCenterPosition(double x, double y) {
        this.setPosition(x-(this.getWidth()/2), y-(this.getHeight()/2));
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
        return this.width;
    }   
    
    public double getHeight() {
        return this.height;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    public void setHeight(double height) {
        this.height = height;
    }
    
    public Double[] getCenter() {
        double xCenter = this.positionX + (this.width/2);
        double yCenter = this.positionY + (this.height/2);
        Double[] center = new Double[2];
        center[0] = xCenter;
        center[1] = yCenter;
        return center;
    }
    
    public double getCenterXPos() {
        return this.positionX + (this.width/2);
    }
    
    public double getCenterYPos() {
        return this.positionY + (this.height/2);
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

    public Shape getBoundary() {
    Shape s =  new Rectangle(positionX,positionY,width,height);
    Rotate r = new Rotate(0, 0, this.rotation);
    if (this.rotation != 0) s.setRotate(this.rotation);
    return s;
    }
    
    public double getXPos() {
        return this.positionX;
    }
    
    public double getYPos() {
        return this.positionY;
    }
    
}
