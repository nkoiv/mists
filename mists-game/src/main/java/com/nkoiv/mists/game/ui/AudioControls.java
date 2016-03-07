/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import java.util.logging.Level;
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
    
    public static class MuteMusicButton extends IconButton {
        
        public MuteMusicButton() {
            super("Mute music", 0, 0, Mists.graphLibrary.getImage("musicOnIcon"), Mists.graphLibrary.getImage("musicOffIcon"), Mists.graphLibrary.getImage("buttonSquareBeige"), Mists.graphLibrary.getImage("buttonSquareBeigePressed"));
            this.alpha = 0.5;
        }
        
        @Override
        public void handleMouseEvent(MouseEvent me) {
            if (me.getEventType() == MouseEvent.MOUSE_PRESSED) this.pressed = true;
            if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                this.pressed = false;
                Mists.logger.log(Level.INFO, "{0} was clicked", this.getName());
                Mists.soundManager.toggleMusicMute();
                if (Mists.soundManager.isMusicMuted()) {
                    this.drawAlt = true;
                } else {
                    this.drawAlt = false;
                }
            }
            me.consume();
        }
        
    }

}
