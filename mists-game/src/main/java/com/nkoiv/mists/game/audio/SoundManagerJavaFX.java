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
public class SoundManagerJavaFX implements SoundManager {
    
    private ExecutorService soundPool = Executors.newFixedThreadPool(2);
    private HashMap<String, AudioClip> soundEffects = new HashMap<>();
    private HashMap<String, Media> musicPlaylist = new HashMap<>();
    private MediaPlayer mediaPlayer;
    
    public SoundManagerJavaFX(int numberOfThreads) {
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
        loadMusicIntoPlaylist("menu","audio/music/JDB_Innocence.mp3");
        loadMusicIntoPlaylist("dungeon", "audio/music/Dungeon_-_Dungeon_Delvers_(CS).mp3");
        loadMusicIntoPlaylist("town", "audio/music/Town_-_Small_Town_Welcome_(CS).mp3");
        loadMusicIntoPlaylist("memories", "audio/music/Theme_-_Good_Memories_(MD).mp3");
        
    }
    
    public void loadMusicIntoPlaylist(String id, String filename) {
        try {
            Media music = new Media(Paths.get(filename).toUri().toString());
            musicPlaylist.put(id, music);
        } catch (Exception e) {
            Mists.logger.log(Level.WARNING, "Tried to [{0}] but failed", filename);
        }
        
    }
    
    @Override
    public void playMusic (String id) {
        if ("none".equals(id)) {
            if (this.mediaPlayer != null) this.mediaPlayer.stop(); //Stop the old music
            return;
        }
        if (this.musicPlaylist.containsKey(id)) {
            MediaPlayer newPlayer = new MediaPlayer(this.musicPlaylist.get(id));
            newPlayer.setVolume(mediaPlayer.getVolume());
            newPlayer.setOnEndOfMedia(mediaPlayer.getOnEndOfMedia());
            if (this.mediaPlayer != null) this.mediaPlayer.stop(); //Stop the old music
            this.mediaPlayer = newPlayer;
            Mists.logger.info("Music "+id+" found, starting playback");
        } else {
            Mists.logger.log(Level.WARNING, "Tried to play music with id <{0}>, but it was not in the playlist", id);
            return;
        }
        this.mediaPlayer.play();
    }
    
    @Override
    public void stopMusic() {
        try {
            this.mediaPlayer.stop();
        } catch (Exception e) {
            Mists.logger.warning("Stopping mediaplayer failed - player missing?");
        }
        
    }
    
    @Override
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
    
    @Override
    public boolean isMusicMuted() {
        return this.mediaPlayer.isMute();
    }
    
    //TODO: Test init
    private void initializeSounds() {
        this.loadSoundEffects("weapon_blow", "audio/sounds/weapon_blow.wav");
        this.loadSoundEffects("weapon_swing", "audio/sounds/weapon_swing.mp3");
        this.loadSoundEffects("bow_shoot", "audio/sounds/bow_shoot.mp3");
        this.loadSoundEffects("flame_woosh", "audio/sounds/flame_woosh.mp3");
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
    
    public void setMusicVolume(double volume) {
        this.mediaPlayer.setVolume(volume);
    }
    
    public double getMusicVolume() {
        return this.mediaPlayer.getVolume();
    }
    
    @Override
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
    
    @Override
    public void shutdown() {
        soundPool.shutdown();
    }
    
}
