import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.androidx.room)
}

kotlin {
    ksp {
        arg("circuit.codegen.mode", "kotlin_inject_anvil")
        arg("kotlin-inject-anvil-contributing-annotations", "com.slack.circuit.codegen.annotations.CircuitInject")
    }

    jvm()

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=pl.wojtek.focusfuel.util.parcelize.CommonParcelize"
            )
        }
    }
    
    sourceSets {
        val jvmMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.process)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(compose.animation)

            implementation(libs.kotlin.inject.runtime)
            implementation(libs.kotlin.inject.anvil.runtime)
            implementation(libs.kotlin.inject.anvil.runtimeoptional)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.arrow.core)
            implementation(libs.arrow.fx.coroutines)

            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.room.runtime)

            implementation(libs.circuit.foundation)
            implementation(libs.circuit.overlay)
            implementation(libs.circuitx.overlay)
            implementation(libs.circuit.codegen.annotation)

            implementation(libs.coroutines.core)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)

            implementation(libs.kotlinx.datetime)

            implementation(libs.kermit)

            implementation(libs.kmpnotifier)

            implementation(libs.haze.materials)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.circuit.test)
            implementation(libs.coroutines.test)
            implementation(libs.mockk)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.coroutines.swing)
        }
    }
}

android {
    namespace = "pl.wojtek.focusfuel"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "pl.wojtek.focusfuel"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

}

compose.desktop {
    application {
        mainClass = "pl.wojtek.focusfuel.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "FocusFuel"
            packageVersion = "1.0.0"

            macOS {
                iconFile.set(project.file("icons/app_icon.icns"))
            }
            windows {
                iconFile.set(project.file("icons/app_icon.ico"))
            }
            linux {
                iconFile.set(project.file("icons/app_icon.png"))
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    debugImplementation(compose.uiTooling)

    kspCommonMainMetadata(libs.kotlin.inject.compiler)
    kspCommonMainMetadata(libs.kotlin.inject.anvil.compiler)
    kspCommonMainMetadata(libs.circuit.codegen.ksp)
    kspCommonMainMetadata(libs.androidx.room.compiler)
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.kotlin.inject.anvil.compiler)
addKspDependencyForAllTargets(libs.circuit.codegen.ksp)
addKspDependencyForAllTargets(libs.androidx.room.compiler)

// source: https://github.com/chrisbanes/tivi/tree/main
fun Project.addKspDependencyForAllTargets(dependencyNotation: Any) = addKspDependencyForAllTargets("", dependencyNotation)
fun Project.addKspTestDependencyForAllTargets(dependencyNotation: Any) = addKspDependencyForAllTargets("Test", dependencyNotation)

private fun Project.addKspDependencyForAllTargets(
    configurationNameSuffix: String,
    dependencyNotation: Any,
) {
    val kmpExtension = extensions.getByType<KotlinMultiplatformExtension>()
    dependencies {
        kmpExtension.targets
            .asSequence()
            .filter { target ->
                // Don't add KSP for common target, only final platforms
                target.platformType != KotlinPlatformType.common
            }
            .forEach { target ->
                add(
                    "ksp${target.targetName.capitalized()}$configurationNameSuffix",
                    dependencyNotation,
                )
            }
    }
}
