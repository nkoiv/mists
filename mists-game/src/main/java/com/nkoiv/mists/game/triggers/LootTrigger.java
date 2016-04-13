/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.items.Item;
import java.util.logging.Level;

/**
* LootTrigger, when toggled, gives the player the
* top item from the pile.
* @author nikok
*/
public class LootTrigger implements Trigger {
   private ItemContainer ic;

   public LootTrigger(ItemContainer itemContainer) {
       this.ic = itemContainer;
   }

   @Override
   public boolean toggle(MapObject toggler) {
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

   @Override
   public void setTarget(MapObject ic) {
       if (ic instanceof ItemContainer) this.ic = (ItemContainer)ic;
   }

   @Override
   public MapObject getTarget() {
       return this.ic;
   }

   @Override
   public String getDescription() {
       Item i = this.ic.peekTopItem();
       if (i == null) return "Empty";
       else return ("Take "+i.getName());
   }

   @Override
   public LootTrigger createFromTemplate() {
       LootTrigger lt = new LootTrigger(this.ic);
       return lt;
   }

}
