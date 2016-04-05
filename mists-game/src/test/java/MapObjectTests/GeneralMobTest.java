/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapObjectTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.Location;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author daedra
 */
public class GeneralMobTest {
    
    private Location testLocation;
    private Structure testStructure;
    private Creature testCreature;
    private Effect testEffect;
    @Rule 
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    
    /*
    public GeneralMobTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    */
    
    @Before
    public void setUp() {
        testLocation = new Location("TestLocation",new BGMap(new Image("/images/pocmap.png")));
        testCreature = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        testStructure = new Structure("Rock", new Image("/images/block.png"), 100);
    }
    
    @Test
    public void structureCanHaveExtraFrills() {
        testStructure.addExtra(new Image("/images/structures/tree1_frill.png"), -20, -106);
        assertTrue(!testStructure.getExtras().isEmpty());
    }
    
    @Test
    public void structuralExtrasCanBeRemoved() {
        testStructure.addExtra(new Image("/images/structures/tree1_frill.png"), -20, -106);
        testStructure.removeExtras();
        assertTrue(testStructure.getExtras().isEmpty());
    }
    @Test
    public void structuresCanBeMoved() {
        testStructure.setPosition(100, 100);
        assertTrue(testStructure.getXPos() == 100);
    }
    
    @Test
    public void mobsAtSamePositionIntersect() {
        testStructure.setPosition(100,100);
        testCreature.setPosition(100, 100);
        assertTrue(testCreature.instersects(testStructure));
    }
    
    @Test
    public void creatureFlagValuesAreSaved() {
        testCreature.setFlag("Testflag", 500);
        assertTrue(testCreature.getFlag("Testflag") == 500);
    }
    
    @Test
    public void creatureFlagsAreTrueWhenOver0() {
        Random rng = new Random();
        int randomNumber = rng.nextInt(500)+1;
        testCreature.setFlag("Testflag", randomNumber);
        assert(testCreature.isFlagged("Testflag"));       
    }
    
    @Test
    public void creatureFlagReturnsFalseWhenThereIsNoFlag() {
        assertTrue(!testCreature.isFlagged("Testflag"));
    }
    
    @Test
    public void creatureFlagReturnsFalseWhenFlagIsZero() {
        testCreature.setFlag("Testflag", 0);
        assertTrue(!testCreature.isFlagged("Testflag"));
    }
    
    @Test
    public void creatureAttributesAlwaysReturnAtleastZero() {
        assertTrue(testCreature.getAttribute("Testattribute") >= 0);
    }
    
    @Test
    public void creatureFacingMatchesMovement() {
        testCreature.moveTowards(Direction.UP);
        assertTrue(testCreature.getFacing() == Direction.UP);
    }
    
    @Test
    public void mobCenterXPositionIsPositionPlusHalfWidth() {
        testCreature.setPosition(100,100);
        double centerX = testCreature.getCenterXPos();
        assertTrue(centerX == 100+(testCreature.getWidth()/2));
    }
    
    @Test
    public void mobCenterYPositionIsPositionPlusHalfHeight() {
        testCreature.setPosition(200,200);
        double centerY = testCreature.getCenterYPos();
        assertTrue(centerY == 200+(testCreature.getHeight()/2));
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
