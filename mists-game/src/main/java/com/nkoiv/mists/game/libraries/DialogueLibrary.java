/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.dialogue.Card;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.dialogue.Link;
import com.nkoiv.mists.game.dialogue.linktriggers.LinkChangeDialogueOnOwnerTrigger;
import com.nkoiv.mists.game.dialogue.linktriggers.LinkGiveItemToTalkerTrigger;
import com.nkoiv.mists.game.dialogue.linktriggers.LinkTrigger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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
        if (lib.containsKey(dialogueID)) {
            /*
            //Logging for testing, ignore unless debugging
            Mists.logger.info("Found dialogue with ID "+dialogueID);
            int triggerCount = 0;
            Dialogue d = lib.get(dialogueID).createFromTemplate();
            int currentCard = 1;
            while (true) {
                d.setCurrentCard(currentCard);
                if (d.getCurrentCard() == null) break;
                for (Link l : d.getCurrentCard().getLinks()) {
                    for (LinkTrigger lt : l.getTriggers()) {
                        triggerCount++;
                    }
                }
                currentCard++;
            }
            Mists.logger.info("Returning Dialogue with "+triggerCount+" triggers");
            */
            return lib.get(dialogueID).createFromTemplate();
        }
        else return null;
    }
        
    public static Dialogue generateDialogueFromYAML(Map object) {
        Mists.logger.info("Generating dialogue...");
        Dialogue d = new Dialogue();
        //Set cardSet = (Set)object.get("cards");
        ArrayList cardList = (ArrayList)object.get("cards");
        Mists.logger.log(Level.INFO, "{0} cards in dialogue", cardList.size());
        Mists.logger.info(cardList.toString());
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
        Mists.logger.info("Generating link from linkdata: "+linkData.toString());
        String linkText = (String)linkData.get("linkText");
        int linkDestination = Integer.parseInt((String)linkData.get("linkDestination"));
        Link l = new Link(linkText, linkDestination);
        if (linkData.keySet().contains("triggers")) {
            Mists.logger.info("Link contains triggers!");
            ArrayList<LinkTrigger> lts = generateLinkTriggersFromYAML(linkData);
            for (LinkTrigger trigger : lts) {
                Mists.logger.info("Adding linktrigger to the link");
                l.addTrigger(trigger);
            }
        }
        Mists.logger.info("Generated link with "+l.getTriggers().size()+" triggers");
        return l;
    }
    
    private static ArrayList<LinkTrigger> generateLinkTriggersFromYAML(Map linkData) {
        ArrayList<LinkTrigger> linkTriggers = new ArrayList<>();
        if (linkData.containsKey("triggers")) {
            ArrayList triggers = (ArrayList)linkData.get("triggers");
            for (Object triggerData : triggers) {
                LinkTrigger t = generateTriggerFromYAML((Map)triggerData);
                if (t!=null)linkTriggers.add(t);
            }
        }
        return linkTriggers;
    }
    
    private static LinkTrigger generateTriggerFromYAML(Map triggerData) {
        Mists.logger.info("Generating linktrigger from triggerdata");
        String triggerType = (String)triggerData.get("triggerType");
        LinkTrigger lt = null;
        switch(triggerType) {
            case "GiveItem": 
                int itemID = Integer.parseInt((String)triggerData.get("itemID"));
                int itemCount = 1;
                if (triggerData.containsKey("amount")) itemCount = Integer.parseInt((String)triggerData.get("amount"));
                lt = new LinkGiveItemToTalkerTrigger(itemID, itemCount);
                Mists.logger.info("GiveItem trigger generated");
                break;
            case "ChangeDialogue": 
                int targetDialogueID = -1;
                if (triggerData.containsKey("dialogueID")) targetDialogueID = Integer.parseInt((String)triggerData.get("dialogueID"));
                lt = new LinkChangeDialogueOnOwnerTrigger(targetDialogueID);
                Mists.logger.info("ChangeDialogue trigger generated");
                break;
            default: break;
        }
        return lt;
    }
    
    private static Link generateEndOfConversationLink() {
        Link l = new Link("[End conversation]", -1);
        return l;
    }
    
}
