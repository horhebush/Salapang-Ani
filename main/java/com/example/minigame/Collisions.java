package com.example.minigame;

import java.util.ArrayList;

public class Collisions {

    /**
     * Checks and resolves collisions for all circles (fruits, flowers, and worms).
     * The bottom boundary is not handled (so objects that fall off remain removed in GamePanel).
     *
     * @param squares      List of game objects.
     * @param screenWidth  Screen width in pixels.
     * @param screenHeight Screen height in pixels.
     */
    public static void checkCollisions(ArrayList<rndSqr> squares, int screenWidth, int screenHeight) {
        // --- Boundary Collisions (using circle centers) ---
        for (rndSqr square : squares) {
            float radius = square.size / 2f;
            float centerX = square.pos.x + radius;
            float centerY = square.pos.y + radius;

            // Left boundary.
            if (centerX - radius < 0) {
                square.pos.x = 0;
                square.dx = Math.abs(square.dx);
            }
            // Right boundary.
            if (centerX + radius > screenWidth) {
                square.pos.x = screenWidth - square.size;
                square.dx = -Math.abs(square.dx);
            }
            // Top boundary.
            if (centerY - radius < 0) {
                square.pos.y = 0;
                square.dy = Math.abs(square.dy);
            }
            // Bottom boundary: intentionally do nothing.
        }

        // --- Inter-Object Collisions using circle collision detection ---
        int n = squares.size();
        for (int i = 0; i < n; i++) {
            rndSqr a = squares.get(i);
            for (int j = i + 1; j < n; j++) {
                rndSqr b = squares.get(j);

                // If both objects are non-penalty (fruit/flower) and at least one is a flower (points == 5),
                // skip collision processing.
                if (!a.isPenalty() && !b.isPenalty() && (a.getPoints() == 5 || b.getPoints() == 5)) {
                    continue;
                }

                if (circlesOverlap(a, b)) {
                    // Simple elastic collision: swap velocity vectors.
                    float tempDx = a.dx;
                    float tempDy = a.dy;
                    a.dx = b.dx;
                    a.dy = b.dy;
                    b.dx = tempDx;
                    b.dy = tempDy;

                    // Adjust positions so the circles are no longer overlapping.
                    resolveOverlap(a, b);
                }
            }
        }
    }

    /**
     * Determines whether two circles overlap.
     */
    private static boolean circlesOverlap(rndSqr a, rndSqr b) {
        float radiusA = a.size / 2f;
        float radiusB = b.size / 2f;
        float centerAX = a.pos.x + radiusA;
        float centerAY = a.pos.y + radiusA;
        float centerBX = b.pos.x + radiusB;
        float centerBY = b.pos.y + radiusB;

        float dx = centerAX - centerBX;
        float dy = centerAY - centerBY;
        float distanceSquared = dx * dx + dy * dy;
        float radiusSum = radiusA + radiusB;
        return distanceSquared < (radiusSum * radiusSum);
    }

    /**
     * Resolves the overlap between two circles by pushing them apart along the line
     * connecting their centers.
     */
    private static void resolveOverlap(rndSqr a, rndSqr b) {
        float radiusA = a.size / 2f;
        float radiusB = b.size / 2f;
        float centerAX = a.pos.x + radiusA;
        float centerAY = a.pos.y + radiusA;
        float centerBX = b.pos.x + radiusB;
        float centerBY = b.pos.y + radiusB;

        float dx = centerBX - centerAX;
        float dy = centerBY - centerAY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            // Avoid division by zero.
            dx = 1;
            dy = 0;
            distance = 1;
        }
        float overlap = (radiusA + radiusB) - distance;
        // Push each circle away half the overlap distance.
        float separationX = (dx / distance) * (overlap / 2);
        float separationY = (dy / distance) * (overlap / 2);

        a.pos.x -= separationX;
        a.pos.y -= separationY;
        b.pos.x += separationX;
        b.pos.y += separationY;
    }
}