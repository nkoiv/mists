/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.quests;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.items.Item;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * QuestManager handles the quests currently active in the game.
 * Quest advancement should be parsed by the quest manager and
 * relayed to relevant quests, where needed.
 * @author nikok
 */
public class QuestManager {
    private HashMap<Integer, Quest> allQuests = new HashMap<>();
    private HashMap<Integer, Quest> openQuests;
    private HashMap<Integer, Quest> closedQuests;
    private HashSet<QuestTaskType> unneededTasks;
    
    public QuestManager() {
        this.openQuests = new HashMap<>();
        this.closedQuests = new HashMap<>();
        this.unneededTasks = new HashSet<>();
        this.initializeUnneededTypes();
    }
    
    private void initializeUnneededTypes() {
        this.unneededTasks.add(QuestTaskType.CREATUREKILL);
        this.unneededTasks.add(QuestTaskType.ITEMHAVE);
        this.unneededTasks.add(QuestTaskType.ITEMUSE);
        this.unneededTasks.add(QuestTaskType.TRIGGERVALUE);
    }
    
    /**
     * Check through the current open quests lists and
     * refresh the unneeded quest task types to match
     * current needs.
     */
    private void refreshUnneededTypes() {
        this.initializeUnneededTypes();
        for (int qID : this.openQuests.keySet()) {
            for (QuestTaskType qt : this.openQuests.get(qID).getNeededTaskTypes()) {
                this.unneededTasks.remove(qt);
            }
        }
    }
    
    /**
     * Add a quest to the manager, on the open list.
     * Quest is added with the ID set within the quest,
     * possibly overwriting existing quest on the list.
     * @param quest Quest to add to Open list
     * @return True if quest was successfully opened
     */
    public boolean openQuest(Quest quest) {
        this.openQuests.put(quest.getID(), quest);
        for (QuestTaskType qt : quest.getNeededTaskTypes()) {
            this.unneededTasks.remove(qt);
        }
        return true;
    }
    
    /**
     * Find the specified quest (by ID) from the allQuests
     * map and open it up for the player.
     * @param questID ID of the Quest to open up
     * @return True if quest was successfully opened
     */
    public boolean openQuest(int questID) {
        if (!this.allQuests.containsKey(questID)) return false;
        return this.openQuest(this.allQuests.get(questID));
    }
    
    
    /**
     * Close a quest from the open list, if found
     * @param quest Quest to close
     * @return 
     */
    public boolean closeQuest(Quest quest) {
        return this.closeQuest(quest.getID());
    }
    
    /**
     * Close a quest from the open list, if found
     * @param questID The ID of the quest to close
     * @return 
     */
    public boolean closeQuest(int questID) {
        if (this.openQuests.containsKey(questID)) {
            this.closedQuests.put(questID, this.openQuests.get(questID));
            this.openQuests.remove(questID);
            this.refreshUnneededTypes();
            return true;
        }
        return false;
    }
    
    /**
     * Inform the quest manager of a single MapObject destruction
     * Same thing can be done with registerQuestEvent(), albeit with
     * more parameters.
     * @param mob MapObject that died
     */
    public void registerMobDeath(MapObject mob) {
        if (mob instanceof Creature) this.registerQuestEvent(QuestTaskType.CREATUREKILL, mob.getTemplateID(), 1);
    }
    
    public void registerItemCountInInventory(Item item, int count)     {
        this.registerQuestEvent(QuestTaskType.ITEMHAVE, item.getBaseID(), count);
    }
    
    /**
     * Inform the open quests of the event, possibly
     * updating quest progress as a result
     * @param tasktype Type of event that happened
     * @param objectiveID Specifier ID for the event
     * @param count Amount of events, if variable
     */
    public void registerQuestEvent(QuestTaskType tasktype, int objectiveID, int count) {
        if (this.unneededTasks.contains(tasktype)) return; //No need to check quests if we dont track this tasktype
        if (tasktype == QuestTaskType.CREATUREKILL || tasktype == QuestTaskType.ITEMUSE) {
            //Cumulative tasks
            for (int qID : this.openQuests.keySet()) {
                openQuests.get(qID).addProgress(tasktype, objectiveID, count);
            }
        }
        if (tasktype == QuestTaskType.ITEMHAVE) {
            //Tasks that can go down in value
            for (int qID : this.openQuests.keySet()) {
                openQuests.get(qID).setProgress(tasktype, objectiveID, count);
            }
        }
    }
    
    public HashMap<Integer, Quest> getOpenQuests() {
        return this.openQuests;
    }
    
    public HashMap<Integer, Quest> getClosedQuests() {
        return this.closedQuests;
    }
    
    /**
     * Check if the given quest can be found
     * on the AllQuests listing
     * @param questID ID of the quest
     * @return 
     */
    public boolean questAvailable(int questID) {
        return (this.allQuests.keySet().contains(questID));
    }
    
    /**
     * Add a quest to the AllQuests listing
     * @param quest Quest to add
     */
    public void addQuest(Quest quest) {
        this.allQuests.put(quest.getID(), quest);
    }
    
    public static void loadQuestsFromYAML(QuestManager questManager, String libFile) {
        File libraryYAML = new File(libFile);
        try {
            Mists.logger.info("Attempting to read Quests YAML from "+libraryYAML.getCanonicalPath());
            YamlReader reader = new YamlReader(new FileReader(libraryYAML));
            while (true) {
                Object object = reader.read();
                if (object == null) break;
                try {
                    Map libraryObjectData = (Map)object;
                    Quest q = parseQuestFromYAML(libraryObjectData);
                    questManager.addQuest(q);
                } catch (Exception e) {
                    Mists.logger.warning("Failed parsing "+object.toString());
                    Mists.logger.warning(e.toString());
                }
                
            }

        } catch (Exception e) {
            Mists.logger.warning("Was unable to read quest YAML data!");
            Mists.logger.warning(e.toString());
        }
        
    }
    
    private static Quest parseQuestFromYAML(Map questData) {
        String title = "Unnamed quest";
        int id = -1;
        
        Quest q = new Quest(title, id);
        return q;
    }
    
    public static Quest generateTestKillQuest() {
        Quest q = new Quest("TestKillQuest", 1);
        QuestTask qt = new QuestTask("Kill two worms", QuestTaskType.CREATUREKILL, 1, 2);
        q.addTask(qt);
        return q;
    }
    
    public static Quest generateTestFetchQuest() {
        Quest q = new Quest("TestFetchQuest", 2);
        QuestTask qt = new QuestTask("Acquire the Himmutoys", QuestTaskType.ITEMHAVE, 3, 1);
        q.addTask(qt);
        return q;
    }
    
}
