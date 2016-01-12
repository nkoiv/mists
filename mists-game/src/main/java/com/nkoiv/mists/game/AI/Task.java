/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.AI;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 *
 * @author nikok
 */
 public class Task implements KryoSerializable {
        int taskID;
        int actorID;
        int argumentCount;
        int arguments[];
        
        public Task(int taskID, int actorID, int[] arguments) {
            this.taskID = taskID;
            this.actorID = actorID;
            this.argumentCount = arguments.length;
            this.arguments = arguments;
        }

        @Override
        public void write(Kryo kryo, Output output) {
            output.writeInt(this.taskID);
            output.writeInt(this.actorID);
            output.writeInt(this.argumentCount);
            output.writeInts(this.arguments);
        }

        @Override
        public void read(Kryo kryo, Input input) {
            this.taskID = input.readInt();
            this.actorID = input.readInt();
            this.argumentCount = input.readInt();
            this.arguments = input.readInts(this.argumentCount);
        }
    }
