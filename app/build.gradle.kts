@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    kotlin("plugin.parcelize")
    alias(libs.plugins.com.squareup.anvil)
    alias(libs.plugins.com.google.devtools.ksp)
}

android {
    namespace = "com.francescsoftware.circuitsample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.francescsoftware.circuitsample"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        jvmTarget = JavaVersion.VERSION_17.toString()
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

kapt {
    correctErrorTypes = true
}

dependencies {
    ksp(libs.com.slack.circuit.circuit.codegen)
    implementation(libs.bundles.circuit)
    implementation(libs.bundles.compose)
    implementation(libs.com.squareup.anvil.annotations)

    implementation(libs.androidx.activity.activity.compose)
    implementation(libs.androidx.appcompat.appcompat)
    implementation(libs.com.google.android.material.material)

    implementation(libs.com.google.dagger.dagger.compiler)
    kapt(libs.com.google.dagger.dagger.compiler)
}
