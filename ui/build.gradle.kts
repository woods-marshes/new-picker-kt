plugins {
    alias(libs.plugins.new.picker.kt.library)
    alias(libs.plugins.new.picker.kt.compose)
    alias(libs.plugins.new.picker.kt.parcelize)
    alias(libs.plugins.new.picker.kt.hilt)
    id("kotlinx-serialization")
}

android {
    buildFeatures {
        buildConfig = true
        compose = true
    }

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    namespace = "io.github.woods_marshes.ui"
}

dependencies {
    api(projects.base)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.window.core)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.tracing.ktx)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.navigationSuite)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.util)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.appcompat)
    implementation(libs.paging.compose)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)
    implementation(libs.coil.kt.video)
    implementation(libs.coil.kt.gif)

    implementation(libs.landscapist.coil3)
    implementation(libs.landscapist.palette)
    implementation(libs.landscapist.animation)
    implementation(libs.landscapist.placeholder)
    implementation(libs.landscapist.transformation)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.junit)

    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.compose.ui.testManifest)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.androidx.navigation.testing)

    implementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.lifecycle.runtimeTesting)
}