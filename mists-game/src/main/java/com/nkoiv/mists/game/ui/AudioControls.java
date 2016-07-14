/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;

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
