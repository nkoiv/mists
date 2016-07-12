/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.worldmap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Templatable;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class MapNode implements KryoSerializable, Templatable {
		protected int id;
        protected String name;
        protected String imageName;
        protected Image imageOnMap;
        protected boolean bigNode;
        protected double xPos;
        protected double yPos;
        protected int[] neighboursByDirection; //0 is left empty, as the direction would be Direction.STAY
        /* Neighbours by direction
        * [8][1][2]
        * [7]   [3]
        * [6][5][4]
        */  
        
        public MapNode() {
        	this.neighboursByDirection = new int[9];
        }
        

        public MapNode(String name, Image image) {
            this.name = name;
            this.neighboursByDirection = new int[9];
            if (image != null) {
                this.imageOnMap = image;
                if (image.getWidth() > 32) bigNode = true;
            }
        }
        
        public void enterNode() {
            Mists.logger.info("Node "+this.name+" entered");
        }
        
        public void exitNode() {
            Mists.logger.info("Node "+this.name+" exited");
        }
        
        public int getNeighbour(Direction d) {
            return neighboursByDirection[Toolkit.getDirectionNumber(d)];
        }
        
        public void setNeighbour(MapNode neighbour, Direction d) {
            this.neighboursByDirection[Toolkit.getDirectionNumber(d)] = neighbour.getID();
        }
        
        public void setNeighbour(int neighbourID, Direction d) {
        	this.neighboursByDirection[Toolkit.getDirectionNumber(d)] = neighbourID;
        }
        
        public ArrayList<Integer> getNeighboursAsAList() {
            ArrayList<Integer> n = new ArrayList<>();
            for (int i = 1; i < neighboursByDirection.length; i++) {
                n.add(neighboursByDirection[i]);
            }
            return n;
        }
        
        /**
         * Return the size of the node (circle surrounding it)
         * @return Node circle size
         */
        public double getSize() {
        	if (bigNode) return 64;
        	else return 32;
        }
        
        public int getID() {
        	return this.id;
        }
        
        public void setID(int id) {
        	this.id = id;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setImage(Image image) {
            this.imageOnMap = image;
        }
        
        public Image getImage() {
            return this.imageOnMap;
        }
        
        public void render(GraphicsContext gc, double xOffset, double yOffset) {
            if (bigNode) gc.drawImage(Mists.graphLibrary.getImage("circle64"), xPos - xOffset, yPos - yOffset);
            if (!bigNode) gc.drawImage(Mists.graphLibrary.getImage("circle32"), xPos - xOffset, yPos - yOffset);
            if (imageOnMap!=null) gc.drawImage(imageOnMap, xPos - xOffset, yPos - yOffset);
        }
        
        public double getXPos() {
            return this.xPos;
        }
        
        public double getYPos() {
            return this.yPos;
        }
        
        public double getCenterXPos() {
        	return this.xPos+(this.getSize()/2);
        }
        
        public double getCenterYPos() {
        	return this.yPos+(this.getSize()/2);
        }
        
        public void setXPos(double xPos) {
            this.xPos = xPos;
        }
        
        public void setYPos(double yPos) {
            this.yPos = yPos;
        }
        
        public void setPosition(double xPos, double yPos) {
            this.xPos = xPos;
            this.yPos = yPos;
        }

		@Override
		public void write(Kryo kryo, Output output) {
			output.writeString(name);
			output.writeInt(id);
			output.writeString(imageName);
			output.writeBoolean(bigNode);
			for (int i = 0; i < 9; i++) {
				output.writeInt(neighboursByDirection[i]);
			}
		}

		@Override
		public void read(Kryo kryo, Input input) {
			this.name = input.readString();
			this.id = input.readInt();
			this.imageName = input.readString();
			this.bigNode = input.readBoolean();
			for (int i = 0; i < 9; i++) {
				int nid = input.readInt();
				this.neighboursByDirection[i] = nid;
			}
			loadGraphics();
		}
		
		private void loadGraphics() {
			MapNode mn = Mists.worldmapLibrary.getMapNodeTemplate(id);
			if (mn!=null) this.imageOnMap = mn.getImage();
			else Mists.logger.warning("Could not load image for MapNode "+name+" #"+id+ " - missing from library!");
		}

		@Override
		public MapNode createFromTemplate() {
			MapNode n = new MapNode(this.name, this.imageOnMap);
			n.id = this.id;
			n.imageName = this.imageName;
			n.bigNode = this.bigNode;
			return n;
		}
        
        
    }
