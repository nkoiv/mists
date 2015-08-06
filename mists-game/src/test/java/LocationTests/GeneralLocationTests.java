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
 * @author daedra
 */
public class GeneralLocationTests extends Application {
    
    public GeneralLocationTests() {
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
       

    }
    
    @Test
    public void testLocationStartsWithEmptyMOBList() {
        Location testLocation = new Location("TestLocation");
        assert(testLocation.getMOBList().isEmpty());
    }
    
    @Test
    public void addedStructuresShouldBeInMOBList() {
        Location testLocation = new Location("TestLocation");
        Structure testRock = new Structure("Rock", new Image("/images/block.png"), 100);
        
        testLocation.addStructure(testRock, 500 , 200);
        assert(testLocation.getMOBList().contains(testRock));
        
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
