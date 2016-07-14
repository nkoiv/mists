/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gamestate.LocationState;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * ActionButtons (currently) extend the Test button,
 * with the addition of being mapped to an action of a player.
 * When the button is pressed, the action is used via PlayerCharacter.useAction() -method
 * @author nikok
 */
public class ActionButton extends TextButton {
    private PlayerCharacter player;
    private String actionName;
    private boolean quickCast = false;
    
    public ActionButton (PlayerCharacter player, String actionName, double width, double height) {
        this(player, actionName, width, height, 0, 0);
    }
    
    public ActionButton(PlayerCharacter player, String actionName, double width, double height, double xPosition, double yPosition) {
        super(actionName, width, height, xPosition, yPosition);
        this.player = player;
        this.actionName = actionName;
    }
    
    public void setQuickCast(boolean quickCast) {
    	this.quickCast = quickCast;
    }
    
    public boolean isQuickCast() {
    	return this.quickCast;
    }

    @Override
    protected void renderBackground(GraphicsContext gc, double xPosition, double yPosition) {
        gc.setGlobalAlpha(background.getOpacity());
        gc.setFill(background.getFill());
        gc.fillRect(xPosition, yPosition, background.getWidth(), background.getHeight());
        Action a = player.getAvailableActions().get(actionName);
        if (a==null) return;
        if (a.isOnCooldown()) {
            double cdPortion = a.getRemainingCooldown() / a.getCooldown();
            gc.setFill(Color.DARKRED);
            gc.fillRect(xPosition, yPosition, background.getWidth(), background.getHeight()*cdPortion);
        }
    }
    
    @Override
    public void buttonPress() {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        if (player.getAvailableActions().get(actionName) == null) {
        	Mists.logger.warning("Action button "+this.getName()+" was tied to an Action the player doesn't have ("+actionName+")");
        	return;
        }
        if (player.getAvailableActions().get(actionName).isOnCooldown()) {
        	Mists.logger.info("Tried to activate "+actionName+" by ActionButton, but it was on cooldown");
        	return;
        }
        if (quickCast) {
        	//If we're quickcasting, just fire skill at current mouse position
	        Point p = MouseInfo.getPointerInfo().getLocation();
	        double mouseX = p.getX() - Mists.primaryStage.getX();
	        double mouseY = p.getY() - Mists.primaryStage.getY();
	        //Mists.logger.info("Using action "+actionName+" towards "+mouseX+player.getLocation().getLastxOffset()+"x"+ mouseY+player.getLocation().getLastyOffset());
	        player.useAction(actionName, mouseX+player.getLocation().getLastxOffset(), mouseY+player.getLocation().getLastyOffset());
        } else {
        	//If we're not quickcasting, the skill target needs to be verified with mouse click
        	if (Mists.MistsGame.currentState instanceof LocationState) ((LocationState)Mists.MistsGame.currentState).castWithMouse(actionName);
        }
        
    }
    
    
}
