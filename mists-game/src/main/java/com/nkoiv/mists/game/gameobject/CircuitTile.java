/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.puzzle.Circuit;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
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
        this.gRotation = this.gRotation + 90;
        if (this.gRotation >= 360) this.gRotation = 0;
        this.litUpGraphics.setRotation(this.gRotation);
        this.unLitGraphics.setRotation(this.gRotation);
    }
    
    public void rotateCCW() {
        if (this.circuit != null) this.circuit.rotateCCW();
        this.gRotation = this.gRotation - 90;
        if (this.gRotation < 0) this.gRotation = 270;
        this.litUpGraphics.setRotation(this.gRotation);
        this.unLitGraphics.setRotation(this.gRotation);
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
        if (circuit.getOpenPaths() == this.openPaths)  {
            this.circuit = circuit;
            return true;
        } else {
            //The circuit didn't match instantly. Try to rotate around to get it to match
            return rotateAndSetCircuit(circuit);
        }
    }
    
    private boolean rotateAndSetCircuit(Circuit circuit) {
        int tries = 0;
        while (tries < 4) {
            this.rotateCW();
            if (circuit.getOpenPaths() == this.openPaths) {
                this.circuit = circuit;
                return true;
            }
        }
        return false;
    }
    
    public boolean[] getOpenPaths() {
        return this.openPaths;
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
