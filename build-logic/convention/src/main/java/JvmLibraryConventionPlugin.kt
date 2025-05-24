import io.github.woods_marshes.convention.configureKotlinJvm
import io.github.woods_marshes.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("new.picker.kt.android.lint")
            }
            configureKotlinJvm()
            dependencies {
                add("testImplementation", libs.findLibrary("kotlin.test").get())
            }
        }
    }
}