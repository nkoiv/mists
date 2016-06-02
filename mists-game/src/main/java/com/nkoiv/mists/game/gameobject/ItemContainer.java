/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.items.Inventory;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.LootTrigger;
import com.nkoiv.mists.game.triggers.OpenInventoryTrigger;
import com.nkoiv.mists.game.triggers.Trigger;
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
    private boolean permanentInventory;
    
    public ItemContainer(String name, MovingGraphics graphics) {
        super(name, graphics, 0);
        this.renderContent = false;
        this.inv = new Inventory();
    }
    
 
    @Override
    public void update(double time) {
        if (this.inv.getSize() <= 0) {
            this.remove();
            return;
        }
        
        super.update(time);
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        //Update the image if needed
        if (this.renderContent && this.inv.contentHash() != this.oldInvHash) {
            Sprite newSprite = new Sprite(this.compileImageFromInventory());
            newSprite.setPosition(this.graphics.getXPos(), this.graphics.getYPos());
            this.graphics = newSprite;
        }
        super.render(xOffset, yOffset, gc);
    }
    
    @Override
    public Inventory getInventory() {
        return this.inv;
    }
    
    public void setPermanency(boolean permanent) {
        this.permanentInventory = permanent;
    }
    
    public boolean isPermanent() {
        return this.permanentInventory;
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
                if (this.inv.getSize() < 1) this.remove();
                topItemInPile =  this.inv.removeItem(i);
                break;
            }
        }
        if (this.inv.getSize() < 1) this.remove();
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
     * @param renderContent If set to true, items inside the container will be rendered on it
     */
    public void setRenderContent(boolean renderContent) {
        this.renderContent = renderContent;
    }
    
    public boolean isRenderContent() {
        return this.renderContent;
    }
    
    @Override
    public Trigger[] getTriggers() {
        if (permanentInventory) {
            Trigger[] a = new Trigger[]{new LootTrigger(this)};
            return a;
        }
        else {
            Trigger[] a = new Trigger[]{new OpenInventoryTrigger(this)};
            return a;
        }
    }
    
    @Override
    public ItemContainer createFromTemplate() {
        ItemContainer nic = new ItemContainer(this.name, new Sprite(this.getSprite().getImage()));
        nic.setCollisionLevel(this.collisionLevel);
        if (this.getSprite().isAnimated()) {
            nic.getSprite().setAnimation(this.getSprite().getAnimation());
        }
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getSprite().getXPos();
                double yOffset = s.getYPos() - this.getSprite().getYPos();
                Sprite extra = new Sprite(s.getImage(), 0, 0);
                if (s.isAnimated()) extra.setAnimation(s.getAnimation());
                nic.addExtra(extra, xOffset, yOffset);
            }
        }
        return nic;
    }
    
}
