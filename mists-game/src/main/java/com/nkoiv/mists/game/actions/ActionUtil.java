/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import java.util.Random;

/**
 * Static class for Action related utilities
 * @author nikok
 */
public class ActionUtil {
    
    
    public static String randomWoosh() {
        Random rnd = new Random();
        int n = rnd.nextInt(7)+1;
        String w = "woosh"+n;
        return w;
    }
    
    
}
