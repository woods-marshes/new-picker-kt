import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import io.github.woods_marshes.convention.configureKotlinAndroid
import io.github.woods_marshes.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class LibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("new.picker.kt.android.lint")
            }

            extensions.configure<LibraryExtension>{
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testOptions.animationsDisabled = true

                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                resourcePrefix = path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_").lowercase() + "_"
            }

            dependencies {
                add("androidTestImplementation", libs.findLibrary("kotlin.test").get())
                add("testImplementation", libs.findLibrary("kotlin.test").get())

                add("implementation", libs.findLibrary("androidx.tracing.ktx").get())
            }
        }
    }
}