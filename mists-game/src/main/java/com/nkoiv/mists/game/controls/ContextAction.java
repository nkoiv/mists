/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.ArrayList;

/**
 * ContextAction looks around a player and shows up what can
 * be done at a given location. When next to a closed door, player
 * can open it - opened door can be closed. 
 * 
 * @author nikok
 */
public class ContextAction {
    private ArrayList<Action> availableActions;
    MapObject actor; //Usually the player?
    
    private void refreshNearbyObjects() {
        
    }
    
}