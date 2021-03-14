package com.shaon2016.facelandmarkusingjavacv

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat.getExternalFilesDirs

object C {

    fun modelHarcascadePath(context: Context) =
        "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/$faceDetectionModelName"

    fun modelFaceLandMark(context: Context) =
        "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/$faceMarkModelName"

    const val faceMarkModelName = "lbfmodel.yaml"
    const val faceDetectionModelName = "haarcascade_frontalface_alt2.xml"
}