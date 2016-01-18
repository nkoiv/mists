/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.actions.MeleeWeaponAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.HasInventory;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.networking.LocationNetwork.*;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.TileMap;
import java.io.IOException;
import java.util.Stack;
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
    private boolean paused;
    private int port;

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

        name = "Player"; //TODO: input for this
        Login login = new Login();
        login.name = name;
        client.sendTCP(login);
    }
    
    public void tick(double time) {
        this.sendObjectUpdates();
        this.handleServerUpdates(time);
        if (this.location !=null ) {
            for (Creature c : this.location.getCreatures()) {
                c.updateByClient (time, this);
            }
            location.updateEffects(time);
            location.fullCleanup(false, false, true);
        }
        if (this.locationID != 0) addObjectUpdate(new MapObjectUpdate(this.locationID, game.getPlayer().getXPos(), game.getPlayer().getYPos()));
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
               g.moveToLocation(l);
            }
        });
    }
    
    private void handleIncomingMob(AddMapObject mob) {
        if (this.location == null || mob == null) return;
        Mists.logger.info("Received a mob: "+mob.templateName);
        MapObject m = null;
        if (mob.type.equals(Creature.class.toString())) {
            m = Mists.creatureLibrary.create(mob.templateName);
        }
        if (mob.type.equals(Structure.class.toString())) {
            m = Mists.structureLibrary.create(mob.templateName);
        }
        if (mob.type.equals(Effect.class.toString())) {
            
        }
        if (mob.type.equals(ItemContainer.class.toString())) {
            ItemContainer itemPile = new ItemContainer("ItemPile", new Sprite(Mists.graphLibrary.getImage("blank")));
            itemPile.setRenderContent(true);
            m = itemPile;
            RequestAllItems r = new RequestAllItems();
            r.inventoryOwnerID = m.getID();
            this.addObjectUpdate(r);
        }
        
        if (mob.type.equals(PlayerCharacter.class.toString())) {
            if (mob.templateName.equals("Lini")) {
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
                location.setPlayer((PlayerCharacter)m);
                location.setScreenFocus(m);
            }
            
        }
        if (m == null) return;
        if (this.location.getMapObject(mob.id) != null) this.location.removeMapObject(mob.id);
        this.location.setNextID(mob.id);
        this.location.addMapObject(m);
        this.location.getMapObject(mob.id).setPosition(mob.xPos, mob.yPos);
        Mists.logger.info("Mob succesfully placed at "+mob.xPos+","+mob.yPos);
    }
    
    private void handleServerUpdates(double time) {
        if (this.location == null) return;
        if (!this.incomingUpdatesStack.isEmpty()) {
            //Mists.logger.info("Handling "+this.incomingUpdatesStack.size()+" server updates");
        }
        while (!this.incomingUpdatesStack.isEmpty()) {
            Object object = this.incomingUpdatesStack.pop();
            
            if (object instanceof AddMapObject) {
                AddMapObject mobPackage = (AddMapObject)object;
                handleIncomingMob(mobPackage);
                return;
            }

            if (object instanceof MapObjectUpdate) {
                MapObject m = location.getMapObject(((MapObjectUpdate)object).id);
                if (m!=null) GenericTasks.checkCoordinates(m,((MapObjectUpdate)object).x, ((MapObjectUpdate)object).y);
                else this.outgoingUpdateStack.add(new MapObjectRequest(((MapObjectUpdate)object).id));
            }

            if (object instanceof RemoveMapObject) {
                RemoveMapObject mob = (RemoveMapObject)object;
                MapObject m = location.getMapObject(mob.id);
                if (m != null) {
                    Mists.logger.info(m.getName()+" removed");
                    location.removeMapObject(location.getMapObject(mob.id));
                }
                return;
            }

            if (object instanceof Task) {
                MapObject c = location.getMapObject(((Task) object).actorID);
                if (c instanceof Creature) ((Creature) c).setNextTask((Task) object);
            }
        }
    }
    
    public void askMapObjectUpdate(int MapObjectID) {
        
    }

    public void addObjectUpdate(Object o) {
        //TODO: sanitize
        if (o instanceof Task) this.outgoingUpdateStack.push(o);
        if (o instanceof AddItem) {
            Item i = Mists.itemLibrary.create(((AddItem)o).itemType);
            if (i != null) {
                MapObject owner = location.getMapObject(((AddItem)o).inventoryOwnerID);
                if (owner instanceof HasInventory) {
                    ((HasInventory) owner).addItem(i);
                }
            }
        }
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
