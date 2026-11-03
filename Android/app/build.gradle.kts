plugins { id("com.android.application") }

// Upload-key values are supplied only through protected build environment variables.
// No passwords or keystores are committed to the repository.
val uploadKeyFile = System.getenv("VOYA_UPLOAD_KEYSTORE_FILE")
val uploadStorePassword = System.getenv("VOYA_UPLOAD_STORE_PASSWORD")
val uploadKeyAlias = System.getenv("VOYA_UPLOAD_KEY_ALIAS")
val uploadKeyPassword = System.getenv("VOYA_UPLOAD_KEY_PASSWORD")
val uploadSigningEnabled = listOf(uploadKeyFile, uploadStorePassword, uploadKeyAlias, uploadKeyPassword)
    .all { !it.isNullOrBlank() }

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

    signingConfigs {
        if (uploadSigningEnabled) {
            create("voyaUpload") {
                storeFile = file(requireNotNull(uploadKeyFile))
                storePassword = requireNotNull(uploadStorePassword)
                keyAlias = requireNotNull(uploadKeyAlias)
                keyPassword = requireNotNull(uploadKeyPassword)
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("voyaUpload")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
