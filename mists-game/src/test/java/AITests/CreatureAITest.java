/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AITests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.world.Location;
import javafx.scene.image.ImageView;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;

/**
 *
 * @author nikok
 */
public class CreatureAITest {
    
    Location testLocation;
    PlayerCharacter testPlayer;
    Creature testCreature;
    
    public CreatureAITest() {
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
       testLocation.addPlayerCharacter(testPlayer, 200, 200);
       testCreature = new Creature("AITest", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
       testLocation.addCreature(testCreature, 100, 100);
       
    }
    
    @Test
    public void movingTowardsPlayerShouldGetCreatureCoordinatesCloserToPlayer() {
        testCreature.setFlag("testFlag", 1); //Set on the testflag that makes creature move towards player
        double xDistance = Math.abs(testPlayer.getXPos() - testCreature.getXPos());
        double yDistance = Math.abs(testPlayer.getYPos() - testCreature.getYPos());
        testCreature.update(0.15f);
        assert(Math.abs(testPlayer.getXPos() - testCreature.getXPos()) <= xDistance || Math.abs(testPlayer.getYPos() - testCreature.getYPos()) <= xDistance);
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}