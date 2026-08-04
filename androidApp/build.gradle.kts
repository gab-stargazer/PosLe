import org.jetbrains.kotlin.gradle.dsl.JvmTarget

import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.crashlytic)
    alias(libs.plugins.google.gms)
    alias(libs.plugins.stability.analyzer)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)

    implementation(libs.koin.android)

    implementation("com.github.DantSu:ESCPOS-ThermalPrinter-Android:3.3.0")

    //  Apache POI on Android needs a StAX implementation (javax.xml.stream is
    //  not part of the Android platform); see PosLeApplication.onCreate for the
    //  factory system properties that route POI to aalto-xml.
    implementation(libs.aalto.xml)
    implementation(libs.stax.api)

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    //  Google
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytic)
    implementation(libs.firebase.crashlytic)

    //  Permission Compose
    implementation(libs.permissions.compose)

    //  WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    //  Testing
    androidTestImplementation(libs.androidx.testExt.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.github.ireward:compose-html:1.0.2")
}

//  Release signing — the keystore is NEVER committed.
//  Local: keystore.properties at repo root (gitignored). CI: env vars
//  KEYSTORE_PATH / KEYSTORE_PASSWORD / KEY_ALIAS / KEY_PASSWORD.
val keystoreProps = Properties()
val keystorePropsFile = rootProject.file("keystore.properties")
if (keystorePropsFile.exists()) {
    keystorePropsFile.inputStream().use { stream -> keystoreProps.load(stream) }
}

fun signingValue(propName: String, envName: String): String? {
    val fromProps = keystoreProps.getProperty(propName)
    return if (!fromProps.isNullOrBlank()) fromProps else System.getenv(envName)
}

val releaseStoreFile = signingValue("storeFile", "KEYSTORE_PATH")?.let { rootProject.file(it) }
val hasReleaseSigning = releaseStoreFile?.isFile == true &&
    signingValue("storePassword", "KEYSTORE_PASSWORD") != null &&
    signingValue("keyAlias", "KEY_ALIAS") != null &&
    signingValue("keyPassword", "KEY_PASSWORD") != null

android {
    namespace = "org.lelestacia.posle"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.lelestacia.posle"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE.md"
        }
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = releaseStoreFile
                storePassword = signingValue("storePassword", "KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dev"
            versionNameSuffix = " Dev"
        }
        create("staging") {
            //  Debug with minification: inherits debug's app id suffix (.dev),
            //  debug signing and debuggability, but R8-minified like release.
            initWith(getByName("debug"))
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            //  shared (KMP android library) only publishes debug/release variants
            matchingFallbacks += "debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            //  Signed only when a keystore is present (local keystore.properties
            //  or CI env vars); otherwise release builds stay unsigned.
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

//  staging reuses debug's google-services.json (same Firebase project 'posle-dev',
//  same applicationId org.lelestacia.posle.dev) — no separate JSON or CI secret needed.
val copyStagingGoogleServices = tasks.register("copyStagingGoogleServices") {
    val debugJson = layout.projectDirectory.file("src/debug/google-services.json")
    val stagingJson = layout.projectDirectory.file("src/staging/google-services.json")
    inputs.file(debugJson)
    outputs.file(stagingJson)
    doLast {
        val src = debugJson.asFile
        check(src.exists()) {
            "src/debug/google-services.json not found — staging reuses debug's " +
                "google-services.json. Supply it locally or via CI (GOOGLE_SERVICES_DEBUG_JSON)."
        }
        stagingJson.asFile.parentFile.mkdirs()
        src.copyTo(stagingJson.asFile, overwrite = true)
    }
}

//  The google-services plugin checks file existence at task execution time, so the
//  copy above must finish before processStagingGoogleServices runs.
tasks.matching { it.name == "processStagingGoogleServices" }.configureEach {
    dependsOn(copyStagingGoogleServices)
}