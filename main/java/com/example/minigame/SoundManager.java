package com.example.minigame;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

public class SoundManager {

    private static SoundManager instance;

    private SoundPool soundPool;
    private int[] fruitSoundIDs;
    private int[] flowerSoundIDs;
    private int[] pestSoundIDs;
    private int gameOverSoundId;
    private int loseLifeSoundId;
    private int popSoundId; // Added Pop Sound Effect

    private int gameOverStreamId = 0;
    private float pestVolume = 10.5f;

    private int[] fruitSoundResourceIDs = {
            R.raw.fruit_tap1, R.raw.fruit_tap2, R.raw.fruit_tap3
    };
    private int[] flowerSoundResourceIDs = {
            R.raw.flower_tap1, R.raw.flower_tap2, R.raw.flower_tap3
    };
    private int[] pestSoundResourceIDs = {
            R.raw.pest_tap1, R.raw.pest_tap2
    };

    private MediaPlayer bgmPlayer;

    private SoundManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) {
                Log.d("SoundManager", "Sound loaded successfully: " + sampleId);
            } else {
                Log.e("SoundManager", "Sound failed to load: " + sampleId);
            }
        });

        fruitSoundIDs = new int[fruitSoundResourceIDs.length];
        flowerSoundIDs = new int[flowerSoundResourceIDs.length];
        pestSoundIDs = new int[pestSoundResourceIDs.length];

        for (int i = 0; i < fruitSoundResourceIDs.length; i++) {
            fruitSoundIDs[i] = soundPool.load(context, fruitSoundResourceIDs[i], 1);
        }

        for (int i = 0; i < flowerSoundResourceIDs.length; i++) {
            flowerSoundIDs[i] = soundPool.load(context, flowerSoundResourceIDs[i], 1);
        }

        for (int i = 0; i < pestSoundResourceIDs.length; i++) {
            pestSoundIDs[i] = soundPool.load(context, pestSoundResourceIDs[i], 1);
        }

        gameOverSoundId = soundPool.load(context, R.raw.game_over, 1);
        loseLifeSoundId = soundPool.load(context, R.raw.lose_life, 1);
        popSoundId = soundPool.load(context, R.raw.click_sound, 1); // Load Pop Sound

        bgmPlayer = MediaPlayer.create(context, R.raw.background_music);
        bgmPlayer.setLooping(true);
        bgmPlayer.setVolume(0.5f, 0.5f);
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context.getApplicationContext());
        }
        return instance;
    }

    public void playFruitTap() {
        int index = (int) (Math.random() * fruitSoundIDs.length);
        soundPool.play(fruitSoundIDs[index], 0.7f, 0.7f, 1, 0, 1f);
    }

    public void playFlowerTap() {
        int index = (int) (Math.random() * flowerSoundIDs.length);
        soundPool.play(flowerSoundIDs[index], 0.7f, 0.7f, 1, 0, 1f);
    }

    public void playPestTap() {
        int index = (int) (Math.random() * pestSoundIDs.length);
        soundPool.play(pestSoundIDs[index], pestVolume, pestVolume, 1, 0, 1f);
    }

    public void playLoseLifeSfx() {
        soundPool.play(loseLifeSoundId, 1f, 1f, 1, 0, 1f);
    }

    public void playGameOverSfx() {
        gameOverStreamId = soundPool.play(gameOverSoundId, 1f, 1f, 1, 0, 1f);
    }

    public void pauseGameOverSfx() {
        if (gameOverStreamId != 0) {
            soundPool.stop(gameOverStreamId);
            gameOverStreamId = 0;
        }
    }

    public void playPopSound() {
        soundPool.play(popSoundId, 1f, 1f, 1, 0, 1f);
    }

    public void startBgm() {
        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    public void pauseBgm() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    public void release() {
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}
