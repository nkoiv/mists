/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.util;

import com.nkoiv.mists.game.world.pathfinding.Node;
import java.util.Random;

/**
 * Testbench is used for testing the performance and utility of the
 * various node-storages used in pathfinding.
 * @author daedra
 */
public class TestBench {
    
    /**
     * Create a bunch of nodes in an array for testing purposes
     * @param number Number of nodes to return
     * @return An array with the nodes
     */
    private static Node[] createNodes(int number) {
        Node[] nodeArray = new Node[number];
        int xMax = 100;
        int yMax = 100;
        Random rnd = new Random();
        for (int i = 0; i < number; i++ ) {
            int xCoor = rnd.nextInt(xMax);
            int yCoor = rnd.nextInt(yMax);
            Node n = new Node(xCoor, yCoor);
            n.setCostEstimate(xCoor+yCoor); //Manhattan cost from 0
            nodeArray[i] = n; //Set the node in the array;
        }
        return nodeArray;
    }
    
    private static double addIntoMinHeap(MinHeap heap, Node[] nodeArray) {
        double starttime = System.nanoTime();
        for (Node node : nodeArray) {
            heap.add(node);
        }
        double exectime = (System.nanoTime() - starttime);
        //System.out.println(nodeArray.length + " added into MH in "+exectime+"nanosecs");
        return exectime;
    }
    
    private static double addIntoComparingQueue(ComparingQueue queue, Node[] nodeArray) {
        double starttime = System.nanoTime();
        for (Node node : nodeArray) {
            queue.add(node);
        }
        double exectime = (System.nanoTime() - starttime);
        //System.out.println(nodeArray.length + " added into CQ in "+exectime+"nanosecs");
        return exectime;
    }
    
    private static double addIntoSortedList (SortedList list, Node[] nodeArray) {
        double starttime = System.nanoTime();
        for (Node node : nodeArray) {
            list.add(node);
        }
        double exectime = (System.nanoTime() - starttime);
        //System.out.println(nodeArray.length + " added into CQ in "+exectime+"nanosecs");
        return exectime;
    }
    
    private static double mean(Double[] m) {
        double sum = 0;
        for (Double n : m) {
            sum += n;
        }
        return (sum / m.length);
    }
    
    private static double meanNsAddTimeCQ(Node[] nodes, int timesToRun) {
        ComparingQueue cq = new ComparingQueue();
        Double[] cqTimes = new Double[timesToRun];
        for (int i = 0; i < timesToRun; i++) {
            cq = new ComparingQueue();
            cqTimes[i] = addIntoComparingQueue(cq, nodes);
        }
        return mean(cqTimes);
    }
    
    private static double meanNsAddTimeSL(Node[] nodes, int timesToRun) {
        SortedList sl = new SortedList();
        Double[] cqTimes = new Double[timesToRun];
        for (int i = 0; i < timesToRun; i++) {
            sl = new SortedList();
            cqTimes[i] = addIntoSortedList(sl, nodes);
        }
        return mean(cqTimes);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int nodeAmount = 100;
        int runtimes = 100;
        Node[] nodes = createNodes(nodeAmount);
        MinHeap testHeap = new MinHeap();
        
        Node tn1 = new Node(5, 3);
        tn1.setCostEstimate(50);
        Node tn2 = new Node(4, 1);
        tn2.setCostEstimate(90);
        Node tn3 = new Node(2, 5);
        tn3.setCostEstimate(15);
        Node tn4 = new Node(4, 9);
        tn4.setCostEstimate(26);
        
        testHeap.add(tn1);
        System.out.println("Heap: "+testHeap.toString());
        testHeap.add(tn2);
        testHeap.add(tn3);
        testHeap.add(tn4);
        System.out.println("Heap: "+testHeap.toString());
        testHeap.remove(tn3);
        System.out.println("Heap: "+testHeap.toString());
        addIntoMinHeap(testHeap, nodes);
        System.out.println("Heap: "+testHeap.toString());
        addIntoMinHeap(testHeap, nodes);
        addIntoMinHeap(testHeap, nodes);
        addIntoMinHeap(testHeap, nodes);
        System.out.println("Heap: "+testHeap.toString());
        testHeap.remove(tn4);
        testHeap.add(tn2);
        System.out.println("Heap: "+testHeap.toString());
    }
    
}
