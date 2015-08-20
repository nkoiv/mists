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
       testPlayer.setLocation(testLocation);
       testLocation.addPlayerCharacter(testPlayer, 300, 200);
    }
    
    @Test
    public void thereIsNoPlayerMovementBeforeUpdate() {
        testPlayer.setSpeed(50); //Should move 50 per tick
        double originalYPos = testPlayer.getYPos();
        double originalXPos = testPlayer.getXPos();
        testPlayer.moveTowards(Direction.UPRIGHT);
        testPlayer.moveTowards(Direction.RIGHT);
        testPlayer.moveTowards(Direction.DOWNRIGHT);
        testPlayer.moveTowards(Direction.DOWN);
        testPlayer.moveTowards(Direction.DOWNLEFT);
        testPlayer.moveTowards(Direction.LEFT);
        testPlayer.moveTowards(Direction.UPLEFT);
        testPlayer.moveTowards(Direction.UP);
        //testPlayer.update(0.16f);
        assert(testPlayer.getXPos()==originalXPos);
        assert(testPlayer.getYPos()==originalYPos);
        
    }
    
    
    @Test
    public void movementShouldChangePlayerCoordinates() {
        testPlayer.setSpeed(50); //Should move 50 per tick
        
        double originalYPos = testPlayer.getYPos();
        testPlayer.moveTowards(Direction.DOWN);
        testPlayer.applyMovement(0.16f);
        
        assert(originalYPos != testPlayer.getYPos());
    }
    
    @Test
    public void zeroSpeedPlayerShouldNotMove() {
        testPlayer.setSpeed(0); //Movement set to zero
        
        double originalYPos = testPlayer.getXPos();
        testPlayer.moveTowards(Direction.UP);
        testPlayer.update(0.16f);
        
        assert(originalYPos == testPlayer.getXPos()); //Should still be at original position
    }
    
    @Test
    public void testPlayerCollisionsOnStructure() {
        System.out.println("Testing Player collisions on Structures");
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testRock.setLocation(testLocation);
        testLocation.addStructure(testRock, 500 , 200);
        testPlayer.setPosition(300, 200); //Same Y as testRock, just 200 to the left
        testPlayer.setSpeed(50); //Should move 50 per tick
        for (int i=0;i<10;i++) {
            System.out.println(testPlayer.getName()+ " currently at "+ testPlayer.getXPos() + " / "+testPlayer.getYPos());
            testPlayer.moveTowards(Direction.RIGHT);
            testPlayer.update(0.16f); //At 60 FPS, one tick is 0.16f
        }
        
        //testPlayer should still be on the left (smaller X) side of the rock, because collisions prevented it going past it:
        assert(TestTools.CompareTools.isGreaterThan(testRock.getXPos(), testPlayer.getXPos()));
        
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
