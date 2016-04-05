/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationTests;

import com.nkoiv.mists.game.world.pathfinding.Node;
import com.nkoiv.mists.game.world.util.SortedList;
import java.util.Random;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author daedra
 */
public class SortedNodeListTest {
    
    public SortedNodeListTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    /*
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    */
    @Test
    public void nodesCanBeFoundFromTheList() {
        SortedList testList = new SortedList();
        Node testNode = new Node(1, 1);
        testList.add(testNode);
        assertTrue(testList.contains(testNode));
    }
    
    @Test
    public void nodesCanBeAddedOnTheList() {
        SortedList testList = new SortedList();
        Node testNode = new Node(1, 1);
        System.out.println("Adding node to list");
        testList.add(testNode);
        System.out.println("List size: " +testList.size());
        assertTrue(testList.size() == 1);
    }
    
    @Test
    public void nodesCanBeRemovedFromTheList() {
        SortedList testList = new SortedList();
        Node testNode = new Node(1, 1);
        testList.add(testNode);
        testList.remove(testNode);
        assertTrue(testList.size() == 0);
        
    }

    @Test
    public void bechmarkNodeListSortSpeed(){
        SortedList testList = new SortedList();
        Random rnd = new Random();
        for (int i = 0; i < 1000; i++) {
            Node testNode = new Node(i,i);
            testNode.setCostEstimate(rnd.nextInt(500));
            testList.addWithoutSorting(testNode);
        }
        System.out.println( testList.toString());
        double startTime = System.nanoTime();
        testList.quickSort();
        double endTime = System.nanoTime();
        double sortingTimeMS = ((endTime-startTime)/1000000);
        System.out.println("Sorting done in "+sortingTimeMS+"ms");
        assertTrue(sortingTimeMS < 50);
    }
}
