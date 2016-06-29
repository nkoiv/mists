/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.quests;

import java.util.ArrayList;
import java.util.HashSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A Quest is a task (or a list of tasks) that are tracked
 * for the player. 
 * @author nikok
 */
public class Quest implements KryoSerializable {
    private String title;
    private String textEntry;
    private int questID;
    private ArrayList<QuestTask> tasks;
    
    public Quest(String title, int ID) {
        this (title, "QUEST TEXT MISSING", ID, new ArrayList<QuestTask>());
    }
    
    public Quest(String title, String text, int ID, ArrayList<QuestTask> tasks) {
        this.title = title;
        this.textEntry = text;
        this.questID = ID;
        this.tasks = tasks;
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
    
    public boolean addProgress(QuestTaskType type, int objectiveID, int amount) {
        boolean progressAdded = false;
        for (QuestTask t : this.tasks) {  
            if (t.getType() == type && (t.getObjectiveID()==0 || t.getObjectiveID() == objectiveID )) {
                t.addCompletion(amount);
                progressAdded = true;
            }         
        }
        return progressAdded;
    }
    
    public boolean setProgress(QuestTaskType type, int objectiveID, int amount) {
        boolean progressAdded = false;
        for (QuestTask t : this.tasks) {  
            if (t.getType() == type && (t.getObjectiveID()==0 || t.getObjectiveID() == objectiveID )) {
                t.setCompletion(amount);
                progressAdded = true;
            }         
        }
        return progressAdded;
    }
    
    
    public HashSet<QuestTaskType> getNeededTaskTypes() {
        HashSet<QuestTaskType> qtts = new HashSet<>();
        for (QuestTask qt : this.tasks) {
            qtts.add(qt.getType());
        }
        return qtts;
    }
    
    public String getTitle() {
        if (this.title == null) return "QUEST ID "+this.questID+" TITLE MISSING";
        return this.title;
    }
    
    public String getQuestText() {
        if (this.textEntry == null) return "QUEST TEXT MISSING";
        return this.textEntry;
    }
    
    public int getID() {
        return this.questID;
    }
    
    public ArrayList<QuestTask> getTasks() {
        return this.tasks;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    public void setID(int id) {
        this.questID = id;
    }
    public void setText(String text) {
        this.textEntry = text;
    }
    public void setTasks(ArrayList<QuestTask> tasks) {
        this.tasks = tasks;
    }
    public void addTask(QuestTask task) {
        this.tasks.add(task);
    }

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeInt(questID);
		output.writeString(title);
		output.writeString(textEntry);
		output.writeInt(tasks.size());
		for (QuestTask t : tasks) {
			kryo.writeObject(output, t);
		}
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.questID = input.readInt();
		this.title = input.readString();
		this.textEntry = input.readString();
		int tasks = input.readInt();
		this.tasks = new ArrayList<>();
		for (int i = 0; i < tasks; i++) {
			QuestTask t = kryo.readObject(input, QuestTask.class);
			this.tasks.add(t);
		}
	}
}
