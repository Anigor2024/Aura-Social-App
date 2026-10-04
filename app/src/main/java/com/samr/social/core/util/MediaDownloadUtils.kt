package com.samr.social.core.util

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.samr.social.core.model.MediaKind
import java.io.File
import java.io.FileOutputStream

object MediaDownloadUtils {

    fun saveToDownloads(
        context: Context,
        uriString: String,
        kind: MediaKind,
        title: String
    ): Boolean {
        val uri = runCatching { Uri.parse(uriString) }.getOrNull() ?: return false
        val extension = when (kind) {
            MediaKind.IMAGE -> ".jpg"
            MediaKind.VIDEO -> ".mp4"
            MediaKind.AUDIO -> ".m4a"
        }
        val safeTitle = title
            .ifBlank { "SAMR_media" }
            .replace(Regex("[^\p{L}\p{N}_\- ]"), "")
            .trim()
            .take(48)
            .ifBlank { "SAMR_media" }
        val fileName = "${safeTitle}_${System.currentTimeMillis()}$extension"

        return if (uri.scheme == "http" || uri.scheme == "https") {
            runCatching {
                val request = DownloadManager.Request(uri)
                    .setTitle(safeTitle)
                    .setDescription("SAMR media")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(request)
                true
            }.getOrDefault(false)
        } else {
            copyLocalUri(context, uri, kind, fileName)
        }
    }

    private fun copyLocalUri(
        context: Context,
        source: Uri,
        kind: MediaKind,
        fileName: String
    ): Boolean {
        return runCatching {
            val input = context.contentResolver.openInputStream(source) ?: return false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val mime = when (kind) {
                    MediaKind.IMAGE -> "image/jpeg"
                    MediaKind.VIDEO -> "video/mp4"
                    MediaKind.AUDIO -> "audio/mp4"
                }
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mime)
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/SAMR")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val target = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                ) ?: return false
                context.contentResolver.openOutputStream(target)?.use { output ->
                    input.use { it.copyTo(output) }
                } ?: return false
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                context.contentResolver.update(target, values, null, null)
                true
            } else {
                val directory = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "SAMR"
                ).apply { mkdirs() }
                val target = File(directory, fileName)
                input.use { stream ->
                    FileOutputStream(target).use { output ->
                        stream.copyTo(output)
                    }
                }
                true
            }
        }.getOrDefault(false)
    }
}
