/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.puzzle.Circuit;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.RotateTrigger;
import com.nkoiv.mists.game.triggers.Trigger;
import java.util.Arrays;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * CircuitTile is a type of floortile that makes up the
 * Circuit -puzzle. Circuits can be rotated, and they
 * route power according to their path.
 * @author nikok
 */
public class CircuitTile extends PuzzleTile {
    private boolean[] openPaths;
    /*
    *[ ][N][ ]  [ ][0][ ]
    *[W][ ][E]  [3][ ][1]
    *[ ][S][ ]  [ ][2][ ]
    * fex orientation[]{true, true, false, false} : North-East L-circuit
    */
    private Circuit circuit;
    private double gRotation;
    
    public CircuitTile(String name, boolean[] orientation, MovingGraphics litUpGraphics, MovingGraphics unLitGraphics) {
        super(name, litUpGraphics, unLitGraphics);
        this.openPaths = orientation;
        this.gRotation = 0;
        this.graphics.setRotationPoint(graphics.getWidth()/2, graphics.getHeight()/2);
    }
    
    public CircuitTile(String name, boolean[] orientation, Image litUpGraphics, Image unLitGraphics) {
        this(name, orientation, new Sprite(litUpGraphics), new Sprite(unLitGraphics));
    }
    
    public void rotateCW() {
        if (this.circuit != null) this.circuit.rotateCW();
        this.rotatePathsCW();
        this.gRotation = this.gRotation + 90;
        if (this.gRotation >= 360) this.gRotation = 0;
        this.litUpGraphics.setRotation(this.gRotation);
        this.unLitGraphics.setRotation(this.gRotation);
    }
    
    public void rotateCCW() {
        if (this.circuit != null) this.circuit.rotateCCW();
        this.rotatePathsCCW();
        this.gRotation = this.gRotation - 90;
        if (this.gRotation < 0) this.gRotation = 270;
        this.litUpGraphics.setRotation(this.gRotation);
        this.unLitGraphics.setRotation(this.gRotation);
    }
    
     private void rotatePathsCW() {
        boolean[] newPaths = new boolean[4];
        if (openPaths[0]) newPaths[1] = true;
        if (openPaths[1]) newPaths[2] = true;
        if (openPaths[2]) newPaths[3] = true;
        if (openPaths[3]) newPaths[0] = true;
        this.openPaths = newPaths;
    }
    
    private void rotatePathsCCW() {
        boolean[] newPaths = new boolean[4];
        if (openPaths[0]) newPaths[3] = true;
        if (openPaths[1]) newPaths[0] = true;
        if (openPaths[2]) newPaths[1] = true;
        if (openPaths[3]) newPaths[2] = true;
        this.openPaths = newPaths;
    }
    
    /**
     * Set a puzzle circuit to this tile.
     * The added circuit must match this tile in open pathways orientation.
     * This Tile is rotated around to get it to match, but if unsuccessful,
     * method will return false.
     * @param circuit Circuit to add to this tile
     * @return True if the circuit matched and was added, false if it didn't match.
     */
    public boolean setCircuit(Circuit circuit) {
        Mists.logger.log(Level.INFO, "Setting circuit to tile. Tile setup:{0}. Circuit setup: {1}", new Object[]{Arrays.toString(this.openPaths), Arrays.toString(circuit.getOpenPaths())});
        if (Arrays.equals(circuit.getOpenPaths(), this.openPaths))  {
            this.circuit = circuit;
            if (circuit.isPowered()) this.setLit(true);
            return true;
        } else {
            //The circuit didn't match instantly. Try to rotate around to get it to match
            return rotateAndSetCircuit(circuit);
        }
    }
    
    public Circuit getCircuit() {
        return this.circuit;
    }
    
    private boolean rotateAndSetCircuit(Circuit circuit) {
        int tries = 0;
        while (tries < 4) {
            this.rotateCW();
            if (Arrays.equals(circuit.getOpenPaths(),this.openPaths)) {
                this.circuit = circuit;
                if (circuit.isPowered()) this.setLit(true);
                return true;
            }
            tries++;
        }
        return false;
    }
    
    public boolean[] getOpenPaths() {
        return this.openPaths;
    }
    
    @Override
    public Trigger[] getTriggers() {
        return new Trigger[]{new RotateTrigger(this)};
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        if (this.circuit != null) {
            if (this.isLit && !this.circuit.isPowered()) this.setLit(false);
            if (!this.isLit && this.circuit.isPowered()) this.setLit(true);
        } 
        super.render(xOffset, yOffset, gc);
    }
    
    @Override
    public String[] getInfoText() {
        double lightlevel = 0;
        if (this.location != null) lightlevel = location.getLightLevel(this.getCenterXPos(), this.getCenterYPos());
        String[] s = new String[]{
            this.name,
            "ID "+this.IDinLocation+" @ "+this.location.getName(),
            "X:"+((int)this.getXPos())+" Y:"+((int)this.getYPos()),
            "LightLevel:"+lightlevel,
            "Circuit:"+Arrays.toString(this.circuit.getOpenPaths()),
            "PoweredFrom: "+Arrays.toString(this.circuit.getPowerChart()),
            "Power: "+this.circuit.isPowered()
        };
        return s;
    }
    
    public void setPaths(boolean north, boolean east, boolean south, boolean west) {
        this.openPaths = new boolean[]{north, east, south, west};
    }
    
    @Override
    public CircuitTile createFromTemplate() {
        CircuitTile ct = new CircuitTile(this.name, this.openPaths, this.litUpGraphics.getImage(), this.unLitGraphics.getImage());
        ct.gRotation = this.gRotation;
        ct.getGraphics().setRotation(ct.gRotation);
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getSprite().getXPos();
                double yOffset = s.getYPos() - this.getSprite().getYPos();
                ct.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return ct;
    }
    
}
