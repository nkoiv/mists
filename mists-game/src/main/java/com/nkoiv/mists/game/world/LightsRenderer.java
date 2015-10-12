/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Structure;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * LightsRenderer takes in a bunch of mapObjects
 * (hopefully located on the screen) and turns
 * them into polygons, which are subsequently
 * used in ray tracing what's visible and what isn't.
 * 
 * @author nikok
 */
public class LightsRenderer {
    Location loc;
    Polygon[] obstacles;
    double[][] lightmap;
    double minLightLevel;
    
    public LightsRenderer(Location loc) {
        this.minLightLevel=0;
        this.loc = loc;
        this.obstacles = new Polygon[0];
        int tileWidth = (int)(loc.getMap().getWidth() / Mists.TILESIZE);
        int tileHeight = (int)(loc.getMap().getHeight() / Mists.TILESIZE);
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
        for (int row = (int)(yStart/Mists.TILESIZE); row <= (int)(yEnd/Mists.TILESIZE); row++) {
            for (int column = (int)(xStart/Mists.TILESIZE); column <= (int)(xEnd/Mists.TILESIZE); column++) {
                gc.setFill(Color.BLACK);
                //gc.setStroke(Color.BLACK);
                if(column< lightmap.length && row < lightmap[0].length)gc.setGlobalAlpha(1 - lightmap[column][row]);
                gc.fillRect((column*Mists.TILESIZE)-xOffset, (row*Mists.TILESIZE)-yOffset, Mists.TILESIZE+1, Mists.TILESIZE+1);
                //gc.strokeRect((column*Mists.TILESIZE)-xOffset, (row*Mists.TILESIZE)-yOffset, Mists.TILESIZE, Mists.TILESIZE);
            }
        }
        //Mists.logger.log(Level.INFO, "Drawing shadows around{0}-{1}/{2}-{3}", new Object[]{xStart, xEnd, yStart, yEnd});
        gc.restore();
    }
    
    
    private void clearLightmap() {
        int tileWidth = (int)(loc.getMap().getWidth() / Mists.TILESIZE);
        int tileHeight = (int)(loc.getMap().getHeight() / Mists.TILESIZE);
        this.lightmap = new double[tileWidth][tileHeight];
        
        for (double[] lightmap1 : this.lightmap) {
            for (int row = 0; row < this.lightmap[0].length; row ++) {
                lightmap1[row] = this.getMinLightLevel();
            }
        }
        
    }
    
    /**
    * paintVision clears shadows from the lightmap, flooding
    * light from given coordinates, octant at a time.
    * Whenever paintVision encounters a wall, it stops giving
    * light towards that direction - the wall itself is lit up though.
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
                            }
                        } else { //Lightlevel starts to fall of immediately
                            double lightlevel = Math.max(0.9 - (row * 0.1), 0);
                            lightmap[x][y] = Math.max(this.minLightLevel, lightlevel);
                        }
                        
                        if (loc.getCollisionMap().getNode(x, y).getCollisionLevel()>0) {
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
        
        
        
    }
    
    public static void drawShadows(GraphicsContext gc, MapObject[] mobs, double xOffset, double yOffset) {
        Polygon[] mobPolygons = mobsIntoPolygons(mobs, xOffset, yOffset);
    }
    
    public void updateObstacles(MapObject[] mobs, double xOffset, double yOffset) {
        this.obstacles = mobsIntoPolygons(mobs, xOffset, yOffset);
    }
    
    
    public double[] getIntersection(double[] ray, double[] segment){
	// RAY in parametric: Point + Delta*T1
        //Convert the pair of points into a vector
	double r_px = ray[0]; //xstart
	double r_py = ray[1]; //ystart
	double r_dx = ray[3]-ray[0]; //xdistance
	double r_dy = ray[4]-ray[1]; //ydistance
	// SEGMENT in parametric: Point + Delta*T2
        double s_px = segment[0]; //xstart
	double s_py = segment[1]; //ystart
	double s_dx = segment[3]-segment[0]; //xdistance
	double s_dy = segment[4]-segment[1]; //ydistance
	// Are they parallel? If so, no intersect
	double r_mag = Math.sqrt(r_dx*r_dx+r_dy*r_dy);
	double s_mag = Math.sqrt(s_dx*s_dx+s_dy*s_dy);
	if(r_dx/r_mag==s_dx/s_mag && r_dy/r_mag==s_dy/s_mag){
		// Unit vectors are the same.
		return null;
	}
	// SOLVE FOR T1 & T2
	// r_px+r_dx*T1 = s_px+s_dx*T2 && r_py+r_dy*T1 = s_py+s_dy*T2
	// ==> T1 = (s_px+s_dx*T2-r_px)/r_dx = (s_py+s_dy*T2-r_py)/r_dy
	// ==> s_px*r_dy + s_dx*T2*r_dy - r_px*r_dy = s_py*r_dx + s_dy*T2*r_dx - r_py*r_dx
	// ==> T2 = (r_dx*(s_py-r_py) + r_dy*(r_px-s_px))/(s_dx*r_dy - s_dy*r_dx)
	double T2 = (r_dx*(s_py-r_py) + r_dy*(r_px-s_px))/(s_dx*r_dy - s_dy*r_dx);
	double T1 = (s_px+s_dx*T2-r_px)/r_dx;
	// Must be within parametic whatevers for RAY/SEGMENT
	if(T1<0) return null;
	if(T2<0 || T2>1) return null;
	// Return the POINT OF INTERSECTION
        double[] pointOfIntersection = new double[]{r_px+r_dx*T1, r_py+r_dy*T1};
	return pointOfIntersection;
}
    
    
    /**
     * Turns a bunch of MapObjects into polygons for
     * easier use
     * @param mobs The list of MapObjects
     * @param xOffset needed to get the relative position on screen
     * @param yOffset needed to get the relative position on screen
     * @return list of polygons
     */
    private static Polygon[] mobsIntoPolygons(MapObject[] mobs, double xOffset, double yOffset) {
        Polygon[] mobPolygons = new Polygon[mobs.length];
        int i =0;
        for (MapObject mob : mobs) {
            if (mob instanceof Structure) {
            Polygon mobPolygon = new Polygon();
            mobPolygon.getPoints().addAll(new Double[]{
               mob.getXPos()-xOffset, mob.getYPos()-yOffset,
               mob.getXPos()-xOffset+mob.getSprite().getWidth(), mob.getYPos()-yOffset,
               mob.getXPos()-xOffset, mob.getYPos()-yOffset+mob.getSprite().getHeight(),
               mob.getXPos()-xOffset+mob.getSprite().getWidth(), mob.getYPos()-yOffset+mob.getSprite().getHeight()
            });        
            mobPolygons[i] = mobPolygon;
            i++;
            }
        }
        return mobPolygons;
    }
    
    public void setMinLightLevel(double lightlevel) {
        this.minLightLevel = lightlevel;
    }
    
    public double getMinLightLevel() {
        return this.minLightLevel;
    }
}
