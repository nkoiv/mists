/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.util;

import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Rectangle;


/**
 * QuadTree taken from Steven Lamberts blog (http://gamedevelopment.tutsplus.com/tutorials/)
 * Modified to be be used with MOBs in a Location
 * @author nikok
 * @param <E> Type of MapObject to store in the quad tree
 */
public class QuadTree<E extends MapObject> {
 
    private int MAX_OBJECTS = 20;
    private int MAX_LEVELS = 5;

    private int level;
    private List<E> objects;
    private Rectangle bounds;
    private QuadTree[] nodes;

    public QuadTree(int pLevel, Rectangle pBounds) {
     level = pLevel;
     objects = new ArrayList();
     bounds = pBounds;
     nodes = new QuadTree[4];
    }

    /**
    * Clears the quadtree
    */
    public void clear() {
     objects.clear();

     for (int i = 0; i < nodes.length; i++) {
       if (nodes[i] != null) {
         nodes[i].clear();
         nodes[i] = null;
       }
     }
    }
    
    /**
    * Heart of the QuadTree
    * Splits the node into 4 subnodes
    */
    private void split() {
      int subWidth = (int)(bounds.getWidth() / 2);
      int subHeight = (int)(bounds.getHeight() / 2);
      int x = (int)bounds.getX();
      int y = (int)bounds.getY();

      nodes[0] = new QuadTree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
      nodes[1] = new QuadTree(level+1, new Rectangle(x, y, subWidth, subHeight));
      nodes[2] = new QuadTree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
      nodes[3] = new QuadTree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }
    
    /*
    * Determine which node the object belongs to. -1 means
    * object cannot completely fit within a child node and is part
    * of the parent node
    */
    private int getIndex(E mob) {
    int index = -1;
    double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
    double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

    // Object can completely fit within the top quadrants
    boolean topQuadrant = (mob.getYPos() < horizontalMidpoint && mob.getYPos() + mob.getHeight() < horizontalMidpoint);
    // Object can completely fit within the bottom quadrants
    boolean bottomQuadrant = (mob.getYPos() > horizontalMidpoint);

    // Object can completely fit within the left quadrants
    if (mob.getXPos() < verticalMidpoint && mob.getXPos() + mob.getWidth() < verticalMidpoint) {
      if (topQuadrant) {
        index = 1;
      }
      else if (bottomQuadrant) {
        index = 2;
      }
    }
    // Object can completely fit within the right quadrants
    else if (mob.getXPos() > verticalMidpoint) {
     if (topQuadrant) {
       index = 0;
     }
     else if (bottomQuadrant) {
       index = 3;
     }
    }

    return index;
    }
    
    /*
    * Insert the object into the quadtree. If the node
    * exceeds the capacity, it will split and add all
    * objects to their corresponding nodes.
    */
    public void insert(E mob) {
        if (nodes[0] != null) {
         int index = getIndex(mob);

         if (index != -1) {
           nodes[index].insert(mob);

           return;
         }
        }

        objects.add(mob);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
          if (nodes[0] == null) { 
             split(); 
          }

         int i = 0;
         while (i < objects.size()) {
           int index = getIndex(objects.get(i));
           if (index != -1) {
             nodes[index].insert(objects.remove(i));
           }
           else {
             i++;
           }
         }
        }
    }
    
    
    /*
    * Recursively dive the quadrants until the correct depth is found
    * Return all objects that could collide with the given object
    */
    public List retrieve(List returnObjects, E mob) {
      int index = getIndex(mob);
      if (index != -1 && nodes[0] != null) {
        nodes[index].retrieve(returnObjects, mob);
      }

      returnObjects.addAll(objects);

      return returnObjects;
    }
}