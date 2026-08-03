import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.stability.analyzer)
    alias(libs.plugins.dokka)
}

kotlin {
    compilerOptions {
        //  Opt into stable expect/actual (KT-61573) — removes the Beta warning
        freeCompilerArgs.add("-Xexpect-actual-classes")
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
            implementation(libs.permissions.compose)
            implementation(libs.kscan)
            implementation("com.github.DantSu:ESCPOS-ThermalPrinter-Android:3.3.0")
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
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
            api(libs.decompose.coroutine)
            api(libs.decompose.reaktive)

            //  Datetime
            implementation(libs.kotlinx.datetime)

            // IO
            api(libs.kotlinx.io.core)

            //  Serialization
            api(libs.kotlinx.serialization)

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

            //  Filekit
            api(libs.filekit.core)
            api(libs.filekit.dialogs)
            api(libs.filekit.compose)

            //  Coil
            api(libs.coil.compose)

            //  Datastore
            api(libs.androidx.datastore)
            api(libs.androidx.datastore.preferences)

            api(libs.vico.compose.m3)

            api("io.github.koalaplot:koalaplot-core:0.11.2")

            api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.5.0")

            //  KScan — declared in androidMain.dependencies (JVM variant would
            //  pull JavaCV/OpenCV; desktop scanner is a no-op)

            //  Apache POI
            implementation(libs.poi)
            implementation(libs.poi.ooxml)

            implementation("com.itextpdf:itext-core:9.7.0")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmTest.dependencies {
            implementation(libs.mockk)
            implementation("org.jetbrains.compose.ui:ui-test-junit4:${libs.versions.composeMultiplatform.get()}")
            implementation(compose.desktop.currentOs)
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
