/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.pathfinding.CollisionMap;
import com.nkoiv.mists.game.world.pathfinding.PathFinder;
import java.util.ArrayList;
import java.util.Random;
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
        testLocation.loadMap(new TileMap("/mapdata/pathfinder_test.map"));
        testCollisionMap = new CollisionMap(testLocation, 32);
        testPathFinder = new PathFinder(testCollisionMap, 50, true);
        crossableTerrain = new ArrayList<>();
        crossableTerrain.add(0);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void mobsOnLocationAreDisplayedOnCollisionMap() {
        Random rnd = new Random();
        //Make sure creatures count for collisionmap too
        testCollisionMap.setStructuresOnly(false); 
        //Update the collisionMap
        testCollisionMap.updateCollisionLevels();
        //Note that testMap has to have at least 50 mobs on it or the following fails:
        MapObject randomMob = testLocation.getMOBList().get(rnd.nextInt(50));
        int testMobCollisionX = (int)(randomMob.getCenterXPos()/testCollisionMap.getNodeSize());
        int testMobCollisionY = (int)(randomMob.getCenterYPos()/testCollisionMap.getNodeSize());
        //Make sure the tile looks blocked on collisionMap
        assertTrue(testCollisionMap.isBlocked(0, testMobCollisionX, testMobCollisionY));
    }
    
    @Test
    public void goingTowardsDirectlyLeftShouldGiveCoordinatesAtLeft() {
        double startPositionX = 32;
        double startPositionY = 32;
        double goalPositionX = 64;
        double goalPositionY = 32;
        
        double[] coordinates = testPathFinder.coordinatesTowards(32,crossableTerrain, startPositionX, startPositionY, goalPositionX, goalPositionY);
        assertTrue(coordinates[0] > 32);
    }
}
