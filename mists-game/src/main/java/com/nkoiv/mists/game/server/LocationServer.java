/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.Stack;
import javafx.scene.input.KeyCode;

/**
 * LocationServer runs a Location and serves the information from it
 * to the clients connected to it.
 * @author nikok
 */
public class LocationServer {
    private Game game;
    private Location location;
    private Server server;
    private boolean paused;
    private int port;
    
    private Stack<Object> objectUpdateStack;
    private Stack<Object> incomingUpdates;
    
    public LocationServer(Server server, Game game) throws Exception {
        this.server = server;
        this.game = game;
        this.location = game.getCurrentLocation();
        this.objectUpdateStack = new Stack<>();
        Kryo kryo = server.getKryo();
        server.addListener(new Listener() {
            @Override
            public void received(Connection c, Object object) {
                addUpdate(c, object);
            }

            @Override
            public void disconnected(Connection c) {

            }
        });
        
        server.bind(port);
        server.start();
        
        Mists.logger.info(("Server started. Port: "+port));
        
    }
    
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        
        if(this.paused == false) {
            game.getCurrentLocation().update(time);
        }
        this.handleUpdates();
        this.sendUpdates();
    }
    
    private void addUpdate(Connection c, Object o) {
        this.incomingUpdates.add(o);
    }
    
    private void handleUpdates() {
        while (!this.incomingUpdates.isEmpty()) {
            this.handleUpdate(this.incomingUpdates.pop());
        }
    }
    
    private void handleUpdate(Object o) {
        
    }
    
    
    private void sendUpdates() {
        this.sendObjectUpdates();
    }
    
    
    
    private void sendObjectUpdates() {
        while (!this.objectUpdateStack.empty()) {
            this.server.sendToAllTCP(objectUpdateStack.pop());
        }
    }
    
}
