/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.pathfinding.CollisionMap;
import com.nkoiv.mists.game.world.pathfinding.MoveCostCalculator;
import com.nkoiv.mists.game.world.pathfinding.Path;
import com.nkoiv.mists.game.world.pathfinding.PathFinder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.image.Image;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * 
 * @author daedra
 */
public class PathFinderTest {
    
    private static Location testLocation;
    private CollisionMap testCollisionMap;
    private PathFinder testPathFinder;
    private ArrayList<Integer> crossableTerrain;
    
    public PathFinderTest() {
    }
    @Rule 
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    /*
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    */
    @Before
    public void setUp() {
        if (testLocation == null) {
            testLocation = new Location("TestLocation", new BGMap(new Image("/images/pocmap.png")));
            testLocation.loadMap(new TileMap("/mapdata/pathfinder_test.map"));
        } 
        if (testCollisionMap == null) {
            testCollisionMap = new CollisionMap(testLocation, Mists.TILESIZE);
            testPathFinder = new PathFinder(testCollisionMap, 50, true);
        }
        if (crossableTerrain == null) {
            crossableTerrain = new ArrayList<>();
            crossableTerrain.add(0);
        }
    }
    
    @After
    public void tearDown() {
    }
    
    /*
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
    */
    @Test
    public void goingTowardsDirectlyLeftShouldGiveCoordinatesAtLeft() {
        double startPositionX = 32;
        double startPositionY = 32;
        double goalPositionX = 64;
        double goalPositionY = 32;
        
        double[] coordinates = testPathFinder.coordinatesTowards(32,crossableTerrain, startPositionX, startPositionY, goalPositionX, goalPositionY);
        assertTrue(coordinates[0] > 32);
    }
    /*
    //Rewrite to take in acocunt of the SortedNodeList changes
    @Test
    public void lastStepOnPathIsTheGoal() {
        //NOTE: Because we're doing random locations for pathfinding
        //It is possible to random start and goal that cant be matched
        //Consider tweaking this test further to avoid (rare) random fails
        Random rnd = new Random();
        List<Integer> crossableTerrain = new ArrayList<>();
        crossableTerrain.add(0);
        int randomStartX = rnd.nextInt(testCollisionMap.getMapTileWidth()-1);
        int randomStartY = rnd.nextInt(testCollisionMap.getMapTileHeight()-1);
        int randomGoalX = rnd.nextInt(testCollisionMap.getMapTileWidth()-1);
        int randomGoalY = rnd.nextInt(testCollisionMap.getMapTileHeight()-1);
        Path testPath = testPathFinder.findPath(crossableTerrain, randomStartX, randomStartY, randomGoalX, randomGoalY);
        System.out.println(randomStartX + ","+randomStartY+"  ->  "+randomGoalX+","+randomGoalY);
        System.out.println(testPath.toString());
        assertTrue(testPath.getNode(testPath.getLength()-1).getX() == randomGoalX && testPath.getNode(testPath.getLength()-1).getY() == randomGoalY);
    }
    */
    @Test
    public void pathFinderNeverLeapsOverANode() {
        //NOTE: Because we're doing random locations for pathfinding
        //It is possible to random start and goal that cant be matched
        //Consider tweaking this test further to avoid (rare) random fails
        Random rnd = new Random();
        List<Integer> crossableTerrain = new ArrayList<>();
        crossableTerrain.add(0);
        int randomStartX = rnd.nextInt(testCollisionMap.getMapTileWidth()-1);
        int randomStartY = rnd.nextInt(testCollisionMap.getMapTileHeight()-1);
        int randomGoalX = rnd.nextInt(testCollisionMap.getMapTileWidth()-1);
        int randomGoalY = rnd.nextInt(testCollisionMap.getMapTileHeight()-1);
        Path testPath = testPathFinder.findPath(32, crossableTerrain, randomStartX, randomStartY, randomGoalX, randomGoalY);
        System.out.println(randomStartX + ","+randomStartY+"  ->  "+randomGoalX+","+randomGoalY);
        System.out.println(testPath.toString());
        
        //Check that none of the steps is more than 1 away 
        //from the next one on X or Y
        for (int i=0; i < testPath.getLength()-2; i++) {
            assertTrue(Math.abs(testPath.getNode(i).getX() - testPath.getNode(i+1).getX()) <= 1
            && Math.abs(testPath.getNode(i).getY() - testPath.getNode(i+1).getY()) <= 1);
        }
    }
    
    @Test
    public void manhattanCostForMovementIsXCostPlusZCost() {
        Random rnd = new Random();
        List<Integer> crossableTerrain = new ArrayList<>();
        crossableTerrain.add(0);
        MoveCostCalculator testCalc = new MoveCostCalculator(0);
        int startX = rnd.nextInt(50);
        int startY = rnd.nextInt(50);
        int goalX = rnd.nextInt(50);
        int goalY = rnd.nextInt(50);
        int xDistance = Math.abs(startX-goalX);
        int yDistance = Math.abs(startY-goalY);
        assertTrue(testCalc.getCost(testCollisionMap, crossableTerrain, startX, startY, goalX, goalY) == xDistance+yDistance);
    }
    
    @Test
    public void euclideanCostForMovementIsPythagorans() {
        Random rnd = new Random();
        List<Integer> crossableTerrain = new ArrayList<>();
        crossableTerrain.add(0);
        MoveCostCalculator testCalc = new MoveCostCalculator(2);
        int startX = rnd.nextInt(50);
        int startY = rnd.nextInt(50);
        int goalX = rnd.nextInt(50);
        int goalY = rnd.nextInt(50);
        int AB = Math.abs(startX-goalX);
        int BC = Math.abs(startY-goalY);
        //AC = sqrt(AB^2 + BC^2
        double AC = Math.sqrt(Math.pow(AB, 2)
                            + Math.pow(BC, 2));
        
        assertTrue(testCalc.getCost(testCollisionMap, crossableTerrain, startX, startY, goalX, goalY) == AC);
    }
    
    @Test
    public void diagonalCostForMovementIsSameAsManhattan() {
        Random rnd = new Random();
        List<Integer> crossableTerrain = new ArrayList<>();
        crossableTerrain.add(0);
        int startX = rnd.nextInt(50);
        int startY = rnd.nextInt(50);
        int goalX = rnd.nextInt(50);
        int goalY = rnd.nextInt(50);
        MoveCostCalculator testCalcManhattan = new MoveCostCalculator(1);
        MoveCostCalculator testCalcDiagonal = new MoveCostCalculator(1);
        assertTrue(testCalcManhattan.getCost(testCollisionMap, crossableTerrain, startX, startY, goalX, goalY)
                == testCalcDiagonal.getCost(testCollisionMap, crossableTerrain, startX, startY, goalX, goalY));
    }
    
}
