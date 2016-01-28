/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.ui.UIComponent;
import com.nkoiv.mists.game.world.WorldMap.MapLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
/**
 *
 * @author nikok
 */
public class WorldmapState implements GameState {
    private HashMap<String, UIComponent> uiComponents;
    private final TreeSet<UIComponent> drawOrder;
    private final Game game;
    private UIComponent currentMenu;
    private boolean gameMenuOpen;
    private double lastDragX;
    private double lastDragY;
    
    public WorldmapState(Game game) {
        Mists.logger.info("Generating worldmapstate for "+Mists.gameMode);
        this.game = game;
        uiComponents = new HashMap<>();
        this.drawOrder = new TreeSet<>();
    }
    
    @Override
    public Game getGame() {
        return this.game;
    }
    
    @Override
    public void exit() {
        Mists.soundManager.stopMusic();
    }

    @Override
    public void enter() {
        Mists.soundManager.playMusic("dungeon");
    }

    
    @Override
    public void addUIComponent(UIComponent uic) {
        this.uiComponents.put(uic.getName(), uic);
        this.drawOrder.add(uic);
    }
    
    @Override
    public boolean removeUIComponent(String uicName) {
        if (this.uiComponents.keySet().contains(uicName)) {
            //Mists.logger.info("Removing UIC "+uicName);
            this.drawOrder.remove(this.uiComponents.get(uicName));
            this.uiComponents.remove(uicName);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeUIComponent(UIComponent uic) {
        if (this.uiComponents.containsValue(uic)) {
            this.drawOrder.remove(uic);
            this.uiComponents.remove(uic.getName());
            return true;
        }
        return false;
    }
    
    @Override
    public UIComponent getUIComponent(String uicName) {
        return this.uiComponents.get(uicName);
    }

    @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

@Override
    public void handleMouseEvent(MouseEvent me) {
        //See if there's an UI component to click
        if (me.getEventType() == MouseEvent.MOUSE_CLICKED || me.getEventType() == MouseEvent.MOUSE_PRESSED || me.getEventType() == MouseEvent.MOUSE_RELEASED) this.handleClicks(me);
        if (me.getEventType() == MouseEvent.MOUSE_DRAGGED) this.handleMouseDrags(me);
        me.consume();
    }
    
    private void handleClicks(MouseEvent me) {
        this.lastDragX = 0; this.lastDragY = 0;
        if(!mouseClickOnUI(me)){
            //If not, give the click to the underlying gameLocation
            //Mists.logger.info("Click didnt land on an UI button");
            this.mouseClickOnMap(me);
        }
    }
    
    /**
     * 
     * @param me MouseEvent that landed on the world map
     */
    private void mouseClickOnMap(MouseEvent me) {
        double clickX = me.getX();
        double clickY = me.getY();
        MapObject mob = game.getCurrentWorldMap().mobAtCoordinates(clickX, clickY);
        MapLocation ml = game.getCurrentWorldMap().locationAtCoordinates(clickX, clickY);
        if (mob != null) Mists.logger.info("Click landed on "+mob.getName());
        if (ml != null) Mists.logger.info("Click landed on "+ml.getName());
        if (ml == null && mob == null) Mists.logger.info("Click didn't land on anything");
    }
    
    private void handleMouseDrags(MouseEvent me) {
        if (lastDragX == 0 || lastDragY == 0) {
            lastDragX = me.getX();
            lastDragY = me.getY();
        }
        UIComponent uic = this.getUIComponentAtCoordinates(me.getX(), me.getY());
        if (uic != null) uic.handleMouseDrag(me, lastDragX, lastDragY);
        lastDragX = me.getX(); lastDragY = me.getY();
    }
    
    protected UIComponent getUIComponentAtCoordinates(double xCoor, double yCoor) {
        for (UIComponent uic : this.drawOrder.descendingSet()) {
            double uicHeight = uic.getHeight();
            double uicWidth = uic.getWidth();
            double uicX = uic.getXPosition();
            double uicY = uic.getYPosition();
            //Check if the click landed on the ui component
            if (xCoor >= uicX && xCoor <= (uicX + uicWidth)) {
                if (yCoor >= uicY && yCoor <= uicY + uicHeight) {
                    return uic;
                }
            }
        }
        //Click landed on area without UI component
        return null;
    }
    
    /**
     * Check if there's any UI component at the mouse event location.
     * If so, trigger that UI components "onClick". 
     * @param me MouseEvent got from the game user (via Game)
     * @return True if UI component was clicked. False if there was no UI there
     */
    public boolean mouseClickOnUI(MouseEvent me) {
        UIComponent uic = this.getUIComponentAtCoordinates(me.getX(), me.getY());
        if (uic != null) {
            uic.handleMouseEvent(me);
            me.consume();
            return true;
        }
        return false;
        
    }

    @Override
    public void updateUI() {
        //Move the actionbar to where it should be
        Mists.logger.info("Updating UI. Game dimensions: "+game.WIDTH+"x"+game.HEIGHT);
    }
    
}
