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
            api(libs.decompose.coroutine)
            api(libs.decompose.reaktive)

            //  Datetime
            implementation(libs.kotlinx.datetime)

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

            //  QR Kit
            api(libs.qr.kit)

            implementation("com.itextpdf:itext-core:9.7.0")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmTest.dependencies {
            implementation(libs.mockk)
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
