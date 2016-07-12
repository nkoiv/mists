package com.nkoiv.mists.game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.dialogue.DialogueManager;
import com.nkoiv.mists.game.gameobject.CircuitTile;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.LocationDoorway;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.TriggerPlate;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.gameobject.Water;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.gamestate.LoadingScreen;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.items.Potion;
import com.nkoiv.mists.game.items.Weapon;
import com.nkoiv.mists.game.quests.Quest;
import com.nkoiv.mists.game.quests.QuestManager;
import com.nkoiv.mists.game.quests.QuestTask;
import com.nkoiv.mists.game.triggers.*;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.util.Flags;
import com.nkoiv.mists.game.world.worldmap.LocationNode;
import com.nkoiv.mists.game.world.worldmap.MapNode;
import com.nkoiv.mists.game.world.worldmap.WorldMap;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SaveManager handles saving and loading games.
 * 
 * @author nkoiv
 *
 */
public class SaveManager {
	
    private static String testfile = "testfile.sav";


    private static final KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            // configure kryo instance, customize settings
            //Register mobs
            kryo.register(Flags.class, 1);
            kryo.register(MapObject.class, 2);
            kryo.register(Creature.class, 3);
            kryo.register(PlayerCharacter.class, 4);
            kryo.register(Structure.class, 10);
            kryo.register(Wall.class, 11);
            kryo.register(Water.class, 12);
            kryo.register(Door.class, 13);
            kryo.register(ItemContainer.class, 14);
            kryo.register(WorldMapEntrance.class, 15);
            kryo.register(LocationDoorway.class, 16);
            kryo.register(TriggerPlate.class, 20);
            kryo.register(PuzzleTile.class, 21);
            kryo.register(CircuitTile.class, 22);
            //Register triggers
            kryo.register(DialogueTrigger.class, 40);
            kryo.register(DoorTrigger.class, 41);
            kryo.register(FlaggerTrigger.class, 42);
            kryo.register(FreezeTilesTrigger.class, 43);
            kryo.register(InsertMobTrigger.class, 44);
            kryo.register(KillTrigger.class, 45);
            kryo.register(LocationEntranceTrigger.class, 46);
            kryo.register(LootTrigger.class, 47);
            kryo.register(OpenInventoryTrigger.class, 48);
            kryo.register(PuzzleTrigger.class, 49);
            kryo.register(RotateTrigger.class, 50);
            kryo.register(TextPopUpTrigger.class, 51);
            kryo.register(ToggleTrigger.class, 52);
            kryo.register(WorldMapEntranceTrigger.class, 53);
            //Register inventories and items
            kryo.register(Inventory.class, 70);
            kryo.register(Item.class, 71);
            kryo.register(Weapon.class, 72);
            kryo.register(Potion.class, 73);
            //Register locations and maps
            kryo.register(Location.class, 90);
            kryo.register(TileMap.class, 91);
            kryo.register(WorldMap.class, 96);
            kryo.register(MapNode.class, 97);
            kryo.register(LocationNode.class, 98);
            //Register quests and dialogue
            kryo.register(Dialogue.class, 100);
            kryo.register(DialogueManager.class, 101);
            kryo.register(Quest.class, 105);
            kryo.register(QuestManager.class, 106);
            kryo.register(QuestTask.class, 107);
            return kryo;
        }
    };

    public static final KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    /**
     * Testfuction for saving the game to default testfile
     */
    public static void saveGame() {
        try {
            saveGame(Mists.MistsGame, testfile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaveManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Testfunction for loading the game from default testfile
     */
    public static void loadGame() {
        try {
            loadGame(Mists.MistsGame, testfile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SaveManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void saveGame(Game game, String savefile) throws FileNotFoundException {
        Kryo kryo = SaveManager.pool.borrow();
        Output output = new Output(new FileOutputStream(savefile));
        //TODO: Saving of the game!
        //Save the game version
        output.writeString(Mists.gameVersion);
        //Save the current state of the game
        output.writeInt(game.currentState.getStateID());
        int locationID = -1;
        if (game.getCurrentLocation() != null) locationID = game.getCurrentLocation().getBaseID();
        output.writeInt(locationID);
        //Save Player
        
        output.writeString("<PLAYERDATA>");
        kryo.writeClassAndObject(output, game.getPlayer());
        
        //Save QuestManager
        output.writeString("<QUESTDATA>");
        kryo.writeClassAndObject(output, game.questManager);
        //Save DialogueManager
        output.writeString("<DIALOGUEDATA>");
        kryo.writeClassAndObject(output, game.dialogueManager);
        //Save generated Locations 
        output.writeString("<LOCATIONDATA>");
        saveGeneratedLocations(game, kryo, output);
        //Save worldmap
        output.writeString("<WORLDMAPDATA>");
        kryo.writeClassAndObject(output, game.getCurrentWorldMap());
    	//Close the output stream and release the kryo
        output.close();
        pool.release(kryo);
    }
    
    
    /**
     * Serialize the generated Locations and push them into output with Kryo
     * @param game Game to take Generated Locations from
     * @param kryo Kryo serializer
     * @param output For pushing the serialized object
     */
    private static void saveGeneratedLocations(Game game, Kryo kryo, Output output) {
    	int generatedLocationCount = game.getGeneratedLocationIDs().size();
    	output.writeInt(generatedLocationCount);
    	for (Integer id : game.getGeneratedLocationIDs()) {
    		output.writeInt(id);
    		kryo.writeClassAndObject(output, game.getLocation(id));
    	}
    }
    
    private static void loadGeneratedLocations(Game game, Kryo kryo, Input input) {
    	Mists.logger.info("Loading locations from save data");
    	int locationID;
    	Location location;
    	int locationCount = input.readInt();
    	Mists.logger.info(locationCount+" locations to load");
    	for (int i = 0; i < locationCount; i++) {
            locationID = input.readInt();
            Mists.logger.info("Loading ID: "+locationID+" ("+(i+1)+"/"+locationCount+")");
            location = (Location)kryo.readClassAndObject(input);
            location.setBaseID(locationID);
            game.setLocation(locationID, location);
            Mists.logger.info("Set "+location.getName()+" to ID "+locationID);
    	}
    }
    
    public static void loadGame(Game game, String savefile) throws FileNotFoundException {
    	//Set up a loading screen for the process
    	String check;
    	LoadingScreen loadingScreen = new LoadingScreen("Loading game data", 10);
    	game.setLoadingScreen(loadingScreen);
    	//Move to a blank gamestate to avoid mutex problems in loading up data that's being used
    	game.moveToState(Game.LOADSCREEN);
    	//Open the savefile and start loading
    	Kryo kryo = SaveManager.pool.borrow();
    	Input input = new Input(new FileInputStream(savefile));
    	//Load the game version
    	//TODO: what to do if version has changed?
    	String version = input.readString();
    	//Load the gamestateID
    	int gameStateID = input.readInt();
    	int currentLocationID = input.readInt();
    	//Load Player
    	loadingScreen.updateProgress(1, "Loading Player data");
    	
    	check = input.readString();
    	if (!"<PLAYERDATA>".equals(check)) Mists.logger.warning("Mismatch on save loading:"+check);
    	PlayerCharacter player = (PlayerCharacter)kryo.readClassAndObject(input);
    	game.setPlayer(player);
    	
    	//Load QuestManager
    	loadingScreen.updateProgress(1, "Loading Quests");
    	check = input.readString();
    	if (!"<QUESTDATA>".equals(check)) Mists.logger.warning("Mismatch on save loading:"+check);
    	QuestManager qm = (QuestManager)kryo.readClassAndObject(input);
    	game.questManager = qm;
    	//Load DialogueManager
    	loadingScreen.updateProgress(1, "Loading Dialogues");
    	check = input.readString();
    	if (!"<DIALOGUEDATA>".equals(check)) Mists.logger.warning("Mismatch on save loading:"+check);
    	DialogueManager dm = (DialogueManager)kryo.readClassAndObject(input);
    	game.dialogueManager = dm;
    	//Load generated Locations
    	loadingScreen.updateProgress(1, "Loading generated Locations");
    	check = input.readString();
    	if (!"<LOCATIONDATA>".equals(check)) Mists.logger.warning("Mismatch on save loading:"+check);
    	loadGeneratedLocations(game, kryo, input);
    	if (currentLocationID != -1) game.moveToLocation(currentLocationID, player.getXPos(), player.getYPos());
    	//Load worldmap
    	loadingScreen.updateProgress(1, "Loading worldmap");
    	check = input.readString();
    	if (!"<WORLDMAPDATA>".equals(check)) Mists.logger.warning("Mismatch on save loading:"+check);
    	WorldMap wm = (WorldMap)kryo.readClassAndObject(input);
    	wm.setPlayerCharacter(player);
    	game.moveToWorldMap(wm);
    	//Close the input stream and release the kryo
    	input.close();
    	pool.release(kryo);
    	
    	//Return the game to active state
    	game.moveToState(gameStateID);
        if (game.currentState instanceof LocationState) {
            ((LocationState)game.currentState).closeGameMenu();
        }
    	game.clearLoadingScreen();
    	game.getCurrentLocation().loading = false;
    }
    
    
    
    /**
     * Temporary method for testing Kryo serialization
     * and writing on disk
     * @param ks Object to write
     * @throws FileNotFoundException
     */
    public static void testKryoSave(KryoSerializable ks) throws FileNotFoundException {
        Kryo kryo = SaveManager.pool.borrow();
        Output output = new Output(new FileOutputStream(testfile));
        kryo.writeClassAndObject(output, ks);
        output.close();
        pool.release(kryo);
        Mists.logger.info("Saved "+ks.toString());
    }

    public static void testKryoLoad(Game game) throws FileNotFoundException {
        Kryo kryo = SaveManager.pool.borrow();
        Input input = new Input(new FileInputStream(testfile));
        PlayerCharacter o = (PlayerCharacter)kryo.readClassAndObject(input);
        input.close();
        pool.release(kryo);
        Mists.logger.info("Loaded "+o.toString());
        if (o instanceof PlayerCharacter) {
                game.setPlayer((PlayerCharacter)o);
                Mists.logger.info("Replaced old player with loaded one");
        } else {
                Mists.logger.info("Loaded object was not a player");
        }
    }

}
