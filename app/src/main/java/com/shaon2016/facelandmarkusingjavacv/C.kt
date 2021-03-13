package com.shaon2016.facelandmarkusingjavacv

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat.getExternalFilesDirs

object C {

    fun modelHarcascadePath(context: Context) =
        "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/haarcascade_frontalface_alt2.xml"

    fun modelFaceLandMark(context: Context) =
        "${context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/face_landmark_model.dat"

}