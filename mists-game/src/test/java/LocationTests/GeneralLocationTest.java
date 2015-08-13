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
public class GeneralLocationTest extends Application {
    
    Location testLocation;
    
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
       testLocation = new Location("TestLocation");

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
    
    @Test
    public void removableFlaggedMobsShouldBeRemovedOnUpdate() {
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        testRock.setFlag("removable", 1);
        testLocation.addStructure(testRock, 500 , 200);
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
