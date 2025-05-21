package com.example.mtc_app.splashScreen;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.mtc_app.R;
import com.example.mtc_app.admin.AdminHomePageActivity;
import com.example.mtc_app.customer.CustomerHomePageActivity;
import com.example.mtc_app.customerRepresentative.CrMain;
import com.example.mtc_app.login.CustomerLoginActivity;
import com.example.mtc_app.staff.staff_home;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.skyfishjy.library.RippleBackground;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final int SPLASH_DELAY = 3000; // 3 seconds
    private Handler handler = new Handler();
    private View pulseCircle;
    private ValueAnimator pulseAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen for immersive experience
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();

        // Set up background resources
        setupBackgroundResources();

        // Initialize and start animations
        initializeAnimations();

        // Handle navigation after animations complete
        handler.postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String userRole = sharedPreferences.getString("userRole", "");

            // Check if user is logged in
            if (isLoggedIn && auth.getCurrentUser() != null) {
                navigateToHome(userRole);
            } else {
                // Start login activity with reveal animation
                startLoginActivityWithAnimation();
            }
        }, SPLASH_DELAY);
    }

    private void setupBackgroundResources() {
        // Create gradient background programmatically
        View backgroundGradient = findViewById(R.id.backgroundGradient);
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {
                        Color.parseColor("#F8F9FA"),
                        Color.parseColor("#E1F5FE")
                });
        backgroundGradient.setBackground(gradient);

        // Set up pulse circle outline
        pulseCircle = findViewById(R.id.pulseCircle);
        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setStroke(3, Color.parseColor("#2196F3"));
        pulseCircle.setBackground(circleDrawable);
    }

    private void initializeAnimations() {
        try {
            // Find views
            final RippleBackground rippleBackground = findViewById(R.id.rippleBackground);
            final CircularProgressBar progressBar = findViewById(R.id.progressBar);
            final CardView logoContainer = findViewById(R.id.logoContainer);
            final TextView brandText = findViewById(R.id.brandText);
            final TextView taglineText = findViewById(R.id.taglineText);
            final View particleView = findViewById(R.id.particleView);

            // Start particle animation
            if (particleView instanceof ParticlesView) {
                ((ParticlesView) particleView).startAnimation();
            }

            // Start ripple animation with slight delay
            handler.postDelayed(() -> {
                rippleBackground.startRippleAnimation();
            }, 300);

            // Scaling and rotation animation for logo container
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.1f, 1f);
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.1f, 1f);
            PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, -30f, 0f);

            ObjectAnimator logoAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    logoContainer, scaleX, scaleY, rotation);
            logoAnimator.setDuration(1000);
            logoAnimator.setInterpolator(new AnticipateOvershootInterpolator(1.2f));
            logoAnimator.start();

            // Pulse animation for outer circle
            pulseAnimator = ValueAnimator.ofFloat(0.8f, 1.05f, 0.8f);
            pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
            pulseAnimator.setDuration(2000);
            pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            pulseAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                pulseCircle.setScaleX(value);
                pulseCircle.setScaleY(value);
                pulseCircle.setAlpha(1.2f - value);
            });
            pulseAnimator.start();

            // Progress animation
            ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 100);
            progressAnimator.setDuration(SPLASH_DELAY - 200);
            progressAnimator.setInterpolator(new DecelerateInterpolator());
            progressAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                progressBar.setProgress(value);
            });
            progressAnimator.start();

            // Rotate animation for progress bar
            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(progressBar, View.ROTATION, 0f, 360f);
            rotateAnimator.setDuration(10000);
            rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
            rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            rotateAnimator.start();

            // Text animations with sequential timing
            handler.postDelayed(() -> {
                // Brand text reveal animation
                ObjectAnimator brandFade = ObjectAnimator.ofFloat(brandText, View.ALPHA, 0f, 1f);
                ObjectAnimator brandSlide = ObjectAnimator.ofFloat(brandText, View.TRANSLATION_Y, 50f, 0f);
                ObjectAnimator brandScale = ObjectAnimator.ofFloat(brandText, View.SCALE_X, 0.7f, 1f);

                AnimatorSet brandSet = new AnimatorSet();
                brandSet.playTogether(brandFade, brandSlide, brandScale);
                brandSet.setDuration(700);
                brandSet.setInterpolator(new OvershootInterpolator(1.2f));
                brandSet.start();

                // Tagline text reveal animation with delay
                handler.postDelayed(() -> {
                    ObjectAnimator taglineFade = ObjectAnimator.ofFloat(taglineText, View.ALPHA, 0f, 1f);
                    ObjectAnimator taglineSlide = ObjectAnimator.ofFloat(taglineText, View.TRANSLATION_Y, 30f, 0f);

                    AnimatorSet taglineSet = new AnimatorSet();
                    taglineSet.playTogether(taglineFade, taglineSlide);
                    taglineSet.setDuration(600);
                    taglineSet.setInterpolator(new DecelerateInterpolator());
                    taglineSet.start();
                }, 200);
            }, 900);

        } catch (Exception e) {
            // Fail gracefully
            e.printStackTrace();
        }
    }

    private void startLoginActivityWithAnimation() {
        Intent intent = new Intent(this, CustomerLoginActivity.class);
        startActivity(intent);

        // Apply slide up transition
        overridePendingTransition(R.anim.slide_up_in, R.anim.fade_out);
        finish();
    }

    private void navigateToHome(String userRole) {
        Intent intent;
        switch (userRole.toLowerCase()) {
            case "admin":
                intent = new Intent(this, AdminHomePageActivity.class);
                break;
            case "customer":
                intent = new Intent(this, CustomerHomePageActivity.class);
                break;
            case "staff":
                intent = new Intent(this, staff_home.class);
                break;
            case "cr":
                intent = new Intent(this, CrMain.class);
                break;
            default:
                Toast.makeText(this, "Unknown role: " + userRole, Toast.LENGTH_SHORT).show();
                intent = new Intent(this, CustomerLoginActivity.class);
                break;
        }
        startActivity(intent);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean up animators
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
        }

        // Remove callbacks to prevent leaks
        handler.removeCallbacksAndMessages(null);
    }
}