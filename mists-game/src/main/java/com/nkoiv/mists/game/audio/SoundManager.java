/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.audio;

/**
 *
 * @author nikok
 */
public interface SoundManager {

    boolean isMusicMuted();

    void playMusic(String id);

    void playSound(final String id);

    void shutdown();

    void stopMusic();
    
    double getMusicVolume();
    void setMusicVolume(double volume);

    void toggleMusicMute();
    
}
