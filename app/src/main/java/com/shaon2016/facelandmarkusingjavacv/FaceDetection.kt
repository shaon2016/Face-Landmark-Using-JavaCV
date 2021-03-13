package com.shaon2016.facelandmarkusingjavacv

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.shaon2016.facelandmarkusingjavacv.util.FileUtil
/*import org.bytedeco.javacpp.opencv_face.*
import org.bytedeco.javacpp.opencv_objdetect.*
import org.bytedeco.javacpp.opencv_core.*
import org.bytedeco.javacpp.opencv_imgproc.**/
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point2fVectorVector
import org.bytedeco.opencv.opencv_core.RectVector
import org.bytedeco.opencv.opencv_face.FacemarkKazemi
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier
import org.opencv.core.CvType.CV_8U
import org.opencv.core.CvType.channels
import java.io.File


object FaceDetection {

     val TAG = FaceDetection::class.java.simpleName

    private lateinit var faceCascade: CascadeClassifier
    private lateinit var facemark: FacemarkKazemi

    fun loadFaceDetectionModel(context: Context) {
        val file = File(C.modelHarcascadePath(context))
        if (!file.exists())
            FileUtil.copyFileFromAsset(
                context, "haarcascade_frontalface_alt2.xml",
                C.modelHarcascadePath(context)
            )

        faceCascade = CascadeClassifier(C.modelHarcascadePath(context))
    }

     fun loadFaceMarkModel(context: Context) {
        val file = File(C.modelFaceLandMark(context))
        if (!file.exists())
            FileUtil.copyFileFromAsset(
                context, "face_landmark_model.dat",
                C.modelFaceLandMark(context)
            )

        facemark = FacemarkKazemi.create()

        // Load landmark detector
        facemark.loadModel(C.modelFaceLandMark(context))
    }

    fun detectFaces(mat: Mat): RectVector {
        // convert to grayscale and equalize histograe for better detection
        val grayMat = Mat()
        cvtColor(mat, grayMat, COLOR_BGR2GRAY)
        equalizeHist(grayMat, grayMat)

        // Find faces on the image
        val faces = RectVector()
        faceCascade.detectMultiScale(grayMat, faces)

        Log.d(TAG, "Faces detected: ${faces.size()}")

        return faces
    }

    fun detectLandmarks(mat: Mat, faces: RectVector): Point2fVectorVector {
        val landmarks = Point2fVectorVector()

        // Run landmark detector
        facemark.fit(mat, faces, landmarks)

        return landmarks
    }

    fun bitmapToMat(bitmap: Bitmap): Mat {
        val frame = AndroidFrameConverter().convert(bitmap)
        return OpenCVFrameConverter.ToMat().convertToMat(frame)
    }
}