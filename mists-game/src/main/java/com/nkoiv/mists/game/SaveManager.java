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
import com.nkoiv.mists.game.gameobject.PlayerCharacter;

/**
 * SaveManager handles saving and loading games.
 * 
 * @author nkoiv
 *
 */
public class SaveManager {
	
	private static String testfile = "testfile";
	
	
	public static KryoFactory factory = new KryoFactory() {
		public Kryo create () {
			Kryo kryo = new Kryo();
			// configure kryo instance, customize settings
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
        kryo.writeObject(output, ks);
        output.close();
        pool.release(kryo);
        Mists.logger.info("Saved "+ks.toString());
	}
	
	public static void testKryoLoad(Game game) throws FileNotFoundException {
		Kryo kryo = SaveManager.pool.borrow();
        Input input = new Input(new FileInputStream(testfile));
        PlayerCharacter o = kryo.readObject(input, PlayerCharacter.class);
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
