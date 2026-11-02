plugins { id("com.android.application") }

android {
    namespace = "com.voya.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.voya.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-mobile-preview"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            // Production signing: configure a secure signingConfig in Android Studio.
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
