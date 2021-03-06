/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.networking;

import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Level;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.actions.MeleeWeaponAttack;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.HasInventory;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.networking.LocationNetwork.AddItem;
import com.nkoiv.mists.game.networking.LocationNetwork.AddMapObject;
import com.nkoiv.mists.game.networking.LocationNetwork.FullMapObjectIDList;
import com.nkoiv.mists.game.networking.LocationNetwork.LocationClear;
import com.nkoiv.mists.game.networking.LocationNetwork.Login;
import com.nkoiv.mists.game.networking.LocationNetwork.MapObjectRequest;
import com.nkoiv.mists.game.networking.LocationNetwork.MapObjectUpdate;
import com.nkoiv.mists.game.networking.LocationNetwork.Register;
import com.nkoiv.mists.game.networking.LocationNetwork.RegistrationRequired;
import com.nkoiv.mists.game.networking.LocationNetwork.RemoveMapObject;
import com.nkoiv.mists.game.networking.LocationNetwork.RequestAllItems;
import com.nkoiv.mists.game.networking.LocationNetwork.RequestLocationClear;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;

import javafx.application.Platform;

/**
 * Client doesnt run AI routines or do cleanup.
 * Client receives information from server and updates
 * based on that.
 * @author nikok
 */
public class LocationClient {
    private Game game;
    private Location location;
    private Client client;
    private String name;
    private int locationID;
    private boolean ready;
    private double lastReadyCheck;
    
    private Stack<Object> outgoingUpdateStack;
    private Stack<Object> incomingUpdatesStack;

    public LocationClient (Game game) {
        client = new Client(16384,16384);
        this.game = game;
        client.start();

        // For consistency, the classes to be sent over the network are
        // registered by the same method for both the client and server.
        LocationNetwork.register(client);

        this.location = game.getCurrentLocation();
        this.outgoingUpdateStack = new Stack<>();
        this.incomingUpdatesStack = new Stack<>();
        Kryo kryo = client.getKryo();
        client.setKeepAliveTCP(2000);
        // ThreadedListener runs the listener methods on a different thread.
        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void connected (Connection connection) {
            }

            @Override
            public void received (Connection connection, Object object) {
                //Mists.logger.info("Got "+object.getClass());
                if (object instanceof RegistrationRequired) {
                    Mists.logger.info("Registering...");
                    Register register = new Register();
                    register.name = name;
                    client.sendTCP(register);
                } else if (object instanceof Register) {
                    Mists.logger.info("Got ID for register");
                    name = ((Register)object).name;
                    locationID = ((Register)object).locationID;
                } else if (object instanceof TileMap) {
                    Mists.logger.info("Received a new map from server, processing...");
                    handleTileMap(object);
                }   else {
                    addServerUpdate(object);
                }
            }

            @Override
            public void disconnected (Connection connection) {
                    //System.exit(0);
            }
        }));
        
        String host = Global.serverAddress; //TODO: Obviously input for this
        
        try {
                Mists.logger.info("Trying to connect...");
                client.connect(5000, host, LocationNetwork.PORT);
                // Server communication after connection can go here, or in Listener#connected().
        } catch (IOException ex) {
                ex.printStackTrace();
        }

        name = "Player "+client.getID(); //TODO: input for this
        Login login = new Login();
        login.name = name;
        client.sendTCP(login);
    }
    
    public void tick(double time) {
        if (game.getPlayer().getNextTask() != null) addOutgoingUpdate(game.getPlayer().getNextTask());
        this.sendUpdates();
        this.handleServerUpdates(time);
        if (this.location !=null ) {
            for (Creature c : this.location.getCreatures()) {
                c.update(time);
                //c.updateByClient (time, this);
            }
            location.updateEffects(time);
            location.fullCleanup(true, true, true);
        }
        
        if (!this.ready && location != null) { //Not yet running
            lastReadyCheck += time;
            if (lastReadyCheck > 1) {
                RequestLocationClear rlc = new RequestLocationClear();
                rlc.mobCount = location.getMobCount();
                this.addOutgoingUpdate(rlc);
            }
        }
        //if (this.locationID != 0) addObjectUpdate(new MapObjectUpdate(this.locationID, game.getPlayer().getXPos(), game.getPlayer().getYPos()));
    }

    private void addServerUpdate(Object o) {
        this.incomingUpdatesStack.push(o);
    }
    
    private void handleTileMap(Object object) {
        final TileMap t = (TileMap)object;
        final Game g = this.game;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
               t.buildMap();
               Location l = new Location("ServerMap", t);
               g.moveToLocation(l, null);
            }
        });
        if (!this.ready) {
            game.getLoadingScreen().addProgress(0.2);
            game.getLoadingScreen().setLoadingText("Map built");
        }
    }
    
    private void handleIncomingMob(AddMapObject mob) {
        if (this.location == null || mob == null) return;
        Mists.logger.info("Received a mob: "+mob.templateName+" id:"+mob.id);
        MapObject m = null;
        if (mob.type.equals(Creature.class.toString())) {
            m = Mists.creatureLibrary.create(mob.templateName);
        }
        if (mob.type.equals(Structure.class.toString())) {
            m = Mists.structureLibrary.create(mob.templateName);
        }
        if (mob.type.equals(WorldMapEntrance.class.toString())) {
            m = Mists.structureLibrary.create(mob.templateName);
        }
        if (mob.type.equals(Effect.class.toString())) {
            
        }
        if (mob.type.equals(ItemContainer.class.toString())) {
            ItemContainer itemPile = new ItemContainer("ItemPile", new Sprite(Mists.graphLibrary.getImage("blank")));
            itemPile.setRenderContent(true);
            m = itemPile;
        }
        
        if (mob.type.equals(PlayerCharacter.class.toString())) {
            if ("Lini".equals(mob.templateName)) {
                m = new PlayerCharacter();
                ((PlayerCharacter)m).addAction(new MeleeWeaponAttack());
            }
            else {
                m = new PlayerCharacter(mob.templateName);
                ((PlayerCharacter)m).addAction(new MeleeAttack());
            }
            if (mob.id == this.locationID) {
                Mists.logger.info("Got a character to control: "+mob.id);
                game.setPlayer((PlayerCharacter)m);
                ((LocationState)game.getGameState(Game.LOCATION)).loadDefaultUI();
                location.setPlayer((PlayerCharacter)m);
                location.setScreenFocus(m);
            }
            
        }
        if (m == null) {
            Mists.logger.log(Level.WARNING, "Was unable to generate mob. Template: {0}, id: {1}", new Object[]{mob.templateName, mob.id});
            return;
        }
        if (mob.hasItems) addOutgoingUpdate(new RequestAllItems(mob.id));
        //If MapObject already exists with the same ID, remove that map object to make room for this new one
        if (this.location.getMapObject(mob.id) != null) this.location.removeMapObject(mob.id);
        this.location.addMapObject(m, mob.id);
        this.location.getMapObject(mob.id).setPosition(mob.xPos, mob.yPos);
        Mists.logger.log(Level.INFO, "{0} succesfully placed at {1},{2}", new Object[]{m.getName(), mob.xPos, mob.yPos});
        if (!this.ready) {
            game.getLoadingScreen().addProgress(0.05);
            game.getLoadingScreen().setLoadingText("Placing creatures");
        }
    }
    
    private void handleServerUpdates(double time) {
        if (this.location == null) return;
        if (!this.incomingUpdatesStack.isEmpty()) {
            //Mists.logger.info("Handling "+this.incomingUpdatesStack.size()+" server updates");
        }
        while (!this.incomingUpdatesStack.isEmpty()) {
            Object object = this.incomingUpdatesStack.pop();
            
            if (object instanceof LocationClear) {
                if (((LocationClear)object).clear) {
                    ready = true;
                    game.clearLoadingScreen();
                    ((LocationState)game.currentState).loadDefaultUI();
                }
                continue;
            }
            
            if (object instanceof AddMapObject) {
                AddMapObject mobPackage = (AddMapObject)object;
                handleIncomingMob(mobPackage);
                continue;
            }

            if (object instanceof MapObjectUpdate) {
                MapObject m = location.getMapObject(((MapObjectUpdate)object).id);
                if (m!=null) GenericTasks.checkCoordinates(m,((MapObjectUpdate)object).x, ((MapObjectUpdate)object).y);
                else {
                    Mists.logger.info("Tried updating Mob that doesn't exist - requesting mob (id:"+(((MapObjectUpdate)object).id)+")");
                    this.addOutgoingUpdate(new MapObjectRequest(((MapObjectUpdate)object).id));
                }
                continue;
            }

            if (object instanceof RemoveMapObject) {
                RemoveMapObject mob = (RemoveMapObject)object;
                Mists.logger.info("removing mobID "+mob.id);
                //location.removeMapObject(mob.id);
                if (location.getMapObject(mob.id) == null) return;
                location.getMapObject(mob.id).remove();
                continue;
            }

            if (object instanceof Task) {
                MapObject c = location.getMapObject(((Task) object).actorID);
                if (c instanceof Creature && c.getID() != game.getPlayer().getID()) ((Creature) c).setNextTask((Task) object);
            }
            if (object instanceof AddItem) {
                AddItem ai = (AddItem)object;
                Mists.logger.info("Got AddItem ID:"+ai.itemBaseID+", ownerID: "+ai.inventoryOwnerID+" slot:"+ai.slotID);
                Item i = Mists.itemLibrary.create(ai.itemBaseID);
                if (i != null) {
                    //TODO: get the data needed from the AddItem.item and update the item i
                    MapObject owner = location.getMapObject(ai.inventoryOwnerID);
                    Mists.logger.info("Giving "+i.getName()+" to "+owner.getName());
                    if (owner instanceof HasInventory) {
                        ((HasInventory) owner).getInventory().forceItemIntoSlot(i, ai.slotID);
                    }
                }
                continue;
            }
            
            if (object instanceof FullMapObjectIDList) {
                FullMapObjectIDList fl = ((FullMapObjectIDList)object);
                Mists.logger.log(Level.INFO, "Got FullMapObjectIDList, size {0} Currently mobs in loc: {1}", new Object[]{fl.mobIDList.length, location.getAllMobsIDs().size()});
                syncMobsWithServer(fl);
                continue;
            }
            
        }
    }
    
    private void syncMobsWithServer(FullMapObjectIDList fullMobList) {
        HashSet<Integer> mobsOnServer = new HashSet<>();
        for (int i : fullMobList.mobIDList) {
            mobsOnServer.add(i);
        }
        
        HashSet<Integer> mobsOnClient = new HashSet<>();
        for (int i : location.getAllMobsIDs()) {
            mobsOnClient.add(i);
        }
        
        for (int mobID : mobsOnClient) {
            if (!(mobsOnServer.contains(mobID))) {
                Mists.logger.info("mobID "+mobID+" was not found on server, removing");
                location.removeMapObject(mobID);
            }
        }
        if (fullMobList.mobIDList.length == location.getMobCount()) return;
        else {
            for (int mobID : fullMobList.mobIDList) {
                if (!(mobsOnClient.contains(mobID))) {
                    Mists.logger.info("mobID "+mobID+" was not found on client, requesting from server");
                    MapObjectRequest mobRequest = new MapObjectRequest();
                    mobRequest.id = mobID;
                    addOutgoingUpdate(mobRequest);
                }
            }
        }
    }
    
    public void askMapObjectUpdate(int MapObjectID) {
        
    }

    public void addOutgoingUpdate(Object o) {
        //TODO: sanitize
        if (o instanceof MapObjectUpdate) {
            this.outgoingUpdateStack.push(o);
            return;
        }
        if (o instanceof Task) {
            //Mists.logger.info("Pushed Task to outgoing stack: "+ ((Task)o).taskID);
            this.outgoingUpdateStack.push(o);
            return;
        }
        if (o instanceof AddItem) {
            this.outgoingUpdateStack.push(o);
            return;
        }
        if (o instanceof MapObjectRequest) {
            this.outgoingUpdateStack.push(o);
            return;
        }
        if (o instanceof RequestAllItems) {
            this.outgoingUpdateStack.push(o);
            return;
        }
        if (o instanceof RequestLocationClear) {
            this.outgoingUpdateStack.push(o);
            return;
        }
        
        Mists.logger.info("Unknown object update discarded ("+o.getClass()+")");
    }
    
    private void sendUpdates() {
        this.sendObjectUpdates();
    }

    private void sendObjectUpdates() {
        while (!this.outgoingUpdateStack.isEmpty()) {
            client.sendTCP(this.outgoingUpdateStack.pop());
        }
    }

    public Location getLocation() {
        return this.location;
    }
    
    public void setLocation(Location l) {
        this.location = l;
    }
}
