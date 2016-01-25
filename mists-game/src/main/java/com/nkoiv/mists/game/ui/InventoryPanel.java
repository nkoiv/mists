/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.GenericTasks;
import com.nkoiv.mists.game.actions.Task;
import com.nkoiv.mists.game.gamestate.GameState;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.items.Weapon;
import com.nkoiv.mists.game.ui.PopUpMenu.MenuButton;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 * InventoryPanel is a panel linked to an inventory.
 * All the items in the inventory are represented by buttons
 * in the panel.
 * @author nikok
 */
public class InventoryPanel extends TiledPanel {
    private static double topMargin = 20;
    private static double buttonSize = Mists.TILESIZE;
    private static double pacing = 5;
    private static double defaultWidth = 300;
    private static double defaultHeight= 200;
    private static String defaultPanelImages = "panelBlue";
    private Inventory inv;
    private int oldInvHash;
    
    public InventoryPanel(GameState parent, String name, double width, double height, double xPos, double yPos, Image[] images, Inventory inv) {
        super(parent, name, width, height, xPos, yPos, images);
        this.inv = inv;
        this.draggable = true;
    }
    
    public InventoryPanel(GameState parent, Inventory inv) {
        super(parent, "Inventory", defaultWidth, defaultHeight, Mists.WIDTH/2, Mists.HEIGHT/2, Mists.graphLibrary.getImageSet(defaultPanelImages));
        this.inv = inv;
        this.populateItemsIntoButtons();
        this.draggable = true;
    }
    
    private void populateItemsIntoButtons() {
        this.oldInvHash = this.inv.contentHash();
        this.subComponents.clear();
        int rowSize = (int)(this.width/(buttonSize+pacing));
        int row = 0;
        int column = 0;
        for (int i = 0; i < this.inv.getCapacity(); i++) {
            if (this.inv.getItem(i) != null) {
                ItemButton ib = new ItemButton(this.parent, this.inv, i, this.xPosition+pacing+(column*(buttonSize+pacing)), topMargin + this.yPosition+pacing+(row*(buttonSize+pacing)));
                column++;
                if ((i+1)%rowSize == 0) {
                    row++;
                    column = 0;
                }
                this.subComponents.add(ib);
            }   
        }
        //Add a close button
        CloseButton cb = new CloseButton (this, this.xPosition + this.width - 20, this.yPosition+5);
        this.subComponents.add(cb);
    }
    
    public Inventory getInventory() {
        return this.inv;
    }
    
    
    
    @Override
    public void render(GraphicsContext gc, double xOffset, double yOffset) {
        if (this.oldInvHash != this.inv.contentHash()) this.populateItemsIntoButtons();
        //this.populateItemsIntoButtons();
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
                Mists.logger.info("Last click was at "+this.lastClickTime);
                if (System.currentTimeMillis()-this.lastClickTime <= 500)  {
                    if (this.inv.getItem(invID) instanceof Weapon) Inventory.equipItem(this.inv, this.invID);
                    else this.inv.getItem(invID).use();
                }
            }
            this.lastClickTime = System.currentTimeMillis();
        }
        
        private void itemMenuToggle(double xCoor, double yCoor) {
            if (this.inv.getItem(invID) == null) Mists.logger.log(Level.INFO, "Inventory slot {0} with no item was clicked", invID);
            Mists.logger.log(Level.INFO, "Inventory slot {0} with {1} was menu popping", new Object[]{this.invID, this.inv.getItem(invID).getName()});
            popUpItemMenu(inv, invID, xCoor, yCoor);
            
        }
        
        public void popUpItemMenu(Inventory inv, int itemSlot, double xCoor, double yCoor) {
            this.parent.removeUIComponent("PopUpMenu");
            PopUpMenu pmenu = new PopUpMenu(parent);
            pmenu.setPosition(xCoor, yCoor);
            pmenu.setRenderZ(this.renderZ+1);
            //pmenu.setOpenUpwards(pressed);
            this.populateItemMenu(pmenu, inv, itemSlot);
            this.parent.addUIComponent(pmenu);
        }
        
        private void populateItemMenu(PopUpMenu pmenu, Inventory inv, int itemSlot) {
            pmenu.addMenuButton(new ItemMenuButton(pmenu, inv, itemSlot, ItemMenuButton.USE_ITEM));
            pmenu.addMenuButton(new ItemMenuButton(pmenu, inv, itemSlot, ItemMenuButton.EQUIP_ITEM));
            pmenu.addMenuButton(new ItemMenuButton(pmenu, inv, itemSlot, ItemMenuButton.DROP_ITEM));
        }
        
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            Mists.logger.info("MouseEvent on "+this.getName());
            if (this.inv.getOwner() != null) Mists.logger.info("Inventory owned by "+this.inv.getOwner());
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED && me.getButton() == MouseButton.PRIMARY) this.itemClicked(me.getX(), me.getY());
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED && me.getButton() == MouseButton.SECONDARY) this.itemMenuToggle(me.getX(), me.getY());
        }
        
    }
    
    private class ItemMenuButton extends MenuButton {
        private int actionType;
        private Inventory inv;
        private int slot;
        private final static int USE_ITEM = 1;
        private final static int EQUIP_ITEM = 2;
        private final static int DROP_ITEM = 9;
        
        
        public ItemMenuButton(PopUpMenu parent, Inventory inv, int invSlot, int actionType) {
            super(parent);
            this.actionType = actionType;
            this.inv = inv;
            this.slot = invSlot;
            this.updateText();
        }
        
        private void updateText() {
            switch (this.actionType) {
                case USE_ITEM: this.text = "Use"; break;
                case EQUIP_ITEM: this.text = "Equip"; break;
                case DROP_ITEM: this.text = "Drop"; break;
                default: this.text = "----";
            }
        }
        
        @Override
        protected boolean click() {
            switch (this.actionType) {
                case USE_ITEM: {
                    this.inv.getItem(slot).use();
                    this.parent.close();
                    return true;
                } 
                case EQUIP_ITEM: {
                    if (Inventory.equipItem(this.inv, slot)) {
                        this.parent.close();
                        return true;
                    } return false;
                }
                case DROP_ITEM: {
                    //if (Inventory.dropItem(this.inv, slot)) {
                        Task drop = new Task(GenericTasks.ID_DROP_ITEM, inv.getOwner().getID(), new int[]{slot});
                        inv.getOwner().setNextTask(drop);
                        this.parent.close();
                        return true;
                    //} return false;
                }
                default: break;
            }
            return false;
        }
        
        @Override
        public String getName() {
            return this.text;
        }
        
    }
    
}


