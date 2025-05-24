import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.lint)
}

group = "io.github.woods-marshes.new-picker-kt.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    implementation(libs.truth)
    lintChecks(libs.androidx.lint.gradle)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidCompose") {
            id = libs.plugins.new.picker.kt.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("androidApplication") {
            id = libs.plugins.new.picker.kt.application.get().pluginId
            implementationClass = "ApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.new.picker.kt.library.get().pluginId
            implementationClass = "LibraryConventionPlugin"
        }
        register("androidTest") {
            id = libs.plugins.new.picker.kt.test.get().pluginId
            implementationClass = "TestConventionPlugin"
        }
        register("hilt") {
            id = libs.plugins.new.picker.kt.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.new.picker.kt.room.get().pluginId
            implementationClass = "RoomConventionPlugin"
        }
        register("androidLint") {
            id = libs.plugins.new.picker.kt.lint.get().pluginId
            implementationClass = "LintConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.new.picker.kt.jvm.library.get().pluginId
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("kotlinParcelize") {
            id = libs.plugins.new.picker.kt.parcelize.get().pluginId
            implementationClass = "ParcelizeConventionPlugin"
        }
    }
}