/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.util;

import java.util.Random;
import java.util.Scanner;

import com.nkoiv.mists.game.dialogue.Card;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.dialogue.Link;
import com.nkoiv.mists.game.world.mapgen.*;
import com.nkoiv.mists.game.world.pathfinding.Node;

/**
 * Testbench is used for testing the performance and utility of the
 * various subcomponents of the game
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
    	DungeonGenerator dg;
        //dg = new BSPDungeonGenerator();
        dg = new MazeDungeonGenerator();
    	dg.generateDungeon(40, 30);
    	/*
    	DungeonContainer dc = new DungeonContainer(40,20);
    	DungeonRoom r1 = new DungeonRoom(6, 5);
    	DungeonRoom r2 = new DungeonRoom(5, 5);
    	dc.addRoom(r1, 10, 5);
    	dc.addRoom(r2, 19, 12);
    	dc.printMap();
    	System.out.print("Distance: "+r1.distanceTo(r2));
    	*/
    }
    
    private static Dialogue buildTestDialogue() {
        Dialogue d = new Dialogue();
        
        Link linkToFirstCard = new Link("Move to the first card", 1);
        Link linkToSecondCard = new Link("Move to second card", 2);
        Link linkToThirdCard = new Link("Move to third card", 3);
        Link linkToEndDialogue = new Link("End dialogue", -1);
        
        Card firstCard = new Card("This is the first card\nMake your choice:");
        firstCard.addLink(linkToSecondCard);
        firstCard.addLink(linkToThirdCard);
        Card secondCard = new Card("Welcome to the second card.\nChoose again");
        secondCard.addLink(linkToThirdCard);
        secondCard.addLink(linkToFirstCard);
        Card thirdCard = new Card("This is the third card\nand contains conversation exit");
        thirdCard.addLink(linkToFirstCard);
        thirdCard.addLink(linkToEndDialogue);
        
        d.addCard(1, firstCard);
        d.addCard(2, secondCard);
        d.addCard(3, thirdCard);
        
        return d;
    }
    
}
