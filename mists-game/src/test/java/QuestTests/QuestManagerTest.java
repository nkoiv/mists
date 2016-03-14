/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuestTests;

import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.quests.Quest;
import com.nkoiv.mists.game.quests.QuestManager;
import com.nkoiv.mists.game.quests.QuestTask;
import com.nkoiv.mists.game.quests.QuestTaskType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;

/**
 *
 * @author nikok
 */
public class QuestManagerTest {
    QuestManager qm;
    
    public QuestManagerTest() {
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
        qm = new QuestManager();
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
    public void questsCanBeAddedToQuestManager() {
        Quest q = generateTestQuest();
        qm.addQuest(q);
        assert(qm.questAvailable(q.getID()));
    }
    
    @Test
    public void questsCanBeAddedToTheOpenList() {
        Quest q = generateTestQuest();
        qm.openQuest(q);
        assert(qm.getOpenQuests().containsValue(q));
    }
    
    @Test
    public void questsInTheAllListCanBeOpened() {
        Quest q = generateTestQuest();
        qm.addQuest(q);
        int questID = q.getID();
        qm.openQuest(questID);
        assert(qm.getOpenQuests().containsValue(q));
    }
    
    @Test
    public void questsCanBeClosed() {
        Quest q = generateTestQuest();
        qm.openQuest(q);
        qm.closeQuest(q.getID());
        assert(!qm.getOpenQuests().containsKey(q.getID()));
    }
    
    @Test
    public void questEventsUpdateQuestProgress() {
        Quest q = generateTestQuest();
        qm.openQuest(q);
        qm.registerQuestEvent(QuestTaskType.CREATUREKILL, 1, 1);
        assert(qm.getOpenQuests().get(q.getID()).isComplete());
    }
    
}
