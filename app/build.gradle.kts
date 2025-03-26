plugins {
    id("com.android.application")
    // Bez Kotlin pluginu (používáme Java kód)
    // id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.denik" // Tohle se vygeneruje podle package name
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.denik"
        minSdk = 27   // Android 8.1
        targetSdk = 33

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // (Nepovinné) Java 8+ kompatibilita
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Test libraries (volitelně)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
