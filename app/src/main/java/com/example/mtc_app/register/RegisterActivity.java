package com.example.mtc_app.register;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mtc_app.R;
import com.example.mtc_app.login.CustomerLoginActivity;
import com.example.mtc_app.splashScreen.ParticlesView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // Existing fields
    private EditText nameField, emailField, passwordField, phoneField;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private TextView redirectToLogin;

    // Animation fields
    private View pulseCircle1, pulseCircle2, pulseCircle3;
    private CircularProgressBar backgroundProgressBar;
    private CardView registerCard;
    private Handler animationHandler;
    private ParticlesView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Initialize animations
        initializeAnimations();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Existing views
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        phoneField = findViewById(R.id.phoneField);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        redirectToLogin = findViewById(R.id.tv_redirect_to_login);

        // Animation views
        particleView = findViewById(R.id.particleView);
        pulseCircle1 = findViewById(R.id.pulseCircle1);
        pulseCircle2 = findViewById(R.id.pulseCircle2);
        pulseCircle3 = findViewById(R.id.pulseCircle3);
        backgroundProgressBar = findViewById(R.id.backgroundProgressBar);
        registerCard = findViewById(R.id.registerCard);

        animationHandler = new Handler();
    }

    private void initializeAnimations() {
        // Start animations after a short delay to ensure views are ready
        animationHandler.postDelayed(() -> {
            startPulseAnimations();
            startProgressAnimation();
            startRegisterCardAnimation();
        }, 100);
    }

    private void startPulseAnimations() {
        // Pulse Circle 1 Animation
        startContinuousPulse(pulseCircle1, 2000, 0.3f, 0.6f);

        // Pulse Circle 2 Animation (delayed)
        animationHandler.postDelayed(() -> {
            if (pulseCircle2 != null) {
                startContinuousPulse(pulseCircle2, 2500, 0.2f, 0.5f);
            }
        }, 500);

        // Pulse Circle 3 Animation (more delayed)
        animationHandler.postDelayed(() -> {
            if (pulseCircle3 != null) {
                startContinuousPulse(pulseCircle3, 3000, 0.25f, 0.55f);
            }
        }, 1000);
    }

    private void startContinuousPulse(View view, int duration, float fromAlpha, float toAlpha) {
        if (view == null) return;

        // Scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.8f, 1.2f,  // From 80% to 120% of original size
                0.8f, 1.2f,  // From 80% to 120% of original size
                Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point X
                Animation.RELATIVE_TO_SELF, 0.5f   // Pivot point Y
        );
        scaleAnimation.setDuration(duration);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        // Alpha animation
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", fromAlpha, toAlpha);
        alphaAnimator.setDuration(duration);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.REVERSE);
        alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // Start animations
        view.startAnimation(scaleAnimation);
        alphaAnimator.start();
    }

    private void startProgressAnimation() {
        if (backgroundProgressBar == null) return;

        // Continuous progress bar animation
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0f, 100f);
        progressAnimator.setDuration(4000);
        progressAnimator.setRepeatCount(ValueAnimator.INFINITE);
        progressAnimator.setRepeatMode(ValueAnimator.RESTART);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        progressAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            backgroundProgressBar.setProgress(progress);
        });

        progressAnimator.start();

        // Rotation animation for progress bar
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(backgroundProgressBar, "rotation", 0f, 360f);
        rotationAnimator.setDuration(8000);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotationAnimator.start();
    }

    private void startRegisterCardAnimation() {
        if (registerCard == null) return;

        // Initial entrance animation for register card
        registerCard.setAlpha(0f);
        registerCard.setScaleX(0.8f);
        registerCard.setScaleY(0.8f);
        registerCard.setTranslationY(100f);

        // Animate card entrance
        AnimatorSet cardAnimatorSet = new AnimatorSet();

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(registerCard, "alpha", 0f, 1f);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(registerCard, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(registerCard, "scaleY", 0.8f, 1f);
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(registerCard, "translationY", 100f, 0f);

        cardAnimatorSet.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator, translationAnimator);
        cardAnimatorSet.setDuration(800);
        cardAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        cardAnimatorSet.setStartDelay(300);

        cardAnimatorSet.start();

        // Subtle floating animation for register card
        startFloatingAnimation();
    }

    private void startFloatingAnimation() {
        if (registerCard == null) return;

        ObjectAnimator floatingAnimator = ObjectAnimator.ofFloat(registerCard, "translationY", 0f, -10f, 0f);
        floatingAnimator.setDuration(3000);
        floatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        floatingAnimator.setRepeatMode(ValueAnimator.RESTART);
        floatingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatingAnimator.setStartDelay(1000);
        floatingAnimator.start();
    }

    private void setupClickListeners() {
        redirectToLogin.setOnClickListener(v -> {
            animateButtonPress(redirectToLogin);
            startActivity(new Intent(RegisterActivity.this, CustomerLoginActivity.class));
            finish();
        });

        registerButton.setOnClickListener(view -> {
            animateButtonPress(registerButton);
            registerUser();
        });
    }

    private void animateButtonPress(View button) {
        // Simple button press animation
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    button.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();
                })
                .start();
    }

    private boolean validateInputs(String name, String email, String password, String phone) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            nameField.setError("Name is required");
            isValid = false;
        }
//        else if (!Pattern.matches("^[a-zA-Z ]{2,25}$", name)) {
//            nameField.setError("Name must contain only letters (2–25 characters)");
//            isValid = false;
//        }

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            isValid = false;
        } else if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            emailField.setError("Please enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters long");
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneField.setError("Phone number is required");
            isValid = false;
        } else if (!Pattern.matches("^[0-9]{10}$", phone)) {
            phoneField.setError("Phone number must be exactly 10 digits");
            isValid = false;
        }

        return isValid;
    }

    private void registerUser() {
        if (progressBar == null) {
            Toast.makeText(this, "Progress bar not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String role = "customer";

        if (!validateInputs(name, email, password, phone)) return;

        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);
        registerButton.setText("");

        // First check if phone is already registered
        firestore.collection("users")
                .whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            registerButton.setEnabled(true);
                            registerButton.setText("Create Account");
                            phoneField.setError("This phone number is already registered.");
                        } else {
                            // Proceed to check email
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(authTask -> {
                                        progressBar.setVisibility(View.GONE);
                                        registerButton.setEnabled(true);
                                        registerButton.setText("Create Account");

                                        if (authTask.isSuccessful()) {
                                            String userId = auth.getCurrentUser().getUid();
                                            saveUserDetailsToFirestore(userId, name, email, phone, role);
                                        } else {
                                            if (authTask.getException() instanceof FirebaseAuthUserCollisionException) {
                                                emailField.setError("This email address is already registered.");
                                            } else if (authTask.getException() != null && authTask.getException().getMessage().contains("PERMISSION_DENIED")) {
                                                Toast.makeText(this, "Registration failed: Permission denied. Please contact admin.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(this, "Registration failed: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        registerButton.setEnabled(true);
                        registerButton.setText("Create Account");
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(this, "Error while checking phone: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Unknown error occurred while checking phone", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserDetailsToFirestore(String userId, String name, String email, String phone, String role) {
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("role", role);
        user.put("created_at", createdAt);

        firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, CustomerLoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // Animation lifecycle methods
    private void pauseAnimations() {
        if (pulseCircle1 != null) pulseCircle1.clearAnimation();
        if (pulseCircle2 != null) pulseCircle2.clearAnimation();
        if (pulseCircle3 != null) pulseCircle3.clearAnimation();
    }

    private void resumeAnimations() {
        startPulseAnimations();
    }

    private void cleanupAnimations() {
        if (animationHandler != null) {
            animationHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAnimations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeAnimations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupAnimations();
    }
}