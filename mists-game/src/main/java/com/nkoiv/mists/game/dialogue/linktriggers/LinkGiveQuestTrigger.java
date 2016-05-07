/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.dialogue.linktriggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.quests.Quest;

/**
 *
 * @author nikok
 */
public class LinkGiveQuestTrigger implements LinkTrigger {
    private int questID;

    public LinkGiveQuestTrigger(Quest quest) {
        this.questID = quest.getID();
    }
    
    public LinkGiveQuestTrigger(int questID) {
        this.questID = questID;
    }
    
    @Override
    public boolean toggle(MapObject owner, MapObject talker) {
        return Mists.MistsGame.questManager.openQuest(questID);
    }
    
    
}

