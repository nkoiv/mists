package LocationTests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.GameMap;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import java.util.EnumSet;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;



/**
 *
 * @author nkoiv
 */
public class GeneralLocationTest {
    
    private static Location testLocation;
    private Creature testCreature;
    private static GameMap testMap; 
    private Mists mists;
    
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
       if (Mists.creatureLibrary == null) {
           Mists.loadLibraries();
       }
       testLocation = new Location("TestLocation", new BGMap(new Image("/images/pocmap.png")));
       testMap = new BGMap(new Image("/images/pocmap.png"));
       testCreature = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
    }
    
    @Test
    public void testLocationStartsWithEmptyMOBList() {
        assertTrue(testLocation.getCreatures().isEmpty());
    }
    
    @Test
    public void addedStructuresShouldBeInMOBList() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testLocation.addMapObject(testRock, 500 , 200);
        assertTrue(testLocation.getStructures().contains(testRock));
    }
    /*
    @Test
    public void locationsCanBeChanged(){
        Game testGame = new Game();
        PlayerCharacter testPlayer = new PlayerCharacter();
        Location testLocation2 = new Location(testPlayer);
        testGame.moveToLocation(testLocation2);
        assertTrue(testGame.currentLocation == testLocation2);
    }
    */
    @Test
    public void locationFlagsAreSetRight(){
        testLocation.setFlag("swamp",1);
        assertTrue(testLocation.isFlagged("swamp"));
    }
    
    @Test
    public void updatingLocationCleansDeadMobs() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testLocation.addMapObject(testRock, 500 , 200);
        PlayerCharacter testPlayer = new PlayerCharacter("Lini",new Image("/images/himmutoy.png"));
        testLocation.addPlayerCharacter(testPlayer, 500, 500);
        testLocation.addMapObject(testCreature, 300, 300);
        testCreature.setRemovable();
        testLocation.update(0.15f);
        assertFalse(testLocation.getCreatures().contains(testCreature));
    }
    
    @Test
    public void movingIntoLocationUpdatesCurrentPlayer() {
        PlayerCharacter testPlayer = new PlayerCharacter("Lini",new Image("/images/himmutoy.png"));
        testLocation.enterLocation(testPlayer, null);
        assertTrue(testLocation.getPlayer()==testPlayer);
    }
    
    @Test
    public void creaturesCanBeFoundByName() {
        Creature rotta = new Creature("Rotta", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        testLocation.addMapObject(rotta, 500, 500);
        assertTrue(testLocation.getCreatureByName("Rotta").getName().equals("Rotta"));  
    }
    
    @Test
    public void structureExtrasMoveWithStructure() {
        Structure tree = new Structure("Tree", new Image("/images/structures/tree1_stump.png"), 1);
        tree.addExtra(new Image("/images/structures/tree1_frill.png"), -20, -106);
        testLocation.addMapObject(tree, 200 , 200);
        double startX = tree.getExtras().get(0).getXPos();
        tree.setPosition(500, 500);
        
        assertTrue(tree.getExtras().get(0).getXPos() != startX);
        
    }
        
    @Test
    public void removableFlaggedMobsShouldBeRemovedOnUpdate() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testRock.setRemovable();
        testLocation.addMapObject(testRock, 500 , 200);
        testLocation.update(1);
        assertTrue(testLocation.getStructures().isEmpty());
    }
    
    
    //TODO: Rewrite these to take in account pixel based collision
    @Test
    public void mobCollidingOnSomethingThatHasBiggerXCoordHasCollidedOnRightHandSide() {
        Creature testCreature1 = new Creature("TestCreature", new Image("/images/himmu.png"));
        Creature testCreature2 = new Creature("TestCreature", new Image("/images/himmu.png"));
        
        testLocation.addMapObject(testCreature1, 250, 250);
        testLocation.addMapObject(testCreature2, 280, 250);
        testLocation.update(1);
        EnumSet<Direction> collidedDirections = testLocation.collidedSides(testCreature1);
        System.out.println(collidedDirections);
        assertTrue(testLocation.collidedSides(testCreature1).contains(Direction.RIGHT));
    }
    
    @Test
    public void mobCollidingOnSomethingThatHasSmallerXCoordHasCollidedOnLeftHandSide() {
        Creature testCreature1 = new Creature("TestCreature", new Image("/images/himmu.png"));
        Creature testCreature2 = new Creature("TestCreature", new Image("/images/himmu.png"));
        
        testLocation.addMapObject(testCreature1, 250, 250);
        testLocation.addMapObject(testCreature2, 230, 250);
        testLocation.update(1);
        EnumSet<Direction> collidedDirections = testLocation.collidedSides(testCreature1);
        System.out.println(collidedDirections);
        assertTrue(testLocation.collidedSides(testCreature1).contains(Direction.LEFT));
    }
    
    @Test
    public void mobCollidingOnSomethingThatHasBiggeryCoordHasCollidedOnUpperHandSide() {
        Creature testCreature1 = new Creature("TestCreature", new Image("/images/himmu.png"));
        Creature testCreature2 = new Creature("TestCreature", new Image("/images/himmu.png"));
        
        testLocation.addMapObject(testCreature1, 250, 250);
        testLocation.addMapObject(testCreature2, 250, 230);
        testLocation.update(1);
        EnumSet<Direction> collidedDirections = testLocation.collidedSides(testCreature1);
        System.out.println(collidedDirections);
        assertTrue(testLocation.collidedSides(testCreature1).contains(Direction.UP));
    }
    
    @Test
    public void mobCollidingOnSomethingThatHasSmallerYCoordHasCollidedOnLowerHandSide() {
        Creature testCreature1 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        Creature testCreature2 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        
        testLocation.addMapObject(testCreature1, 250, 250);
        testLocation.addMapObject(testCreature2, 250, 270);
        testLocation.update(1);
        EnumSet<Direction> collidedDirections = testLocation.collidedSides(testCreature1);
        System.out.println(collidedDirections);
        assertTrue(testLocation.collidedSides(testCreature1).contains(Direction.DOWN));
    }
    
    @Test
    public void callingUpdateOnLocationUpdatesMobs() {
        Creature testCreature1 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        Creature testCreature2 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        Creature testCreature3 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        Creature testCreature4 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        testLocation.addMapObject(testCreature1, 50, 50);
        testLocation.addMapObject(testCreature2, 50, 50);
        testLocation.addMapObject(testCreature3, 150, 50);
        testLocation.addMapObject(testCreature4, 250, 50);
        int mobsAtLocation = testLocation.getCreatures().size();
        testCreature1.setRemovable();
        testCreature2.setRemovable();
        testLocation.update(0.15f);
        assertTrue(testLocation.getCreatures().size() == mobsAtLocation-2);
    }
    
    @Test
    public void randomOpenSpotsHaveNothingInThem() {
        //Load the pathfinding map, because it has a lot of obstacles
        testLocation.loadMap(new TileMap("/mapdata/pathfinder_test.map"));
        testLocation.setMobInRandomOpenSpot(testCreature);
        //Only testCreature itself should be at this random open spot
        assertTrue(testLocation.checkCollisions(testCreature).size() <= 1);
    }
    
    @Test
    public void addedCreaturesKnowTheirLocation() {
        testLocation.addMapObject(testCreature, 7, 8);
        assertTrue(testCreature.getLocation()==testLocation);
    }
    
    @Test
    public void addedStructuresKnowTheirLocation() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testLocation.addMapObject(testRock, 300 , 200);
        assertTrue(testRock.getLocation() == testLocation);
    }
    
    @Test
    public void locationAlwaysHasAMap() {
        assertTrue(testLocation.getMap() != null);
    }
    
   
    @Test
    public void removableMobsAreRemovedOnUpdate() {
        testLocation.addMapObject(testCreature, 50, 70);
        testCreature.setRemovable();
        testLocation.update(1);
        assertTrue(testLocation.getCreatures().isEmpty());
    }
    
    /*
    @After
    public void tearDown() {
    }
    */
}
