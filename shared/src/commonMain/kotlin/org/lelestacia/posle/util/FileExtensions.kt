package org.lelestacia.posle.util

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.sink
import kotlinx.io.buffered

fun PlatformFile.writeBytes(bytes: ByteArray) {
    this.sink().buffered().use {
        it.write(bytes)
    }
}
