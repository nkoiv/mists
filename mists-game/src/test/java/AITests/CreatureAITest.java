/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AITests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.Location;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author nikok
 */
public class CreatureAITest {
    
    private Location testLocation;
    private PlayerCharacter testPlayer;
    private Creature testCreature;
    
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
       testLocation = new Location("TestLocation", new BGMap(new Image("/images/pocmap.png")));
       testPlayer = new PlayerCharacter("Lini",new Image("/images/himmutoy.png"));
       testLocation.enterLocation(testPlayer, null);
       testPlayer.setPosition(200, 200);
       testCreature = new Creature("AITest", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
       testLocation.addMapObject(testCreature, 100, 100);
    }
    
    @Test
    public void movingTowardsPlayerShouldGetCreatureCoordinatesCloserToPlayer() {
        //testCreature.setFlag("testFlag", 1); //Set on the testflag that makes creature move towards player
        double xDistance = Math.abs(testPlayer.getXPos() - testCreature.getXPos());
        //double yDistance = Math.abs(testPlayer.getYPos() - testCreature.getYPos());
        GenericTasks.moveTowardsTarget(testCreature, testPlayer.getID());
        testCreature.update(0.15f);
        assertTrue(Math.abs(testPlayer.getXPos() - testCreature.getXPos()) <= xDistance || Math.abs(testPlayer.getYPos() - testCreature.getYPos()) <= xDistance);
    }
    
    /*
    @After
    public void tearDown() {
    }
    */
}
