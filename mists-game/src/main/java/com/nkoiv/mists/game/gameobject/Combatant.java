/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.world.Location;

/**
 *
 * @author nkoiv
 */
public interface Combatant {
	
	int getDV();

	int getAV();
	
	void takeDamage(int damage);
	
	void healHealth(int healing);

	String getName();
        
	Location getLocation();
        
        void setEffect(String effect, int value);
                
        int getEffectValue(String effect);
	
	boolean moveTowards (Direction direction);

	double getXPos();

	double getYPos();
}