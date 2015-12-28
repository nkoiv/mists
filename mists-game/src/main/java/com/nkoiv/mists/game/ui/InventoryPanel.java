/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.ui.PopUpMenu.MenuButton;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * InventoryPanel is a panel linked to an inventory.
 * All the items in the inventory are represented by buttons
 * in the panel.
 * @author nikok
 */
public class InventoryPanel extends TiledPanel {
    private static double buttonSize = Mists.TILESIZE;
    private static double pacing = 5;
    private static double defaultWidth = 300;
    private static double defaultHeight= 200;
    private static String defaultPanelImages = "panelBlue";
    private Inventory inv;
    private int invSizeOnLastUpdate;
    
    public InventoryPanel(GameState parent, String name, double width, double height, double xPos, double yPos, Image[] images, Inventory inv) {
        super(parent, name, width, height, xPos, yPos, images);
        this.inv = inv;
    }
    
    public InventoryPanel(GameState parent, Inventory inv) {
        super(parent, "Inventory", defaultWidth, defaultHeight, Mists.WIDTH/2, Mists.HEIGHT/2, Mists.graphLibrary.getImageSet(defaultPanelImages));
        this.inv = inv;
        this.populateItemsIntoButtons();
    }
    
    private void populateItemsIntoButtons() {
        this.invSizeOnLastUpdate = this.inv.getSize();
        this.subComponents.clear();
        int rowSize = (int)(this.width/(buttonSize+pacing));
        int row = 0;
        int column = 0;
        for (int i = 0; i < this.inv.getSize(); i++) {
            if (this.inv.getItem(i) != null) {
                ItemButton ib = new ItemButton(this.parent, this.inv, i, this.xPosition+pacing+(column*(buttonSize+pacing)), this.yPosition+pacing+(row*(buttonSize+pacing)));
                column++;
                if ((i+1)%rowSize == 0) {
                    row++;
                    column = 0;
                }
                this.subComponents.add(ib);
            }
        }
    }
    
    public Inventory getInventory() {
        return this.inv;
    }
    
    
    
    @Override
    public void render(GraphicsContext gc, double xOffset, double yOffset) {
        if (this.invSizeOnLastUpdate != this.inv.getSize()) this.populateItemsIntoButtons();
        this.renderBackground(gc);
        //Render all the subcomponents so that they are tiled in the window area
        //tileSubComponentPositions(xOffset, yOffset);
        for (UIComponent sc : this.subComponents) {
            sc.render(gc, sc.getXPosition(), sc.getYPosition());
        }       
    }
    
    private class ItemButton extends IconButton {
        private GameState parent;
        private Inventory inv;
        private int invID;
        
        public ItemButton(GameState parent, Inventory inv, int inventoryID, double xPos, double yPos) {
            super (inv.getItem(inventoryID).getName(), Mists.TILESIZE, Mists.TILESIZE, xPos, yPos, inv.getItem(inventoryID).getImage(), inv.getItem(inventoryID).getImage());
            this.parent = parent;
            this.inv = inv;
            this.invID = inventoryID;   
        }
        
        private void itemClicked(double xCoor, double yCoor) {
            if (this.inv.getItem(invID) == null) Mists.logger.log(Level.INFO, "Inventory slot {0} with no item was clicked", invID);
            else {
                Mists.logger.log(Level.INFO, "Inventory slot {0} with {1} was clicked", new Object[]{this.invID, this.inv.getItem(invID).getName()});
                popUpItemMenu(inv, invID, xCoor, yCoor);
            }
            
        }
        
        public void popUpItemMenu(Inventory inv, int itemSlot, double xCoor, double yCoor) {
            PopUpMenu pmenu = new PopUpMenu(parent);
            pmenu.setPosition(xCoor, yCoor);
            pmenu.setOpenUpwards(pressed);
            pmenu.addMenuButton(new ItemMenuButton(pmenu, inv, itemSlot, ItemMenuButton.USE_ITEM));
            pmenu.addMenuButton(new ItemMenuButton(pmenu, inv, itemSlot, ItemMenuButton.DROP_ITEM));
            this.parent.getUIComponents().put(pmenu.getName(), pmenu);
        }
        
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED && me.getButton() == MouseButton.PRIMARY) this.itemClicked(me.getX(), me.getY());
        }
        
    }
    
    private class ItemMenuButton extends MenuButton {
        private int actionType;
        private Inventory inv;
        private int slot;
        private final static int USE_ITEM = 1;
        private final static int DROP_ITEM = 2;
        
        
        public ItemMenuButton(PopUpMenu parent, Inventory inv, int invSlot, int actionType) {
            super(parent);
            this.actionType = actionType;
            this.inv = inv;
            this.slot = invSlot;
            this.updateText();
        }
        
        private void updateText() {
            switch (this.actionType) {
                case USE_ITEM: this.text = "Use Item"; break;
                case DROP_ITEM: this.text = "Drop Item"; break;
                default: this.text = "----";
            }
        }
        
        @Override
        protected boolean click() {
            switch (this.actionType) {
                case USE_ITEM: this.inv.getItem(slot).use(); this.parent.close() ;return true;
                case DROP_ITEM: this.inv.removeItem(slot); this.parent.close(); return true;
                default: break;
            }
            return false;
        }
        
    }
    
}


