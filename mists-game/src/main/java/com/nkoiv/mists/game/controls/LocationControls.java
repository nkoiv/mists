/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.GameMode;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.networking.LocationClient;
import com.nkoiv.mists.game.networking.LocationServer;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.ui.InventoryPanel;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.Random;
import java.util.logging.Level;
import javafx.scene.image.ImageView;


/**
 * LocationControls are a layer that relays commands to the location
 * The idea behind separate LocationControls is that externally loaded
 * config / world data can utilize these via scripting.
 * @author nikok
 */
public class LocationControls {
    
    private final Game game;
    private GameMode gameMode;
    private LocationServer server;
    private LocationClient client;
    
    public LocationControls(Game game) {
        this.game = game;
        this.gameMode = GameMode.SINGLEPLAYER;
    }
    
    private Location currentLoc() {
        return this.game.getCurrentLocation();
    }
    
    public Game getGame() {
        return this.game;
    }
    
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
    
    public void setLocationServer(LocationServer server) {
        this.server = server;
    }
    
    public void setLocationClient(LocationClient client) {
        this.client = client;
    }
    
    /**
     * Trigger is used for interpreting a command,
     * either via console or by some external script
     * @param command the supplied command to execute
     * @param arguments for the command
     * @return returns true if command was executed
     */
    
    public boolean trigger(String command, String ...arguments) {
        switch (command) {
            case "toggleFlag": if (arguments!=null) this.toggleFlag(arguments[0]); return true;
            
                
            default: return false;
        }
        
    }
    
    //------------Individual triggers for controlling a location----
    
    public void toggleFlag(String flag) {
        this.game.getCurrentLocation().toggleFlag(flag);
    }
    
    public void printClearanceMapIntoConsole(){
        game.getCurrentLocation().getPathFinder().printClearanceMapIntoConsole(0);
    }
    
    public void printCollisionMapIntoConsole() {
        game.getCurrentLocation().getPathFinder().printCollisionMapIntoConsole();
    }
    
    public void toggleLocationMenu() {
        LocationState LS = (LocationState)this.game.currentState;
        LS.toggleGameMenu();
    }
    
    public void increseLightLevel() {
        this.game.getCurrentLocation().setMinLightLevel(this.game.getCurrentLocation().getMinLightLevel()+0.1);
    }
    
    public void reduceLightLevel() {
        this.game.getCurrentLocation().setMinLightLevel(this.game.getCurrentLocation().getMinLightLevel()-0.1);
    }
    
    
    //----------Player controls-----
    
    public void playerAttack() {
        Task attack = new Task(GenericTasks.ID_USE_MELEE_TOWARDS_DIRECTION, game.getPlayer().getID(), new int[]{Toolkit.getDirectionNumber(game.getPlayer().getFacing())});
        game.getPlayer().setNextTask(attack);
        if (this.gameMode == GameMode.CLIENT) this.client.addObjectUpdate(attack);
    }
    
    public void playerMove(Direction direction) {
        Task move = new Task(GenericTasks.ID_MOVE_TOWARDS_DIRECTION, game.getPlayer().getID(), new int[]{Toolkit.getDirectionNumber(direction)});
        game.getPlayer().setNextTask(move);
        if (this.gameMode == GameMode.CLIENT) this.client.addObjectUpdate(move);
    }
    
    public void teleportPlayer(double xCoor, double yCoor) {
        if (this.gameMode != GameMode.CLIENT) this.game.getCurrentLocation().getPlayer().setCenterPosition(xCoor, yCoor);
    }
    
    public void toggleInventory(InventoryPanel inv) {
        if (game.currentState.removeUIComponent(inv.getName()));
        else {
            game.currentState.addUIComponent(inv); 
            Mists.logger.log(Level.INFO, "Inventory size: {0} items, capacity {1}", new Object[]{inv.getInventory().getSize(), inv.getInventory().getCapacity()});
        }
    }
    
    public void takeItem(MapObject targetInventory, int slot) {
        Task take = new Task(GenericTasks.ID_TAKE_ITEM, game.getPlayer().getID(), new int[]{targetInventory.getID(), slot});
        game.getPlayer().setNextTask(take);
    }
    
    public void dropItem(int slot) {
        Task drop = new Task(GenericTasks.ID_DROP_ITEM, game.getPlayer().getID(), new int[]{slot});
        game.getPlayer().setNextTask(drop);
    }
    
    public void equipItem(int slot) {
        Task equip = new Task(GenericTasks.ID_EQUIP_ITEM, game.getPlayer().getID(), new int[]{slot});
        game.getPlayer().setNextTask(equip);
    }
    
    public void useItem(Inventory inventory, int slot) {
        Task use = new Task(GenericTasks.ID_USE_ITEM, game.getPlayer().getID(), new int[]{slot});
        game.getPlayer().setNextTask(use);
    }
    
    
    
    //-------- Mob creation ------
    
    public void addCreature(String mobTemplate) {
        if ("".equals(mobTemplate)) addCreature(); 
        else {
            if ("blob".equals(mobTemplate)) addBlob();
        }
        
    }
    
    public void createItemPile(Item item, double xCoor, double yCoor) {
        ItemContainer itemPile = new ItemContainer(item.getName(), new Sprite(Mists.graphLibrary.getImage("blank")));
        itemPile.addItem(item);
        itemPile.setRenderContent(true);
        this.currentLoc().addStructure(itemPile, xCoor, yCoor);
    }
    
    //------- Inventory manipulation ------
    
    public void giveItem(String attributes) {
        int space = attributes.indexOf(" ");
        if (space>1) {
            String target = attributes.substring(0, space);
            String item = attributes.substring(space+1, attributes.length());
            Creature targetCreature;
            if (target.equals(game.getPlayer().getName())) targetCreature = game.getPlayer();
            else targetCreature = game.getCurrentLocation().getCreatureByName(target);
            Item itemToGive = Mists.itemLibrary.create(item);
            if (targetCreature == null || itemToGive == null) Mists.logger.warning ("Could not parse giveItem. Item was: "+item+ " Target was: "+target);
            else targetCreature.addItem(itemToGive);
        } else Mists.logger.warning ("Could not parse giveItem");
    }
    
    /**
     * Create a random mob at spot mouse cursor is at
     */
    public void addCreature() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        double x = p.x - Mists.primaryStage.getX();
        double y = p.y - Mists.primaryStage.getY();
        Random rnd = new Random();
        int startX = rnd.nextInt(1);
        int startY = rnd.nextInt(1);
        Mists.logger.log(Level.INFO, "Creating monster from sprite sheet position {0},{1} at coordinates {2}+{3}x{4}+{5}", new Object[]{startX, startY, x, game.getCurrentLocation().getLastxOffset(), y, game.getCurrentLocation().getLastyOffset()});
        Creature monster = new Creature("Otus", new ImageView("/images/monster_small.png"), 3, startX*3, startY*4, 32, 32);
        game.getCurrentLocation().addCreature(monster, x+game.getCurrentLocation().getLastxOffset(), y+game.getCurrentLocation().getLastyOffset());   
    }
    
    public void addBlob() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        double x = p.x - Mists.primaryStage.getX();
        double y = p.y - Mists.primaryStage.getY();
        Creature monster = new Creature("Blob", new ImageView("/images/blob.png"), 3, 0, 0, 84, 84);
        game.getCurrentLocation().addCreature(monster, x+game.getCurrentLocation().getLastxOffset(), y+game.getCurrentLocation().getLastyOffset());   
    }
    
    //----------Location creation------------
    
    public void createLoc(String location) {
        if (location.equals("testmap")) {
            //game.moveToState(0);
            Location newlocation = new Location ("ConsoleLoc", new TileMap("/mapdata/pathfinder_test.map"));
            game.moveToLocation(newlocation);
            //game.moveToState(1);
        }
        
    }
    
}
