/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.gamestate.LocationState;
import com.nkoiv.mists.game.world.Location;

/**
 * LocationControls are a layer that relays commands to the location
 * The idea behind separate LocationControls is that externally loaded
 * config / world data can utilize these via scripting.
 * @author nikok
 */
public class LocationControls {
    
    private final Game game;
    
    public LocationControls(Game game) {
        this.game = game;
    }
    
    private Location currentLoc() {
        return this.game.currentLocation;
    }
    
    public Game getGame() {
        return this.game;
    }
    
    
    //------------Individual triggers for controlling a location----
    
    public void toggleFlag(String flag) {
        this.game.currentLocation.toggleFlag(flag);
    }
    
    public void printClearanceMapIntoConsole(){
        game.currentLocation.getPathFinder().printClearanceMapIntoConsole(0);
    }
    
    public void printCollisionMapIntoConsole() {
        game.currentLocation.getPathFinder().printCollisionMapIntoConsole();
    }
    
    public void toggleLocationMenu() {
        LocationState LS = (LocationState)this.game.currentState;
        LS.toggleGameMenu();
    }
    
    public void increseLightLevel() {
        this.game.currentLocation.setMinLightLevel(this.game.currentLocation.getMinLightLevel()+0.1);
    }
    
    public void reduceLightLevel() {
        this.game.currentLocation.setMinLightLevel(this.game.currentLocation.getMinLightLevel()-0.1);
    }
    
    
    //----------Player controls-----
    
    public void playerAttack() {
        game.currentLocation.getPlayer().useAction("MeleeAttack");
    }
    
    public void playerMove(Direction direction) {
        //TODO: Current movement lets player move superspeed diagonal. should call moveTowards(Direction.UPRIGHT) etc.
        switch (direction) {
            case UP: game.currentLocation.getPlayer().moveTowards(Direction.UP); break;         
            case DOWN: game.currentLocation.getPlayer().moveTowards(Direction.DOWN); break;
            case LEFT: game.currentLocation.getPlayer().moveTowards(Direction.LEFT); break;
            case RIGHT: game.currentLocation.getPlayer().moveTowards(Direction.RIGHT);break;
            default: break;
        } 
    }
}
