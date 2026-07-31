package org.lelestacia.posle.util

expect class FileStorage {

    fun saveImage(
        fileName: String,
        data: ByteArray
    ): String

    fun deleteImage(fileName: String): Boolean

    fun saveToPublicPictures(
        fileName: String,
        data: ByteArray
    ): String?

    fun saveToPublicDocuments(
        fileName: String,
        subFolder: String,
        data: ByteArray
    ): String?
}
