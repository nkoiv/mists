/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.dialogue.Dialogue;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nikok
 */
public class DialogueLibrary {
    
    private HashMap<Integer, Dialogue> lib;
    
    public DialogueLibrary() {
        this.lib = new HashMap<>();
    }
        
    public static void generateDialogueFromYAML(Map object) {
        
    }
    
}
