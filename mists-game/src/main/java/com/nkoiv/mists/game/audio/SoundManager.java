/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.audio;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.media.AudioClip;

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
    
    public SoundManager(int numberOfThreads) {
        soundPool = Executors.newFixedThreadPool(numberOfThreads);
        this.initializeSounds();
    }
    
    //TODO: Test init
    private void initializeSounds() {
        this.loadSoundEffects("attack", "src/main/resources/audio/sounds/weapon_blow.wav");
    }
    
    public void loadSoundEffects(String id, String filename) {
        
        AudioClip sound = new AudioClip(Paths.get(filename).toUri().toString());
        soundEffects.put(id, sound);
    }
    
    public void playSound(final String id) {
        if(!this.soundEffects.containsKey(id)) {
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
