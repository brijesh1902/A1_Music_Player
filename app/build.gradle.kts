plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "com.brizzs.a1musicplayer"
        minSdk = 23
        targetSdk = 36
        versionCode = 12
        versionName = "1.4.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = false
        compose = true
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    namespace = "com.brizzs.a1musicplayer"
}

dependencies {
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.0.21"))

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-perf")
    implementation("com.google.firebase:firebase-messaging")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation("com.google.code.gson:gson:2.13.2")

    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation("com.google.android.play:review:2.0.2")

    implementation("androidx.media:media:1.7.1")
    implementation("com.karumi:dexter:6.2.3")

    implementation("androidx.palette:palette:1.0.0")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")

    implementation("com.airbnb.android:lottie:6.7.1")
    implementation(project(":linguistic"))

    // Room DB
    implementation("androidx.room:room-runtime:2.8.3")
    kapt("androidx.room:room-compiler:2.8.3")
}
