/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.world.Location;

import java.util.logging.Level;

/**
* LootTrigger, when toggled, gives the player the
* top item from the pile.
* @author nikok
*/
public class LootTrigger implements Trigger {
	private int icID; 
	private ItemContainer ic;

   public LootTrigger(ItemContainer itemContainer) {
       this.ic = itemContainer;
   }

   @Override
   public boolean toggle(MapObject toggler) {
	   if (ic == null) updateItemContainer(toggler.getLocation());
       Mists.logger.info("LootTrigger toggled by "+toggler.getName());
       if (!(toggler instanceof Creature)) {
           Mists.logger.log(Level.INFO, "{0} tried to toggle {1} but is not a creature", new Object[]{toggler.getName(), ic.getName()});
           return false;
       }
       //int topItemID = ic.topItemID();
       //((Creature)toggler).setNextTask(new Task(GenericTasks.ID_TAKE_ITEM, toggler.getID(), new int[]{ic.getID(), topItemID}));

       if (!((Creature)toggler).getInventory().isFull()) {
           ((Creature)toggler).addItem(ic.takeTopItem());
           return true;
       }
       return false;

   }
   
   private void updateItemContainer(Location loc) {
	   MapObject mob = loc.getMapObject(icID);
	   if (mob instanceof ItemContainer) this.ic = (ItemContainer)loc.getMapObject(icID);
   }

   @Override
   public void setTarget(MapObject ic) {
       if (ic instanceof ItemContainer) this.ic = (ItemContainer)ic;
   }

   @Override
   public MapObject getTarget() {
	   if (ic == null) updateItemContainer(Mists.MistsGame.getCurrentLocation());
       return this.ic;
   }

   @Override
   public String getDescription() {
	   if (ic == null) updateItemContainer(Mists.MistsGame.getCurrentLocation());
       Item i = this.ic.peekTopItem();
       if (i == null) return "Empty";
       else return ("Take "+i.getName());
   }

   @Override
   public LootTrigger createFromTemplate() {
       LootTrigger lt = new LootTrigger(this.ic);
       lt.icID = this.icID;
       return lt;
   }

@Override
public void write(Kryo kryo, Output output) {
	if (this.ic != null) this.icID = ic.getID();
	output.writeInt(this.icID);
}

@Override
public void read(Kryo kryo, Input input) {
	this.icID = input.readInt();
	
}

}
