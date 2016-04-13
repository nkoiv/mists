/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.util.Arrays;

/**
 * Task is a ToDo for creatures. It's simple and fundamental
 * action meant to be transferred over network instead of
 * mirroring actual creature states.
 * The actual executions of a task are handled mainly by
 * actions.GenericTasks.java
 * @author nikok
 */
 public class Task implements KryoSerializable {
     public static final String[]ID_CODE = new String[]{
        "IDLE",
        "CONTINUE_MOVEMENT",
        "MOVE_TOWARDS_DIRECTION",
        "MOVE_TOWARDS_TARGET",
        "MOVE_TOWARDS_COORDINATES",
        "DASH_TOWARDS_DIRECTION",
        "DASH_TOWARDS_TARGET",
        "DASH_TOWARDS_COORDINATES",
        "ID_CHECK_COORDINATES",
        "STOP_MOVEMENT",
        "UNSPECIFIED",
        "TURN_TOWARDS_MOB",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "USE_MELEE_TOWADS_MOB",
        "ID_USE_MELEE_TOWARDS_COORDINATES",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "ID_DROP_ITEM",
        "ID_TAKE_ITEM",
        "ID_EQUIP_ITEM",
        "ID_USE_ITEM",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "UNSPECIFIED",
        "ID_USE_TRIGGER"
     };
    
        public int taskID;
        public int actorID;
        public int argumentCount;
        public int arguments[];
        
        public Task() {
            
        }
        
        public Task(int taskID, int actorID, int[] arguments) {
            this.taskID = taskID;
            this.actorID = actorID;
            if (arguments == null) {
                this.argumentCount = 0;
            } else {
                this.argumentCount = arguments.length;
            }
            this.arguments = arguments;
        }

        @Override
        public void write(Kryo kryo, Output output) {
            output.writeInt(this.taskID);
            output.writeInt(this.actorID);
            output.writeInt(this.argumentCount);
            if (this.arguments!=null)output.writeInts(this.arguments);
        }

        @Override
        public void read(Kryo kryo, Input input) {
            this.taskID = input.readInt();
            this.actorID = input.readInt();
            this.argumentCount = input.readInt();
            if(this.argumentCount>0)this.arguments = input.readInts(this.argumentCount);
        }
        
        @Override
        public String toString() {
            if (taskID>=ID_CODE.length) return "UNSPECIFIED";
            String s = "["+ID_CODE[taskID]+":"+actorID+"]:"+Arrays.toString(arguments);
            return s;
        }
    }
