/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.quests;

import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.HashMap;
import java.util.HashSet;

/**
 * QuestManager handles the quests currently active in the game.
 * Quest advancement should be parsed by the quest manager and
 * relayed to relevant quests, where needed.
 * @author nikok
 */
public class QuestManager {
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
     */
    public void addQuest(Quest quest) {
        this.openQuests.put(quest.getID(), quest);
        for (QuestTaskType qt : quest.getNeededTaskTypes()) {
            this.unneededTasks.remove(qt);
        }
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
    
    /**
     * Inform the open quests of the event, possibly
     * updating quest progress as a result
     * @param tasktype Type of event that happened
     * @param objectiveID Specifier ID for the event
     * @param count Amount of events, if variable
     */
    public void registerQuestEvent(QuestTaskType tasktype, int objectiveID, int count) {
        if (this.unneededTasks.contains(tasktype)) return; //No need to check quests if we dont track this tasktype
        for (int qID : this.openQuests.keySet()) {
            openQuests.get(qID).addProgress(tasktype, objectiveID, count);
        }
    }
    
    public HashMap<Integer, Quest> getOpenQuests() {
        return this.openQuests;
    }
    
    public HashMap<Integer, Quest> getClosedQuests() {
        return this.closedQuests;
    }
    
}
