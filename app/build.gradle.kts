plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
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
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        // Load signing credentials from gradle properties or environment variables.
        // gradle.properties should define: keystoreFile, keystorePassword,
        // keyAlias and keyPassword.  Environment variables override them.
        create("release") {
            val keystorePath = project.properties["keystoreFile"]?.toString()
                ?: System.getenv("KEYSTORE_FILE")
            storeFile = keystorePath?.let { file(it) }
            storePassword = project.properties["keystorePassword"]?.toString()
                ?: System.getenv("KEYSTORE_PASSWORD")
            keyAlias = project.properties["keyAlias"]?.toString()
                ?: System.getenv("KEY_ALIAS")
            keyPassword = project.properties["keyPassword"]?.toString()
                ?: System.getenv("KEY_PASSWORD")
        }
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
            // Use debug signing if no release keystore is provided
            signingConfig = if (signingConfigs.findByName("release")?.storeFile?.exists() == true) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }

    // Filter only supported locales (Portuguese and English)
    androidResources {
        localeFilters += listOf("pt", "en")
    }

    packaging.resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    lint {
        abortOnError = true
        warningsAsErrors = true
        checkReleaseBuilds = true
        lintConfig = file("lint.xml")
        disable += listOf(
            "MissingTranslation",
            "ExtraTranslation",
            "NewerVersionAvailable",
            "GradleDependency",
            "MonochromeLauncherIcon",
            "ObsoleteSdkInt",
            "UnusedResources",
            "AndroidGradlePluginVersion"
        )
    }
}

dependencies {
    // Desugaring for Java 8+ APIs
    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    // AndroidX core and lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.androidx.lifecycle)



    // Jetpack Compose BOM and related libraries
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)

    // Kotlinx collections, serialization and coroutines
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    // DataStore for key/value preferences
    implementation(libs.androidx.datastore.preferences)

    // Room database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Networking (Retrofit + OkHttp)
    implementation(libs.bundles.networking)

    // Compose utilities
    implementation(libs.google.material)
    implementation(libs.androidx.interpolator)
    implementation(libs.androidx.navigation.compose)

    // Unit testing
    testImplementation(libs.bundles.testing.unit)
    kspTest(libs.hilt.compiler)

    // Instrumented testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.testing.android)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Debug tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Configure Java toolchain explicitly
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
