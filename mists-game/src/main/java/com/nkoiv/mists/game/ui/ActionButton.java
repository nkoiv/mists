/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.Action;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import java.util.logging.Level;
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
    
    public ActionButton (PlayerCharacter player, String actionName, double width, double height) {
        this(player, actionName, width, height, 0, 0);
    }
    
    public ActionButton(PlayerCharacter player, String actionName, double width, double height, double xPosition, double yPosition) {
        super(actionName, width, height, xPosition, yPosition);
        this.player = player;
        this.actionName = actionName;
    }

    @Override
    protected void renderBackground(GraphicsContext gc, double xPosition, double yPosition) {
        gc.setGlobalAlpha(background.getOpacity());
        gc.setFill(background.getFill());
        gc.fillRect(xPosition, yPosition, background.getWidth(), background.getHeight());
        Action a = player.getAvailableActions().get(actionName);
        if (a.isOnCooldown()) {
            double cdPortion = a.getRemainingCooldown() / a.getCooldown();
            gc.setFill(Color.DARKRED);
            gc.fillRect(xPosition, yPosition, background.getWidth(), background.getHeight()*cdPortion);
        }
    }
    
    @Override
    public void buttonPress() {
        Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
        if (!player.getLocation().getTargets().isEmpty()) {
            Mists.logger.log(Level.INFO, "ActionButton: Using {0} towards {1}", new Object[]{actionName, player.getLocation().getTargets().get(0).getName()});
            player.useAction(actionName, player.getLocation().getTargets().get(0));
        } else {
            player.useAction(actionName);
            Mists.logger.log(Level.INFO, "ActionButton: Using {0}", actionName);
        }
    }
    
    
}
