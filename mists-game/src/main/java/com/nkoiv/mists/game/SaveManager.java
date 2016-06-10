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
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.gameobject.Water;

/**
 * SaveManager handles saving and loading games.
 * 
 * @author nkoiv
 *
 */
public class SaveManager {
	
	private static String testfile = "testfile";
	
	
	private static KryoFactory factory = new KryoFactory() {
		public Kryo create () {
			Kryo kryo = new Kryo();
			// configure kryo instance, customize settings
			kryo.register(MapObject.class, 1);
			kryo.register(Creature.class, 2);
			kryo.register(Structure.class, 3);
			kryo.register(PlayerCharacter.class, 4);
			kryo.register(Wall.class, 5);
			kryo.register(Water.class, 6);
			kryo.register(Door.class, 7);
			return kryo;
		}
	};

	public static KryoPool pool = new KryoPool.Builder(factory).softReferences().build();
	
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
