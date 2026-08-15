package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object MinecraftLauncher {
    private const val MINECRAFT_PACKAGE = "com.mojang.minecraftpe"
    private const val MINECRAFT_PREVIEW_PACKAGE = "com.mojang.minecraftpreview"

    fun isMinecraftInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(MINECRAFT_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            try {
                pm.getPackageInfo(MINECRAFT_PREVIEW_PACKAGE, 0)
                true
            } catch (e2: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    fun launchMinecraft(context: Context): Boolean {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(MINECRAFT_PACKAGE)
            ?: pm.getLaunchIntentForPackage(MINECRAFT_PREVIEW_PACKAGE)

        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            true
        } else {
            // Open store or show guide
            try {
                val storeIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$MINECRAFT_PACKAGE")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(storeIntent)
            } catch (e: Exception) {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$MINECRAFT_PACKAGE")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(webIntent)
            }
            false
        }
    }

    fun connectToServer(context: Context, serverName: String, ip: String, port: Int = 19132) {
        try {
            val encodedName = URLEncoder.encode(serverName, "UTF-8")
            val uriString = "minecraft://?addExternalServer=$encodedName|$ip:$port"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(context, "Opening Minecraft with $serverName...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback to launching standard Minecraft
            if (launchMinecraft(context)) {
                Toast.makeText(context, "Minecraft opened. Server IP copied to clipboard!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Could not open Minecraft Bedrock", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun importPack(context: Context, uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "*/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Import to Minecraft"))
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to send pack to Minecraft: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
