/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.nkoiv.mists.game.AI.Task;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.networking.LocationNetwork.Login;
import com.nkoiv.mists.game.networking.LocationNetwork.Register;
import com.nkoiv.mists.game.networking.LocationNetwork.RegistrationRequired;
import com.nkoiv.mists.game.networking.LocationNetwork.RemoveMapObject;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;

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
    
    HashSet<Player> loggedIn = new HashSet();
    
    private Stack<Object> outgoingUpdateStack;
    private Stack<Object> incomingUpdatesStack;
    
    public LocationServer(Game game) throws Exception {
        server = new Server(16384,16384) {
            @Override
            protected Connection newConnection () {
                    // By providing our own connection implementation, we can store per
                    // connection state without a connection ID to state look up.
                    return new PlayerConnection();
            }
        };
        this.port = LocationNetwork.PORT;
        LocationNetwork.register(server);
        this.game = game;
        this.location = game.getCurrentLocation();
        this.outgoingUpdateStack = new Stack<>();
        this.incomingUpdatesStack = new Stack<>();
        Kryo kryo = server.getKryo();
        server.addListener(new Listener() {
            @Override
            public void received(Connection c, Object object) {
                Mists.logger.info("Received connection");
                PlayerConnection connection = (PlayerConnection)c;
                Player player = connection.player;
                if (object instanceof Login) {
                    Mists.logger.info("Received Login");
                    // Ignore if already logged in.
                    if (player != null) return;

                    // Reject if the name is invalid.
                    String name = ((Login)object).name;
                    if (!isValid(name)) {
                        Mists.logger.info("Name invalid, closing connection");
                        c.close();
                        return;
                    }

                    // Reject if already logged in.
                    for (Player other : loggedIn) {
                        if (other.name.equals(name)) {
                            Mists.logger.info("Same name player found, closing connection");
                            c.close();
                            return;
                        }
                    }

                    //player = loadPlayer(name);

                    // Reject if couldn't load character.
                    if (player == null) {
                        Mists.logger.info("Asking for registration");
                        c.sendTCP(new RegistrationRequired());
                        return;
                    }
                    loggedIn(connection, player);
                    return;
                }

                if (object instanceof Register) {
                    Mists.logger.info("Received Register");
                    // Ignore if already logged in.
                    if (player != null) return;

                    Register register = (Register)object;

                    // Reject if the login is invalid.
                    if (!isValid(register.name)) {
                        Mists.logger.info("Name invalid, closing connection");
                        c.close();
                        return;
                    }

                    // Reject if character alread exists.
                    /*
                    if (loadCharacter(register.name) != null) {
                            c.close();
                            return;
                    }
                    */
                    player = new Player();
                    player.name = register.name;
                    player.id = 0;
                    /*
                    if (!saveCharacter(player)) {
                            c.close();
                            return;
                    }
                    */
                    loggedIn(connection, player);
                    return;
                }
                //addClientUpdate(c, object);
            }
            
            private boolean isValid (String value) {
                if (value == null) return false;
                value = value.trim();
                if (value.length() == 0) return false;
                return true;
            }

            @Override
            public void disconnected (Connection c) {
                PlayerConnection connection = (PlayerConnection)c;
                if (connection.player != null) {
                    Mists.logger.info("Player "+connection.player.name+" disconnected");
                    loggedIn.remove(connection.player);
                    /*
                    RemoveMapObject removePlayer = new RemoveMapObject();
                    removePlayer.id = connection.player.id;
                    server.sendToAllTCP(removePlayer);
                    */
                }
            }
        });
        
        server.bind(port);
        server.start();
        
        Mists.logger.info(("Server started. Port: "+port));
    }
    
    public void tick(double time) {
        if(this.paused == false) {
            game.getCurrentLocation().update(time, this);
        }
        this.handleClientUpdates();
        this.sendUpdates();
    }
    
    private void sendMap(PlayerConnection c) {
        Mists.logger.info("Sending map for location to "+c.player.name);
        this.server.sendToTCP(c.getID(), this.location.getMap());
    }

    
    private void addClientUpdate(Connection c, Object o) {
        this.incomingUpdatesStack.add(o);
    }
    
    private void handleClientUpdates() {
        if (!this.incomingUpdatesStack.isEmpty()) {
            Mists.logger.info("Handling "+this.incomingUpdatesStack.size()+" incoming updates");
        }
        while (!this.incomingUpdatesStack.isEmpty()) {
            this.handleUpdate(this.incomingUpdatesStack.pop());
        }
    }
    
    private void handleUpdate(Object o) {
        //TODO: Actually handle the update
        if (o instanceof Task) {
            
        }
    }
    
    public void addServerUpdate(Object o) {
        if (o == null) return;
        this.outgoingUpdateStack.push(o);
    }
    
    private void sendUpdates() {
        //this.sendObjectUpdates();
    }
    
    private void sendObjectUpdates() {
        if (!this.outgoingUpdateStack.isEmpty()) {
            //Mists.logger.info("Sending "+this.outgoingUpdateStack.size()+" outgoing updates to "+this.loggedIn.size()+" clients");
        }
        while (!this.outgoingUpdateStack.empty()) {
            this.server.sendToAllTCP(outgoingUpdateStack.pop());
        }
    }
    
    public void compileRemovals(Stack<Integer> removedMobIDs) {
        if (removedMobIDs.isEmpty()) return;
        for (Integer i : removedMobIDs) {
            this.outgoingUpdateStack.add(new RemoveMapObject(i));
        }
    }
    
    
    private void loggedIn (PlayerConnection c, Player player) {
        c.player = player;
        this.loggedIn.add(player);
        Mists.logger.info(player.name+" logged in");
        sendMap(c);
        //TODO: Load players character and place it in the world
        //Use either loadCharacter() or pick from characterLibrary?
    }
    
    /**
     * TODO: Loading characters from file is probably the wrong way to do it.
     * All the characters in the game are (probably?) saved in the same spot,
     * and loaded up as the game is loaded. That character only needs to be fetched
     * and placed in the right location
     * @param name Name of the player connecting
     * @return 
     */
    private Player loadCharacter (String name) {
        File file = new File("characters", name.toLowerCase());
        if (!file.exists()) return null;
        Input input = null;
        Kryo kryo = server.getKryo();

        try {
            input = new Input(new FileInputStream(file));
            Player player = new Player();
            player.id = input.readInt();
            player.name = name;
            player.character = kryo.readObject(input, PlayerCharacter.class);
            input.close();
            return player;
        } catch (IOException ex) {
                ex.printStackTrace();
                return null;
        } 
    }
    
    /**
     * Saving characters on logout is probably not the way
     * things will be done.
     * TODO: Save characters along with everything else in the same
     * place in game-files. See LoadCharacter.
     * @param player
     * @return Name of the player getting saved
     */
    boolean saveCharacter (Player player) {
        File file = new File("characters", player.name.toLowerCase());
        file.getParentFile().mkdirs();
        Kryo kryo = server.getKryo();
        if (player.id == 0) {
                String[] children = file.getParentFile().list();
                if (children == null) return false;
                player.id = children.length + 1;
        }

        Output output = null;
        try {
                output = new Output(new FileOutputStream(file));
                output.writeInt(player.id);
                kryo.writeObject(output, player.character);
                return true;
        } catch (IOException ex) {
                ex.printStackTrace();
                return false;
        }
    }

    static class PlayerConnection extends Connection {
        public Player player;
    }

}
