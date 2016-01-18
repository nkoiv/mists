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
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.HasInventory;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.networking.LocationNetwork.AddItem;
import com.nkoiv.mists.game.networking.LocationNetwork.AddMapObject;
import com.nkoiv.mists.game.networking.LocationNetwork.Login;
import com.nkoiv.mists.game.networking.LocationNetwork.MapObjectRequest;
import com.nkoiv.mists.game.networking.LocationNetwork.MapObjectUpdate;
import com.nkoiv.mists.game.networking.LocationNetwork.MapObjectUpdateRequest;
import com.nkoiv.mists.game.networking.LocationNetwork.Register;
import com.nkoiv.mists.game.networking.LocationNetwork.RegistrationRequired;
import com.nkoiv.mists.game.networking.LocationNetwork.RemoveMapObject;
import com.nkoiv.mists.game.networking.LocationNetwork.RequestAllItems;
import com.nkoiv.mists.game.world.Location;
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
    
    private static final int playerCap = 4;
    
    HashSet<Player> loggedIn = new HashSet();
    
    
    private final Stack<Object> outgoingUpdateStack; //Sent to everyone
    private final Stack<Object>[] outgoingUpdateStacks = new Stack[playerCap]; //Sent to a single player
    private final Stack<Object>[] incomingUpdateStacks  = new Stack[playerCap];; //Incoming from given player
    
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
        for (int i = 0; i < outgoingUpdateStacks.length; i++) {
            this.incomingUpdateStacks[i] = new Stack<>();
        }
        for (int i = 0; i < incomingUpdateStacks.length; i++) {
            this.incomingUpdateStacks[i] = new Stack<>();
        }
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
                    player.playerID = nextFreePlayerID();
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
                    
                    /*
                    if (!saveCharacter(player)) {
                            c.close();
                            return;
                    }
                    */
                    player.name = register.name;
                    player.playerID = nextFreePlayerID();
                    loggedIn(connection, player);
                    return;
                }
                if (object instanceof Task || object instanceof MapObjectUpdateRequest || object instanceof MapObjectUpdate) {
                    addClientUpdate(c, object);
                }
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
    
    private int giveConnectedPlayerCompanion(String name) {
        PlayerCharacter companion = new PlayerCharacter(name);
        location.addMapObject(companion);
        companion.setCenterPosition(location.getPlayer().getXPos()+50, location.getPlayer().getYPos()+50);
        companion.addAction(new MeleeAttack());
        Mists.logger.info("Gave the connecting player: "+companion.getName()+" id: "+companion.getID());
        addMapObject(companion);
        return companion.getID();
    }
    
    private void sendAllMobs(PlayerConnection c) {
        Object[] a = this.location.getAllMobsIDs().toArray();
        for (Object o : a) {
            MapObject mob = location.getMapObject((Integer)o);
            this.server.sendToTCP(c.getID(), new AddMapObject(mob.getID(), mob.getName(), mob.getClass().toString(), mob.getXPos(), mob.getYPos()));
        }
    }
    
    private void sendAllCreatures(PlayerConnection c) {  
        Object[] a = this.location.getAllMobsIDs().toArray();
        for (Object o : a) {
            MapObject mob = location.getMapObject((Integer)o);
            if (mob instanceof Creature) this.server.sendToTCP(c.getID(), new AddMapObject(mob.getID(), mob.getName(), mob.getClass().toString(), mob.getXPos(), mob.getYPos()));
        }
    }
    
    private void sendAllNonWalls(PlayerConnection c) {
        Object[] a = this.location.getAllMobsIDs().toArray();
        for (Object o : a) {
            MapObject mob = location.getMapObject((Integer)o);
            if (!(mob instanceof Wall)) this.server.sendToTCP(c.getID(), new AddMapObject(mob.getID(), mob.getName(), mob.getClass().toString(), mob.getXPos(), mob.getYPos()));
            //if (mob instanceof HasInventory)
        }
    }
    
    public void addMapObjectUpdate(MapObject mob) {
        this.addServerUpdate(new MapObjectUpdate(mob.getID(), mob.getXPos(), mob.getYPos()));
    }
    
    public void addMapObject(MapObject mob) {
        this.addServerUpdate(new AddMapObject(mob.getID(), mob.getName(), mob.getClass().toString(), mob.getXPos(), mob.getYPos()));
    }
    
    private void addClientUpdate(Connection c, Object o) {
        int id = ((PlayerConnection)c).player.playerID;
        this.incomingUpdateStacks[id].add(o);
    }
    
    private void handleClientUpdates() {
        
        
        for (int i = 0; i < this.incomingUpdateStacks.length; i++) {
            handleClientUpdates(i);
        }
    }
    
    private void handleClientUpdates(int playerID) {
        //Mists.logger.info("Client updates in stack: "+this.incomingUpdatesStack.size());
        if (!this.incomingUpdateStacks[playerID].isEmpty()) {
            //Mists.logger.info("Handling "+this.incomingUpdateStacks[playerID].size()+" incoming updates");
        }
        while (!this.incomingUpdateStacks[playerID].isEmpty()) {
            this.handleUpdate(this.incomingUpdateStacks[playerID].pop());
        }
    }
    
    private void handleUpdate(Object o) {
        //TODO: Actually handle the update
        if (o instanceof Task) {
            //TODO: Ensure the client has the right to push this task
            int actorID = ((Task)o).actorID;
            ((Creature)location.getMapObject(actorID)).setNextTask((Task)o);
        }
        if (o instanceof MapObjectUpdateRequest) {
            MapObject m = location.getMapObject(((MapObjectUpdateRequest)o).id);
            if (m == null) this.addServerUpdate(new RemoveMapObject(((MapObjectUpdateRequest)o).id));
            else this.addServerUpdate(new MapObjectUpdate(m.getID(), m.getXPos(), m.getYPos()));
        }
        if (o instanceof MapObjectRequest) {
            MapObject mob = location.getMapObject(((MapObjectRequest)o).id);
            if (mob != null) addMapObject(mob);
        }
        if (o instanceof MapObjectUpdate) {
            MapObject m = location.getMapObject(((MapObjectUpdate)o).id);
            if (m!=null) m.setPosition(((MapObjectUpdate)o).x, ((MapObjectUpdate)o).y);
        }
        if (o instanceof RequestAllItems) {
            MapObject m = location.getMapObject(((RequestAllItems)o).inventoryOwnerID);
            if (m instanceof HasInventory) {
                Inventory inv = ((HasInventory)m).getInventory();
                int invSize = inv.getCapacity();
                for (int slot = 0; slot < invSize; slot++) {
                    Item it = inv.getItem(slot);
                    if (it!=null) {
                        AddItem ai = new AddItem();
                        ai.inventoryOwnerID = ((RequestAllItems)o).inventoryOwnerID;
                        ai.slotID=slot;
                        ai.itemBaseID=it.getBaseID(); //TODO:
                        ai.item=it;
                        
                    }
                }
            }
        }
        
    }
    
    public void addServerUpdate(Object o) {
        if (o == null) return;
        this.outgoingUpdateStack.push(o);
    }
    
    private void sendUpdates() {
        this.sendGlobalUpdates();
        this.sendIndividualUpdates();
    }
    
    private void sendGlobalUpdates() {
        if (!this.outgoingUpdateStack.isEmpty()) {
            //Mists.logger.info("Sending "+this.outgoingUpdateStack.size()+" outgoing updates to "+this.loggedIn.size()+" clients");
        }
        while (!this.outgoingUpdateStack.empty()) {
            this.server.sendToAllTCP(outgoingUpdateStack.pop());
        }
    }
    
    private void sendIndividualUpdates() {
        PlayerConnection[] cons = (PlayerConnection[])server.getConnections();
        for (PlayerConnection p : cons) {
            sendUpdatesToPlayer(p.player, p.getID());
        }
    }
    
    private void sendUpdatesToPlayer(Player player, int connectionID) {
        while (!this.outgoingUpdateStacks[player.playerID].isEmpty()) {
            this.server.sendToTCP(connectionID, outgoingUpdateStacks[player.playerID].pop());
        }
    }
    
    public void compileRemovals(Stack<Integer> removedMobIDs) {
        if (removedMobIDs.isEmpty()) return;
        for (Integer i : removedMobIDs) {
            addServerUpdate(new RemoveMapObject(i));
        }
    }
    
    private int nextFreePlayerID() {
        boolean[] b = new boolean[playerCap];
        for (Player p : this.loggedIn) {
            b[p.playerID] = true;
        }
        for (int i = 0; i < b.length; i++) {
            if (b[i]==false) return i;
        }
        return -1;
    }
    
    private void loggedIn (PlayerConnection c, Player player) {
        c.player = player;
        this.loggedIn.add(player);
        Mists.logger.info(player.name+" logged in");
        sendMap(c);
        sendAllNonWalls(c);
        Register register = new Register();
        register.name = player.name;
        player.locationID = giveConnectedPlayerCompanion(player.name);
        register.locationID = player.locationID;
        c.sendTCP(register);
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
            player.locationID = input.readInt();
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
        if (player.locationID == 0) {
                String[] children = file.getParentFile().list();
                if (children == null) return false;
                player.locationID = children.length + 1;
        }

        Output output = null;
        try {
                output = new Output(new FileOutputStream(file));
                output.writeInt(player.locationID);
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
