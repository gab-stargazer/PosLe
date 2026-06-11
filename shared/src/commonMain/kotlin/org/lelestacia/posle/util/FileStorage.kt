package org.lelestacia.posle.util

expect class FileStorage {

    fun saveImage(
        fileName: String,
        data: ByteArray
    ): String

    fun deleteImage(fileName: String): Boolean
}