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
                ItemButton ib = new ItemButton(this.inv, i, this.xPosition+pacing+(column*(buttonSize+pacing)), this.yPosition+pacing+(row*(buttonSize+pacing)));
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
    
    private static class ItemButton extends IconButton {
    
        private Inventory inv;
        private int invID;
        
        public ItemButton(Inventory inv, int inventoryID, double xPos, double yPos) {
            super (inv.getItem(inventoryID).getName(), Mists.TILESIZE, Mists.TILESIZE, xPos, yPos, inv.getItem(inventoryID).getImage(), inv.getItem(inventoryID).getImage());
            this.inv = inv;
            this.invID = inventoryID;   
        }
        
        private void itemClicked() {
            if (this.inv.getItem(invID) == null) Mists.logger.log(Level.INFO, "Inventory slot {0} with no item was clicked", invID);
            Mists.logger.log(Level.INFO, "Inventory slot {0} with {1} was clicked", new Object[]{this.invID, this.inv.getItem(invID).getName()});
        }
        
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED && me.getButton() == MouseButton.PRIMARY) this.itemClicked();
        }
        
    }
    
}


