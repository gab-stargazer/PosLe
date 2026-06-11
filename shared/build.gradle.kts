import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    jvm()

    androidLibrary {
       namespace = "org.lelestacia.posle.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }

       androidResources {
           enable = true
       }

       withHostTest {
           isIncludeAndroidResources = true
       }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }

        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            api(libs.compose.components.resources)
            api(libs.compose.material.icon.extended)
            api(libs.compose.uiToolingPreview)
            api(libs.androidx.lifecycle.viewmodelCompose)
            api(libs.androidx.lifecycle.runtimeCompose)

            //  Decompose
            api(libs.decompose.core)
            api(libs.decompose.compose)

            //  Datetime
            implementation(libs.kotlinx.datetime)

            //  Serialization
            implementation(libs.kotlinx.serialization)

            //  Koin
            api(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)
            api(libs.koin.compose)

            //  Room
            api(libs.androidx.room.runtime)
            api(libs.androidx.room.paging)
            api(libs.androidx.sqlite.bundled)

            //  Paging
            api(libs.androidx.paging.common)
            api(libs.androidx.paging.compose)

            api("io.github.vinceglb:filekit-core:0.14.1")
            api("io.github.vinceglb:filekit-dialogs:0.14.1")
            api("io.github.vinceglb:filekit-dialogs-compose:0.14.1")

            api("io.coil-kt.coil3:coil-compose:3.5.0")

        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.resources {
    publicResClass = true
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}