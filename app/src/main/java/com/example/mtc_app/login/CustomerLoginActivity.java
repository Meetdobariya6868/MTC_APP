package com.example.mtc_app.login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.example.mtc_app.admin.AdminHomePageActivity;
import com.example.mtc_app.customer.CustomerHomePageActivity;
import com.example.mtc_app.customerRepresentative.CrMain;
import com.example.mtc_app.register.RegisterActivity;
import com.example.mtc_app.splashScreen.ParticlesView;
import com.example.mtc_app.staff.staff_home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class CustomerLoginActivity extends AppCompatActivity {

    // Existing fields
    private EditText emailField, passwordField;
    private Button loginButton;
    private TextView registerTextView;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    // Animation fields
    private View pulseCircle1, pulseCircle2, pulseCircle3;
    private CircularProgressBar backgroundProgressBar;
    private CardView loginCard;
    private Handler animationHandler;
    private ParticlesView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Initialize animations
        initializeAnimations();

        // Check if user is already logged in
        checkExistingLogin();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Existing views
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerButton);

        // Animation views
        particleView = findViewById(R.id.particleView);
        pulseCircle1 = findViewById(R.id.pulseCircle1);
        pulseCircle2 = findViewById(R.id.pulseCircle2);
        pulseCircle3 = findViewById(R.id.pulseCircle3);
        backgroundProgressBar = findViewById(R.id.backgroundProgressBar);
        loginCard = findViewById(R.id.loginCard);

        animationHandler = new Handler();
    }

    private void initializeAnimations() {
        // Start animations after a short delay to ensure views are ready
        animationHandler.postDelayed(() -> {
            startPulseAnimations();
            startProgressAnimation();
            startLoginCardAnimation();
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

    private void startLoginCardAnimation() {
        if (loginCard == null) return;

        // Initial entrance animation for login card
        loginCard.setAlpha(0f);
        loginCard.setScaleX(0.8f);
        loginCard.setScaleY(0.8f);
        loginCard.setTranslationY(100f);

        // Animate card entrance
        AnimatorSet cardAnimatorSet = new AnimatorSet();

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(loginCard, "alpha", 0f, 1f);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(loginCard, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(loginCard, "scaleY", 0.8f, 1f);
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(loginCard, "translationY", 100f, 0f);

        cardAnimatorSet.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator, translationAnimator);
        cardAnimatorSet.setDuration(800);
        cardAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        cardAnimatorSet.setStartDelay(300);

        cardAnimatorSet.start();

        // Subtle floating animation for login card
        startFloatingAnimation();
    }

    private void startFloatingAnimation() {
        if (loginCard == null) return;

        ObjectAnimator floatingAnimator = ObjectAnimator.ofFloat(loginCard, "translationY", 0f, -10f, 0f);
        floatingAnimator.setDuration(3000);
        floatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        floatingAnimator.setRepeatMode(ValueAnimator.RESTART);
        floatingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        floatingAnimator.setStartDelay(1000);
        floatingAnimator.start();
    }

    private void checkExistingLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userRole = sharedPreferences.getString("userRole", "");
            redirectToRoleBasedPage(userRole);
        }
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(view -> {
            animateButtonPress(loginButton);
            loginUser();
        });

        registerTextView.setOnClickListener(v -> {
            animateButtonPress(registerTextView);
            Intent intent = new Intent(CustomerLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
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

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            isValid = false;
        }

        if (!isValid) return;

        // Show progress inside button
        ProgressBar loginButtonProgress = findViewById(R.id.loginButtonProgress);
        loginButtonProgress.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        loginButton.setText("");

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loginButtonProgress.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    loginButton.setText("Sign In");

                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user.getUid());
                        }
                    } else {
                        Exception exception = task.getException();
                        String errorMessage;
                        String errorCode = "";

                        if (exception instanceof com.google.firebase.auth.FirebaseAuthException) {
                            errorCode = ((com.google.firebase.auth.FirebaseAuthException) exception).getErrorCode();
                        }

                        switch (errorCode) {
                            case "ERROR_USER_NOT_FOUND":
                                errorMessage = "This email is not registered";
                                break;
                            case "ERROR_WRONG_PASSWORD":
                                errorMessage = "Incorrect password";
                                break;
                            case "ERROR_INVALID_EMAIL":
                                errorMessage = "Invalid email format";
                                break;
                            case "ERROR_USER_DISABLED":
                                errorMessage = "This account has been disabled";
                                break;
                            default:
                                errorMessage = "Login failed. Please check your credentials.";
                                break;
                        }

                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String userId) {
        firestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("role");
                            if (role != null) {
                                saveUserRoleInPreferences(role);
                                redirectToRoleBasedPage(role);
                            } else {
                                Toast.makeText(this, "Role is missing for this user.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "User role not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to retrieve user role: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserRoleInPreferences(String role) {
        getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", true)
                .putString("userRole", role)
                .apply();
    }

    private void redirectToRoleBasedPage(String role) {
        if (role == null) {
            Toast.makeText(this, "Invalid role.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent;
        switch (role.toLowerCase()) {
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
                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish();
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