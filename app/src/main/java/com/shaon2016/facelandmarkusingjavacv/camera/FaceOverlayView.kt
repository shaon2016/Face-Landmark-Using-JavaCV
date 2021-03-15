package com.shaon2016.facelandmarkusingjavacv.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.shaon2016.facelandmarkusingjavacv.R
import org.bytedeco.opencv.opencv_core.Point2fVector
import org.bytedeco.opencv.opencv_core.Point2fVectorVector
import org.bytedeco.opencv.opencv_core.Rect

class FaceOverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var faceRect = Rect()
    private val paint = Paint()

    // Landmark
    // Stroke & paint.
    private val WIDTH = 2f
    private var mStrokeWidth = 2
    private var mStrokePaint: Paint
    private val mRenderMatrix = Matrix()
    private val rPaint = Paint()

    private var landmarks: Point2fVector? = null

    init {
        // Landmark
        val density = getContext()
            .resources.displayMetrics.density

        mStrokeWidth = (density * WIDTH).toInt()

        mStrokePaint = Paint()
        mStrokePaint.color = ContextCompat.getColor(getContext(), R.color.purple_200)

        rPaint.color = Color.rgb(255, 160, 0)
        rPaint.style = Paint.Style.STROKE
        rPaint.strokeWidth = 5f

        // Face
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            it.drawRect(
                android.graphics.Rect(
                    faceRect.x(),
                    faceRect.y(),
                    faceRect.x() + faceRect.width(),
                    faceRect.y() + faceRect.height()
                ), paint
            )

            landmarks?.let {
                for (i in 0 until landmarks!!.size()) {
                    val v = landmarks!![i]
                    val x = v.x()
                    val y = v.y()

//                    canvas.drawCircle(x, y, 8f, mStrokePaint)

                    canvas.drawText("$i", x, y, mStrokePaint)

                }
            }
        }
    }

    fun setFaceLandmarks(
        rect: Rect,
        landmarks: Point2fVector
    ) {
        faceRect = rect
        this.landmarks = landmarks
        invalidate()
    }

}