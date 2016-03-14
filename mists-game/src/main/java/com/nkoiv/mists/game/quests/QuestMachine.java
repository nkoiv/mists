/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.quests;

import java.util.HashMap;

/**
 * QuestMachine handles the quests currently active in the game.
 * Quest advancement should be parsed by the quest machine and
 * relayed to relevant quests, where needed.
 * @author nikok
 */
public class QuestMachine {
    private HashMap<Integer, Quest> openQuests;
    private HashMap<Integer, Quest> closedQuests;
    
    public QuestMachine() {
        this.openQuests = new HashMap<>();
        this.closedQuests = new HashMap<>();
    }
    
    
    public void addQuest(Quest quest) {
        this.openQuests.put(quest.getID(), quest);
    }
    
    public boolean closeQuest(Quest quest) {
        return this.closeQuest(quest.getID());
    }
    
    public boolean closeQuest(int questID) {
        if (this.openQuests.containsKey(questID)) {
            this.closedQuests.put(questID, this.openQuests.get(questID));
            this.openQuests.remove(questID);
            return true;
        }
        return false;
    }
    
}
