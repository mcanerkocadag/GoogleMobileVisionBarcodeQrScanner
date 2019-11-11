package com.quiz.googlemobilevisionbarcodeqrscanner2

import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.Detector
import android.widget.Toast
import android.view.SurfaceHolder
import android.Manifest.permission
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.BarcodeDetector
import android.os.Bundle
import android.widget.TextView
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View


class ScannedBarcodeActivity : RuntimePermissionsActivity() {
    override fun onPermissionsGranted(requestCode: Int) {
        recreate()
    }

    private lateinit var view: View
    private lateinit var scanListener: ScanListener
    internal val TAG = "ScannedBarcodeActivity"
    internal lateinit var surfaceView: SurfaceView
    internal lateinit var previewSurfaceView: SurfaceView
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    internal var intentData = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode)

        initViews()
    }

    private fun initViews() {
        surfaceView = findViewById(R.id.surfaceView)
    }

    private fun initialiseDetectorsAndSources() {

        Toast.makeText(applicationContext, "Barcode scanner started", Toast.LENGTH_SHORT).show()

        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        cameraSource = CameraSource.Builder(this, barcodeDetector!!)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScannedBarcodeActivity,
                            permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource!!.start(surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@ScannedBarcodeActivity,
                            arrayOf(permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })


        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Log.i(TAG, "To prevent memory leaks barcode scanner has been stopped")
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {

                    if (barcodes.valueAt(0).email != null) {
                        intentData = barcodes.valueAt(0).email.address
                    } else {
                        intentData = barcodes.valueAt(0).displayValue
                    }
                    if (intentData.isEmpty())
                        intentData = ""
                    val returnIntent = Intent()
                    returnIntent.putExtra("result", intentData)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }


    companion object {
        private val REQUEST_CAMERA_PERMISSION = 201
        val SCAN_REQUEST_CODE = 2019
    }
}