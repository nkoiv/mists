/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.audio;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;

/**
 *
 * @author nikok
 */
public class SoundManagerOgg implements SoundManager {

    private HashMap<String, OggClip> soundEffects = new HashMap<>();
    private HashMap<String, OggClip> musicPlaylist = new HashMap<>();
    private OggClip currentMusic;
    private double musicVolume;
    
    public SoundManagerOgg() {
        this.musicVolume = 1;
        this.initializeSounds();
        this.initializePlaylist();
        
    }
    
    private void initializeSounds() {
        
    }
    
    private void initializePlaylist() {
        loadMusicIntoPlaylist("town", "audio/music/Town_-_Small_Town_Welcome_(CS).ogg");
        loadMusicIntoPlaylist("menu", "audio/music/Town_-_Small_Town_Welcome_(CS).ogg");
        loadMusicIntoPlaylist("dungeon", "audio/music/Town_-_Small_Town_Welcome_(CS).ogg");
    }
    
    public void loadSoundEffects(String id, String filename) {
        try {
            OggClip sound = new OggClip(new FileInputStream(filename));
            //OggClip sound = new OggClip(Paths.get(filename).toUri().toString());
            soundEffects.put(id, sound);
        } catch (Exception e) {
            Mists.logger.log(Level.WARNING, "Failed to load {0} - file missing?", Paths.get(filename).toUri().toString());
            Mists.logger.info(e.toString());
        }
    }
    
    public void loadMusicIntoPlaylist(String id, String filename) {
        try {
            OggClip music = new OggClip(new FileInputStream(filename));
            //OggClip music = new OggClip(Paths.get(filename).toUri().toString());
            musicPlaylist.put(id, music);
        } catch (Exception e) {
            Mists.logger.log(Level.WARNING, "Tried to load [{0}] but failed", Paths.get(filename).toUri().toString());
            Mists.logger.info(e.toString());
        }
        
    }
    
    @Override
    public boolean isMusicMuted() {
        if (this.currentMusic != null) {
            return currentMusic.isPaused();
        }
        return false;
    }

    @Override
    public void playMusic(String id) {
        if (this.musicPlaylist.containsKey(id)) {
            currentMusic = this.musicPlaylist.get(id);
            currentMusic.setGain((float)musicVolume);
            currentMusic.play();
        }
    }

    @Override
    public void playSound(String id) {
        if (this.soundEffects.containsKey(id)) {
            this.soundEffects.get(id).pause();
        }
    }

    @Override
    public void setMusicVolume(double volume) {
        this.musicVolume = volume;
        if (this.currentMusic != null) currentMusic.setGain((float)volume);
    }
    
    @Override
    public double getMusicVolume() {
        return this.musicVolume;
    }
    
    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopMusic() {
        if (this.currentMusic != null) {
            currentMusic.stop();
        }
    }

    @Override
    public void toggleMusicMute() {
        if (this.currentMusic != null) {
            if (currentMusic.isPaused()) currentMusic.resume();
            else currentMusic.pause();
        }
    }
    
}
