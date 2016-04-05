/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.CircuitTile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * In Circuit tiles must be rotated to convey
 * power from point A to point B (and possibly C, D, E...)
 * @author nikok
 */
public class CircuitPuzzle {
    private ArrayList<CircuitTile> tiles;
    private ArrayList<Circuit> circuits;
    private ArrayList<Circuit> innatePoweredCircuits;
    
    public void updateTileLights() {
        for (CircuitTile t : tiles) {
            if (t.getCircuit() != null) {
                t.setLit(t.getCircuit().isPowered());
            }
        }
    }
    
    public void routePower() {
        for (Circuit c : this.innatePoweredCircuits) {
            c.givePowerToNeighbours();
        }
    }
    
    
    /**
     * Clear the current power routing and
     * route power again from innate powered circuits onwards
     */
    public void checkPower() {
        this.innatePoweredCircuits.clear();
        for (Circuit c : this.circuits) {
            c.loseAllPower();
            if (c.isInnatePowered()) {
                innatePoweredCircuits.add(c);
            }
        }
        for (Circuit c : this.innatePoweredCircuits) {
            c.givePowerToNeighbours();
        }    
    }
    
    public void addCircuit(Circuit c) {
        this.circuits.add(c);
        if (c.isInnatePowered()) this.innatePoweredCircuits.add(c);
    }
    
    public static CircuitTile[] generateRandomCircuitPuzzle(int widthInTiles, int heightInTiles) {
        //DrunkenWalk a path that works, then generate random fillers around it
        Random rnd = new Random();
        int circuitsInPuzzle = 0;
        Circuit[][] circuitMap = new Circuit[widthInTiles][heightInTiles];
        int startingSide = rnd.nextInt(4);
        int xCoor = 0;
        int yCoor = 0;
        switch (startingSide) {
            case 0: xCoor = rnd.nextInt(widthInTiles); yCoor = 0; break; //Start from upper side
            case 1: xCoor = widthInTiles-1; yCoor = rnd.nextInt(heightInTiles); break; //Start from right side
            case 2: xCoor = rnd.nextInt(widthInTiles); yCoor = heightInTiles-1; break; //Start from lower side
            case 3: xCoor = 0; yCoor = rnd.nextInt(heightInTiles); break; //Start from left side
            default: xCoor = 0; yCoor = rnd.nextInt(heightInTiles); break;
        }
        
        //The drunk walk here!
        
        return generateTileArrayFromCircuitMap(circuitMap, circuitsInPuzzle);
    }
    
    /**
     * Generate a simple test puzzle for testing
     * [L][T][S]
     * [I][I][L]
     * [L][I][X]
     * [S][L][I]
     * [L][S][L]
     * @param xCoordinate xCoordinate for the top left tile
     * @param yCoordinate yCoordinate for the top left tile
     * @return 
     */
    public static CircuitTile[] generateTestPuzzle(double xCoordinate, double yCoordinate) {
        Mists.logger.info("Generating a new Test Puzzle for CircuitPuzzle");
        Circuit[][] circuits = new Circuit[3][5];
        //Generate the circuits
        circuits[0][0] = new Circuit(new boolean[]{false, true, true, false});
        circuits[1][0] = new Circuit(new boolean[]{false, true, true, true});
        circuits[2][0] = new Circuit(new boolean[]{false, false, false, false});
        circuits[0][1] = new Circuit(new boolean[]{true, false, true, false});
        circuits[1][1] = new Circuit(new boolean[]{true, false, true, false});
        circuits[2][1] = new Circuit(new boolean[]{false, true, true, false});
        circuits[0][2] = new Circuit(new boolean[]{false, true, true, false});
        circuits[1][2] = new Circuit(new boolean[]{true, false, true, false});
        circuits[2][2] = new Circuit(new boolean[]{true, true, true, true});
        circuits[0][3] = new Circuit(new boolean[]{false, false, true, false});
        circuits[1][3] = new Circuit(new boolean[]{false, true, true, false});
        circuits[2][3] = new Circuit(new boolean[]{true, false, true, false});
        circuits[0][4] = new Circuit(new boolean[]{false, true, true, false});
        circuits[1][4] = new Circuit(new boolean[]{false, false, false, false});
        circuits[2][4] = new Circuit(new boolean[]{false, true, true, false});
        
        //Power up the circuit at the bottom row
        circuits[1][4].setInnatePower(true);
        linkCircuitsToNeighbours(circuits);
        CircuitTile[] tiles = generateTileArrayFromCircuitMap(circuits, 15);
        for (CircuitTile t : tiles) {
            t.setPosition(t.getXPos()+xCoordinate, t.getYPos()+yCoordinate);
        }
        //Randomize the puzzle rotations
        randomizePuzzleTileRotations(tiles);
        return tiles;
    }
    
    private static void linkCircuitsToNeighbours(Circuit[][] circuitMap) {
        Mists.logger.info("Linking puzzle circuits to neighbours");
        for (int x = 0; x < circuitMap.length; x++) {
            for (int y = 0; y < circuitMap[0].length; y++) {
                if (circuitMap[x][y] != null) {
                    if (y<circuitMap[0].length-1) circuitMap[x][y].setNeighbour(circuitMap[x][y+1],2); //Set top neighbour
                    if (x<circuitMap.length-1) circuitMap[x][y].setNeighbour(circuitMap[x+1][y],1); //Set right neighbour
                    if (y>0) circuitMap[x][y].setNeighbour(circuitMap[x][y-1],0); //Set bottom neighbour
                    if (x>0) circuitMap[x][y].setNeighbour(circuitMap[x-1][y],3); //Set left neighbour   
                }
            }
        }
    }
    
    /**
     * Rotate each circuit in a puzzle for a number of times (0-3)
     * @param circuitMap Array[] with the circuits in it
     */
    private static void randomizePuzzleRotations(Circuit[][] circuitMap) {
        Random rnd = new Random();
        for (int i = 0; i < circuitMap.length; i++) {
            for (int j = 0; j < circuitMap[0].length; j++) {
                int rotations = rnd.nextInt(4);
                for (int e = 0; e < rotations; e++) {
                    circuitMap[i][j].rotateCW();
                }
            }
        }   
    }
    
    private static void randomizePuzzleTileRotations(CircuitTile[] circuitTileMap) {
         Random rnd = new Random();
        for (CircuitTile tile : circuitTileMap) {
            int rotations = rnd.nextInt(4);
            for (int e = 0; e < rotations; e++) {
                tile.rotateCW();
            }
        }  
    }
    
    /**
     * Take the circuits from a map and generate an array with
     * corresponding tiles
     * @param circuitMap circuit[][] with the circuits in it
     * @param circuitsInMap number of circuts on the map (as there might be empty spots)
     * @return an array with circuitTiles with circuits connected to them
     */
    private static CircuitTile[] generateTileArrayFromCircuitMap(Circuit[][] circuitMap, int circuitsInMap) {
        Mists.logger.info("Generating tiles for circuits");
        CircuitTile[] returnCircuits = new CircuitTile[circuitsInMap];
        int i = 0;
        for (int x = 0; x < circuitMap.length; x++) {
            for (int y = 0; y < circuitMap[0].length; y++) {
                if (circuitMap[x][y] != null) {
                    Mists.logger.info("Generating tile at "+x+","+y);
                    CircuitTile c = generateTileFromCircuit(circuitMap[x][y]);
                    Mists.logger.info("Setting tile in position");
                    c.setPosition(x*c.getSprite().getWidth(), y*c.getSprite().getHeight());
                    Mists.logger.info("Adding tile to the array");
                    returnCircuits[i] = c;
                    i++;
                }
            }
        }
        return returnCircuits;
    }
    
    /**
     * Generate a single CircuitTile for the underlying circuit,
     * based on the circuits getCircuitShape() 
     * @param c the Circuit to generate tile for
     * @return A circuitTile with circuit connected to it
     */
    private static CircuitTile generateTileFromCircuit(Circuit c) {
        String shape = c.getCircuitShape();
        Mists.logger.info("Generating tile with the shape of "+shape);
        Mists.logger.info("Circuit was: "+Arrays.toString(c.getOpenPaths()));
        CircuitTile tile;
        switch (shape) {
            case "I":
                tile = (CircuitTile)Mists.structureLibrary.create("CircuitI");
                break;
            case "L":
                tile = (CircuitTile)Mists.structureLibrary.create("CircuitL");
                break;
            case "X":
                tile = (CircuitTile)Mists.structureLibrary.create("CircuitX");
                break;
            case "T":
                tile = (CircuitTile)Mists.structureLibrary.create("CircuitT");
                break;
            case "S":
                tile = (CircuitTile)Mists.structureLibrary.create("CircuitS");
                break;
            case "O":
                tile = (CircuitTile)Mists.structureLibrary.create("CircuitO");
                tile.setPaths(false, false, false, false); //temporarily disable paths to get a match with the circuit
                break;
            default: 
                tile = (CircuitTile)Mists.structureLibrary.create("CircuitS");
                break;
        }
        Mists.logger.info("Generated a tile: "+tile.getName());
        if (!tile.setCircuit(c)) Mists.logger.warning("Failure in connecting a Circuit to Tile in puzzle generation");
        if (shape.equals("O")) {
            //O-style starting points default at {false,false,false,false} - give it an extra corridor (south);
            tile.setPaths(false, false, true, false);
            c.setPaths(false, false, true, false);
        }
        return tile;
    }
    
}
