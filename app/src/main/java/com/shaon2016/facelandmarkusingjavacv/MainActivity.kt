package com.shaon2016.facelandmarkusingjavacv

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.shaon2016.facelandmarkusingjavacv.camera.CameraXManager
import com.shaon2016.facelandmarkusingjavacv.camera.FaceOverlayView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    // views
    private lateinit var viewFinder: PreviewView
    private lateinit var viewFaceOverlay: FaceOverlayView

    // Permission
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val REQUEST_CODE_PERMISSIONS = 999

    // Camera
    private val cm by lazy {
        CameraXManager(this, viewFinder, viewFaceOverlay)
    }

    private var isModelLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pb = findViewById<ProgressBar>(R.id.pb)
        viewFinder = findViewById(R.id.viewFinder)
        viewFaceOverlay = findViewById(R.id.viewFaceOverlay)

        lifecycleScope.launch(Dispatchers.Default) {
            FaceDetection.loadFaceDetectionModel(this@MainActivity)
            FaceDetection.loadFaceMarkModel(this@MainActivity)

            isModelLoaded = true

            lifecycleScope.launch(Dispatchers.Main) {
                pb.visibility = View.GONE

                // Request camera permissions
                if (allPermissionsGranted()) {
                    viewFinder.post {
                        cm.setupCamera()
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        this@MainActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                    )
                }
            }
        }
    }

    /**
     * Check all permissions are granted - use for Camera permission in this example.
     */
    private fun allPermissionsGranted(): Boolean = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cm.setupCamera()
            } else {
                Toast.makeText(
                    this, getString(R.string.permission_deny_text), Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Lifecycle
    override fun onResume() {
        super.onResume()

        if (isModelLoaded) cm.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

        cm.onDestroyed()
    }

}