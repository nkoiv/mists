/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import java.util.logging.Level;

/**
 * ActionButtons (currently) extend the Test button,
 * with the addition of being mapped to an action of a player.
 * When the button is pressed, the action is used via PlayerCharacter.useAction() -method
 * @author nikok
 */
public class ActionButton extends TextButton {
    private PlayerCharacter player;
    private String actionName;
    
    public ActionButton (PlayerCharacter player, String actionName, double width, double height) {
        this(player, actionName, width, height, 0, 0);
    }
    
    public ActionButton(PlayerCharacter player, String actionName, double width, double height, double xPosition, double yPosition) {
        super(actionName, width, height, xPosition, yPosition);
        this.player = player;
        this.actionName = actionName;
    }

    public void buttonPress() {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        if (!player.getLocation().getTargets().isEmpty()) {
            player.useAction(actionName, player.getLocation().getTargets().get(0));
        } else {
            player.useAction(actionName);
        }
    }
    
    
}
