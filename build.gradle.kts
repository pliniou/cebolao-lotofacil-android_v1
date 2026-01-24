plugins {
    // Define plugin aliases once at the root.  Modules apply them as needed.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// Central place for common build settings.  Additional configuration
// may be added later (e.g. code quality plugins).

// Nothing else is needed here; individual modules contain their own
// android{} blocks.