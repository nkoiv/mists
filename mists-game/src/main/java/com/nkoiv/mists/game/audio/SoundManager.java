/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.audio;

import com.nkoiv.mists.game.Mists;
import java.io.FileNotFoundException;
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
 * and music as Media
 * Based on Carl Dea's blogpost
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
        Media music = new Media(Paths.get(filename).toUri().toString());
        musicPlaylist.put(id, music);
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
        this.mediaPlayer.stop();
    }
    
    public void toggleMusicMute() {
        if (this.mediaPlayer.isMute()) {
            this.mediaPlayer.setMute(false);
        } else {
           this.mediaPlayer.setMute(true);
        }
    }
    
    public boolean isMusicMuted() {
        return this.mediaPlayer.isMute();
    }
    
    //TODO: Test init
    private void initializeSounds() {
        try {
            this.loadSoundEffects("weapon_blow", "audio/sounds/weapon_blow.wav");
        } catch (Exception e) {
            Mists.logger.warning("Tried to load audio/sounds/weapon_blow.wav but failed");
            return;
        }   
    }
    
    public void loadSoundEffects(String id, String filename) {
        
        AudioClip sound = new AudioClip(Paths.get(filename).toUri().toString());
        soundEffects.put(id, sound);
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
