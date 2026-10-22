plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "de.example.minecraftfunfacts"
    compileSdk = 36
    defaultConfig { applicationId = "de.example.minecraftfunfacts"; minSdk = 28; targetSdk = 35; versionCode = 1; versionName = "1.0.0" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation("androidx.media3:media3-exoplayer:1.11.1")
    implementation("androidx.media3:media3-session:1.11.1")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("com.google.guava:guava:33.4.8-android")
}
