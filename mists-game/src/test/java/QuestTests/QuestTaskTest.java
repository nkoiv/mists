/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuestTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.quests.Quest;
import com.nkoiv.mists.game.quests.QuestTask;
import com.nkoiv.mists.game.quests.QuestTaskType;
import java.util.HashSet;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author nikok
 */
public class QuestTaskTest {
    private Quest testQuest;
    
    public QuestTaskTest() {
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
        testQuest = generateTestQuest();
    }
    
    @After
    public void tearDown() {
    }

    private Quest generateTestQuest() {
        Quest q = new Quest("TestQuest", 1);
        QuestTask qt = new QuestTask("Kill a monster", QuestTaskType.CREATUREKILL, 1, 1);
        q.addTask(qt);
        return q;
    }
    
    @Test
    public void questIsNotCompleteBeforeTasksAreComplete() {
        assertFalse(testQuest.isComplete());
    }
    
    @Test
    public void questTasksCanBeProgressed() {
        testQuest.addProgress(QuestTaskType.CREATUREKILL, 1, 1);
        assertTrue(testQuest.isComplete());
    }
    
    @Test
    public void questCompletesWhenAllSubtasksAreComplete() {
        testQuest.addTask(new QuestTask("Kill more monsters of type 2", QuestTaskType.CREATUREKILL, 2, 10));
        testQuest.addProgress(QuestTaskType.CREATUREKILL, 2, 10);
        testQuest.addProgress(QuestTaskType.CREATUREKILL, 1, 1);
        assertTrue(testQuest.isComplete());
    }
    
    @Test
    public void questTaskTypesAreNeededCorrectly() {
        testQuest.addTask(new QuestTask("Use an item", QuestTaskType.ITEMUSE, 1, 1));
        HashSet s = testQuest.getNeededTaskTypes();
        assertTrue(s.size() == 2);
        assertTrue(s.contains(QuestTaskType.CREATUREKILL) && s.contains(QuestTaskType.ITEMUSE));
    }
}
