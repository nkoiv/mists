/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gamestate.LocationState;
import java.util.logging.Level;

/**
 *
 * @author nikok
 */
public class DialogueTrigger implements Trigger {
        private Dialogue dialogue;
        private MapObject owner;
        
        public DialogueTrigger(MapObject owner, Dialogue dialogue) {
            this.owner = owner;
            this.dialogue = dialogue;
        }
        
        @Override
        public String getDescription() {
            String s = "Talk with " + owner.getName();
            return s;
        }

        @Override
        public boolean toggle(MapObject toggler) {
            Mists.logger.log(Level.INFO, "Initiating dialogue between {0} and {1}", new Object[]{owner.getName(), toggler.getName()});
            dialogue.initiateDialogue(owner, toggler);
            //TODO:Fix this bubblegum thing...
            if (Mists.MistsGame.currentState instanceof LocationState) {
                ((LocationState)Mists.MistsGame.currentState).openDialogue(this.dialogue);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public MapObject getTarget() {
            return this.owner;
        }

        @Override
        public void setTarget(MapObject mob) {
            this.owner = mob;
        }

        @Override
        public Trigger createFromTemplate() {
            DialogueTrigger t = new DialogueTrigger(this.owner, this.dialogue);
            return t;
        }
        
    }
