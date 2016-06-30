/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Roof is a type of graphic object that's placed
 * over other graphics when no player is below it.
 * Roofs maybe also be rendered translucent in various situations.
 * 
 * @author nikok
 */
public class Roof implements KryoSerializable {
	private String imageName;
    private Image image;
    private boolean visible;
    private double transparency;
    private double xCoordinate;
    private double yCoordinate;
    private double graphwidth;
    private double graphheight;
    private CollisionBox hiddenArea;
    
    public Roof(String roofImageName) {
    	this(roofImageName, Mists.graphLibrary.getImage(roofImageName));
    }
    
    private Roof(String imageName, Image roofImage) {
    	this.imageName = imageName;
        this.image = roofImage;
        this.graphwidth = roofImage.getWidth();
        this.graphheight = roofImage.getHeight();
        this.transparency = 0.2;
    }
    
    public void renderWithPlayerVision(boolean playerSeesUnderneath, double xOffset, double yOffset, GraphicsContext gc) {
        gc.save();
        if (playerSeesUnderneath) gc.setGlobalAlpha(transparency);
        else gc.setGlobalAlpha(1);
        this.render(xOffset, yOffset, gc);
        gc.restore();
    }
    
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        gc.drawImage(image, xCoordinate - xOffset, yCoordinate - yOffset);
    }
       
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double getTransparency() {
        return transparency;
    }
    
    /**
     * Define the area hidden under the roof.
     * If player has vision to this area, then the roof should be
     * translucent or transparent
     * @param xStart Upper left corner of the hidden area
     * @param yStart Upper left corner of the hidden area
     * @param width Width of the hidden area
     * @param height Height of the hidden area
     */
    public void setHiddenArea(double xStart, double yStart, double width, double height) {
        this.hiddenArea = new CollisionBox(xStart, yStart, width, height);
    }
    
    public CollisionBox getHiddenArea() {
        return this.hiddenArea;
    }

    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }
       
    public void setPosition(double xCoor, double yCoor) {
        this.xCoordinate = xCoor;
        this.yCoordinate = yCoor;
    }
    
    public double getXCoor() {
        return this.xCoordinate;
    }
    
    public double getYCoor() {
        return this.yCoordinate;
    }
    
    public double getWidth() {
        return this.graphwidth;
    }
    
    public double getHeigth() {
        return this.graphheight;
    }

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeString(this.imageName);
		output.writeBoolean(visible);
	    output.writeDouble(transparency);
	    output.writeDouble(xCoordinate);
	    output.writeDouble(yCoordinate);
	    //HiddenArea
	    output.writeDouble(hiddenArea.minX);
	    output.writeDouble(hiddenArea.minY);
	    output.writeDouble(hiddenArea.maxX-hiddenArea.minX);
	    output.writeDouble(hiddenArea.maxY-hiddenArea.minY);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.imageName = input.readString();
		this.image = Mists.graphLibrary.getImage(imageName);
		this.visible = input.readBoolean();
		this.transparency = input.readDouble();
		this.xCoordinate = input.readDouble();
		this.yCoordinate = input.readDouble();
        this.graphwidth = image.getWidth();
        this.graphheight = image.getHeight();
        //HiddenArea
        double haX = input.readDouble();
        double haY = input.readDouble();
        double haW = input.readDouble();
        double haH = input.readDouble();
        this.setHiddenArea(haX, haY, haW, haH);
	}
}
