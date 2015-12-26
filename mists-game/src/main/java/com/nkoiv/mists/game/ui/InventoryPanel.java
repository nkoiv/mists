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
import javafx.scene.image.Image;

/**
 * 
 * @author nikok
 */
public class InventoryPanel extends TiledPanel {
    
    private static double defaultWidth = 300;
    private static double defaultHeight= 200;
    private static String defaultPanelImages = "panelBlue";
    private Inventory inv;
    
    public InventoryPanel(GameState parent, String name, double width, double height, double xPos, double yPos, Image[] images, Inventory inv) {
        super(parent, name, width, height, xPos, yPos, images);
        this.inv = inv;
    }
    
    public InventoryPanel(GameState parent, Inventory inv) {
        super(parent, "Inventory", defaultWidth, defaultHeight, Mists.WIDTH/2, Mists.HEIGHT/2, Mists.graphLibrary.getImageSet(defaultPanelImages));
        this.inv = inv;
    }
    
    private static class ItemButton extends IconButton {
    
        private Inventory inv;
        private int invID;
        
        public ItemButton(Inventory inv, int inventoryID, double xPos, double yPos) {
            super (inv.getItem(inventoryID).getName(), Mists.TILESIZE, Mists.TILESIZE, xPos, yPos, inv.getItem(inventoryID).getImage(), inv.getItem(inventoryID).getImage());
            this.inv = inv;
            this.invID = inventoryID;
            
        }
        
    }
    
}


