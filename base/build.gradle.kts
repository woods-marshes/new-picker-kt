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
    }

    namespace = "io.github.woods_marshes.base"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.paging.runtime)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.paging.common)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}