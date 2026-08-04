package org.lelestacia.posle.util

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Central UUID v7 generator for all database entity ids.
 *
 * UUID v7 is time-ordered and monotonic, which keeps rows roughly
 * chronological without sacrificing global uniqueness — safe for
 * client-generated primary keys across devices and imports.
 */
object UuidProvider {
    @OptIn(ExperimentalUuidApi::class)
    fun newUuid(): String = Uuid.generateV7().toString()
}
