package LocationTests;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.Location;
import javafx.application.Application;
import javafx.scene.image.Image;
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
public class MovementTest extends Application {
    
    Location testLocation;
    PlayerCharacter testPlayer;
    
    public MovementTest() {
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
       testPlayer = new PlayerCharacter();
    }
    
    
    @Test
    public void movementShouldChangePlayerCoordinates() {
        testPlayer.setLocation(testLocation);
        testLocation.addPlayerCharacter(testPlayer);
        testPlayer.setPosition(300, 200);
        testPlayer.setSpeed(50); //Should move 50 per tick
        
        double originalYPos = testPlayer.getyPos();
        testPlayer.moveTowards(Direction.DOWN);
        testPlayer.update(0.16f);
        
        assert(originalYPos != testPlayer.getyPos());
        
    }
    
    @Test
    public void zeroSpeedPlayerShouldNotMove() {
        testPlayer.setLocation(testLocation);
        testLocation.addPlayerCharacter(testPlayer);
        testPlayer.setPosition(300, 200);
        testPlayer.setSpeed(0); //Movement set to zero
        
        double originalYPos = testPlayer.getyPos();
        testPlayer.moveTowards(Direction.UP);
        testPlayer.update(0.16f);
        
        assert(originalYPos == testPlayer.getyPos()); //Should still be at original position
    }
    
    @Test
    public void testPlayerCollisionsOnStructure() {
        System.out.println("Testing Player collisions on Structures");
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testPlayer.setLocation(testLocation);
        testRock.setLocation(testLocation);
        testLocation.addPlayerCharacter(testPlayer);
        testLocation.addStructure(testRock, 500 , 200);
        testPlayer.setPosition(300, 200); //Same Y as testRock, just 200 to the left
        testPlayer.setSpeed(50); //Should move 50 per tick
        for (int i=0;i<10;i++) {
            System.out.println(testPlayer.getName()+ " currently at "+ testPlayer.getxPos() + " / "+testPlayer.getyPos());
            testPlayer.moveTowards(Direction.RIGHT);
            testPlayer.update(0.16f); //At 60 FPS, one tick is 0.16f
        }
        
        //testPlayer should still be on the left (smaller X) side of the rock, because collisions prevented it going past it:
        assert(TestTools.CompareTools.isGreaterThan(testRock.getxPos(), testPlayer.getxPos()));
        
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
