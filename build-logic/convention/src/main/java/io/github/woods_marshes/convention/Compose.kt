package io.github.woods_marshes.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import kotlin.apply
import kotlin.io.toRelativeString
import kotlin.let
import kotlin.takeIf
import kotlin.text.toBoolean

internal fun Project.configureCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        // 启用 Jetpack Compose
        buildFeatures {
            compose = true
        }
        // 配置相关依赖项
        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
            add(
                "implementation",
                libs.findLibrary("androidx-compose-ui-tooling-preview").get()
            )
            add(
                "debugImplementation",
                libs.findLibrary("androidx-compose-ui-tooling").get()
            )
        }
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }
        fun Provider<*>.relativeToRootProject(dir: String) = flatMap {
            rootProject.layout.buildDirectory.dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        stabilityConfigurationFiles
            .add(rootProject.layout.projectDirectory.file("compose_compiler_config.conf"))
    }
}