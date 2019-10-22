package com.quiz.googlemobilevisionbarcodeqrscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.app.Activity
import android.widget.Toast
import com.quiz.googlemobilevisionbarcodeqrscanner2.PictureBarcodeActivity
import com.quiz.googlemobilevisionbarcodeqrscanner2.ScannedBarcodeActivity


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var btnTakePicture: Button
    private lateinit var btnScanBarcode:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        btnTakePicture = findViewById(R.id.btnTakePicture)
        btnScanBarcode = findViewById(R.id.btnScanBarcode)
        btnTakePicture.setOnClickListener(this)
        btnScanBarcode.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        when (v.getId()) {
            R.id.btnTakePicture -> startActivityForResult(
                Intent(
                    this@MainActivity,
                    PictureBarcodeActivity::class.java
                ), ScannedBarcodeActivity.SCAN_REQUEST_CODE
            )
            R.id.btnScanBarcode -> startActivityForResult(
                Intent(
                    this@MainActivity,
                    ScannedBarcodeActivity::class.java
                ), ScannedBarcodeActivity.SCAN_REQUEST_CODE
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ScannedBarcodeActivity.SCAN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                Toast.makeText(applicationContext,""+result,Toast.LENGTH_LONG).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        super.onActivityResult(requestCode,resultCode,data)
    }
}
