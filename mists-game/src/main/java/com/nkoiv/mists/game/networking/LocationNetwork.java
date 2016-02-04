/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.items.Weapon;
import com.nkoiv.mists.game.world.TileMap;

/**
 *
 * @author nikok
 */
public class LocationNetwork {
    static public final int PORT = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
                kryo.register(int[].class);
		kryo.register(Login.class);
		kryo.register(RegistrationRequired.class);
		kryo.register(Register.class);
                kryo.register(CreatureAct.class);
                kryo.register(RequestLocationClear.class);
                kryo.register(LocationClear.class);
                //MapObject related classes
                kryo.register(FullMapObjectIDList.class);
                kryo.register(MapObjectRequest.class);
                kryo.register(AddMapObject.class);
                kryo.register(MapObjectUpdateRequest.class);
		kryo.register(MapObjectUpdate.class);
		kryo.register(RemoveMapObject.class);
		kryo.register(MoveMapObject.class);
                //Item classes
                kryo.register(AddItem.class);
                kryo.register(RequestAllItems.class);
                //Kryo-serialized generic classes:
                kryo.register(Task.class);
                kryo.register(TileMap.class);
                kryo.register(Item.class);
                kryo.register(Weapon.class);
	}

	static public class Login {
            public String name;
	}
        
        static public class RequestLocationClear {
            public int mobCount;
        }
        
        static public class LocationClear {
            public boolean clear;
        }

	static public class RegistrationRequired {
	}

	static public class Register {
            public String name;
            public int locationID;
	}
        
        static public class CreatureAct {
            public Task task;
        }
        
        //--- MapObjects---
        static public class FullMapObjectIDList {
            int mobCount;
            int[] mobIDList;
        }
        
        static public class MapObjectUpdateRequest {
            public int id;
            public MapObjectUpdateRequest(){}
            public MapObjectUpdateRequest(int id){this.id=id;}
        }
        
	static public class MapObjectUpdate {
            public int id;
            public double x, y;
            public MapObjectUpdate(){}
            public MapObjectUpdate(int id, double x, double y) {this.id = id; this.x = x; this.y = y;}
	}
        
        static public class MapObjectRequest {
            public int id;
            public MapObjectRequest(){}
            public MapObjectRequest(int id){this.id=id;}
        }
        
        static public class AddMapObject {
            public int id;
            public String templateName;
            public String type;
            public double xPos;
            public double yPos;
            public boolean hasItems;
            public AddMapObject(int id, String templateName, String type, double xPos, double yPos){
                this.id = id; this.templateName = templateName; this.type = type; this.xPos = xPos; this.yPos = yPos;
            }
            public AddMapObject(){}
        }

	static public class RemoveMapObject {
            public int id;
            public RemoveMapObject(int id) {this.id = id;}
            public RemoveMapObject(){}
	}

	static public class MoveMapObject {
            public int x, y;
	}
        
        
        //--- Items and Inventory ----
        
        static public class AddItem {
            public int itemBaseID;
            public int inventoryOwnerID;
            public int slotID;
            //TODO: Possible item variables
        }
        
        static public class RequestAllItems {
            public int inventoryOwnerID;
            public RequestAllItems(){}
            public RequestAllItems(int inventoryOwnerID){this.inventoryOwnerID = inventoryOwnerID;}
        }

}
