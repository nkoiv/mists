/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationTests;

import com.nkoiv.mists.game.world.pathfinding.Node;
import com.nkoiv.mists.game.world.pathfinding.SortedList;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
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
        assert(sortingTimeMS < 10);
    }
}
