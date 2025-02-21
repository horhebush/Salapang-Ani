package com.example.minigame;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainUI extends AppCompatActivity {

    private MediaPlayer mediaPlayer, clickSound;
    private ImageView movingImageView, salapangLogo;
    private TextView salapangText, aniText;
    private ImageButton playButton, playButtonMenu, playSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);


        // Initialize MediaPlayer for background music
        mediaPlayer = MediaPlayer.create(this, R.raw.bgmusic);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Initialize click sound
        clickSound = MediaPlayer.create(this, R.raw.click_sound);

        // Find Views
        movingImageView = findViewById(R.id.moving_image);
        salapangLogo = findViewById(R.id.salapanglogo);
        salapangText = findViewById(R.id.salapang_text);
        aniText = findViewById(R.id.ani_text);
        playButton = findViewById(R.id.myImageButton);
        playButtonMenu = findViewById(R.id.menubutton);
        playSettingsButton = findViewById(R.id.settingsbutton);

        // Apply animation to all elements
        animateView(movingImageView);
        animateView(salapangLogo);
        animateView(salapangText);
        animateView(aniText);

        // Play button animation on click
        playButton.setOnClickListener(v -> {
            // Load pop animation
            Animation popAnimation = AnimationUtils.loadAnimation(this, R.anim.button_pop);
            playButton.startAnimation(popAnimation);

            // Play click sound effect
            if (clickSound != null) {
                clickSound.start();
            }

            Intent intent = new Intent(MainUI.this, GameStart.class);
            startActivity(intent);
        });

        // Open Menu Dialog
        playButtonMenu.setOnClickListener(v -> {
            Animation popAnimation = AnimationUtils.loadAnimation(this, R.anim.button_pop);
            playButtonMenu.startAnimation(popAnimation);

            if (clickSound != null) {
                clickSound.start();
            }

            showMenuDialog();
        });

        // Open Settings Dialog
        playSettingsButton.setOnClickListener(v -> {
            Animation popAnimation = AnimationUtils.loadAnimation(this, R.anim.button_pop);
            playSettingsButton.startAnimation(popAnimation);

            if (clickSound != null) {
                clickSound.start();
            }

            showSettingsDialog();
        });
    }

    // Function to animate views (Floating effect)
    private void animateView(Object view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0f, 100f);
        animator.setDuration(1000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    // Function to show Menu Dialog
    // Function to show Menu Dialog
    private void showMenuDialog() {
        Dialog menuDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        menuDialog.setContentView(R.layout.menu_layout);
        menuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuDialog.setCancelable(false);

        ImageButton menuCloseButton = menuDialog.findViewById(R.id.menuCloseButton);
        ImageButton paanoLaroinButton = menuDialog.findViewById(R.id.howToPlay);

        menuCloseButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pop));
            if (clickSound != null) clickSound.start();

            menuDialog.findViewById(android.R.id.content).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            new android.os.Handler().postDelayed(() -> {
                menuDialog.dismiss();
            }, 300);

        });

        paanoLaroinButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pop));
            if (clickSound != null) clickSound.start();

            menuDialog.findViewById(android.R.id.content).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));

            // Delay opening Mechanics Dialog until the fade-out finishes
            new android.os.Handler().postDelayed(() -> {
                menuDialog.dismiss();
                showMechanicsDialog();
            }, 300);
        });

        menuDialog.show();
        menuDialog.findViewById(android.R.id.content).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }


    private void showMechanicsDialog() {
        Dialog mechanicsDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mechanicsDialog.setContentView(R.layout.mechanics_layout);
        mechanicsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mechanicsDialog.setCancelable(false);

        ImageButton mechanicsCloseButton = mechanicsDialog.findViewById(R.id.mechanicsCloseButton);

        mechanicsCloseButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_pop));
            if (clickSound != null) clickSound.start();

            mechanicsDialog.findViewById(android.R.id.content).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));

            // Delay opening Menu Dialog until fade-out finishes
            new android.os.Handler().postDelayed(() -> {
                mechanicsDialog.dismiss();
                showMenuDialog();
            }, 300);
        });

        mechanicsDialog.show();
        mechanicsDialog.findViewById(android.R.id.content).startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }



    private void showSettingsDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.settings_layout);

        // Make background outside transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Prevent accidental dismiss when tapping outside
        dialog.setCancelable(false);

        // Close button functionality
        ImageButton settingsCloseButton = dialog.findViewById(R.id.settingsCloseButton);
        settingsCloseButton.setOnClickListener(v -> {
            // Load pop animation
            Animation popAnimation = AnimationUtils.loadAnimation(this, R.anim.button_pop);
            settingsCloseButton.startAnimation(popAnimation);

            // Play click sound effect
            if (clickSound != null) {
                clickSound.start();
            }

            // Delay the dismissal slightly to ensure the sound plays
            settingsCloseButton.postDelayed(dialog::dismiss, 150);
        });

        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
    }
}
