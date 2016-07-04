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
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.gameobject.Water;
import com.nkoiv.mists.game.gamestate.LoadingScreen;
import com.nkoiv.mists.game.quests.Quest;
import com.nkoiv.mists.game.quests.QuestManager;
import com.nkoiv.mists.game.quests.QuestTask;
import com.nkoiv.mists.game.triggers.*;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;

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
            kryo.register(MapObject.class, 1);
            kryo.register(Creature.class, 2);
            kryo.register(PlayerCharacter.class, 4);
            kryo.register(Structure.class, 10);
            kryo.register(Wall.class, 11);
            kryo.register(Water.class, 12);
            kryo.register(Door.class, 13);
            //Register maps
            kryo.register(TileMap.class, 20);
            //Register quests and dialogue
            kryo.register(Dialogue.class, 30);
            kryo.register(DialogueManager.class, 31);
            kryo.register(Quest.class, 35);
            kryo.register(QuestManager.class, 36);
            kryo.register(QuestTask.class, 37);
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
            return kryo;
        }
    };

    public static final KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    public static void saveGame() {
        try {
            saveGame(Mists.MistsGame, testfile);
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
        kryo.writeClassAndObject(output, game.getPlayer());
        //Save QuestManager
        kryo.writeClassAndObject(output, game.questManager);
        //Save DialogueManager
        kryo.writeClassAndObject(output, game.dialogueManager);
        //Save generated Locations 
        saveGeneratedLocations(game, kryo, output);
        
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
    	Iterator<Integer> it = game.getGeneratedLocationIDs().iterator();
    	while (it.hasNext()) {
    		Integer locationID = it.next();
    		Location loc = game.getLocation(locationID);
    		kryo.writeObject(output, loc);
    	}
    }
    
    private static void loadGeneratedLocations(Game game, Kryo kryo, Input input) {
    	int locationID;
    	Location location;
    	int locationCount = input.readInt();
    	for (int i = 0; i < locationCount; i++) {
    		locationID = input.readInt();
    		location = kryo.readObject(input, Location.class);
    		game.setLocation(locationID, location);
    	}
    }
    
    public static void loadGame(Game game, String savefile) throws FileNotFoundException {
    	//Set up a loading screen for the process
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
    	PlayerCharacter player = (PlayerCharacter)kryo.readClassAndObject(input);
    	game.setPlayer(player);
    	//Load QuestManager
    	QuestManager qm = (QuestManager)kryo.readClassAndObject(input);
    	game.questManager = qm;
    	//Load DialogueManager
    	DialogueManager dm = (DialogueManager)kryo.readClassAndObject(input);
    	game.dialogueManager = dm;
    	//Load generated Locations
    	loadGeneratedLocations(game, kryo, input);
    	if (currentLocationID != -1) game.moveToLocation(currentLocationID, player.getXPos(), player.getYPos());
    	
    	//Close the input stream and release the kryo
    	input.close();
    	pool.release(kryo);
    	
    	//Return the game to active state
    	game.moveToState(gameStateID);
    	game.clearLoadingScreen();
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
