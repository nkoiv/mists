/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.nkoiv.mists.game.AI.Task;

/**
 *
 * @author nikok
 */
public class LocationNetwork {
    static public final int port = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Login.class);
		kryo.register(RegistrationRequired.class);
		kryo.register(Register.class);
		kryo.register(Task.class);
                kryo.register(CreatureAct.class);
		kryo.register(UpdateMapObject.class);
		kryo.register(RemoveMapObject.class);
		kryo.register(MoveMapObject.class);
	}

	static public class Login {
		public String name;
	}

	static public class RegistrationRequired {
	}

	static public class Register {
		public String name;
		public String otherStuff;
	}
        
        static public class CreatureAct {
                public Task task;
        }

	static public class UpdateMapObject {
		public int id, x, y;
	}

	static public class RemoveMapObject {
		public int id;
                public RemoveMapObject(int id) {this.id = id;}
	}

	static public class MoveMapObject {
		public int x, y;
	}
}
