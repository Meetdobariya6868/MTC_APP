plugins {
    alias(libs.plugins.android.application) // Android Application plugin
    alias(libs.plugins.google.gms.google.services) // Google Services plugin
}

android {
    namespace = "com.example.mtc_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mtc_app"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    // Core Android Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase (Using BOM for consistent versions)
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Google Play Services
    implementation ("com.google.android.gms:play-services-auth:20.7.0") // Update version as needed
    implementation("com.google.android.gms:play-services-base:18.2.0") // Base dependency
    implementation("com.google.android.gms:play-services-auth:20.6.0") // Authentication
    implementation("com.google.android.gms:play-services-maps:18.1.0") // Maps (if needed)
    implementation ("com.google.android.gms:play-services-base:latest_version")
    implementation ("com.google.android.gms:play-services-maps:latest_version") // Example for maps


    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation(libs.core)
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    implementation ("com.android.volley:volley:1.2.1")

    implementation ("com.cloudinary:cloudinary-android:2.3.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Additional Material Design Library
    implementation("com.google.android.material:material:1.9.0")
}