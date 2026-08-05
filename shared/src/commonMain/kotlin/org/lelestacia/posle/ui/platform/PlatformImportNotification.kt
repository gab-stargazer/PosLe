package org.lelestacia.posle.ui.platform

import androidx.compose.runtime.Composable

/**
 * Returns a callback that shows a platform notification once an import finishes.
 *
 * Call the returned function from any context (e.g. a coroutine) when the
 * import succeeded.
 *
 * Example:
 * ```
 * val notifyImport = rememberImportNotifier()
 * ...
 * component.onEvent(ProductListComponentEvent.OnImportProducts(bytes) { success ->
 *     if (success) notifyImport()
 * })
 * ```
 */
@Composable
expect fun rememberImportNotifier(): () -> Unit
