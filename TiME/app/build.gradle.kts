@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
    id ("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.9.0" // or your version

}

android {
    namespace = "com.time.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.time.app"
        minSdk = 26
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.foundation.layout)

    // Compose BOM and UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material) // SwipeToDismiss
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)


    implementation(libs.glide)
    implementation(libs.coil.compose)
    implementation (libs.androidx.foundation) // For Compose
    implementation (libs.androidx.ui.tooling.preview.v100) // Compose Preview


    // Material Icons TwoTone (Extended)
    implementation(libs.androidx.material.icons.extended.v161)
    implementation(libs.material)

    implementation(libs.material3)




    // Google Fonts (if you use text-google-fonts)
    implementation(libs.androidx.ui.text.google.fonts)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    //Calendar
    // Retrofit + Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)


    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.play.services)


    //Serialization

    implementation (libs.androidx.datastore.core)
    implementation (libs.kotlinx.serialization.json)



    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation (libs.androidx.foundation.layout)

    // JUnit for unit testing
    testImplementation (libs.junit)

// AndroidX Test
    androidTestImplementation (libs.androidx.junit.v115)
    androidTestImplementation (libs.androidx.rules)

// Espresso core
    androidTestImplementation (libs.androidx.espresso.core.v351)

// (Optional but useful)
    androidTestImplementation (libs.androidx.espresso.intents)
    androidTestImplementation (libs.androidx.espresso.contrib)


    //ANIMATION
    implementation(libs.lottie.compose) // check for latest version
    testImplementation(kotlin("test"))

}
