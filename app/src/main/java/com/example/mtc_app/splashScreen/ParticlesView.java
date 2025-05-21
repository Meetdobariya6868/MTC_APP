package com.example.mtc_app.splashScreen;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A custom view that creates an animated particle background effect
 * Based on the Lottie JSON animation from the example, but implemented natively
 */
public class ParticlesView extends View {

    private static final int PARTICLE_COUNT = 30;
    private static final int MAX_PARTICLE_SIZE = 10;
    private static final int MIN_PARTICLE_SIZE = 3;
    private static final int ANIMATION_DURATION = 15000;

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int viewWidth;
    private int viewHeight;
    private ValueAnimator animator;

    public ParticlesView(Context context) {
        super(context);
        init();
    }

    public ParticlesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParticlesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Set up the paint for particles
        particlePaint.setStyle(Paint.Style.FILL);

        // Default to semi-transparent blue, matches the theme
        particlePaint.setColor(Color.parseColor("#332196F3"));

        // Setup animation
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIMATION_DURATION);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            updateParticles(progress);
            invalidate();
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;

        // Initialize particles once we know the view dimensions
        createParticles();
    }

    private void createParticles() {
        particles.clear();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            Particle particle = new Particle();

            // Random size between min and max
            particle.size = MIN_PARTICLE_SIZE + random.nextInt(MAX_PARTICLE_SIZE - MIN_PARTICLE_SIZE);

            // Random opacity between 10-40%
            particle.alpha = 25 + random.nextInt(30);

            // Random starting position off-screen (left or top)
            if (random.nextBoolean()) {
                // Start from left
                particle.startX = -particle.size * 2;
                particle.startY = random.nextInt(viewHeight);
                particle.endX = viewWidth + particle.size * 2;
                particle.endY = random.nextInt(viewHeight);
            } else {
                // Start from top
                particle.startX = random.nextInt(viewWidth);
                particle.startY = -particle.size * 2;
                particle.endX = random.nextInt(viewWidth);
                particle.endY = viewHeight + particle.size * 2;
            }

            // Random shape (circle, square, or triangle)
            particle.shape = random.nextInt(3);

            // Random delay for start of particle animation
            particle.delay = random.nextFloat();

            // Random duration multiplier (0.7 to 1.3)
            particle.durationMultiplier = 0.7f + (random.nextFloat() * 0.6f);

            particles.add(particle);
        }
    }

    private void updateParticles(float globalProgress) {
        for (Particle particle : particles) {
            // Apply individual delay and duration
            float adjustedProgress = (globalProgress + particle.delay) % 1.0f;
            adjustedProgress = adjustedProgress / particle.durationMultiplier;
            if (adjustedProgress > 1.0f) adjustedProgress = adjustedProgress % 1.0f;

            // Calculate current position
            particle.currentX = particle.startX + (particle.endX - particle.startX) * adjustedProgress;
            particle.currentY = particle.startY + (particle.endY - particle.startY) * adjustedProgress;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw each particle
        for (Particle particle : particles) {
            // Skip if not visible
            if (particle.currentX < -particle.size || particle.currentX > viewWidth + particle.size ||
                    particle.currentY < -particle.size || particle.currentY > viewHeight + particle.size) {
                continue;
            }

            // Set opacity
            particlePaint.setAlpha(particle.alpha);

            // Draw based on shape type
            switch (particle.shape) {
                case 0: // Circle
                    canvas.drawCircle(particle.currentX, particle.currentY, particle.size, particlePaint);
                    break;

                case 1: // Square
                    canvas.drawRect(
                            particle.currentX - particle.size,
                            particle.currentY - particle.size,
                            particle.currentX + particle.size,
                            particle.currentY + particle.size,
                            particlePaint);
                    break;

                case 2: // Triangle
                    Path path = new Path();
                    path.moveTo(particle.currentX, particle.currentY - particle.size);
                    path.lineTo(particle.currentX + particle.size, particle.currentY + particle.size);
                    path.lineTo(particle.currentX - particle.size, particle.currentY + particle.size);
                    path.close();
                    canvas.drawPath(path, particlePaint);
                    break;
            }
        }
    }

    public void startAnimation() {
        if (animator != null && !animator.isRunning()) {
            animator.start();
        }
    }

    public void stopAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    /**
     * Class to hold particle properties
     */
    private static class Particle {
        float startX, startY;           // Starting position
        float endX, endY;               // Ending position
        float currentX, currentY;       // Current position
        int size;                       // Particle size
        int alpha;                      // Opacity (0-255)
        int shape;                      // 0=circle, 1=square, 2=triangle
        float delay;                    // Start delay (0-1)
        float durationMultiplier;       // Animation speed multiplier
    }
}