/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.dialogue;

import java.util.ArrayList;

/**
 * Card is a piece of Dialogue.
 * Generally cards have text on them.
 * @author nikok
 */
public class Card extends LocalizableText {
 
    private ArrayList<Link> dialogueLinks;
    
    public Card(String dialogueText) {
        this.originalText = dialogueText;
        this.dialogueLinks = new ArrayList<>();
    }
    
    public void addLink(Link dialogueLink) {
        this.dialogueLinks.add(dialogueLink);
    }
    
    public void addLink(String linkText, int destinationCardID) {
        Link l = new Link(linkText, destinationCardID);
        this.dialogueLinks.add(l);
    }
    
    public ArrayList<Link> getLinks() {
        return this.dialogueLinks;
    }
    
    public int getLinkDestination(int linkNumber) {
        if (linkNumber < 0 || linkNumber >= this.dialogueLinks.size()) return -1;
        return this.dialogueLinks.get(linkNumber).destinationCardID;
    }
    
    public Card createFromTemplate() {
        Card c  = new Card(this.originalText);
        for (Link l : this.dialogueLinks) {
            Link ln = l.createFromTemplate();
            c.addLink(ln);
        }
        
        return c;
    }

}
