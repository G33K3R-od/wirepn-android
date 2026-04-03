import java.io.File
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val keystoreProperties = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) load(FileInputStream(f))
}

fun envOrProp(envKey: String, propKey: String): String? =
    System.getenv(envKey)?.takeIf { it.isNotBlank() } ?: keystoreProperties.getProperty(propKey)

val storeFilePath = envOrProp("SIGNING_STORE_FILE", "storeFile")
val releaseStorePassword = envOrProp("SIGNING_STORE_PASSWORD", "storePassword")
val releaseKeyAlias = envOrProp("SIGNING_KEY_ALIAS", "keyAlias")
val releaseKeyPassword = envOrProp("SIGNING_KEY_PASSWORD", "keyPassword")

fun resolveReleaseKeystore(pathStr: String?): File? {
    if (pathStr.isNullOrBlank()) return null
    val trimmed = pathStr.trim().removeSurrounding("\"")
    if (trimmed.isEmpty()) return null
    val relative = rootProject.file(trimmed)
    if (relative.exists()) return relative
    val absolute = File(trimmed)
    return absolute.takeIf { it.isAbsolute && it.exists() }
}

val releaseKeystoreFile = resolveReleaseKeystore(storeFilePath)

val releaseSigningReady = releaseKeystoreFile != null &&
    !releaseStorePassword.isNullOrBlank() &&
    !releaseKeyAlias.isNullOrBlank() &&
    !releaseKeyPassword.isNullOrBlank()

if (rootProject.file("keystore.properties").exists() && !releaseSigningReady) {
    logger.lifecycle("")
    logger.lifecycle("WirePN: keystore.properties found but release signing is NOT active.")
    when {
        storeFilePath.isNullOrBlank() -> logger.lifecycle("  → Set storeFile=… to your .jks path (repo-relative or absolute).")
        releaseKeystoreFile == null -> logger.lifecycle("  → Keystore file not found: $storeFilePath")
        releaseStorePassword.isNullOrBlank() -> logger.lifecycle("  → storePassword (or SIGNING_STORE_PASSWORD) is empty.")
        releaseKeyAlias.isNullOrBlank() -> logger.lifecycle("  → keyAlias (or SIGNING_KEY_ALIAS) is empty.")
        releaseKeyPassword.isNullOrBlank() -> logger.lifecycle("  → keyPassword (or SIGNING_KEY_PASSWORD) is empty.")
    }
    logger.lifecycle("  Unsigned app-release-unsigned.apk will be produced. Fix the above and run assembleRelease again.")
    logger.lifecycle("")
}

android {
    namespace = "com.wirepn.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wirepn.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.0"
    }

    signingConfigs {
        if (releaseSigningReady) {
            create("release") {
                storeFile = releaseKeystoreFile!!
                storePassword = releaseStorePassword!!
                keyAlias = releaseKeyAlias!!
                keyPassword = releaseKeyPassword!!
            }
        }
    }

    buildTypes {
        debug {
            // Matches what Android Studio Run often uses for the debug variant (…android.debug).
            // Without this, a stale Run config can try to launch com.wirepn.android.debug while the
            // APK is com.wirepn.android → “Activity … does not exist”.
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = when {
                releaseSigningReady -> signingConfigs.getByName("release")
                System.getenv("CI") == "true" -> signingConfigs.getByName("debug")
                else -> null
            }
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.security.crypto)
    implementation(libs.wireguard.tunnel)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
