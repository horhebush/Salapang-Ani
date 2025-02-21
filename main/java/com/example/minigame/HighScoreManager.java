package com.example.minigame;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreManager {
    private static final String PREFS_NAME = "GameHighScorePrefs";
    private static final String KEY_HIGHSCORE = "highscore";
    private static HighScoreManager instance;
    private SharedPreferences prefs;

    private HighScoreManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static HighScoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new HighScoreManager(context);
        }
        return instance;
    }

    public int getHighScore() {
        return prefs.getInt(KEY_HIGHSCORE, 0);
    }

    /**
     * Updates the high score if newScore is higher.
     * @param newScore the new score
     * @return true if the high score was updated
     */
    public boolean updateHighScore(int newScore) {
        int currentHighScore = getHighScore();
        if (newScore > currentHighScore) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_HIGHSCORE, newScore);
            editor.apply();
            return true;
        }
        return false;
    }
}
