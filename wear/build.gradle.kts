
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    // (optional) align with phone app package naming
    namespace = "com.johan.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.johan.wear"
        minSdk = 30           // Wear OS 3+
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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

    // Use Java 17 for modern toolchain & Compose
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    // (nice to have—ensures Gradle uses JDK 17 even if your system JDK differs)
    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    // If you’re NOT using the Kotlin Compose plugin, you’d add:
    // composeOptions { kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get() }
    // But since you are using it, you can omit composeOptions.
}


dependencies {
    // Data Layer
    implementation(libs.play.services.wearable)
    implementation(libs.kotlinx.coroutines.play.services)


    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Wear Compose (use these instead of phone Material)
    implementation(libs.androidx.wear.compose.material)
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)

    // Activity/Compose bridge
    implementation(libs.androidx.activity.compose)

    // Optional (keep only if you actually use them)
    // implementation(libs.androidx.ui)
    // implementation(libs.androidx.ui.graphics)
    // implementation(libs.androidx.ui.tooling.preview)
    // implementation(libs.androidx.core.splashscreen)

    // Tests / debug
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
