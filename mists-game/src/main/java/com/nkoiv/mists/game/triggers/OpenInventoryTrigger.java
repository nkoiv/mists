/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
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
       ic.addTextPopup("That is empty");
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
