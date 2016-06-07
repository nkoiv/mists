/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.world.Location;

/**
 *
 * @author nikok
 */
public class TextPopUpTrigger implements Trigger {
	private int targetID;
    private MapObject target;
    private String popupText;
    
    public TextPopUpTrigger (MapObject target, String popupText) {
        this.target = target;
        this.popupText = popupText;
    }
    
    private void updateTarget(Location loc) {
    	this.target = loc.getMapObject(targetID);
    }

    @Override
    public String getDescription() {
    	if (target == null) updateTarget(Mists.MistsGame.getCurrentLocation());
        return "Trigget to popup "+popupText+" on "+target.getName();
    }

    @Override
    public boolean toggle(MapObject toggler) {
    	if (target == null) updateTarget(toggler.getLocation());
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
    	if (target == null) updateTarget(Mists.MistsGame.getCurrentLocation());
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
