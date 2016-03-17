/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.LootTrigger;
import com.nkoiv.mists.game.world.util.Toolkit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * ItemContainer is a MapObject representation
 * of an inventory. A chest or a pile of goods
 * on the ground
 * @author nikok
 */
public class ItemContainer extends Structure implements HasInventory {
    private Inventory inv;
    private int oldInvHash;
    private boolean renderContent;
    
    public ItemContainer(String name, MovingGraphics graphics) {
        super(name, graphics, 0);
        this.inv = new Inventory();
    }
    
 
    @Override
    public void update(double time) {
        if (this.inv.getSize() <= 0) {
            this.setRemovable();
            return;
        }
        
        super.update(time);
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        //Update the image if needed
        if (this.renderContent) {
            if (this.inv.contentHash() != this.oldInvHash) {
                Sprite newSprite = new Sprite(this.compileImageFromInventory());
                newSprite.setPosition(this.graphics.getXPos(), this.graphics.getYPos());
                this.graphics = newSprite;
            }
        }
        super.render(xOffset, yOffset, gc);
    }
    
    @Override
    public Inventory getInventory() {
        return this.inv;
    }
    
    public boolean addItem(Item i) {
        if (this.inv.isFull()) this.inv.expand(10);
        this.inv.addItem(i);
        return true;
    }
    
    public Item peekTopItem() {
        Item topItemInPile = null;
        for (int i  = 0; i < this.inv.getCapacity(); i++)  {
            if (inv.getItem(i) != null) {                
                topItemInPile =  this.inv.getItem(i);
                break;
            }
        }
        return topItemInPile;
    }
    
    public Item takeTopItem() {
        Item topItemInPile = null;
        for (int i  = 0; i < this.inv.getCapacity(); i++)  {
            if (inv.getItem(i) != null) {
                if (this.inv.getSize() < 1) this.setRemovable();
                topItemInPile =  this.inv.removeItem(i);
                break;
            }
        }
        if (this.inv.getSize() < 1) this.setRemovable();
        return topItemInPile;
    }

    private Image compileImageFromInventory() {
        Image compilation;
        Image[] images = new Image[this.inv.getSize()+1];
        images[0] = Mists.graphLibrary.getImage("blank");
        int n = 1;
        for (int i = 0; i < this.inv.getCapacity(); i++) {
            Item it = this.inv.getItem(i);
            if (it != null) {
                images[n] = this.inv.getItem(i).getImage();
                n++;
            }
        }
        if (images.length ==1) compilation = images[0];
        else {
            compilation = Toolkit.mergeImage(true, images);
        }
        
        return compilation;
    }
    
    /**
     * If set to true, the ItemContainer will
     * have its image composed of whatever it contains
     * @param renderContent 
     */
    public void setRenderContent(boolean renderContent) {
        this.renderContent = renderContent;
    }
    
    public boolean isRenderContent() {
        return this.renderContent;
    }
    
    @Override
    public Trigger[] getTriggers() {
        Trigger[] a = new Trigger[]{new LootTrigger(this)};
        return a;
    }
}
