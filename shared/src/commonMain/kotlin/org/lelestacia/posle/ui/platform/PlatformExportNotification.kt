package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable

/**
 * Returns a callback that shows a platform notification once an export finishes.
 *
 * Call the returned function from any context (e.g. an `onClick` or a
 * coroutine) with the exported file name and the result of
 * `FileStorage.saveToPublicDocuments` (a content URI on Android, an absolute
 * path on desktop; `null` means the save failed).
 *
 * Example:
 * ```
 * val notifyExport = rememberExportNotifier()
 * ...
 * val savedPath = fileStorage.saveToPublicDocuments(...)
 * notifyExport("products.xlsx", savedPath)
 * ```
 */
@Composable
expect fun rememberExportNotifier(): (fileName: String, savedPath: String?) -> Unit
