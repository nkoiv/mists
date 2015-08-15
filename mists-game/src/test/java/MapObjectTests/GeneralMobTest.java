/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapObjectTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.Location;
import java.util.Random;
import javafx.scene.image.Image;
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
 * @author daedra
 */
public class GeneralMobTest {
    
    Location testLocation;
    Structure testStructure;
    Creature testCreature;
    Effect testEffect;
    @Rule 
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    
    public GeneralMobTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        testLocation = new Location("TestLocation");
        testCreature = new Creature("TestCreature", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        testStructure = new Structure("Rock", new Image("/images/block.png"), 100);
    }
    
    @Test
    public void structureCanHaveExtraFrills() {
        testStructure.addExtra(new Image("/images/tree.png"), -35, -96);
        assert(!testStructure.getExtras().isEmpty());
    }
    
    @Test
    public void structuralExtrasCanBeRemoved() {
        testStructure.addExtra(new Image("/images/tree.png"), -35, -96);
        testStructure.removeExtras();
        assert(testStructure.getExtras().isEmpty());
    }
    @Test
    public void structuresCanBeMoved() {
        testStructure.setPosition(100, 100);
        assert(testStructure.getxPos() == 100);
    }
    
    @Test
    public void creatureFlagValuesAreSaved() {
        testCreature.setFlag("Testflag", 500);
        assert(testCreature.getFlag("Testflag") == 500);
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
        assert(!testCreature.isFlagged("Testflag"));
    }
    
    @Test
    public void creatureFlagReturnsFalseWhenFlagIsZero() {
        testCreature.setFlag("Testflag", 0);
        assert(!testCreature.isFlagged("Testflag"));
    }
    
    @Test
    public void creatureAttributesAlwaysReturnAtleastZero() {
        assert(testCreature.getAttribute("Testattribute") >= 0);
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