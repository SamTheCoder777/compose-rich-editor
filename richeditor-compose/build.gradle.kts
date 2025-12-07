import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"
    //id("module.publication")
}

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    js(IR).browser()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                enabled = false
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets.commonMain.dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.material3)

        // HTML parsing library
        implementation("com.mohamedrejeb.ksoup:ksoup-html:0.6.0")
        implementation("com.mohamedrejeb.ksoup:ksoup-entities:0.6.0")

        // Markdown parsing library
        implementation("org.jetbrains:markdown:0.7.3")

        // kotlin json
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    }

    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
        @OptIn(ExperimentalComposeLibrary::class)
        implementation(compose.uiTest)
    }

    sourceSets.named("desktopTest").dependencies {
        implementation(compose.desktop.uiTestJUnit4)
        implementation(compose.desktop.currentOs)
    }
}

android {
    namespace = "com.mohamedrejeb.richeditor.compose"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        consumerProguardFile("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}