/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.DoorTrigger;
import com.nkoiv.mists.game.triggers.Trigger;

import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class Door  extends Structure {
    private int closedCollisionLevel;
    private boolean open;
    private Image openImage;
    private Image closedImage;
    
    public Door() {
    	
    }
    
    public Door(String name, Image closedImage, Image openImage, int collisionLevel) {
        super(name, closedImage, collisionLevel);
        this.closedCollisionLevel = collisionLevel;
        this.closedImage = closedImage;
        this.openImage = openImage;
        this.open = false;
    }
    
    @Override
    public Sprite getSprite() {
        return (Sprite)this.graphics;
    }
    
    public void toggle() {
        Mists.logger.info("Toggling door");
        if (this.isFlagged("locked")) {
            this.addTextPopup("Locked!");
        } else {
            if (this.isOpen()) this.close();
            else this.open();
            this.location.getCollisionMap().updateCollisionLevels();
            this.location.getPathFinder().setMapOutOfDate(true);
        }
    }
    
    public void open() {
       this.open = true; 
       this.setCollisionLevel(0);
       this.getSprite().setImage(openImage);
    }
    
    public void close() {
        this.open = false;
        this.setCollisionLevel(closedCollisionLevel);
        this.getSprite().setImage(closedImage);
    }
    
    public boolean isOpen() {
        return this.open;
    }

    public boolean isLocked() {
        return isFlagged("locked");
    }

    public void setLocked(boolean locked) {
        if (locked) {
            this.setFlag("locked", 1);
        } else {
            this.setFlag("locked", 0);
        }
    }
    
    
    @Override
    public Trigger[] getTriggers() {
        Trigger[] a = new Trigger[]{new DoorTrigger(this)};
        return a;
    }
    
    
    @Override
    public Door createFromTemplate() {
        Door nd = new Door(this.name, this.closedImage, this.openImage, this.collisionLevel);
        nd.templateID = this.templateID;
        for (String f : this.flags.keySet()) {
            nd.setFlag(f, this.flags.get(f));
        }
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getXPos();
                double yOffset = s.getYPos() - this.getYPos();
                nd.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return nd;
    }
    
	@Override
	public void write(Kryo kryo, Output output) {
		super.write(kryo, output);
		output.writeInt(this.closedCollisionLevel);
		output.writeBoolean(this.open);
	}


	@Override
	public void read(Kryo kryo, Input input) {
		super.read(kryo,  input);
		this.closedCollisionLevel = input.readInt();
		this.open = input.readBoolean();
	}
	
	@Override
	protected void readGraphicsFromLibrary(int templateID, double xCoor, double yCoor) {
		Mists.logger.info("Reading door graphics from library (id: "+templateID+")");
		if (Mists.structureLibrary != null) {
			Structure dummy = Mists.structureLibrary.create(templateID);
			if (dummy instanceof Door) {
				Mists.logger.info("Found door template "+this.templateID);
				Door d = (Door)dummy;
				this.graphics = d.graphics;
				this.extraSprites = d.extraSprites;
				this.openImage = d.openImage;
				this.closedImage = d.closedImage;
			}
		} 
		if (this.graphics == null) {
			Mists.logger.info("Blank sprite generated for "+this.getName());
			this.graphics = new Sprite();
			this.openImage = Mists.graphLibrary.getImage("blank");
			this.closedImage = Mists.graphLibrary.getImage("blank");
		}
		if (this.open) this.open();
		else this.close();
		this.setPosition(xCoor, yCoor);
	}
} 