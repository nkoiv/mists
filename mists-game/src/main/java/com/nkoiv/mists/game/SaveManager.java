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
import com.nkoiv.mists.game.quests.Quest;
import com.nkoiv.mists.game.quests.QuestManager;
import com.nkoiv.mists.game.quests.QuestTask;
import com.nkoiv.mists.game.triggers.*;
import com.nkoiv.mists.game.world.TileMap;
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
        
        //Save Player
        kryo.writeClassAndObject(output, game.getPlayer());
        //Save QuestManager
        kryo.writeClassAndObject(output, game.questManager);
        //Save DialogueManager
        kryo.writeClassAndObject(output, game.dialogueManager);
        //Save puzzles (?)
        
        output.close();
        pool.release(kryo);
    }
    
    
    public static void loadGame(Game game, String savefile) throws FileNotFoundException {
    	Kryo kryo = SaveManager.pool.borrow();
    	Input input = new Input(new FileInputStream(savefile));
    	
    	input.close();
    	pool.release(kryo);
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
