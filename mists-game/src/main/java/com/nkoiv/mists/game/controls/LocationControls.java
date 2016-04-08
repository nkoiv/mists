/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.GameMode;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
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
    private LocationServer server;
    private LocationClient client;
    
    public LocationControls(Game game) {
        this.game = game;
    }
    
    private Location currentLoc() {
        return this.game.getCurrentLocation();
    }
    
    public Game getGame() {
        return this.game;
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
    
    public void printMobInfo(String mobid) {
        Mists.logger.info("WHOIS for "+mobid);
        int id = -1;
        try {
            id = Integer.parseInt(mobid);
        } catch (NumberFormatException e) {
            Mists.logger.warning("Tried to printMobInfo on invalid mobID");
            return;
        }
        MapObject mob = game.getCurrentLocation().getMapObject(id);
        if (mob!=null) Mists.logger.info("WHOIS : "+mob.toString());
    }
    
    public void printCollisionMapIntoConsole() {
        game.getCurrentLocation().getPathFinder().printCollisionMapIntoConsole();
    }
    
    public void printLightMapIntoConsole() {
        game.getCurrentLocation().printLightMapIntoConsole();
    }
    
    public void toggleLocationMenu() {
        LocationState LS = (LocationState)this.game.currentState;
        LS.toggleGameMenu();
    }
    
    public void toggleQuestPanel() {
        LocationState LS = (LocationState)this.game.currentState;
        LS.toggleQuestPanel();
    }
    
    public void toggleCharacterPanel() {
        LocationState LS = (LocationState)this.game.currentState;
        LS.toggleCharacterPanel();
    }
    
    
    public void increseLightLevel() {
        //this.game.getPlayer().setVisionRange(this.game.getPlayer().getVisionRange()+1);
        this.game.getPlayer().setLightSize(this.game.getPlayer().getLightSize()+0.2);
    }
    
    public void reduceLightLevel() {
        //this.game.getPlayer().setVisionRange(this.game.getPlayer().getVisionRange()-1);
        this.game.getPlayer().setLightSize(this.game.getPlayer().getLightSize()-0.2);
    }
    
    
    //----------Player controls-----
    
    public void playerStop() {
        Task stop = new Task(GenericTasks.ID_IDLE, game.getPlayer().getID(), null);
        game.getPlayer().setNextTask(stop);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(stop);
    }
    
    public void playerAttack() {
        Task attack = new Task(GenericTasks.ID_USE_MELEE_TOWARDS_DIRECTION, game.getPlayer().getID(), new int[]{Toolkit.getDirectionNumber(game.getPlayer().getFacing())});
        game.getPlayer().setNextTask(attack);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(attack);
    }
    
    public void playerMove(Direction direction) {
        Task move = new Task(GenericTasks.ID_MOVE_TOWARDS_DIRECTION, game.getPlayer().getID(), new int[]{Toolkit.getDirectionNumber(direction)});
        game.getPlayer().setNextTask(move);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(move);
    }
    
    public void playerMove(double xTarget, double yTarget) {
        /*
        Direction d = Toolkit.getDirection(game.getPlayer().getCenterXPos(), game.getPlayer().getCenterYPos(), xTarget, yTarget);
        playerMove(d);
        */
        Task move = new Task(GenericTasks.ID_MOVE_TOWARDS_COORDINATES, game.getPlayer().getID(), new int[]{(int)xTarget, (int)yTarget});
        game.getPlayer().setNextTask(move);
        
    }
    
    public void playerAttackMove(double xTarget, double yTarget) {
        MapObject mob = game.getCurrentLocation().getMobAtLocation(xTarget, yTarget);
        if (mob == null) {
            playerMove(xTarget, yTarget);
        } else {
            double distance = Toolkit.distance(game.getPlayer().getCenterXPos(), game.getPlayer().getCenterYPos(), mob.getCenterXPos(), mob.getCenterYPos());
            if ((game.getPlayer().getWidth()/2 + mob.getWidth()/2) + game.getPlayer().getAttackRange() > distance) {
                //Player is in range to attack
                
            } else {
                playerMove(xTarget, yTarget);
            }
        }
    }
    
    public void teleportPlayer(double xCoor, double yCoor) {
        if (Mists.gameMode != GameMode.CLIENT) this.game.getCurrentLocation().getPlayer().setCenterPosition(xCoor, yCoor);
    }
    
    public void toggleInventory(InventoryPanel inv) {
        if (game.currentState.removeUIComponent(inv.getName())){
            //Inventory panel was there and was removed
            Mists.logger.info("Inventory panel closed");
        }
        else {
            game.currentState.addUIComponent(inv); 
            Mists.logger.log(Level.INFO, "Inventory size: {0} items, capacity {1}", new Object[]{inv.getInventory().getSize(), inv.getInventory().getCapacity()});
        }
    }

    public void useTrigger(int triggersourceID, int triggerID) {
        //Trigger t = triggersource.getTriggers()[triggerID];
        Task task = new Task(GenericTasks.ID_USE_TRIGGER, game.getPlayer().getID(), new int[]{triggersourceID, triggerID});
        game.getPlayer().setNextTask(task);
        Mists.logger.log(Level.INFO, "Set players next task to: USE_TRIGGER {0} : {1}", new Object[]{triggersourceID, triggerID});
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(task);
    }
    
    public void takeItem(MapObject targetInventory, int slot) {
        Task take = new Task(GenericTasks.ID_TAKE_ITEM, game.getPlayer().getID(), new int[]{targetInventory.getID(), slot});
        game.getPlayer().setNextTask(take);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(take);
    }
    
    public void dropItem(int slot) {
        Task drop = new Task(GenericTasks.ID_DROP_ITEM, game.getPlayer().getID(), new int[]{slot});
        game.getPlayer().setNextTask(drop);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(drop);
    }
    
    public void equipItem(int slot) {
        Task equip = new Task(GenericTasks.ID_EQUIP_ITEM, game.getPlayer().getID(), new int[]{slot});
        game.getPlayer().setNextTask(equip);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(equip);
    }
    
    public void useItem(Inventory inventory, int slot) {
        Task use = new Task(GenericTasks.ID_USE_ITEM, game.getPlayer().getID(), new int[]{slot});
        game.getPlayer().setNextTask(use);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(use);
    }
    
    
    
    //-------- Mob creation ------
    
    public void addCreature(String mobTemplate) {
        int mobID;
        try {
            mobID = Integer.parseInt(mobTemplate);
        } catch (NumberFormatException e) {
            //ParseInt failed, so we probably got a template name instead
            Creature mob = Mists.creatureLibrary.getTemplate(mobTemplate);
            if (mob!=null) mobID = mob.getTemplateID();
            else mobID = -1;
        }
        addCreature(mobID);
    }
    
    public void createItemPile(Item item, double xCoor, double yCoor) {
        ItemContainer itemPile = new ItemContainer(item.getName(), new Sprite(Mists.graphLibrary.getImage("blank")));
        itemPile.addItem(item);
        itemPile.setRenderContent(true);
        this.currentLoc().addMapObject(itemPile, xCoor, yCoor);
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
     * @param baseID the baseID of the creature to create
     */
    public void addCreature(int baseID) {
        if (baseID < 0 ) {
            Mists.logger.warning("Tried to create creature with templateID of "+baseID+", aborted!");
            return;
        }
        Point p = MouseInfo.getPointerInfo().getLocation();
        double x = p.x - Mists.primaryStage.getX();
        double y = p.y - Mists.primaryStage.getY();
        Random rnd = new Random();
        int startX = rnd.nextInt(1);
        int startY = rnd.nextInt(1);
        Mists.logger.log(Level.INFO, "Creating monster from sprite sheet position {0},{1} at coordinates {2}+{3}x{4}+{5}", new Object[]{startX, startY, x, game.getCurrentLocation().getLastxOffset(), y, game.getCurrentLocation().getLastyOffset()});
        Creature monster = Mists.creatureLibrary.create(baseID);
        if (monster == null) monster = Mists.creatureLibrary.create("Worm");
        game.getCurrentLocation().addMapObject(monster, x+game.getCurrentLocation().getLastxOffset(), y+game.getCurrentLocation().getLastyOffset());   
    }
    
    public void addBlob() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        double x = p.x - Mists.primaryStage.getX();
        double y = p.y - Mists.primaryStage.getY();
        Creature monster = new Creature("Blob", new ImageView("/images/blob.png"), 3, 0, 0, 84, 84);
        game.getCurrentLocation().addMapObject(monster, x+game.getCurrentLocation().getLastxOffset(), y+game.getCurrentLocation().getLastyOffset());   
    }
    
    //----------Location creation------------
    
    public void createLoc(String location) {
        if ("testmap".equals(location)) {
            //TODO: Rewrite this to adhere to LocationLibrary
            //game.moveToState(0);
            Location newlocation = new Location ("ConsoleLoc", new TileMap("/mapdata/pathfinder_test.map"));
            int locationID = game.takeNextFreeLocationID();
            game.addLocation(locationID, newlocation);
            game.moveToLocation(locationID, null);
            ((LocationState)game.getGameState(Game.LOCATION)).closeConsole();
            //game.moveToState(1);
        }
        
    }
    
}
