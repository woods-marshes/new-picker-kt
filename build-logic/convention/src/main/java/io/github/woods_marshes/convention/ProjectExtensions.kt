package io.github.woods_marshes.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * via:
 *
 *    https://github.com/android/nowinandroid/blob/main/build-logic/convention/src/main/kotlin/com/google/samples/apps/nowinandroid/ProjectExtensions.kt
 *
 */
val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")