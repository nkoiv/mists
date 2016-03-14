/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapObjectTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;

/**
 *
 * @author nkoiv
 */
public class GeneralCombatTest {
    
    Location testLocation;
    Creature combatant1;
    Creature combatant2;
    
    public GeneralCombatTest() {
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
        
        combatant1 = new Creature("Combatant 1", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        combatant1.addAction(new MeleeAttack());
        testLocation.addMapObject(combatant1, 380, 400);
        combatant2 = new Creature("Combatant 2", new ImageView("/images/monster3.png"), 3, 0, 0, 64, 64);
        combatant2.addAction(new MeleeAttack());
        testLocation.addMapObject(combatant2, 380, 400);
    }
    
    @Test
    public void creaturesStartWithFullHp(){
        assert(combatant1.getHealth() == combatant1.getMaxHealth());
    }
    
    @Test
    public void hittingCreatureWithDamagingAttackReducesItsHealth() {
        Action testAttack = combatant1.getAvailableActions().get("melee");
        ArrayList<MapObject> targets = new ArrayList<>();
        targets.add(combatant2);
        testAttack.hitOn(targets);
        assert(TestTools.CompareTools.isGreaterThan(combatant2.getMaxHealth(), combatant2.getHealth()));
    }
    
    @Test
    public void usingAttackCreatesAnEffecT() {
        Action testAttack = combatant1.getAvailableActions().get("melee");
        combatant1.useAction("MeleeAttack");
        
    }
    
    @Test
    public void meleeAttackDoesNotDamageUser() {
        Action testAttack = combatant1.getAvailableActions().get("melee");
        ArrayList<MapObject> targets = new ArrayList<>();
        targets.add(combatant1);
        testAttack.hitOn(targets);
        assert(!TestTools.CompareTools.isGreaterThan(combatant1.getMaxHealth(), combatant1.getHealth()));
    }
    
    @Test
    public void abilitiesCannotBeUsedOnCooldown() {
        Action testAttack = combatant1.getAvailableActions().get("melee");
        ArrayList<MapObject> targets = new ArrayList<>();
        targets.add(combatant2);
        combatant1.getAvailableActions().remove("melee");
        testAttack.hitOn(targets);
        int healthAfterFirstHit = combatant2.getHealth();
        assert(TestTools.CompareTools.isGreaterThan(combatant2.getMaxHealth(), combatant2.getHealth()));
        testAttack.hitOn(targets);
        int healthAfterSecondHit = combatant2.getHealth();
        assert(healthAfterFirstHit == healthAfterSecondHit);
    }
    
    @Test
    public void healingCreatureShouldIncreaseItsHealth() {
        combatant1.setHealth(1);
        combatant1.healHealth(50);
        assert(combatant1.getHealth() == 51);
    }
    
    @Test
    public void healingCantHealOverMaxHealth() {
        combatant1.setHealth(combatant1.getMaxHealth());
        combatant1.healHealth(1000);
        assert(combatant1.getHealth() == combatant1.getMaxHealth());
    }
    
    @Test
    public void creaturesHitToZeroHealthGetRemovableFlag() {
        combatant1.takeDamage(combatant1.getMaxHealth());
        assert(combatant1.isRemovable());
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
