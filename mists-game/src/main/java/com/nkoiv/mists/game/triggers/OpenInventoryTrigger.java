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

/**
 *
 * @author nikok
 */
public class OpenInventoryTrigger implements Trigger {

    private ItemContainer ic;

   public OpenInventoryTrigger(ItemContainer itemContainer) {
       this.ic = itemContainer;
   }

   @Override
   public boolean toggle(MapObject toggler) {
       if (!(toggler instanceof Creature)) {
           Mists.logger.info(toggler.getName()+" tried to toggle "+ic.getName()+" but is not a creature");
           return false;
       }
       ic.addTextPopup("TODO: Opening external inventories");
       //TODO: Open a window with the target inventory
       
       return true;

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
       else return ("Open "+i.getName()+" inventory");
   }

   @Override
   public OpenInventoryTrigger createFromTemplate() {
       OpenInventoryTrigger lt = new OpenInventoryTrigger(this.ic);
       return lt;
   }
    
}
