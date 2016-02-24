/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    public Card createFromTemplate() {
        Card c  = new Card(this.originalText);
        for (Link l : this.dialogueLinks) {
            Link ln = l.createFromTemplate();
            c.addLink(ln);
        }
        
        return c;
    }

}
