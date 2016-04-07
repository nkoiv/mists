/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.sprites;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.items.Item;
import java.util.HashMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * SpriteSkeleton is a collection of sprites moving in uniform
 * Class is mainly made for creatures composed of several sprites (head, arms, legs...)
 * Inside a SpriteSkeleton, each individual sprite only knows its relative position
 *                  
 *   _____________
 *  |X  x----     |
 *  |   |head|    |
 *  |x_ x---- x_  |
 *  ||a||body||a| |
 *  ||r||    ||r| |
 *  ||m||    ||m| |
 *  | -  ----  -  |
 *  |_____________| 
 *  (x marks the spot)
 * @author nikok
 */
public class SpriteSkeleton extends MovingGraphics {
    private String[] renderOrder;
    private HashMap<String, Sprite> sprites; //HashMap is used to individualize parts and replace them by name as need be
    private HashMap<String, Image[]> directionalImages; //Different images are used for different directions
    private Direction facing; //Skeleton needs to know its facing to choose right sprites
    private WritableImage snapshot; 
    
    public SpriteSkeleton() {
        this.sprites = new HashMap<>();
        this.collisionBox = new CollisionBox();
        this.directionalImages = new HashMap<>();
        //this.facing = Direction.DOWN;
    }
    
    public void equipItem(Item item) {
        if (item.getEquippedImages() == null) return;
        
    }
    
    /**
     * Add a part to the SpriteSkeleton without providing
     * alternate images for rotated movement
     * @param partName Name of the part to add
     * @param part Sprite of the part to add
     */
    public void addPart(String partName, Sprite part) {
        this.sprites.put(partName, part);
        this.updateDimensions();
        this.updateSnapshot();
        this.refreshCollisionBox();
    }
    
    /**
     * Add a piece to the SpriteSkeleton, supplied with
     * an array of alternative images for directional movement
     * (Note: Array needs to be 4 images long (Up, Down, Left, Right)
     * or it is discarded)
     * @param partName Name of the part to add
     * @param part Sprite of the part to add
     * @param directionalImages Array of alternative images
     */
    public void addPart(String partName, Sprite part, Image[] directionalImages) {
        this.addPart(partName, part);
        this.addDirectionalImages(partName, directionalImages);
        this.updateSnapshot();
    }
    
    /**
     * Makes sure no incorrect sized ImageArrays are placed in directionalImages
     * @param partName Name of the part to add
     * @param directionalImages 4-piece ImageArray to add
     */
    private void addDirectionalImages(String partName, Image[] directionalImages) {
        if (directionalImages.length != 4) return;
        this.directionalImages.put(partName, directionalImages);
    }
    
    public void removePart(String partName) {
        this.sprites.remove(partName);
        this.directionalImages.remove(partName);
        this.updateDimensions();
        this.updateSnapshot();
        this.refreshCollisionBox();
    }
    
    private void updateFacing(Direction facing) {
        this.facing = facing;
        this.renderOrder = getRenderOrder(this.facing);
        this.updateFacingImages(this.facing);
    }
    
    private void updateFacingImages(Direction facing) {
        if (this.directionalImages.isEmpty()) return;
        for (String s : this.directionalImages.keySet()) {
            Image[] i = this.directionalImages.get(s);
            switch (facing) {
                case UP: this.sprites.get(s).setImage(i[0]); break;
                case DOWN: this.sprites.get(s).setImage(i[1]); break;
                case LEFT: this.sprites.get(s).setImage(i[2]); break;
                case RIGHT: this.sprites.get(s).setImage(i[3]); break;
                default: break;
            }
        }
    }
    
    /**
     * SpriteSkeleton is composed of lots of smallest sprites, 
     * so the effective "size" of the skeleton is composite of
     * those individual pieces.
     */
    private void updateDimensions(){
        double smallestX = 0;
        double largestX = 0;
        double smallestY = 0;
        double largestY = 0;
        for (String s : this.sprites.keySet()) {
            Sprite sp = this.sprites.get(s);
            if (sp.getXPos() < smallestX) smallestX = sp.getXPos();
            if (sp.getYPos() < smallestY) smallestY = sp.getYPos();
            if (sp.getXPos()+sp.getWidth() > largestX) largestX = sp.getXPos()+sp.getWidth();
            if (sp.getYPos()+sp.getHeight() > largestY) largestY = sp.getYPos()+sp.getHeight();
        }
        this.width = Math.abs(smallestX) + Math.abs(largestX);
        this.height = Math.abs(smallestY) + Math.abs(largestY);
    }
    
    private void updateSnapshot() {
        this.snapshot = new WritableImage((int)this.width,(int)this.height);
        PixelWriter pw = snapshot.getPixelWriter();
        for (String s : this.sprites.keySet()) {
            Sprite sprite = this.sprites.get(s);
            Image image = sprite.getImage();
            PixelReader pr = image.getPixelReader();
            for(int y=0; y<image.getHeight(); y++){
                for(int x=0; x<image.getWidth(); x++){
                    Color color = pr.getColor(x, y);
                    pw.setColor((int)(x+sprite.getXPos()), (int)(y+sprite.getYPos()), color);
                }
            }
        }
    }
    
    @Override
    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
        this.refreshCollisionBox();
    }
    
    @Override
    public void setCenterPosition(double x, double y) {
        this.setPosition(x-(this.getWidth()/2), y-(this.getHeight()/2));
        this.refreshCollisionBox();
    }
    
    public void render(double xOffset, double yOffset, GraphicsContext gc, Direction facing) {
        if (this.facing != facing) this.updateFacing(facing);
        this.renderInOrder(xOffset, yOffset, gc);
        //Mists.logger.info("ARARA");
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.sprites.isEmpty()) return;
        //Mists.logger.info("rendering skeleton without order");
        for (String s : this.sprites.keySet()) {
            //Mists.logger.log(Level.INFO, "Rendering {0} at {1}x, {2}y", new Object[]{s, -(this.positionX-xOffset), -(this.positionY-yOffset)});
            //Mists.logger.log(Level.INFO, "Subsprite pos: {0}x, {1}y", new Object[]{sprites.get(s).positionX, sprites.get(s).positionY});
            sprites.get(s).render(-this.positionX+xOffset, -this.positionY+yOffset, gc);
        }    
        if (this.renderOrder!=null) this.renderInOrder(xOffset, yOffset, gc);
    }
    
    private void renderInOrder(double xOffset, double yOffset, GraphicsContext gc) {
        //Mists.logger.info(Arrays.toString(renderOrder));
        //Mists.logger.info(""+sprites.keySet());
        for (String ro : this.renderOrder) {
            Sprite s = sprites.get(ro);
            if (s!=null) s.render(-this.positionX+xOffset, -this.positionY+yOffset, gc);
        }
    }
    
    @Override
    public void renderCollisions(double xOffset, double yOffset, GraphicsContext gc) {
        gc.save();
        gc.setStroke(Color.RED);
        gc.strokeRect(this.positionX-xOffset, this.positionY-yOffset, this.width, this.height);
        gc.restore();
    }
    
    @Override
    public void update(double time) {
        super.update(time);
        if (this.sprites.isEmpty()) return;
        for (String s : this.sprites.keySet()) {
            sprites.get(s).update(time);
        }
    }
    
    @Override
    public boolean intersects(MovingGraphics m) {
        if (m instanceof Sprite || m instanceof SpriteSkeleton) return this.intersectsInPixels(m);
        return this.intersectsWithShape(m.getBoundary());
    }
    
     /**
     * Check the if the CollisionBoxes intersects
     * (sweep and prune?) before going to pixel detection
     * @param s Sprite to check collisions with
     * @return True if they overlap somewhere
     */    
    private boolean intersectsInPixels(MovingGraphics m) {
        //Rotated objects are happy with intersection, because pixel collision would require rotating the pixel image too...
        if (this.rotation!=0 || m.rotation != 0) {
           return this.intersectsWithShape(m.getBoundary());
        }
        if (this.collisionBox.intersects(m.collisionBox)) {
            return Sprite.pixelCollision(this.getXPos(), this.getYPos(), this.getImage(), m.getXPos(), m.getYPos(), m.getImage());
        }
        else return false;
    }

    @Override
    public Image getImage() {
        return this.snapshot;
    }
    
    private static String[] getRenderOrder(Direction d) {
        switch (d) {
            case UP: return new String[]{"body", "legs", "feet","weapon", "arms", "cloak", "head"};
            case DOWN: return new String[]{"cloak", "body", "legs", "feet", "arms", "weapon", "head"};
            default: return new String[]{"cloak", "body", "legs", "feet", "arms", "weapon", "head"};
        }
    }
}
