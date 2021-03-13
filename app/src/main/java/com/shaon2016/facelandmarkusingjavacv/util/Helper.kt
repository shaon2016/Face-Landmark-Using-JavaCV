package com.shaon2016.facelandmarkusingjavacv.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.DisplayMetrics
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

object Helper {
    fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun ImageProxy.toBitmap(
        context: Context
    ): Bitmap? {
        val image = image ?: return null

        val rotationMatrix = Matrix()
        // if front camera is enable
        rotationMatrix.preScale(1.0f, -1.0f) // front camera in vertical

        rotationMatrix.postRotate(imageInfo.rotationDegrees.toFloat())


        val bitmap = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        )

        // Pass image to an image analyser
        YuvToRgbConverter(context).yuvToRgb(image, bitmap)

        // Create the Bitmap in the correct orientation
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            rotationMatrix,
            false
        )
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun ImageProxy.toScaledBitmap(
        context: Context,
        imageSizeX: Int,
        imageSizeY: Int
    ): Bitmap? {
        val image = image ?: return null

        val rotationMatrix = Matrix()
        // if front camera is enable
        rotationMatrix.preScale(1.0f, -1.0f) // front camera in vertical

        rotationMatrix.postRotate(imageInfo.rotationDegrees.toFloat())


        var bitmap = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        )

        bitmap = Bitmap.createScaledBitmap(bitmap, imageSizeX, imageSizeY, false)

        // Pass image to an image analyser
        YuvToRgbConverter(context).yuvToRgb(image, bitmap)

        // Create the Bitmap in the correct orientation
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            rotationMatrix,
            false
        )
    }



    private const val IMAGE_MEAN = 127.5f
    private const val IMAGE_STD = 127.5f
    fun convertBitmapToByteBuffer(bitmap: Bitmap, INPUT_SIZE: Int): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3)

        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }
        return byteBuffer
    }
}