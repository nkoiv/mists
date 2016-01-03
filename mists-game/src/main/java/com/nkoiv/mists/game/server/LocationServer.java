/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.server;

import com.esotericsoftware.kryonet.Server;
import com.nkoiv.mists.game.world.Location;

/**
 * LocationServer runs a Location and serves the information from it
 * to the clients connected to it.
 * @author nikok
 */
public class LocationServer {
    private Location location;
    private Server server;
    
}
