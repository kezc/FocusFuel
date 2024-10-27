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
}

kotlin {
    ksp {
        arg("circuit.codegen.mode", "kotlin_inject_anvil")
        arg("kotlin-inject-anvil-contributing-annotations", "com.slack.circuit.codegen.annotations.CircuitInject")
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=pl.wojtek.focusfuel.util.parcelize.CommonParcelize"
            )
        }
    }
    
    listOf(
//        iosX64(),
//        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.kotlin.inject.runtime)
            implementation(libs.kotlin.inject.anvil.runtime)
            implementation(libs.kotlin.inject.anvil.runtimeoptional)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.arrow.core)
            implementation(libs.arrow.fx.coroutines)

            implementation(libs.circuit.foundation)
            implementation(libs.circuit.overlay)
            implementation(libs.circuit.codegen.annotation)
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    kspCommonMainMetadata(libs.kotlin.inject.compiler)
    kspCommonMainMetadata(libs.kotlin.inject.anvil.compiler)
    kspCommonMainMetadata(libs.circuit.codegen.ksp)
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)
addKspDependencyForAllTargets(libs.circuit.codegen.ksp)
addKspDependencyForAllTargets(libs.kotlin.inject.anvil.compiler)

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
