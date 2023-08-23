import com.android.build.api.dsl.ApplicationDefaultConfig

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.firebase)
    alias(libs.plugins.hilt)
    alias(libs.plugins.crashanlytics)
    alias(libs.plugins.serialization)
    kotlin("kapt")
}

android {
    compileSdk = 34
    namespace = "com.prime.toolz"
    defaultConfig {
        applicationId = "com.prime.toolz2"
        minSdk = 21
        targetSdk = 34
        versionCode = 48
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
        // Add necessary const to BuildConfig
        // These filed will be provided in github
        // secrets when released else defualt value will be used
        secrets()
    }
    buildTypes {
        // Make sure release is version is optimised.
        release {
            isMinifyEnabled = true
            isZipAlignEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        // Add necessary changes to debug apk.
        debug {
            // makes it possible to install both release and debug versions in same device.
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Debug")
            versionNameSuffix = "-debug"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
        }
        buildFeatures { compose = true }
        composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
        packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    }
}

// Not moving these to libs.version.toml because i think this is redundant.
dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    val compose_version = "1.6.0-alpha02"
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation("androidx.compose.animation:animation-graphics:$compose_version")
    // Integration with activities
    implementation("androidx.activity:activity-compose:1.7.2")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:$compose_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_version")
    // Constraint layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    //Lottie
    implementation("com.airbnb.android:lottie-compose:6.1.0")
    // Preferences and other widgets
    // Preferences and other widgets
    val toolkit_version = "1.0.9-beta"
    implementation("com.github.prime-zs.toolkit:preferences:$toolkit_version")
    implementation("com.github.prime-zs.toolkit:core-ktx:$toolkit_version")
    implementation("com.github.prime-zs.toolkit:material3:$toolkit_version")
    // Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    // Material 3
    val material3_version = "1.2.0-alpha04"
    implementation("androidx.compose.material3:material3:$material3_version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3_version")
    // Google Play InAppUpdate
    val in_app_update_version = "2.1.0"
    implementation("com.google.android.play:app-update:$in_app_update_version")
    implementation("com.google.android.play:app-update-ktx:$in_app_update_version")
    // Google Play InAppReview
    val in_app_review = "2.0.1"
    implementation("com.google.android.play:review:$in_app_review")
    implementation("com.google.android.play:review-ktx:$in_app_review")
    // Google play in-app billing
    val billing_version = "6.0.1"
    implementation("com.android.billingclient:billing:$billing_version")
    implementation("com.android.billingclient:billing-ktx:$billing_version")
    // Unity Ads
    implementation("com.unity3d.ads:unity-ads:4.8.0")
    // Compose navigation
    implementation("androidx.navigation:navigation-compose:2.7.0")
    // Compose Downloadable fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.0")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-android-compiler:2.47")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    // Room Database
    val room_version = "2.6.0-alpha03"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    // ChatGPT dependency
    //implementation 'com.github.acsbendi:Android-Request-Inspector-WebView:1.0.3'
    //implementation 'org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.2.2'
}

/**
 * Init adn add git secrets to BuildConfig
 */
fun ApplicationDefaultConfig.secrets() {
    var value = "\"" + (System.getenv("IAP_BUY_ME_COFFEE") ?: "empty") + "\""
    buildConfigField("String", "IAP_BUY_ME_COFFEE", value)
    value = "\"" + (System.getenv("IAP_NO_ADS") ?: "empty") + "\""
    buildConfigField("String", "IAP_NO_ADS", value)
    value = "\"" + (System.getenv("PLACEMENT_BANNER_1") ?: "empty") + "\""
    buildConfigField("String", "PLACEMENT_BANNER_1", value)
    value = "\"" + (System.getenv("PLACEMENT_BANNER_2") ?: "empty") + "\""
    buildConfigField("String", "PLACEMENT_BANNER_2", value)
    value = "\"" + (System.getenv("PLACEMENT_BANNER_2") ?: "empty") + "\""
    buildConfigField("String", "PLACEMENT_INTERSTITIAL", value)
    value = "\"" + (System.getenv("UNITY_APP_ID") ?: "empty") + "\""
    buildConfigField("String", "UNITY_APP_ID", value)
    value = "\"" + (System.getenv("PLAY_CONSOLE_APP_RSA_KEY") ?: "empty") + "\""
    buildConfigField("String", "PLAY_CONSOLE_APP_RSA_KEY", value)
}
