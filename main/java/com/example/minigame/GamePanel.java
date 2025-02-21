package com.example.minigame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.Color;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private ArrayList<rndSqr> squares = new ArrayList<>();
    private Random rnd = new Random();
    private boolean running = true;
    private Handler handler = new Handler();
    private int score = 0;
    private int lives = 3;
    // Wave parameters
    private int squaresPerWave = 3;
    private int waveSpeed = 20;  // Constant fast speed throughout the game.
    private int waveCount = 0;
    // Initial pest probability: 15%
    private double pestProbability = 0.15;

    private long gameStartTime;
    private long gameDuration = 2 * 60 * 1000; // 2 minutes
    private Bitmap background;

    private int[] imageResources = {
            R.drawable.banana,
            R.drawable.apple,
            R.drawable.cherry,
            R.drawable.mango,
            R.drawable.plum,
            R.drawable.strawberry,
            R.drawable.raspberry,
            R.drawable.orange
    };

    // Array for pests (randomized images)
    private int[] pestImageResources = {
            R.drawable.pest1,
            R.drawable.pest2,
            R.drawable.pest3
    };

    // Array of flower images (the +5 object)
    private int[] flowerImageResource = {
            R.drawable.flower1,
            R.drawable.flower2,
            R.drawable.flower3
    };

    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        // Ensure any lingering game over sound is paused.
        SoundManager.getInstance(getContext()).pauseGameOverSfx();
    }

    /**
     * Spawns waves of objects. When there are no non-penalty (point-giving) objects on screen,
     * a new wave is spawned.
     */
    private void startWaves() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!running) return;

                long elapsedTime = System.currentTimeMillis() - gameStartTime;
                if (elapsedTime >= gameDuration || lives <= 0) {
                    running = false;
                    handler.removeCallbacksAndMessages(null);
                    showGameOverScreen();
                    return;
                }

                // If no point-giving objects remain, spawn a new wave.
                if (squares.stream().noneMatch(rndSqr::isPointSquare)) {
                    spawnWave(squaresPerWave, waveSpeed);
                    waveSpeed += 2.5;  // Increase base speed by 2.5 every wave.
                    waveCount++;

                    // Every 3 waves, increase number of objects per wave by 2 (capped at 8).
                    if (waveCount % 3 == 0) {
                        squaresPerWave = Math.min(squaresPerWave + 2, 8);
                    }

                    // Every wave, increase pest spawn probability by 0.002, capped at 0.5.
                    pestProbability = Math.min(pestProbability + 0.002, 0.5);
                }

                updateSquares();
                render();
                handler.postDelayed(this, 50);
            }
        }, 50);
    }

    /**
     * Spawns a wave of objects.
     * - With probability based on pestProbability, a penalty (pest) is spawned.
     * - Otherwise, a non-penalty is spawned:
     *    • 10% chance for a flower (worth +5)
     *    • Otherwise a fruit (worth +1)
     */
    private void spawnWave(int numSquares, int speed) {
        for (int i = 0; i < numSquares; i++) {
            int x = rnd.nextInt(getWidth() - 100);
            x = Math.max(x, 0);
            PointF pos = new PointF(x, 0);
            int size = 150;
            boolean isPenalty = (rnd.nextDouble() < pestProbability);
            Bitmap image;
            if (isPenalty) {
                int pestIndex = rnd.nextInt(pestImageResources.length);
                image = BitmapFactory.decodeResource(getResources(), pestImageResources[pestIndex]);
                // Pests fall 1.5 times faster.
                squares.add(new rndSqr(pos, size, image, 0, (int)(speed * 1.5), true));
            } else {
                if (rnd.nextDouble() < 0.1) {  // 10% chance for a flower
                    int flowerIndex = rnd.nextInt(flowerImageResource.length);
                    image = BitmapFactory.decodeResource(getResources(), flowerImageResource[flowerIndex]);
                    rndSqr obj = new rndSqr(pos, size, image, speed);
                    obj.setPoints(5);
                    squares.add(obj);
                } else {  // Otherwise, spawn a fruit.
                    int imageResId = imageResources[rnd.nextInt(imageResources.length)];
                    image = BitmapFactory.decodeResource(getResources(), imageResId);
                    rndSqr obj = new rndSqr(pos, size, image, speed);
                    obj.setPoints(1);
                    squares.add(obj);
                }
            }
        }
    }

    private void updateSquares() {
        Iterator<rndSqr> iterator = squares.iterator();
        while (iterator.hasNext()) {
            rndSqr square = iterator.next();
            square.update(getWidth(), getHeight());
            if (square.pos.y > getHeight()) {
                if (!square.isPenalty()) {
                    lives--;
                }
                iterator.remove();
            }
        }
        Collisions.checkCollisions(squares, getWidth(), getHeight());
    }

    private void render() {
        Canvas c = holder.lockCanvas();
        if (c != null) {
            c.drawBitmap(background, 0, 0, null);
            for (rndSqr square : squares) {
                square.draw(c);
            }

            Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/myfont.ttf");

            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setTextSize(70);
            paint.setTypeface(customFont);
            c.drawText("Puntos: " + score, 30, 80, paint);
            c.drawText("Buhay: " + lives, 30, 140, paint);

            long elapsedTime = System.currentTimeMillis() - gameStartTime;
            long timeRemaining = Math.max(0, gameDuration - elapsedTime);
            String timeText = String.format("%02d:%02d", (timeRemaining / 60000) % 60, (timeRemaining / 1000) % 60);

            Paint timerPaint = new Paint();
            timerPaint.setTypeface(customFont);
            timerPaint.setColor(Color.YELLOW);
            timerPaint.setTextSize(100);
            timerPaint.setTextAlign(Paint.Align.CENTER);
            c.drawText(timeText, getWidth() / 2, 80, timerPaint);

            holder.unlockCanvasAndPost(c);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_MOVE) {
            PointF touchPos = new PointF(event.getX(), event.getY());
            Iterator<rndSqr> iterator = squares.iterator();
            while (iterator.hasNext()) {
                rndSqr square = iterator.next();
                if (square.contains(touchPos)) {
                    if (square.isPenalty()) {
                        lives--;
                        SoundManager.getInstance(getContext()).playPestTap();
                    } else {
                        score += square.getPoints();
                        if (square.getPoints() == 5) {
                            SoundManager.getInstance(getContext()).playFlowerTap();
                        } else {
                            SoundManager.getInstance(getContext()).playFruitTap();
                        }
                    }
                    iterator.remove();
                }
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        running = true;
        gameStartTime = System.currentTimeMillis();

        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.gamebackground);
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        float scaleX = (float) screenWidth / originalBackground.getWidth();
        float scaleY = (float) screenHeight / originalBackground.getHeight();
        float scale = Math.max(scaleX, scaleY);
        int newWidth = Math.round(originalBackground.getWidth() * scale);
        int newHeight = Math.round(originalBackground.getHeight() * scale);
        Bitmap scaledBackground = Bitmap.createScaledBitmap(originalBackground, newWidth, newHeight, true);
        int xOffset = (newWidth - screenWidth) / 2;
        int yOffset = (newHeight - screenHeight) / 2;
        background = Bitmap.createBitmap(scaledBackground, xOffset, yOffset, screenWidth, screenHeight);

        SoundManager.getInstance(getContext()).startBgm();
        startWaves();
        render();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // No additional handling required.
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        running = false;
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Displays a game-over overlay by inflating the game_over.xml layout.
     * This overlay shows the final score, high score, and two buttons: "Play Again" and "Main Menu".
     */
    private void showGameOverScreen() {
        post(new Runnable() {
            @Override
            public void run() {
                SoundManager.getInstance(getContext()).pauseBgm();
                SoundManager.getInstance(getContext()).playGameOverSfx();

                // Update high score using HighScoreManager.
                HighScoreManager highScoreManager = HighScoreManager.getInstance(getContext());
                int currentHighScore = highScoreManager.getHighScore();
                if (score > currentHighScore) {
                    highScoreManager.updateHighScore(score);
                    currentHighScore = score;
                }

                if (getContext() instanceof GameStart) {
                    GameStart activity = (GameStart) getContext();
                    FrameLayout gameContainer = activity.findViewById(R.id.gameContainer);
                    gameContainer.removeAllViews();

                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View gameOverView = inflater.inflate(R.layout.game_over, null);

                    TextView scoreText = gameOverView.findViewById(R.id.finalScoreTextView);
                    scoreText.setText("Puntos: " + score);

                    TextView highScoreText = gameOverView.findViewById(R.id.highScoreTextView);
                    highScoreText.setText("Rurok ng Puntos: " + currentHighScore);

                    ImageButton playAgainButton = gameOverView.findViewById(R.id.playAgainButton);
                    ImageButton homeButton = gameOverView.findViewById(R.id.homeButton);

                    playAgainButton.setOnClickListener(v -> {
                        SoundManager.getInstance(getContext()).playPopSound();
                        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.button_pop));
                        new Handler().postDelayed(() -> activity.startGame(), 100);
                    });

                    homeButton.setOnClickListener(v -> {
                        SoundManager.getInstance(getContext()).playPopSound();
                        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.button_pop));
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(activity, MainUI.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }, 100);
                    });

                    gameContainer.addView(gameOverView);
                }
            }
        });
    }
}
