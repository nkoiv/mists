/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.quests;

import java.util.ArrayList;

/**
 * A Quest is a task (or a list of tasks) that are tracked
 * for the player. 
 * @author nikok
 */
public class Quest {
    private String textEntry;
    private int questID;
    private ArrayList<QuestTask> tasks;
    
    public Quest() {
        
    }
    
    /**
     * Return true if all quest tasks are complete
     * @return true if all subtasks are done
     */
    public boolean isComplete() {
        for (QuestTask t : this.tasks) {
            if (!t.isDone()) return false;
        }
        //Return true if no task is left undone
        return true;
    }
    
    public boolean addProgress(QuestTaskType type, int identifier, int amount) {
        boolean progressAdded = false;
        for (QuestTask t : this.tasks) {  
            if (t.getType() == type) {
                t.addCompletion(amount);
                progressAdded = true;
            }
            
        }
        return progressAdded;
    }
    
    public String getQuestText() {
        return this.textEntry;
    }
    
    public int getID() {
        return this.questID;
    }
    
    public ArrayList<QuestTask> getTasks() {
        return this.tasks;
    }
}
