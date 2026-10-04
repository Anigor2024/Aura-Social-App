package com.samr.social.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import androidx.core.content.FileProvider
import com.samr.social.core.model.MediaKind
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object MediaFileUtils {

    fun mediaDirectory(context: Context): File =
        File(context.filesDir, "media").apply { mkdirs() }

    fun createAudioFile(context: Context): File =
        File(mediaDirectory(context), "samr_audio_${System.currentTimeMillis()}.m4a")

    fun uriForFile(context: Context, file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

    fun copyUriToCache(
        context: Context,
        source: Uri,
        kind: MediaKind
    ): Uri? {
        val extension = when (kind) {
            MediaKind.IMAGE -> ".jpg"
            MediaKind.VIDEO -> ".mp4"
            MediaKind.AUDIO -> ".m4a"
        }
        val target = File(mediaDirectory(context), "import_${UUID.randomUUID()}$extension")
        return runCatching {
            context.contentResolver.openInputStream(source)?.use { input ->
                FileOutputStream(target).use { output -> input.copyTo(output) }
            } ?: return null
            uriForFile(context, target)
        }.getOrNull()
    }

    fun createDesignCard(
        context: Context,
        headline: String,
        subtitle: String,
        emoji: String,
        styleIndex: Int,
        width: Int,
        height: Int
    ): Uri? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val palettes = listOf(
            intArrayOf(Color.rgb(18, 18, 24), Color.rgb(94, 68, 35)),
            intArrayOf(Color.rgb(13, 24, 31), Color.rgb(22, 94, 93)),
            intArrayOf(Color.rgb(28, 18, 42), Color.rgb(91, 49, 132)),
            intArrayOf(Color.rgb(31, 20, 20), Color.rgb(133, 63, 63)),
            intArrayOf(Color.rgb(7, 28, 33), Color.rgb(29, 98, 118)),
            intArrayOf(Color.rgb(24, 24, 24), Color.rgb(73, 73, 73))
        )
        val palette = palettes[((styleIndex % palettes.size) + palettes.size) % palettes.size]
        val background = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                palette[0],
                palette[1],
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), background)

        val accent = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(45, 255, 220, 155)
        }
        canvas.drawCircle(width * 0.82f, height * 0.18f, width * 0.28f, accent)
        canvas.drawCircle(width * 0.14f, height * 0.88f, width * 0.38f, accent)

        val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = width * 0.11f
        }
        canvas.drawText(emoji.ifBlank { "✦" }, width * 0.08f, height * 0.18f, emojiPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = width * 0.075f
            isFakeBoldText = true
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(220, 255, 255, 255)
            textSize = width * 0.036f
        }

        drawWrappedText(
            canvas = canvas,
            text = headline.ifBlank { "سَمَر" },
            paint = titlePaint,
            x = width * 0.08f,
            y = height * 0.48f,
            maxWidth = width * 0.84f,
            lineSpacing = titlePaint.textSize * 1.22f,
            maxLines = 4
        )
        drawWrappedText(
            canvas = canvas,
            text = subtitle,
            paint = bodyPaint,
            x = width * 0.08f,
            y = height * 0.73f,
            maxWidth = width * 0.84f,
            lineSpacing = bodyPaint.textSize * 1.35f,
            maxLines = 3
        )

        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 255, 235, 190)
            textSize = width * 0.025f
            letterSpacing = 0.12f
        }
        canvas.drawText("SAMR • سَمَر", width * 0.08f, height * 0.94f, brandPaint)

        return saveBitmap(context, bitmap, "design")
    }

    fun applyImageEdit(
        context: Context,
        source: Uri,
        filterName: String,
        overlayText: String
    ): Uri? {
        val sourceBitmap = runCatching {
            context.contentResolver.openInputStream(source)?.use(BitmapFactory::decodeStream)
        }.getOrNull() ?: return null

        val output = Bitmap.createBitmap(
            sourceBitmap.width,
            sourceBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val matrix = when (filterName.lowercase()) {
            "warm" -> ColorMatrix(
                floatArrayOf(
                    1.14f, 0f, 0f, 0f, 9f,
                    0f, 1.03f, 0f, 0f, 3f,
                    0f, 0f, 0.88f, 0f, -4f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            "cool" -> ColorMatrix(
                floatArrayOf(
                    0.92f, 0f, 0f, 0f, -3f,
                    0f, 1.01f, 0f, 0f, 1f,
                    0f, 0f, 1.14f, 0f, 8f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            "noir" -> ColorMatrix().apply { setSaturation(0f) }
            "vibrant" -> ColorMatrix().apply { setSaturation(1.35f) }
            else -> ColorMatrix()
        }
        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)

        if (overlayText.isNotBlank()) {
            val overlayBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(118, 0, 0, 0)
            }
            canvas.drawRoundRect(
                sourceBitmap.width * 0.05f,
                sourceBitmap.height * 0.73f,
                sourceBitmap.width * 0.95f,
                sourceBitmap.height * 0.93f,
                28f,
                28f,
                overlayBackground
            )
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = sourceBitmap.width * 0.055f
                isFakeBoldText = true
            }
            drawWrappedText(
                canvas = canvas,
                text = overlayText,
                paint = textPaint,
                x = sourceBitmap.width * 0.09f,
                y = sourceBitmap.height * 0.81f,
                maxWidth = sourceBitmap.width * 0.80f,
                lineSpacing = textPaint.textSize * 1.25f,
                maxLines = 2
            )
        }

        sourceBitmap.recycle()
        return saveBitmap(context, output, "edit")
    }

    private fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        prefix: String
    ): Uri? {
        val target = File(mediaDirectory(context), "${prefix}_${UUID.randomUUID()}.jpg")
        return runCatching {
            FileOutputStream(target).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 94, it)
            }
            bitmap.recycle()
            uriForFile(context, target)
        }.getOrNull()
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        paint: Paint,
        x: Float,
        y: Float,
        maxWidth: Float,
        lineSpacing: Float,
        maxLines: Int
    ) {
        if (text.isBlank()) return
        val words = text.split(" ")
        var line = ""
        var currentY = y
        var lineCount = 0
        for (word in words) {
            val candidate = if (line.isBlank()) word else "$line $word"
            if (paint.measureText(candidate) > maxWidth && line.isNotBlank()) {
                canvas.drawText(line, x, currentY, paint)
                currentY += lineSpacing
                lineCount++
                if (lineCount >= maxLines) return
                line = word
            } else {
                line = candidate
            }
        }
        if (line.isNotBlank() && lineCount < maxLines) {
            canvas.drawText(line, x, currentY, paint)
        }
    }
}
