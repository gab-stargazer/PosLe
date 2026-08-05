package org.lelestacia.posle.util

import java.awt.Image
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType

object DesktopNotifier {

    fun notifyPdfGenerated(fileName: String, success: Boolean) {
        notifyExportFinished(fileName, success, "Report ready", "Report failed")
    }

    fun notifyImportFinished(successTitle: String) {
        if (!SystemTray.isSupported()) return

        val tray = SystemTray.getSystemTray()
        val image: Image = Toolkit.getDefaultToolkit().createImage("")
        val trayIcon = TrayIcon(image, "PosLe").apply {
            isImageAutoSize = true
        }

        try {
            tray.add(trayIcon)
            trayIcon.displayMessage(
                successTitle,
                "Import finished successfully",
                MessageType.INFO
            )
        } finally {
            tray.remove(trayIcon)
        }
    }

    fun notifyExportFinished(
        fileName: String,
        success: Boolean,
        successTitle: String = "Export ready",
        failureTitle: String = "Export failed"
    ) {
        if (!SystemTray.isSupported()) return

        val tray = SystemTray.getSystemTray()
        val image: Image = Toolkit.getDefaultToolkit().createImage("")
        val trayIcon = TrayIcon(image, "PosLe").apply {
            isImageAutoSize = true
        }

        try {
            tray.add(trayIcon)
            if (success) {
                trayIcon.displayMessage(
                    successTitle,
                    "$fileName has been generated successfully",
                    MessageType.INFO
                )
            } else {
                trayIcon.displayMessage(
                    failureTitle,
                    "Failed to generate $fileName",
                    MessageType.ERROR
                )
            }
        } finally {
            tray.remove(trayIcon)
        }
    }
}
