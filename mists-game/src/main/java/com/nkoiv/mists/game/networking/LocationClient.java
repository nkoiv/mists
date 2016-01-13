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
import com.nkoiv.mists.game.AI.GenericTasks;
import com.nkoiv.mists.game.AI.Task;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.networking.LocationNetwork.*;
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
                Mists.logger.info("Got "+object.getClass());
                if (object instanceof RegistrationRequired) {
                    Mists.logger.info("Registering...");
                    Register register = new Register();
                    register.name = name;
                    client.sendTCP(register);
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
        
        String host = "localhost"; //TODO: Obviously input for this
        
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
    
    private void handleServerUpdates(double time) {
        if (!this.incomingUpdatesStack.isEmpty()) {
            //Mists.logger.info("Handling "+this.incomingUpdatesStack.size()+" server updates");
        }
        while (!this.incomingUpdatesStack.isEmpty()) {
            Object object = this.incomingUpdatesStack.pop();
            
            if (object instanceof AddMapObject) {
                AddMapObject msg = (AddMapObject)object;
                //TODO: Location.addMapObject...
                return;
            }

            if (object instanceof UpdateMapObject) {
                //TODO: Location.getMapObject.set...
                return;
            }

            if (object instanceof RemoveMapObject) {
                RemoveMapObject msg = (RemoveMapObject)object;
                //TODO: Location.removeMapObject...
                return;
            }

            if (object instanceof Task) {
                GenericTasks.performTask(location, (Task)object, time);
            }
        }
    }

    public void addObjectUpdate(Object o) {
        //TODO: sanitize
        this.outgoingUpdateStack.push(o);
    }
    
    private void sendUpdates() {
        this.sendObjectUpdates();
    }

    private void sendObjectUpdates() {
        while (!this.outgoingUpdateStack.isEmpty()) {
            client.sendTCP(this.outgoingUpdateStack.pop());
        }
    }

}
