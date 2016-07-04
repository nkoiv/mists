/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.quests;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * QuestTask is a single tracked task within a quest.
 * @author nikok
 */
public class QuestTask implements KryoSerializable {
        private String description;
        private int currentCount;
        private int requiredCount;
        
        private QuestTaskType type;
        private int objectiveID;
        
        public QuestTask() {
        	
        }
        
        public QuestTask(String description, QuestTaskType tasktype, int objectiveID, int requiredCount) {
            this.description = description;
            this.type = tasktype;
            this.objectiveID = objectiveID;
            this.requiredCount = requiredCount;
        }
        
        public String getDescription() {
            if (this.description == null) return "UNSPECIFIED";
            return this.description;
        }
        
        public int getCurrentCompletion() {
            return this.currentCount;
        }
        
        public int getRequiredCompletion() {
            return this.requiredCount;
        }
        
        /**
         * Add a variable amount of completion to the quest task
         * @param amount The number to increment the completions with
         */
        public void addCompletion(int amount) {
            if (this.currentCount < this.requiredCount) {
                //TODO: Would it be more fun to show "overdone" amounts? If so, remove this IF
                this.currentCount = this.currentCount + amount;
            }
        }
        
        /**
         * Add one to quest completion status
         */
        public void addCompletion() {
            this.addCompletion(1);
        }
        
        /**
         * Set the quest status to given number,
         * regardless of where it was at before
         * @param completion number of quest count to set the quest to
         */
        public void setCompletion(int completion) {
            this.currentCount = completion;
        }
        
        /**
         * Set quest completion to done,
         * regardless of what the progress was
         */
        public void setDone() {
            this.currentCount = requiredCount;
        }
        
        /**
         * Return quest completion status
         * @return true if quest is completed
         */
        public boolean isDone() {
            return (this.currentCount >= this.requiredCount);
        }
        
        /**
         * Return true if the quest only has one
         * "count", meaning that completion can
         * be represented by a simple yes/no
         * @return true if quest has only one count
         */
        public boolean isBinary() {
            return (this.requiredCount == 1);
        }
        
        public QuestTaskType getType() {
            return this.type;
        }
        
        public int getObjectiveID() {
            return this.objectiveID;
        }
        
        public static int getQuestTypeID(QuestTaskType qt) {
        	switch (qt) {
        	case TRIGGERVALUE: return 1;
        	case CREATUREKILL:return 2;
        	case ITEMHAVE: return 3;
        	case ITEMUSE: return 4;
        	default: return -1;
        	}
        }
        public static QuestTaskType getQuestTaskType(int typeID) {
        	switch (typeID) {
        	case 1: return QuestTaskType.TRIGGERVALUE;
        	case 2: return QuestTaskType.CREATUREKILL;
        	case 3: return QuestTaskType.ITEMHAVE;
        	case 4: return QuestTaskType.ITEMUSE;
        	default: return null;
        	}
        }

		@Override
		public void write(Kryo kryo, Output output) {
			output.writeString(description);
			output.writeInt(objectiveID);
			output.writeByte(getQuestTypeID(this.type));
			output.writeInt(requiredCount);
			output.writeInt(currentCount);
		}

		@Override
		public void read(Kryo kryo, Input input) {
			this.description = input.readString();
			this.objectiveID = input.readInt();
			this.type = getQuestTaskType(input.readByte());
			this.requiredCount = input.readInt();
			this.currentCount = input.readInt();
		}
    }
