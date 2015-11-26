package LocationTests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import static com.nkoiv.mists.game.Global.TILESIZE;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.GameMap;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import java.util.HashSet;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;



/**
 *
 * @author nkoiv
 */
public class GeneralLocationTest extends Application {
    
    Location testLocation;
    Creature testCreature;
    GameMap testMap; 
    Mists mists;
    
    public GeneralLocationTest() {
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
       testLocation = new Location("TestLocation", new BGMap(new Image("/images/pocmap.png")));
       testCreature = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
       testMap = new BGMap(new Image("/images/pocmap.png"));
    }
    
    @Test
    public void testLocationStartsWithEmptyMOBList() {
        assert(testLocation.getMOBList().isEmpty());
    }
    
    @Test
    public void addedStructuresShouldBeInMOBList() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testLocation.addStructure(testRock, 500 , 200);
        assert(testLocation.getMOBList().contains(testRock));
    }
    /*
    @Test
    public void locationsCanBeChanged(){
        Game testGame = new Game();
        PlayerCharacter testPlayer = new PlayerCharacter();
        Location testLocation2 = new Location(testPlayer);
        testGame.moveToLocation(testLocation2);
        assert(testGame.currentLocation == testLocation2);
    }
    */
    @Test
    public void locationFlagsAreSetRight(){
        testLocation.setFlag("swamp",1);
        assert(testLocation.isFlagged("swamp"));
    }
    
    @Test
    public void updatingLocationCleansDeadMobs() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testLocation.addStructure(testRock, 500 , 200);
        PlayerCharacter testPlayer = new PlayerCharacter();
        testLocation.addPlayerCharacter(testPlayer, 500, 500);
        testLocation.addCreature(testCreature, 300, 300);
        testCreature.setFlag("removable",1);
        testLocation.update(0.15f);
        assert(testLocation.getMOBList().contains(testCreature) == false);
    }
    
    @Test
    public void movingIntoLocationUpdatesCurrentPlayer() {
        PlayerCharacter testPlayer = new PlayerCharacter();
        testLocation.enterLocation(testPlayer);
        assert(testLocation.getPlayer()==testPlayer);
    }
    
    @Test
    public void creaturesCanBeFoundByName() {
        Creature rotta = new Creature("Rotta", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        testLocation.addCreature(rotta, 500, 500);
        assert(testLocation.getCreatureByName("Rotta").getName().equals("Rotta"));  
    }
    
    @Test
    public void structureExtrasMoveWithStructure() {
        Structure tree = new Structure("Tree", new Image("/images/tree_stump.png"), testLocation, 6*TILESIZE, 5*TILESIZE);
        tree.addExtra(new Image("/images/tree.png"), -35, -96);
        testLocation.addStructure(tree, 200 , 200);
        double startX = tree.getExtras().get(0).getXPos();
        tree.setPosition(500, 500);
        
        assert(tree.getExtras().get(0).getXPos() != startX);
        
    }
        
    @Test
    public void removableFlaggedMobsShouldBeRemovedOnUpdate() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testRock.setFlag("removable", 1);
        testLocation.addStructure(testRock, 500 , 200);
        testLocation.update(1);
        assert(testLocation.getMOBList().isEmpty());
    }
    
    
    //TODO: Rewrite these to take in account pixel based collision
    @Test
    public void mobCollidingOnSomethingThatHasBiggerXCoordHasCollidedOnRightHandSide() {
        Creature testCreature1 = new Creature("TestCreature", new Image("/images/himmu.png"));
        Creature testCreature2 = new Creature("TestCreature", new Image("/images/himmu.png"));
        
        testLocation.addCreature(testCreature1, 250, 250);
        testLocation.addCreature(testCreature2, 280, 250);
        HashSet<Direction> collidedDirections = testLocation.collidedSides(testCreature1);
        System.out.println(collidedDirections);
        assert(testLocation.collidedSides(testCreature1).contains(Direction.RIGHT) == true);
    }
    
    @Test
    public void mobCollidingOnSomethingThatHasSmallerXCoordHasCollidedOnLeftHandSide() {
        Creature testCreature1 = new Creature("TestCreature", new Image("/images/himmu.png"));
        Creature testCreature2 = new Creature("TestCreature", new Image("/images/himmu.png"));
        
        testLocation.addCreature(testCreature1, 250, 250);
        testLocation.addCreature(testCreature2, 230, 250);
        HashSet<Direction> collidedDirections = testLocation.collidedSides(testCreature1);
        System.out.println(collidedDirections);
        assert(testLocation.collidedSides(testCreature1).contains(Direction.LEFT) == true);
    }
    
    @Test
    public void mobCollidingOnSomethingThatHasBiggeryCoordHasCollidedOnUpperHandSide() {
        Creature testCreature1 = new Creature("TestCreature", new Image("/images/himmu.png"));
        Creature testCreature2 = new Creature("TestCreature", new Image("/images/himmu.png"));
        
        testLocation.addCreature(testCreature1, 250, 250);
        testLocation.addCreature(testCreature2, 250, 230);
        HashSet<Direction> collidedDirections = testLocation.collidedSides(testCreature1);
        System.out.println(collidedDirections);
        assert(testLocation.collidedSides(testCreature1).contains(Direction.UP) == true);
    }
    
    @Test
    public void mobCollidingOnSomethingThatHasSmallerYCoordHasCollidedOnLowerHandSide() {
        Creature testCreature1 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        Creature testCreature2 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        
        testLocation.addCreature(testCreature1, 250, 250);
        testLocation.addCreature(testCreature2, 250, 270);
        HashSet<Direction> collidedDirections = testLocation.collidedSides(testCreature1);
        System.out.println(collidedDirections);
        assert(testLocation.collidedSides(testCreature1).contains(Direction.DOWN) == true);
    }
    
    @Test
    public void callingUpdateOnLocationUpdatesMobs() {
        Creature testCreature1 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        Creature testCreature2 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        Creature testCreature3 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        Creature testCreature4 = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        testLocation.addCreature(testCreature1, 50, 50);
        testLocation.addCreature(testCreature2, 50, 50);
        testLocation.addCreature(testCreature3, 150, 50);
        testLocation.addCreature(testCreature4, 250, 50);
        int mobsAtLocation = testLocation.getMOBList().size();
        testCreature1.setFlag("removable", 1);
        testCreature2.setFlag("removable", 1);
        testLocation.update(0.15f);
        assert(testLocation.getMOBList().size() == mobsAtLocation-2);
    }
    
    @Test
    public void randomOpenSpotsHaveNothingInThem() {
        //Load the pathfinding map, because it has a lot of obstacles
        testLocation.loadMap(new TileMap("/mapdata/pathfinder_test.map"));
        testLocation.setMobInRandomOpenSpot(testCreature);
        //Only testCreature itself should be at this random open spot
        assert(testLocation.checkCollisions(testCreature).size() <= 1);
    }
    
    @Test
    public void addedCreaturesKnowTheirLocation() {
        testLocation.addCreature(testCreature, 7, 8);
        assert(testCreature.getLocation()==testLocation);
    }
    
    @Test
    public void addedStructuresKnowTheirLocation() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testLocation.addStructure(testRock, 300 , 200);
        assert(testRock.getLocation() == testLocation);
    }
    
    @Test
    public void locationAlwaysHasAMap() {
        assert(testLocation.getMap() != null);
    }
    
   
    @Test
    public void removableMobsAreRemovedOnUpdate() {
        testLocation.addCreature(testCreature, 50, 70);
        testCreature.setFlag("removable", 1);
        testLocation.update(1);
        assert(testLocation.getMOBList().isEmpty());
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        
    }
}
