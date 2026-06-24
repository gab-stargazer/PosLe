# Standard Android rules
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod,InnerClasses
-keepattributes SourceFile,LineNumberTable

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory { *; }

# Kotlin Serialization
-keep class **$$serializer { *; }
-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    *** $serializer;
}

# Decompose & Essenty
-keep class com.arkivanov.decompose.** { *; }
-keep class com.arkivanov.essenty.** { *; }

# Koin
-keep class org.koin.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity

# Paging
-keep class androidx.paging.PagingSource { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Coil
-keep class coil.** { *; }

# FileKit
-keep class io.github.vinceglb.filekit.** { *; }

# Vico
-keep class com.patrykandpatrick.vico.** { *; }

# KoalaPlot
-keep class io.github.koalaplot.** { *; }

# ESCPOS Thermal Printer
-keep class com.dantsu.escposprinter.** { *; }

# Datastore
-keep class androidx.datastore.** { *; }
