plugins {
    alias(deps.plugins.android.application)
    alias(deps.plugins.kotlin.android)
    alias(deps.plugins.kotlin.compose)
    alias(deps.plugins.hilt)
    alias(deps.plugins.ksp)
    alias(deps.plugins.kotlin.serialization)
}

android {
    namespace = "com.cebolao.lotofacil"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cebolao.lotofacil"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "com.cebolao.lotofacil.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        @Suppress("DEPRECATION")
        resourceConfigurations += setOf("pt", "en")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        // REFINAMENTO: abortOnError = false é uma escolha consciente para permitir builds
        // mesmo com warnings de lint, mas checkReleaseBuilds garante a verificação antes do release.
        abortOnError = false
        warningsAsErrors = false
        checkReleaseBuilds = true
        disable += listOf("MissingTranslation", "ExtraTranslation")
    }
}

dependencies {
    // Core Library Desugaring
    coreLibraryDesugaring(deps.android.desugarJdkLibs)

    // AndroidX Core & Activity
    implementation(deps.androidx.core.ktx)
    implementation(deps.androidx.core.splashscreen)
    implementation(deps.androidx.activity.compose)
    implementation(deps.bundles.androidx.lifecycle)

    // WorkManager for background tasks
    implementation(deps.androidx.work.runtime.ktx)

    // Compose BOM and dependencies
    implementation(platform(deps.androidx.compose.bom))
    implementation(deps.bundles.androidx.compose)

    // Collections & Serialization
    implementation(deps.kotlinx.collections.immutable)
    implementation(deps.kotlinx.serialization.json)
    implementation(deps.kotlinx.coroutines.android)

    // DataStore for persistence
    implementation(deps.androidx.datastore.preferences)

    // Hilt for dependency injection
    implementation(deps.hilt.android)
    ksp(deps.hilt.compiler)
    implementation(deps.hilt.navigation.compose)
    implementation(deps.hilt.work)
    ksp(deps.hilt.compiler.androidx)

    // Networking
    implementation(deps.bundles.networking)

    // Utilities
    implementation(deps.androidx.interpolator)
    implementation(deps.androidx.navigation.compose)

    // Testing - Unit Tests
    testImplementation(deps.bundles.testing.unit)
    kspTest(deps.hilt.compiler)

    // Testing - Android Tests
    androidTestImplementation(platform(deps.androidx.compose.bom))
    androidTestImplementation(deps.bundles.testing.android)
    androidTestImplementation(deps.hilt.android.testing)
    kspAndroidTest(deps.hilt.compiler)

    // Debug Tools
    debugImplementation(deps.androidx.ui.tooling)
    debugImplementation(deps.androidx.ui.test.manifest)
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
