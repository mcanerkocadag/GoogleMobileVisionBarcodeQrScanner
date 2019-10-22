package com.quiz.googlemobilevisionbarcodeqrscanner2

import android.Manifest
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.widget.Toast
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.File
import java.io.FileNotFoundException


class PictureBarcodeActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var txtResultBody: TextView

    private var detector: BarcodeDetector? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_picture)

        initViews()

        if (savedInstanceState != null) {
            if (imageUri != null) {
                imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI))
                txtResultBody.text = savedInstanceState.getString(SAVED_INSTANCE_RESULT)
            }
        }

        detector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
            .build()

        if (!detector!!.isOperational()) {
            txtResultBody.text = "Detector initialisation failed"
            return
        }
    }

    private fun initViews() {
        txtResultBody = findViewById(R.id.txtResultsBody)
    }

    override fun onClick(v: View) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takeBarcodePicture()
            } else {
                Toast.makeText(applicationContext, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            launchMediaScanIntent()
            try {


                val bitmap = decodeBitmapUri(this, imageUri)
                if (detector!!.isOperational() && bitmap != null) {
                    val frame = Frame.Builder().setBitmap(bitmap).build()
                    val barcodes = detector!!.detect(frame)
                    for (index in 0 until barcodes.size()) {
                        val code = barcodes.valueAt(index)
                        txtResultBody.text =
                            txtResultBody.text.toString() + "\n" + code.displayValue + "\n"

                        val type = barcodes.valueAt(index).valueFormat
                        when (type) {
                            Barcode.CONTACT_INFO -> Log.i(TAG, code.contactInfo.title)
                            Barcode.EMAIL -> Log.i(TAG, code.displayValue)
                            Barcode.ISBN -> Log.i(TAG, code.rawValue)
                            Barcode.PHONE -> Log.i(TAG, code.phone.number)
                            Barcode.PRODUCT -> Log.i(TAG, code.rawValue)
                            Barcode.SMS -> Log.i(TAG, code.sms.message)
                            Barcode.TEXT -> Log.i(TAG, code.displayValue)
                            Barcode.URL -> Log.i(TAG, "url: " + code.displayValue)
                            Barcode.WIFI -> Log.i(TAG, code.wifi.ssid)
                            Barcode.GEO -> Log.i(
                                TAG,
                                "" + code.geoPoint.lat + ":" + code.geoPoint.lng
                            )
                            Barcode.CALENDAR_EVENT -> Log.i(TAG, code.calendarEvent.description)
                            Barcode.DRIVER_LICENSE -> Log.i(TAG, code.driverLicense.licenseNumber)
                            else -> Log.i(TAG, code.rawValue)
                        }
                    }
                    if (barcodes.size() == 0) {
                        txtResultBody.text = "No barcode could be detected. Please try again."
                    }
                } else {
                    txtResultBody.text = "Detector initialisation failed"
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Failed to load Image", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, e.toString())
            }

        }
    }

    private fun takeBarcodePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "pic.jpg")
        imageUri = FileProvider.getUriForFile(
            this@PictureBarcodeActivity,
            BuildConfig.APPLICATION_ID + ".provider", photo
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent,
            CAMERA_REQUEST
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri!!.toString())
            outState.putString(SAVED_INSTANCE_RESULT, txtResultBody.text.toString())
        }
        super.onSaveInstanceState(outState)
    }

    private fun launchMediaScanIntent() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = imageUri
        this.sendBroadcast(mediaScanIntent)
    }

    @Throws(FileNotFoundException::class)
    private fun decodeBitmapUri(ctx: Context, uri: Uri?): Bitmap? {
        val targetW = 600
        val targetH = 600
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            uri?.let { ctx.getContentResolver().openInputStream(it) },
            null,
            bmOptions
        )
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeStream(
            uri?.let {
                ctx.getContentResolver()
                    .openInputStream(it)
            }, null, bmOptions
        )
    }

    companion object {
        private val REQUEST_CAMERA_PERMISSION = 200
        private val CAMERA_REQUEST = 101
        private val TAG = "API123"
        private val SAVED_INSTANCE_URI = "uri"
        private val SAVED_INSTANCE_RESULT = "result"
    }
}