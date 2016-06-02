/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
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
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.ui.InventoryPanel;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;


/**
 * LocationControls are a layer that relays commands to the location
 * The idea behind separate LocationControls is that externally loaded
 * config / world data can utilize these via scripting.
 * @author nikok
 */
public class LocationControls {
    
    private final Game game;
    private final TreeMap<LocationCommand, KeyBinding> commands;
    //private LocationServer server;
    //private LocationClient client;
    
    public LocationControls(Game game) {
        this.game = game;
        this.commands = new TreeMap<>();
    }
    
    private Location currentLoc() {
        return this.game.getCurrentLocation();
    }
    
    public Game getGame() {
        return this.game;
    }
    
    /**
     * Execute a command according to the given key presses and releases
     * @param pressedKeys List of keys Player has pressed down
     * @param releasedKeys List of keys Player just released
     * @return true if some command was executed
     */
    public boolean executeKeybind(ArrayList<KeyCode> pressedKeys, ArrayList<KeyCode> releasedKeys) {
        //Mists.logger.info("Executing keybind on "+kc);
        boolean commandExecuted = false;
        for (LocationCommand lc : this.commands.keySet()) {
            KeyBinding kb = this.commands.get(lc);
            if (kb.matchingKeyPress(pressedKeys, releasedKeys))  {
                kb.execute();
                commandExecuted = true;
            }
        }
        return commandExecuted;
    }
    
    /**
     * Initialize the default keybindings.
     * Unless this or some other keybinding initialization is done,
     * no keybs are bound in Location
     */
    public void initializeDefaultKeybinds() {
        Mists.logger.info("Loading default keybinds...");
        //UI buttons
        this.commands.put(LocationCommand.TOGGLE_INVENTORY, new ToggleInventoryCommand(this, new KeyCode[]{KeyCode.I}));
        this.commands.put(LocationCommand.TOGGLE_QUESTPANEL, new ToggleQuestPanelCommand(this, new KeyCode[]{KeyCode.L}));
        Mists.logger.info("UI buttons done");
        //ContextAction
        this.commands.put(LocationCommand.CONTEXT_TOGGLE, new ContextActionToggleCommand(this, new KeyCode[]{KeyCode.E}));
        this.commands.put(LocationCommand.CONTEXT_SELECT, new ContextActionSelectCommand(this, new KeyCode[]{KeyCode.Q}));
        
        //ActionBar buttons
        this.commands.put(LocationCommand.ACTIONBAR1, new ActionBarButtonCommand(this, 0, new KeyCode[]{KeyCode.DIGIT1}));
        this.commands.put(LocationCommand.ACTIONBAR2, new ActionBarButtonCommand(this, 1, new KeyCode[]{KeyCode.DIGIT2}));
        this.commands.put(LocationCommand.ACTIONBAR3, new ActionBarButtonCommand(this, 2, new KeyCode[]{KeyCode.DIGIT3}));
        this.commands.put(LocationCommand.ACTIONBAR4, new ActionBarButtonCommand(this, 3, new KeyCode[]{KeyCode.DIGIT4}));
        this.commands.put(LocationCommand.ACTIONBAR5, new ActionBarButtonCommand(this, 4, new KeyCode[]{KeyCode.DIGIT5}));

        //Movement (arrows)
        this.commands.put(LocationCommand.MOVE_UP, new PlayerMoveCommand(this, Direction.UP, new KeyCode[]{KeyCode.UP}));
        this.commands.put(LocationCommand.MOVE_DOWN, new PlayerMoveCommand(this, Direction.DOWN, new KeyCode[]{KeyCode.DOWN}));
        this.commands.put(LocationCommand.MOVE_LEFT, new PlayerMoveCommand(this, Direction.LEFT, new KeyCode[]{KeyCode.LEFT}));
        this.commands.put(LocationCommand.MOVE_RIGHT, new PlayerMoveCommand(this, Direction.RIGHT, new KeyCode[]{KeyCode.RIGHT}));
        this.commands.put(LocationCommand.MOVE_UPRIGHT, new PlayerMoveCommand(this, Direction.UPRIGHT, new KeyCode[]{KeyCode.UP, KeyCode.RIGHT}));
        this.commands.put(LocationCommand.MOVE_DOWNRIGHT, new PlayerMoveCommand(this, Direction.DOWNRIGHT, new KeyCode[]{KeyCode.DOWN, KeyCode.RIGHT}));
        this.commands.put(LocationCommand.MOVE_DOWNLEFT, new PlayerMoveCommand(this, Direction.DOWNLEFT, new KeyCode[]{KeyCode.LEFT, KeyCode.DOWN}));
        this.commands.put(LocationCommand.MOVE_UPLEFT, new PlayerMoveCommand(this, Direction.UPLEFT, new KeyCode[]{KeyCode.LEFT, KeyCode.UP}));
        //Movement (wasd)
        this.commands.get(LocationCommand.MOVE_UP).secondaryKey = new KeyCode[]{KeyCode.W};
        this.commands.get(LocationCommand.MOVE_DOWN).secondaryKey = new KeyCode[]{KeyCode.S};
        this.commands.get(LocationCommand.MOVE_LEFT).secondaryKey = new KeyCode[]{KeyCode.A};
        this.commands.get(LocationCommand.MOVE_RIGHT).secondaryKey = new KeyCode[]{KeyCode.D};
        this.commands.get(LocationCommand.MOVE_UPRIGHT).secondaryKey = new KeyCode[]{KeyCode.W, KeyCode.D};
        this.commands.get(LocationCommand.MOVE_DOWNRIGHT).secondaryKey = new KeyCode[]{KeyCode.S, KeyCode.D};
        this.commands.get(LocationCommand.MOVE_DOWNLEFT).secondaryKey = new KeyCode[]{KeyCode.S, KeyCode.A};
        this.commands.get(LocationCommand.MOVE_UPLEFT).secondaryKey = new KeyCode[]{KeyCode.W, KeyCode.A};
        
        //Other
        this.commands.put(LocationCommand.DEFAULT_ATTACK, new PlayerDefaultAttackCommand(this, new KeyCode[]{KeyCode.SPACE}));
        
    }
    
    /**
     * Assign the given key to the given LocationCommand
     * @param lc Command to set the binding to
     * @param keybinding KeyCode(s) to bind to the command
     * @param primary Is this Primary binding or not - False means secondary binding
     * @return true if command was successfully bound
     */
    public boolean setKeyBinding(LocationCommand lc, KeyCode[] keybinding, boolean primary) {
        if (this.commands.containsKey(lc) && primary) {
            this.commands.get(lc).setPrimaryKey(keybinding);
            return true;
        } else if (this.commands.containsKey(lc) && !primary) {
            this.commands.get(lc).setSecondaryKey(keybinding);
            return true;
        }
        return false;
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
    
    public void togglePlayerInventoryPanel() {
        LocationState LS = (LocationState)this.game.currentState;
        LS.togglePlayerInventoryPanel();
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
    
    public void playerAttack(double xTarget, double yTarget) {
        Task attack = new Task(GenericTasks.ID_USE_MELEE_TOWARDS_COORDINATES, game.getPlayer().getID(), new double[]{(int)xTarget, (int)yTarget});
        game.getPlayer().setNextTask(attack);
    }
    
    public void playerAttack() {
        Task attack = new Task(GenericTasks.ID_USE_MELEE_TOWARDS_DIRECTION, game.getPlayer().getID(), new double[]{Toolkit.getDirectionNumber(game.getPlayer().getFacing())});
        game.getPlayer().setNextTask(attack);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(attack);
    }
    
    public void playerMove(Direction direction) {
        Task move = new Task(GenericTasks.ID_MOVE_TOWARDS_DIRECTION, game.getPlayer().getID(), new double[]{Toolkit.getDirectionNumber(direction)});
        game.getPlayer().setNextTask(move);
    }
    
    public void playerMove(double xTarget, double yTarget) {
        Task move = new Task(GenericTasks.ID_MOVE_TOWARDS_COORDINATES, game.getPlayer().getID(), new double[]{(int)xTarget, (int)yTarget});
        game.getPlayer().setNextTask(move);   
    }
    
    public void playerDash(Direction direction) {
        Task move = new Task(GenericTasks.ID_DASH_TOWARDS_DIRECTION, game.getPlayer().getID(), new double[]{Toolkit.getDirectionNumber(direction)});
        game.getPlayer().setNextTask(move);
    }
    
    public void playerDash(double xTarget, double yTarget) {
        Task move = new Task(GenericTasks.ID_DASH_TOWARDS_COORDINATES, game.getPlayer().getID(), new double[]{(int)xTarget, (int)yTarget});
        game.getPlayer().setNextTask(move);   
    }
    
    public void playerAttackMove(double xTarget, double yTarget) {
        MapObject mob = game.getCurrentLocation().getMobAtLocation(xTarget, yTarget);
        if (mob == null) {
            playerMove(xTarget, yTarget);
        } else if (mob instanceof Creature) {
            //Mists.logger.info("Trying to attack "+mob.getName());
            double distance = Toolkit.distance(game.getPlayer().getCenterXPos(), game.getPlayer().getCenterYPos(), mob.getCenterXPos(), mob.getCenterYPos());
            if (((game.getPlayer().getWidth()/2 + mob.getWidth()/2) + game.getPlayer().getAttackRange()) > distance) {
                //Player is in range to attack
                playerAttack();
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
        Task task = new Task(GenericTasks.ID_USE_TRIGGER, game.getPlayer().getID(), new double[]{triggersourceID, triggerID});
        game.getPlayer().setNextTask(task);
        Mists.logger.log(Level.INFO, "Set players next task to: USE_TRIGGER {0} : {1}", new Object[]{triggersourceID, triggerID});
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(task);
    }
    
    public void takeItem(MapObject targetInventory, int slot) {
        Task take = new Task(GenericTasks.ID_TAKE_ITEM, game.getPlayer().getID(), new double[]{targetInventory.getID(), slot});
        game.getPlayer().setNextTask(take);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(take);
    }
    
    public void dropItem(int slot) {
        Task drop = new Task(GenericTasks.ID_DROP_ITEM, game.getPlayer().getID(), new double[]{slot});
        game.getPlayer().setNextTask(drop);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(drop);
    }
    
    public void equipItem(int slot) {
        Task equip = new Task(GenericTasks.ID_EQUIP_ITEM, game.getPlayer().getID(), new double[]{slot});
        game.getPlayer().setNextTask(equip);
        //if (this.gameMode == GameMode.CLIENT) this.client.addOutgoingUpdate(equip);
    }
    
    public void useItem(Inventory inventory, int slot) {
        Task use = new Task(GenericTasks.ID_USE_ITEM, game.getPlayer().getID(), new double[]{slot});
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
