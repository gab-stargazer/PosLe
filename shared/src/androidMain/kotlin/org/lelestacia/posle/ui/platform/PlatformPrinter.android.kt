package org.lelestacia.posle.ui.platform

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import org.lelestacia.posle.domain.model.Transaction
import org.lelestacia.posle.util.Name
import org.lelestacia.posle.util.printTransactionToPrinter

@Composable
internal actual fun platformBluetoothPermissionGranted(): Boolean {
    val context = LocalContext.current
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.BLUETOOTH_CONNECT
    ) == PackageManager.PERMISSION_GRANTED
}

internal actual fun platformPrintTransaction(
    transaction: Transaction,
    storeNameValue: Name,
) {
    printTransactionToPrinter(
        transaction = transaction,
        storeNameValue = storeNameValue
    )
}
