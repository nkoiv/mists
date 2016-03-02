/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.dialogue.Card;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.dialogue.Link;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * DialogueLibrary stores the game dialogues in "unlocalized" state.
 * This means that variables such as PLAYER_NAME or LOCATION_NAME
 * on the cards will not locked down to game-specific formats.
 * As such the Dialogue(and the cards within) maybe be reused to
 * some extent.
 * @author nikok
 */
public class DialogueLibrary {
    
    private HashMap<Integer, Dialogue> lib;
    
    public DialogueLibrary() {
        this.lib = new HashMap<>();
    }
    
    public void addTemplate(Dialogue d, int dialogueID) {
        lib.put(dialogueID, d);
    }
    
    public Dialogue getTemplate(int dialogueID) {
        return lib.get(dialogueID);
    }
    
    public Dialogue getDialogue(int dialogueID) {
        return lib.get(dialogueID).createFromTemplate();
    }
        
    public static Dialogue generateDialogueFromYAML(Map object) {
        //Mists.logger.info("Generating dialogue...");
        Dialogue d = new Dialogue();
        //Set cardSet = (Set)object.get("cards");
        ArrayList cardList = (ArrayList)object.get("cards");
        //Mists.logger.log(Level.INFO, "{0} cards in dialogue", cardList.size());
        //Mists.logger.info(cardList.toString());
        for (Object c : cardList) {
            HashMap cardData = (HashMap)c;
            int cardID = Integer.parseInt((String)cardData.get("id"));
            //Mists.logger.info("Generating card" +cardID);
            Card card = generateCardFromYAML(cardData);
            d.addCard(cardID, card);
        }
        return d;
    }
    
    private static Card generateCardFromYAML(Map cardData) {
        int cardID = Integer.parseInt((String)cardData.get("id"));
        String cardText = (String)cardData.get("text");
        ArrayList<Link> cardLinks = new ArrayList<>();
        //Mists.logger.log(Level.INFO, "Creating card {0} : {1}", new Object[]{cardID, cardText});
        if (cardData.keySet().contains("links")) {
            ArrayList linkList = (ArrayList)cardData.get("links");
            for (Object linkData : linkList) {
                Link l = generateLinkFromYAML((Map)linkData);
                if (l!=null)cardLinks.add(l);
            }
        }
        
        //Generate the card
        //Mists.logger.log(Level.INFO, "Generating card: [{0}] ''{1}'', {2} links", new Object[]{cardID, cardText, cardLinks.size()});
        Card card = new Card(cardText);
        if (!cardLinks.isEmpty()) {
            for (Link l : cardLinks) {
                card.addLink(l);
            }
        } else {
            //No links on card, add EndOfConversation -link
            card.addLink(generateEndOfConversationLink());
        }
        
        return card;
    }
    
    private static Link generateLinkFromYAML(Map linkData) {
        //TODO: utilize link requirements
        String linkText = (String)linkData.get("linkText");
        int linkDestination = Integer.parseInt((String)linkData.get("linkDestination"));
        Link l = new Link(linkText, linkDestination);
        return l;
    }
    
    private static Link generateEndOfConversationLink() {
        Link l = new Link("[End conversation]", -1);
        return l;
    }
    
}
