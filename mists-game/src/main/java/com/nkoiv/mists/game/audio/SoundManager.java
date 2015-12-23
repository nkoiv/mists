/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.audio;

import com.nkoiv.mists.game.Mists;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * SoundManager loads sound effects as AudioClips
 * and music as Media.
 * Based on Carl Dea's blogpost:
 * https://carlfx.wordpress.com/2012/08/26/javafx-2-gametutorial-part-5/
 * @author nikok
 */
public class SoundManager {
    
    ExecutorService soundPool = Executors.newFixedThreadPool(2);
    HashMap<String, AudioClip> soundEffects = new HashMap<>();
    HashMap<String, Media> musicPlaylist = new HashMap<>();
    private MediaPlayer mediaPlayer;
    
    public SoundManager(int numberOfThreads) {
        soundPool = Executors.newFixedThreadPool(numberOfThreads);
        this.initializeSounds();
        this.initializePlaylist();
        this.initializeMediaPlayer();
    }
    
    private void initializeMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer(musicPlaylist.get("menu")); 
        } catch (Exception e) {
            Mists.logger.warning("Unable to create mediaplayer - media missing?");
            return;
        }
        mediaPlayer.setVolume(0.3);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
    }
    
    private void initializePlaylist(){
        //TODO: Load these from external file.
        try {
            loadMusicIntoPlaylist("menu","audio/music/JDB_Innocence.mp3");
        } catch (Exception e) {
            Mists.logger.warning("Tried to load audio/music/JDB_Innocence.mp3 but failed");
        }
        
        try {
            loadMusicIntoPlaylist("dungeon", "audio/music/Dungeon_-_Dungeon_Delvers_(CS).mp3");
        } catch (Exception e) {
            Mists.logger.warning("Tried to load audio/music/Dungeon_-_Dungeon_Delvers_(CS).mp3 but failed");
        }
        
    }
    
    public void loadMusicIntoPlaylist(String id, String filename) {
        try {
            Media music = new Media(Paths.get(filename).toUri().toString());
            musicPlaylist.put(id, music);
        } catch (Exception e) {
            Mists.logger.log(Level.WARNING, "Tried to [{0}] but failed", filename);
        }
        
    }
    
    public void playMusic (String id) {
        if (this.musicPlaylist.containsKey(id)) {
            MediaPlayer newPlayer = new MediaPlayer(this.musicPlaylist.get(id));
            newPlayer.setVolume(mediaPlayer.getVolume());
            newPlayer.setOnEndOfMedia(mediaPlayer.getOnEndOfMedia());
            this.mediaPlayer = newPlayer;
        } else {
            Mists.logger.log(Level.WARNING, "Tried to play music with id <{0}>, but it was not in the playlist", id);
            return;
        }
        this.mediaPlayer.play();
    }
    
    public void stopMusic() {
        try {
            this.mediaPlayer.stop();
        } catch (Exception e) {
            Mists.logger.warning("Stopping mediaplayer failed - player missing?");
        }
        
    }
    
    public void toggleMusicMute() {
        try {
            if (this.mediaPlayer.isMute()) {
              this.mediaPlayer.setMute(false);
            } else {
                this.mediaPlayer.setMute(true);
            } 
        } catch (Exception e) {
            Mists.logger.warning("Tried to toggle media player mute-status, but failed. Player missing?");
        }
        
    }
    
    public boolean isMusicMuted() {
        return this.mediaPlayer.isMute();
    }
    
    //TODO: Test init
    private void initializeSounds() {
        this.loadSoundEffects("weapon_blow", "audio/sounds/weapon_blow.wav");
        /*
        this.loadSoundEffects("woosh1", "audio/sounds/woosh/woosh1.ogg");
        this.loadSoundEffects("woosh2", "audio/sounds/woosh/woosh2.ogg");
        this.loadSoundEffects("woosh3", "audio/sounds/woosh/woosh3.ogg");
        this.loadSoundEffects("woosh4", "audio/sounds/woosh/woosh4.ogg");
        this.loadSoundEffects("woosh5", "audio/sounds/woosh/woosh5.ogg");
        this.loadSoundEffects("woosh6", "audio/sounds/woosh/woosh6.ogg");
        this.loadSoundEffects("woosh7", "audio/sounds/woosh/woosh7.ogg");
        this.loadSoundEffects("woosh8", "audio/sounds/woosh/woosh8.ogg");
        */
    }
    
    public void loadSoundEffects(String id, String filename) {
        try {
            AudioClip sound = new AudioClip(Paths.get(filename).toUri().toString());
            soundEffects.put(id, sound);
        } catch (Exception e) {
            Mists.logger.log(Level.WARNING, "Failed to load {0} - file missing?", filename);
        }
    }
    
    public void playSound(final String id) {
        if(!this.soundEffects.containsKey(id)) {
            Mists.logger.log(Level.WARNING, "Tried to play soundEffect with the id <{0}>, but it was not found.", id);
            return;
        }
        Runnable soundPlay = new Runnable() {
            @Override
            public void run() {
                soundEffects.get(id).play();
            }
        };
        soundPool.execute(soundPlay);
    }
    
    public void shutdown() {
        soundPool.shutdown();
    }
    
}
