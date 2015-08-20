/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.pathfinding.CollisionMap;
import com.nkoiv.mists.game.world.pathfinding.PathFinder;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;

/**
 * 
 * @author daedra
 */
public class PathFinderTest {
    
    Location testLocation;
    CollisionMap testCollisionMap;
    PathFinder testPathFinder;
    ArrayList<Integer> crossableTerrain;
    
    public PathFinderTest() {
    }
    @Rule 
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        testLocation = new Location("TestLocation");
        testCollisionMap = new CollisionMap(testLocation, 32);
        testPathFinder = new PathFinder(testCollisionMap, 50, true);
        crossableTerrain = new ArrayList<>();
        crossableTerrain.add(0);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void goingTowardsDirectlyLeftShouldGiveLeftDirection() {
        double startPositionX = 32;
        double startPositionY = 32;
        double goalPositionX = 64;
        double goalPositionY = 32;
        
        double[] coordinates = testPathFinder.coordinatesTowards(32,crossableTerrain, startPositionX, startPositionY, goalPositionX, goalPositionY);
        assertTrue(coordinates[0] > 32);
    }
}
