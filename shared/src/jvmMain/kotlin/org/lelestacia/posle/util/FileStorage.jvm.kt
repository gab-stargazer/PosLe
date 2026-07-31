package org.lelestacia.posle.util

import java.io.File

actual open class FileStorage {
    private val userHome = System.getProperty("user.home")
    
    private val appDataDir = File(userHome, ".posle").apply {
        if (!exists()) mkdirs()
    }

    private val imagesDir = File(appDataDir, "products").apply {
        if (!exists()) mkdirs()
    }

    actual open fun saveImage(fileName: String, data: ByteArray): String {
        val file = File(imagesDir, fileName)
        file.writeBytes(data)
        return file.absolutePath
    }

    actual open fun deleteImage(fileName: String): Boolean {
        val file = File(imagesDir, fileName)
        return if (file.exists()) file.delete() else false
    }

    actual fun saveToPublicPictures(fileName: String, data: ByteArray): String? {
        val picturesDir = File(userHome, "Pictures/PosLe").apply {
            if (!exists()) mkdirs()
        }
        val file = File(picturesDir, fileName)
        return try {
            file.writeBytes(data)
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    actual fun saveToPublicDocuments(fileName: String, subFolder: String, data: ByteArray): String? {
        val documentsDir = File(userHome, "Documents/PosLe/$subFolder").apply {
            if (!exists()) mkdirs()
        }
        val file = File(documentsDir, fileName)
        return try {
            file.writeBytes(data)
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
