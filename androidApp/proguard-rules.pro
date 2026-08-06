# Standard Android rules
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod,InnerClasses
-keepattributes SourceFile,LineNumberTable

# Decompose & Essenty
-keep class com.arkivanov.decompose.** { *; }
-keep class com.arkivanov.essenty.** { *; }

# Koin
-keep class org.koin.** { *; }

# FileKit
-keep class io.github.vinceglb.filekit.** { *; }

# Vico
-keep class com.patrykandpatrick.vico.** { *; }

# ESCPOS Thermal Printer
-keep class com.dantsu.escposprinter.** { *; }

# iText 7/8/9
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# Apache POI & XMLBeans — R8 must NOT obfuscate these. XMLBeans derives the
# schema type-system name and the .xsb resource paths from the runtime class
# name (SchemaTypeSystemImpl#getClass().getName()), and instantiates the
# generated schema classes reflectively from that metadata. Renaming
# TypeSystemHolder crashes XSSFWorkbook with ExceptionInInitializerError
# (StringIndexOutOfBoundsException: begin 0, end -1 in SchemaTypeSystemImpl).
-keep class org.apache.poi.schemas.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.openxmlformats.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }
-keep class com.microsoft.schemas.** { *; }
# POI runtime classes used by ExcelManager. org.apache.poi.ss.formula functions
# are instantiated reflectively via functionMetadata.txt, so keep them name-stable.
-keep class org.apache.poi.ss.** { *; }
-keep class org.apache.poi.xssf.** { *; }
-keep class org.apache.poi.ooxml.** { *; }
-keep class org.apache.poi.util.** { *; }
# aalto-xml StAX implementation + stax-api — instantiated reflectively by the
# StAX FactoryFinder via aalto's META-INF/services registration, so R8 must
# keep both the classes and their names.
-keep class com.fasterxml.aalto.** { *; }
-keep class javax.xml.stream.** { *; }
# Optional codecs/libraries POI references but Android does not ship
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.**
-dontwarn schemaorg_apache_xmlbeans.**
-dontwarn com.microsoft.schemas.**
-dontwarn javax.xml.crypto.**
-dontwarn org.apache.jcp.**
-dontwarn org.etsi.uri.**
-dontwarn org.bouncycastle.**
-dontwarn org.tukaani.xz.**
-dontwarn com.github.luben.zstd.**
-dontwarn org.brotli.**
-dontwarn com.graphbuilder.**

# R8 Missing Classes (automatically generated rules)
-dontwarn aQute.bnd.annotation.baseline.BaselineIgnore
-dontwarn aQute.bnd.annotation.spi.ServiceConsumer
-dontwarn aQute.bnd.annotation.spi.ServiceProvider
-dontwarn com.github.luben.zstd.ZstdInputStream
-dontwarn com.itextpdf.bouncycastle.BouncyCastleFactory
-dontwarn com.itextpdf.bouncycastlefips.BouncyCastleFipsFactory
-dontwarn edu.umd.cs.findbugs.annotations.Nullable
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings
-dontwarn java.awt.**
-dontwarn javax.xml.stream.**
-dontwarn net.sf.saxon.**
-dontwarn org.apache.batik.**
-dontwarn org.osgi.framework.**
-dontwarn sharpen.config.MappingConfiguration
