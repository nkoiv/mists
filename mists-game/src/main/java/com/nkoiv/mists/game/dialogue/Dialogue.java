/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.dialogue;

import java.util.HashMap;

/**
 * Dialogue is a conversation between participants.
 * Generally Dialogue is owned by one party (Non-PlayerCharacter) and
 * participated by another party (PlayerCharacter).
 * Dialogue is traversed by choosing Dialogue options, which either
 * lead to further Dialogue or end it.
 * 
 * @author nikok
 */
public class Dialogue {
    private Card startingCard;
    private Card currentCard;
    private HashMap<Integer, Card> cards;
    
    
}
