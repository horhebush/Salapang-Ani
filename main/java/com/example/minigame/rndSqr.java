package com.example.minigame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

import java.util.Random;

public class rndSqr {
    private static final Random rnd = new Random();
    public PointF pos; // Top-left corner of the circle's bounding box.
    public int size;   // Diameter of the circle.
    private Bitmap image;
    public float dx;
    public float dy;
    private int points;
    private boolean penalty;

    // New fields for rotation.
    private float angle = 0;         // Current rotation angle (in degrees).
    private float rotationSpeed;     // Rotation speed in degrees per update.

    public rndSqr(PointF pos, int size, Bitmap image, int speed) {
        this(pos, size, image,
                (float) (speed * Math.cos(Math.PI / 4 + rnd.nextFloat() * Math.PI / 2)),
                (float) (speed * Math.sin(Math.PI / 4 + rnd.nextFloat() * Math.PI / 2)),
                false);
    }

    public rndSqr(PointF pos, int size, Bitmap image, float dx, float dy, boolean penalty) {
        this.pos = pos;
        this.size = size;
        // Scale the image to fit the bounding box of the circle.
        this.image = Bitmap.createScaledBitmap(image, size, size, false);
        this.dx = dx;
        this.dy = dy;
        this.penalty = penalty;
        this.points = penalty ? -5 : (size < 100 ? 10 : 5);
        // Initialize rotation speed to a random value between -5 and +5 degrees per update.
        this.rotationSpeed = (rnd.nextFloat() - 0.5f) * 10;
    }

    public void update(int screenWidth, int screenHeight) {
        pos.x += dx;
        pos.y += dy;
        // Update the rotation angle.
        angle = (angle + rotationSpeed) % 360;
    }

    public void draw(Canvas c) {
        // Compute the center of the circle.
        float centerX = pos.x + size / 2f;
        float centerY = pos.y + size / 2f;

        // Save the current canvas state.
        c.save();
        // Rotate the canvas around the circle's center.
        c.rotate(angle, centerX, centerY);
        // Draw the image. Because the canvas is rotated,
        // the image will appear rotated.
        c.drawBitmap(image, pos.x, pos.y, null);
        // Restore the canvas to its original state.
        c.restore();

    }

    public boolean contains(PointF point) {
        float radius = size / 2f;
        float centerX = pos.x + radius;
        float centerY = pos.y + radius;
        // Add a buffer of x pixels
        float buffer = 10;
        float diffX = point.x - centerX;
        float diffY = point.y - centerY;
        return (diffX * diffX + diffY * diffY) <= ((radius + buffer) * (radius + buffer));
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public boolean isPenalty() {
        return penalty;
    }

    // Used to determine if an object is a fruit (non-penalty).
    public boolean isPointSquare() {
        return !penalty;
    }

}
