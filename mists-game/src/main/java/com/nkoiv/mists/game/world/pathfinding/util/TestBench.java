/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding.util;

import com.nkoiv.mists.game.world.pathfinding.Node;

/**
 *
 * @author daedra
 */
public class TestBench {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ComparingQueue cq = new ComparingQueue();
        System.out.println("Created list");
        Node testnode1 = new Node(1,1);
        testnode1.setCostEstimate(1);
        Node testnode2 = new Node(2,1);
        testnode2.setCostEstimate(2);
        Node testnode3 = new Node(3,1);
        testnode3.setCostEstimate(0);
        Node testnode4 = new Node(4,1);
        testnode4.setCostEstimate(9);
        cq.add(testnode1);
        cq.add(testnode2);
        cq.add(testnode3);
        cq.add(testnode4);
        System.out.println(cq.toString());
        cq.remove(3);
        System.out.println(cq.toString());
    }
    
}
