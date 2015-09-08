/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

/**
 * SortedNodeList for the Pathfinder.
 * Extension of the general Sorted list, with the added methods
 * of picking Nodes by their X and Y
 * @author nikok
 */
public class SortedNodeList extends SortedList {
    
    public SortedNodeList() {
        super();
    }

    /*
    * Return the first node on the list that matches given X and Y
    */
    public Node get(int x, int y) {
        for (int i = 0; i< num;i++) {
            if (((Node)(data[i])).getX() == x && ((Node)data[i]).getY() == y) {
                return (Node)data[i];
            }
        }
        return null;
    }

    /*
    * Check if the list contains a node with the given coordinates
    */
    public boolean contains(int x, int y) {
        //System.out.println("Checking if list contains "+x+","+y);
        if (num==0) return false;
        for (int i = 0; i < num-1; i++) {
            //System.out.println("Currently at data["+i+"], num is"+num);
            if (((Node)data[i]).getX()==x && ((Node)data[i]).getY() == y) return true;
        }
        return false;   
    }


}
