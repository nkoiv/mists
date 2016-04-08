/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Mists;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;

/**
 * LightsRenderer takes in a bunch of mapObjects
 * (hopefully located on the screen) and turns
 * them into polygons, which are subsequently
 * used in ray tracing what's visible and what isn't.
 * 
 * @author nikok
 */
public class LightsRenderer {
    private Location loc;
    public final double[][] lightmap;
    public boolean[][] explored;
    private double minLightLevel;
    
    public LightsRenderer(Location loc) {
        this.minLightLevel=0.5;
        this.loc = loc;
        int tileWidth = (int)(loc.getMap().getWidth() / Mists.TILESIZE);
        int tileHeight = (int)(loc.getMap().getHeight() / Mists.TILESIZE);
        this.explored = new boolean[tileWidth][tileHeight];
        this.lightmap = new double[tileWidth][tileHeight];
        clearLightmap();
        Mists.logger.log(Level.INFO, "Generated Lightmap ({0}x{1})", new Object[]{tileWidth, tileHeight});
    }
    
    
    /**
     * RenderLightmap draws (black) opaque shadowboxes on
     * all the tiles, based on their light level.
     * @param gc GraphicsContext to draw on
     * @param xOffset position of the screen on the map
     * @param yOffset position of the screen on the map
     */
    public void renderLightMap(GraphicsContext gc, double xOffset, double yOffset) {
        gc.save();
        double xStart = xOffset;
        double xEnd = xOffset + gc.getCanvas().getWidth();
        double yStart = yOffset;
        double yEnd = yOffset + gc.getCanvas().getHeight();
        double xFraction;
        double yFraction;
        xFraction = (xStart/Mists.TILESIZE - (int)(xStart/Mists.TILESIZE));
        yFraction = (yStart/Mists.TILESIZE - (int)(yStart/Mists.TILESIZE));
        //Mists.logger.info(xFraction+"x"+yFraction);
        for (int row = (int)(yStart/Mists.TILESIZE); row <= (int)(yEnd/Mists.TILESIZE); row++) {
            for (int column = (int)(xStart/Mists.TILESIZE); column <= (int)(xEnd/Mists.TILESIZE); column++) {
                gc.setFill(Color.BLACK);
                //gc.setStroke(Color.BLACK);
                if(column< lightmap.length && row < lightmap[0].length) gc.setGlobalAlpha(1 - lightmap[column][row]);
                //if (gc.getGlobalAlpha()<1) gc.setGlobalAlpha(0); //discard gradient values
                gc.fillRect((column*Mists.TILESIZE)-(int)xOffset, (row*Mists.TILESIZE)-(int)yOffset, Mists.TILESIZE, Mists.TILESIZE);
                //gc.strokeRect((column*Mists.TILESIZE)-xOffset, (row*Mists.TILESIZE)-yOffset, Mists.TILESIZE, Mists.TILESIZE);
            }
        }
        gc.setGlobalAlpha(0);
        //Mists.logger.log(Level.INFO, "Drawing shadows around{0}-{1}/{2}-{3}", new Object[]{xStart, xEnd, yStart, yEnd});
        gc.restore();
    }
    
    public void renderLightSource(GraphicsContext shadowCanvas, double xCoor, double yCoor, double lightscale) {
        renderLightSource(shadowCanvas, xCoor, yCoor, lightscale, null);
    }
    
    public void renderLightSource(GraphicsContext shadowCanvas, double xCoor, double yCoor, double lightscale, Color color) {
        shadowCanvas.save();
        Image light = Mists.graphLibrary.getImage("lightspot");
        double drawX = (xCoor/lightscale)-light.getWidth()/(lightscale*2);
        double drawY = (yCoor/lightscale)-light.getHeight()/(lightscale*2);
        /*
        //TODO: Fix this light colour thing so it actually works
        if (color != null) {
            Color c = color.deriveColor(1, 1, 1, 0);
            ColorAdjust monochrome = new ColorAdjust();
            monochrome.setSaturation(-1.0);
            //monochrome.setBrightness(-1.0);
            Blend blend = new Blend(
                BlendMode.OVERLAY,
                new ColorInput(
                        drawX,
                        drawY,
                        light.getWidth() * lightscale,
                        light.getHeight() * lightscale,
                        c
                
                )
                ,monochrome
            );
            shadowCanvas.setEffect(blend);
        }
        */
        
        Scale s = new Scale(lightscale, lightscale, light.getWidth()/2, light.getHeight()/2);
        Affine a = new Affine();
        a.append(s);
        
        shadowCanvas.setTransform(a);
        
        
        shadowCanvas.drawImage(light, drawX, drawY);
        shadowCanvas.restore();
    }
    
    /**
     * Check if the given area contains any tiles
     * that have been made visible by the player vision
     * (via paintVision())
     * @param xCoor upper left corner
     * @param yCoor upper left corner
     * @param width width of the area
     * @param height height of the area
     * @return 
     */
    public boolean containsVisibleTiles(double xCoor, double yCoor, double width, double height) {
        int tileXStart = (int)(xCoor / Mists.TILESIZE);
        int tileYStart = (int)(yCoor / Mists.TILESIZE);
        int tileXEnd = (int)((xCoor+width) / Mists.TILESIZE);
        int tileYEnd = (int)((yCoor+height) / Mists.TILESIZE);
        if (tileXStart < 0 || tileYStart < 0) return false;
        if (tileXEnd > lightmap.length || tileYEnd > lightmap[0].length) return false;
        for (int x = tileXStart; x < tileXEnd; x++) {
            for (int y = tileYStart; y < tileYEnd; y++) {
                if (lightmap[x][y] > this.minLightLevel) return true;
            }
        }
        return false;
    }
    
    private void clearLightmap() {
        //int tileWidth = (int)(loc.getMap().getWidth() / Mists.TILESIZE);
        //int tileHeight = (int)(loc.getMap().getHeight() / Mists.TILESIZE);
        //this.lightmap = new double[tileWidth][tileHeight];
        for (double[] column : this.lightmap) {
            for (int row = 0; row < this.lightmap[0].length; row ++) {
                //if (explored[column][row]) lightmap[column][row] = Math.max(0.1, minLightLevel);
                column[row] = minLightLevel; //TODO: Explored is causing issues with lightning structure extras: rethink!
            }
        }
        
    }
    
    /**
    * paintVision clears shadows from the lightmap, flooding
    * light from given coordinates, octant at a time.
    * Whenever paintVision encounters a wall, it stops giving
    * light towards that direction - the wall itself is lit up though.
     * @param xCoor xCoordinate of the player
     * @param yCoor yCoordinate of the player
     * @param visionRange How far the player can see
    */
    public void paintVision(double xCoor, double yCoor, int visionRange) {
        //clear lightmap
        clearLightmap();
        //Mists.logger.log(Level.INFO, "Cleared new lightmap: {0}x{1}", new Object[]{lightmap.length, lightmap[0].length});
        //do all eight octants
        for (int octant = 0; octant <8; octant++) {
        int[] shadows = new int[visionRange+1];
        for (int row = 0; row < visionRange; row++) {
            for (int col = 0; col <= row; col++) {
                    if(shadows[col]==0) {
                    int[] tile = transformOctant(col, row, octant);
                    int x = (int)(xCoor/Mists.TILESIZE) + tile[0];
                    int y = (int)(yCoor/Mists.TILESIZE) - tile[1];
                    if(x<lightmap.length &&
                       y <lightmap[0].length &&
                       x>=0 && y>= 0) {
                        if (visionRange>8) { //A lot of fully lit up blocks
                            if (row<(visionRange-8))lightmap[x][y] = 0.9;
                            else {
                                double lightlevel = Math.max(0.9 - ((row-(visionRange-8)) * 0.1), 0);
                                lightmap[x][y] = Math.max(this.minLightLevel, lightlevel);
                                explored[x][y] = true;
                            }
                        } else { //Lightlevel starts to fall of immediately
                            double lightlevel = Math.max(0.9 - (row * 0.1), 0);
                            lightmap[x][y] = Math.max(this.minLightLevel, lightlevel);
                            if (lightlevel > this.minLightLevel) explored[x][y] = true;
                        }
                        
                        if (loc.getCollisionMap().getNode(x*(Mists.TILESIZE/loc.getCollisionMap().getNodeSize()),
                                y*(Mists.TILESIZE/loc.getCollisionMap().getNodeSize())).getCollisionLevel()>0) {
                            shadows[col] = 1;
                            if (col==row) {
                                for (int i = col; i < shadows.length; i++)
                                shadows[i] = 1;
                            }
                        }
                    }
                    
                }
            }
        }
        }
    }
    
    private int[] transformOctant(int row, int col, int octant) {
        switch (octant) {
          case 0: return new int[]{col, -row};
          case 1: return new int[]{row, -col};
          case 2: return new int[]{row, col};
          case 3: return new int[]{col, row};
          case 4: return new int[]{-col, row};
          case 5: return new int[]{-row, col};
          case 6: return new int[]{-row, -col};
          case 7: return new int[]{-col, -row};
          default: return new int[]{row, -col};
        }
    }
    
    public void renderLight(GraphicsContext gc, double xCoordinate, double yCoordinate, double intensity, double size) {
        Point2D lightPosition = new Point2D(xCoordinate, yCoordinate);
        
  
    }
    
    public double getLightLevel(int x, int y) {
        return this.lightmap[x][y];
    }
    
    public void setMinLightLevel(double lightlevel) {
        this.minLightLevel = lightlevel;
        //if (this.minLightLevel > 0.8) this.minLightLevel = 0.8;
    }
    
    public double getMinLightLevel() {
        return this.minLightLevel;
    }
    
    public void printLightMapToConsole() {
        NumberFormat formatter = new DecimalFormat("#0.0");
        System.out.println();
        for (double[] column : lightmap){
            for (int i = 0; i < column.length; i ++) {
                double d = column[i];
                formatter.format(i);
                System.out.print(d);
            }
            System.out.println();
        }
    }
}
