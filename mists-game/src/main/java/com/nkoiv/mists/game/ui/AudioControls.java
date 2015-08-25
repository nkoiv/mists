/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 * AudioControls contains the button elements for controlling
 * the SoundManager (Music and Soundeffects).
 * @author nikok
 */
public class AudioControls {
    
    public static int MUTE_MUSIC = 1;
    
    public AudioControls() {
        
    }
    
    public static class MuteMusicButton extends TextButton {
        
        public MuteMusicButton(String name, double width, double height) {
            super(name, width, height);
        }
        
        @Override
        public void onClick(MouseEvent me) {
            Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
            Mists.soundManager.toggleMusicMute();
            if (Mists.soundManager.isMusicMuted()) {
                this.setText("Music muted");
            } else {
                this.setText("Mute music");
            }
        }
        
    }

}
