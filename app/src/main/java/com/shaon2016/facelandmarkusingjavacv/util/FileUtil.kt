package com.shaon2016.facelandmarkusingjavacv.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {
    fun copyFileFromAsset(context: Context, assetFileName: String, outputPath: String) {
        val assetManager = context.assets
        try {
            val `in` = assetManager.open(assetFileName)
            val out = FileOutputStream(File(outputPath))
            val buffer = ByteArray(1024)
            var read: Int = `in`.read(buffer)
            while (read != -1) {
                out.write(buffer, 0, read)
                read = `in`.read(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    /**
     * It creates a image file with png extension
     * */
    fun getImageOutputDirectory(context: Context): File {

        val mediaDir =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                context.getExternalFilesDirs(Environment.DIRECTORY_DCIM).firstOrNull()?.let {
                    File(it, "images").apply { mkdirs() }
                }
            } else {
                null
            }
        return if (mediaDir != null && mediaDir.exists())
            File(
                mediaDir,
                SimpleDateFormat(
                    FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".png"
            )
        else File(
            context.filesDir,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".png"
        )
    }
}