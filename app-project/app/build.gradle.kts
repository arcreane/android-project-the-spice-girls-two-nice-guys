plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.barometre.myapplication"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.barometre.myapplication"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // fragment
    implementation("androidx.fragment:fragment:1.8.1")
    // lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.3")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.3")
    // google maps
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    // GPS
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.google.material)
    implementation(libs.androidx.appcompat)
    annotationProcessor(
        "com.github.bumptech.glide:compiler:4.16.0"
    )
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}