/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
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
