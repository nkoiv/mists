/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UtilTests;

import com.nkoiv.mists.game.world.pathfinding.Node;
import com.nkoiv.mists.game.world.util.ComparingQueue;
import com.nkoiv.mists.game.world.util.MinHeap;
import com.nkoiv.mists.game.world.util.SortedList;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author nikok
 */
public class DataStructureTest {
    
    public DataStructureTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    //---MinHeapTests
    @Test
    public void minHeapStartsEmpty() {
        MinHeap testHeap = new MinHeap();
        assert(testHeap.isEmpty());
        assert(testHeap.first() == null);
    }
    
    @Test
    public void minHeapCanHaveNodesAdded() {
        MinHeap testHeap = new MinHeap();
        Node testNode = new Node(0, 0);
        testHeap.add(testNode);
        assert(testHeap.size() == 1);
    }
    
    @Test
    public void minHeapCanHaveItemsRetrieved() {
        MinHeap testHeap = new MinHeap();
        Node testNode = new Node(0, 0);
        testHeap.add(testNode);
        assert(testHeap.first() == testNode);
    }
    
    @Test
    public void minHeapFloatsMinValueToTop() {
        MinHeap testHeap = new MinHeap();
        Node mediumCostNode = new Node(0, 0);
        mediumCostNode.setCostEstimate(50);
        testHeap.add(mediumCostNode);
        Node largeCostNode = new Node(0,0);
        largeCostNode.setCostEstimate(200);
        testHeap.add(largeCostNode);
        Node smallCostNode = new Node(0,0);
        smallCostNode.setCostEstimate(10);
        testHeap.add(smallCostNode);
        
        assert(testHeap.first() == smallCostNode);
        
    }
    
    @Test
    public void minHeapCanBeEmptied() {
        MinHeap testHeap = new MinHeap();
        Node testNode = new Node(0, 0);
        testHeap.add(testNode);
        testHeap.clear();
        assert(testHeap.isEmpty());
    }
    
    @Test
    public void minHeapFirstItemCanBeRemoved() {
        MinHeap testHeap = new MinHeap();
        Node testNode = new Node(0, 0);
        testHeap.add(testNode);
        testHeap.remove(testHeap.first());
        assert(testHeap.isEmpty());
    }
    
    @Test
    public void minHeapObjectRemovalFloatsNextToTop() {
        MinHeap testHeap = new MinHeap();
        Node mediumCostNode = new Node(0, 0);
        mediumCostNode.setCostEstimate(50);
        testHeap.add(mediumCostNode);
        Node largeCostNode = new Node(0,0);
        largeCostNode.setCostEstimate(200);
        testHeap.add(largeCostNode);
        Node smallCostNode = new Node(0,0);
        smallCostNode.setCostEstimate(10);
        testHeap.add(smallCostNode);
        
        testHeap.remove(testHeap.first());
        assert(testHeap.first() == mediumCostNode);
        
    }
    
    @Test
    public void minHeapGrowsWhenOutOfCapacity() {
        MinHeap testHeap = new MinHeap();
        Node[] testNodes = createNodes(100);
        addIntoMinHeap(testHeap, testNodes);
        assert(testHeap.size() > 50);
    }
    
    @Test
    public void minHeapSizeReturnsCorrectSize() {
        MinHeap testHeap = new MinHeap();
        Node[] testNodes = createNodes(12);
        addIntoMinHeap(testHeap, testNodes);
        int counter = 0;
        while (testHeap.first() != null) {
            counter++;
            Node first = (Node)testHeap.first();
            testHeap.remove(first);
        }
        assertTrue(counter == 12);
    }
    
    @Test
    public void minHeapDataSurvivesGrowth() {
        MinHeap testHeap = new MinHeap();
        Node testNode = new Node(0,0);
        testNode.setCostEstimate(-10);
        testHeap.add(testNode);
        Node[] testNodes = createNodes(500);
        addIntoMinHeap(testHeap, testNodes);
        assert(testHeap.first().equals(testNode));
    }
    
    @Test
    public void minHeapFirstNodeCanBeRemoved() {
        MinHeap testHeap = new MinHeap();
        Node[] testNodes = createNodes(100);
        addIntoMinHeap(testHeap, testNodes);
        Node testNode = new Node(0,0);
        testNode.setCostEstimate(-10);
        testHeap.add(testNode);
        testHeap.remove(testNode);
        assertTrue(testHeap.size()==100);
    }
    
    @Test
    public void minHeapLastNodeCanBeRemoved() {
        MinHeap testHeap = new MinHeap();
        Node[] testNodes = createNodes(100);
        addIntoMinHeap(testHeap, testNodes);
        Node testNode = new Node(0,0);
        testNode.setCostEstimate(9999);
        testHeap.add(testNode);
        testHeap.remove(testNode);
        assertTrue(testHeap.size()==100);
    }
    
    @Test
    public void minHeapRandomDeletionWorks() {
        MinHeap testHeap = new MinHeap();
        Node[] testNodes = createNodes(100);
        addIntoMinHeap(testHeap, testNodes);
        Node testNode = new Node(0,0);
        Random rnd = new Random();
        testNode.setCostEstimate(rnd.nextInt(200));
        testHeap.add(testNode);
        testHeap.remove(testNode);
        assertTrue(testHeap.size()==100);
    }
    
    //---ComparingQueueTests
    @Test
    public void comparingQueueStartsEmpty() {
        ComparingQueue testQueue = new ComparingQueue();
        assert(testQueue.isEmpty());
        assert(testQueue.first() == null);
    }
    
    @Test
    public void comparingQueueCanHaveNodesAdded() {
        ComparingQueue testQueue = new ComparingQueue();
        Node testNode = new Node(0, 0);
        testQueue.add(testNode);
        assert(testQueue.size() == 1);
    }
    
    @Test
    public void comparingQueueCanHaveItemsRetrieved() {
        ComparingQueue testQueue = new ComparingQueue();
        Node testNode = new Node(0, 0);
        testQueue.add(testNode);
        assert(testQueue.first() == testNode);
    }
    
    @Test
    public void comparingQueueAddedMinValueGetsToTop() {
        ComparingQueue testQueue = new ComparingQueue();
        Node mediumCostNode = new Node(0, 0);
        mediumCostNode.setCostEstimate(50);
        testQueue.add(mediumCostNode);
        Node largeCostNode = new Node(0,0);
        largeCostNode.setCostEstimate(200);
        testQueue.add(largeCostNode);
        Node smallCostNode = new Node(0,0);
        smallCostNode.setCostEstimate(10);
        testQueue.add(smallCostNode);
        
        assert(testQueue.first() == smallCostNode);
        
    }
    
    @Test
    public void comparingQueueCanBeEmptied() {
        ComparingQueue testQueue = new ComparingQueue();
        Node testNode = new Node(0, 0);
        testQueue.add(testNode);
        testQueue.clear();
        assert(testQueue.isEmpty());
    }
    
    @Test
    public void comparingQueueFirstItemCanBeRemoved() {
        ComparingQueue testQueue = new ComparingQueue();
        Node testNode = new Node(0, 0);
        testQueue.add(testNode);
        testQueue.remove(testQueue.first());
        assert(testQueue.isEmpty());
    }
    
    @Test
    public void comparingQueueObjectRemovalFloatsNextToTop() {
        ComparingQueue testQueue = new ComparingQueue();
        Node mediumCostNode = new Node(0, 0);
        mediumCostNode.setCostEstimate(50);
        testQueue.add(mediumCostNode);
        Node largeCostNode = new Node(0,0);
        largeCostNode.setCostEstimate(200);
        testQueue.add(largeCostNode);
        Node smallCostNode = new Node(0,0);
        smallCostNode.setCostEstimate(10);
        System.out.println(testQueue.toString());
        testQueue.add(smallCostNode);
        System.out.println(testQueue.toString());
        testQueue.remove(testQueue.first());
        System.out.println(testQueue.toString());
        assert(testQueue.first() == mediumCostNode);
        
    }
    
    @Test
    public void comparingQueueGrowsWhenOutOfCapacity() {
        ComparingQueue testQueue = new ComparingQueue();
        Node[] testNodes = createNodes(100);
        addIntoComparingQueue(testQueue, testNodes);
        assert(testQueue.size() > 50);
    }
    
    
    //---SortedListTests
    @Test
    public void sortedListStartsEmpty() {
        SortedList testList = new SortedList();
        assert(testList.isEmpty());
        assert(testList.first() == null);
    }
    
    @Test
    public void sortedListCanHaveNodesAdded() {
        SortedList testList = new SortedList();
        Node testNode = new Node(0, 0);
        testList.add(testNode);
        assert(testList.size() == 1);
    }
    
    @Test
    public void sortedListCanHaveItemsRetrieved() {
        SortedList testList = new SortedList();
        Node testNode = new Node(0, 0);
        testList.add(testNode);
        assert(testList.first() == testNode);
    }
    
    @Test
    public void sortedListFloatsMinValueToTop() {
        SortedList testList = new SortedList();
        Node mediumCostNode = new Node(0, 0);
        mediumCostNode.setCostEstimate(50);
        testList.add(mediumCostNode);
        Node largeCostNode = new Node(0,0);
        largeCostNode.setCostEstimate(200);
        testList.add(largeCostNode);
        Node smallCostNode = new Node(0,0);
        smallCostNode.setCostEstimate(10);
        testList.add(smallCostNode);
        
        assert(testList.first() == smallCostNode);
        
    }
    
    @Test
    public void sortedListCanBeEmptied() {
        SortedList testList = new SortedList();
        Node testNode = new Node(0, 0);
        testList.add(testNode);
        testList.clear();
        assert(testList.isEmpty());
    }
    
    @Test
    public void sortedListFirstItemCanBeRemoved() {
        System.out.println("+++sortedListFirstItemCanBeRemoved+++");
        SortedList testList = new SortedList();
        System.out.println("SortedList size: "+testList.size()+" - First: "+testList.first());
        Node testNode = new Node(0, 0);
        testList.add(testNode);
        System.out.println("SortedList size: "+testList.size()+" - First: "+testList.first());
        testList.remove(testList.first());
        System.out.println("SortedList size: "+testList.size()+" - First: "+testList.first());
        System.out.println("---sortedListFirstItemCanBeRemoved---");
        assert(testList.isEmpty());
        
    }
    
    @Test
    public void sortedListObjectRemovalFloatsNextToTop() {
        SortedList testList = new SortedList();
        Node mediumCostNode = new Node(0, 0);
        mediumCostNode.setCostEstimate(50);
        testList.add(mediumCostNode);
        Node largeCostNode = new Node(0,0);
        largeCostNode.setCostEstimate(200);
        testList.add(largeCostNode);
        Node smallCostNode = new Node(0,0);
        smallCostNode.setCostEstimate(10);
        testList.add(smallCostNode);
        
        testList.remove(testList.first());
        assert(testList.first() == mediumCostNode);
        
    }
    
    @Test
    public void sortedListGrowsWhenOutOfCapacity() {
        SortedList testList = new SortedList();
        Node[] testNodes = createNodes(100);
        addIntoSortedList(testList, testNodes);
        assert(testList.size() > 50);
    }
    
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
    
}
