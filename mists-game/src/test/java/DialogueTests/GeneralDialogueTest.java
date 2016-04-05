/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DialogueTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.dialogue.Card;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.dialogue.Link;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.Location;
import javafx.scene.image.Image;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author nikok
 */
public class GeneralDialogueTest {
    private Dialogue testDialogue;
    private Creature dialogueOwner;
    private Creature dialogueTalker;
    private Location testLocation;
    
    public GeneralDialogueTest() {
    }
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
        testDialogue = buildTestDialogue();
        testLocation = new Location("TestLocation", new BGMap(new Image("/images/pocmap.png")));
        dialogueOwner = new Creature("TestOwner", new Image("/images/himmutoy.png"));
        dialogueOwner.setLocation(testLocation);
        dialogueTalker = new Creature("TestTalker", new Image("/images/himmutoy.png"));
        dialogueTalker.setLocation(testLocation);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void dialogueCanBeNavigatedManually() {
        int startCard = testDialogue.getCardNumber();
        testDialogue.moveToCard(startCard+1);
        assertTrue(startCard!=testDialogue.getCardNumber());
    }
    
    @Test
    public void dialogueCanBeNavigatedViaLinks() {
        int startCard = testDialogue.getCardNumber();
        int targetCard = testDialogue.getCurrentCard().getLinkDestination(0);
        testDialogue.moveToCard(targetCard);
        assertTrue(startCard!=testDialogue.getCardNumber());
    }
    
    @Test
    public void localizedTextWorksWithOwnerName() {
        //System.out.println("PreInit: "+testDialogue.getCurrentCard().getText());
        testDialogue.initiateDialogue(dialogueOwner, dialogueTalker);
        String talktext = testDialogue.getCurrentCard().getText();
        //System.out.println("PostInit: "+testDialogue.getCurrentCard().getText());
        assertTrue(talktext.contains(dialogueOwner.getName()));
    }
    
    @Test
    public void localizedTextWorksWithTalkerName() {
        testDialogue.setCurrentCard(2);
        //System.out.println("PreInit: "+testDialogue.getCurrentCard().getText());
        testDialogue.initiateDialogue(dialogueOwner, dialogueTalker);
        testDialogue.moveToCard(2);
        String talktext = testDialogue.getCurrentCard().getText();
        //System.out.println("PostInit: "+testDialogue.getCurrentCard().getText());
        assertTrue(talktext.contains(dialogueTalker.getName()));
    }
    
    private static Dialogue buildTestDialogue() {
        Dialogue d = new Dialogue();
        
        Link linkToFirstCard = new Link("Move to the first card", 1);
        Link linkToSecondCard = new Link("Move to second card", 2);
        Link linkToThirdCard = new Link("Move to third card", 3);
        Link linkToEndDialogue = new Link("End dialogue", -1);
        
        Card firstCard = new Card("OWNER_NAME: This is the first card\nMake your choice.:");
        firstCard.addLink(linkToSecondCard);
        firstCard.addLink(linkToThirdCard);
        Card secondCard = new Card("Welcome to the second card TALKER_NAME,\nChoose again");
        secondCard.addLink(linkToThirdCard);
        secondCard.addLink(linkToFirstCard);
        Card thirdCard = new Card("This is the third card\nand contains conversation exit");
        thirdCard.addLink(linkToFirstCard);
        thirdCard.addLink(linkToEndDialogue);
        
        d.addCard(1, firstCard);
        d.addCard(2, secondCard);
        d.addCard(3, thirdCard);
        
        return d;
    }
    
}
