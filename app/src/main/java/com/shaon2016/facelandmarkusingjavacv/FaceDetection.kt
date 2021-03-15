package com.shaon2016.facelandmarkusingjavacv

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import com.shaon2016.facelandmarkusingjavacv.util.FileUtil
/*import org.bytedeco.javacpp.opencv_face.*
import org.bytedeco.javacpp.opencv_objdetect.*
import org.bytedeco.javacpp.opencv_core.*
import org.bytedeco.javacpp.opencv_imgproc.**/
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.*
import org.bytedeco.opencv.opencv_face.FacemarkLBF
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier
import java.io.File
import kotlin.math.sqrt

/**
 * FACIAL_LANDMARKS_68_IDXS = OrderedDict([
("mouth", (48, 68)),
("inner_mouth", (60, 68)),
("right_eyebrow", (17, 22)),
("left_eyebrow", (22, 27)),
("right_eye", (36, 42)),
("left_eye", (42, 48)),
("nose", (27, 36)),
("jaw", (0, 17))
])
 * */
object FaceDetection {

    val TAG = FaceDetection::class.java.simpleName

    private lateinit var faceCascade: CascadeClassifier
    private lateinit var facemark: FacemarkLBF
    private const val EYE_AR_THRESH = 0.25

    fun loadFaceDetectionModel(context: Context) {
        val file = File(C.modelHarcascadePath(context))
        if (!file.exists())
            FileUtil.copyFileFromAsset(
                context, C.faceDetectionModelName,
                C.modelHarcascadePath(context)
            )

        faceCascade = CascadeClassifier(C.modelHarcascadePath(context))
    }

    fun loadFaceMarkModel(context: Context) {
        val file = File(C.modelFaceLandMark(context))
        if (!file.exists())
            FileUtil.copyFileFromAsset(
                context, C.faceMarkModelName,
                C.modelFaceLandMark(context)
            )

        facemark = FacemarkLBF.create()

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

    fun isBlinked(landmarks: Point2fVector): Boolean {
        //  36..41
        //  42..47

        /*val ear1 = ((37 - 41) + (38 - 40)) / (2 * (36 - 39))
       */

        val a1 = euclideanDistance(
            landmarks[41].x(),
            landmarks[41].y(),
            landmarks[37].x(),
            landmarks[37].y()
        )
        val b1 = euclideanDistance(
            landmarks[38].x(),
            landmarks[38].y(),
            landmarks[40].x(),
            landmarks[40].y()
        )
        val c1 = euclideanDistance(
            landmarks[36].x(),
            landmarks[36].y(),
            landmarks[39].x(),
            landmarks[39].y()
        )

        val ear1AspectRatio = (a1 + b1) / (2.0 * c1)

//        val ear2 = ((43 - 47) + (44 - 46)) / (2 * (42 - 45))

        val a2 = euclideanDistance(
            landmarks[43].x(),
            landmarks[43].y(),
            landmarks[47].x(),
            landmarks[47].y()
        )
        val b2 = euclideanDistance(
            landmarks[44].x(),
            landmarks[44].y(),
            landmarks[46].x(),
            landmarks[46].y()
        )
        val c2 = euclideanDistance(
            landmarks[42].x(),
            landmarks[42].y(),
            landmarks[45].x(),
            landmarks[45].y()
        )

        val ear2AspectRatio = (a2 + b2) / (2.0 * c2)

        val finalRatio = (ear1AspectRatio + ear2AspectRatio) / 2

        Log.d(TAG, finalRatio.toString())

        if (finalRatio < EYE_AR_THRESH) return true

        return false
    }

    private fun euclideanDistance(x1: Float, y1: Float, x2: Float, y2: Float) =
        sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1))
}