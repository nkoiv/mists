/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gamestate.LocationState;

/**
 *
 * @author nikok
 */
public class TextPopUpTrigger implements Trigger {
    private MapObject target;
    private String popupText;
    
    public TextPopUpTrigger (MapObject target, String popupText) {
        this.target = target;
        this.popupText = popupText;
    }

    @Override
    public String getDescription() {
        return "Trigget to popup "+popupText+" on "+target.getName();
    }

    @Override
    public boolean toggle(MapObject toggler) {
        if (Mists.MistsGame.currentState instanceof LocationState) {
            ((LocationState)Mists.MistsGame.currentState).addTextFloat(popupText, target);
        }
        return false;
    }

    public void setText(String popupText) {
        this.popupText = popupText;
    }
    
    public String getText() {
        return this.popupText;
    }
    
    @Override
    public MapObject getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.target = mob;
    }

    @Override
    public TextPopUpTrigger createFromTemplate() {
        TextPopUpTrigger t = new TextPopUpTrigger(this.target, this.popupText);
        return t;
    }
    
}
