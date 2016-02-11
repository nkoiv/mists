/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.gameobject.Structure;
import java.util.Comparator;

/**
 *
 * @author nikok
 */
public class StructureYComparator implements Comparator<Structure>{

    @Override
    public int compare(Structure s, Structure s1) {
        if (s.getCenterYPos() < s1.getCenterYPos()) return -1;
        if (s.getCenterYPos() > s1.getCenterYPos()) return 1;
        return 0;
    }
    
}
