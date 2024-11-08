package com.example.modelbookingapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageUtils {
    private const val MAX_IMAGE_SIZE = 1024 // max width/height
    private const val QUALITY = 80 // compression quality
    private const val MAX_FILE_SIZE = 1024 * 1024 // 1MB

    suspend fun compressImage(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Calculate scaling
            val scale = calculateScale(originalBitmap)
            val scaledBitmap = Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * scale).toInt(),
                (originalBitmap.height * scale).toInt(),
                true
            )

            // Compress to file
            val outputFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            FileOutputStream(outputFile).use { outputStream ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
            }

            originalBitmap.recycle()
            scaledBitmap.recycle()

            outputFile
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateScale(bitmap: Bitmap): Float {
        val maxDimension = maxOf(bitmap.width, bitmap.height)
        return if (maxDimension > MAX_IMAGE_SIZE) {
            MAX_IMAGE_SIZE.toFloat() / maxDimension
        } else {
            1f
        }
    }
}