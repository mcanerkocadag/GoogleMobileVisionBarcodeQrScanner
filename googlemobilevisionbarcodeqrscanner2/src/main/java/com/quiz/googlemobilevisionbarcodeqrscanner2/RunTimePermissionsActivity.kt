package com.quiz.googlemobilevisionbarcodeqrscanner2

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.SparseIntArray
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class RuntimePermissionsActivity : AppCompatActivity() {

    private var mErrorString: SparseIntArray? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        mErrorString = SparseIntArray()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var permissionCheck = PackageManager.PERMISSION_GRANTED

        for (permission in grantResults) {

            permissionCheck = permissionCheck + permission

        }

        if (grantResults.size > 0 && permissionCheck == PackageManager.PERMISSION_GRANTED) {

            onPermissionsGranted(requestCode)

        } else {


            val intent = Intent()

            intent.action = ACTION_APPLICATION_DETAILS_SETTINGS

            intent.addCategory(Intent.CATEGORY_DEFAULT)

            intent.data = Uri.parse("package:$packageName")

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

            startActivity(intent)
        }

    }

    fun requestAppPermissions(
        requestedPermissions: Array<String>,

        stringId: Int, requestCode: Int
    ) {

        mErrorString!!.put(requestCode, stringId)

        var permissionCheck = PackageManager.PERMISSION_GRANTED

        var shouldShowRequestPermissionRationale = false

        for (permission in requestedPermissions) {

            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission)

            shouldShowRequestPermissionRationale =
                shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission
                )

        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (!shouldShowRequestPermissionRationale) {

                ActivityCompat.requestPermissions(
                    this@RuntimePermissionsActivity,
                    requestedPermissions,
                    requestCode
                )

            } else {

                Toast.makeText(
                    this,
                    "Lütfen bu işlemi yapabilmek için uygulama-uygulama izinleri bölümünden izinlerini veriniz.",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent()

                intent.action = ACTION_APPLICATION_DETAILS_SETTINGS

                intent.addCategory(Intent.CATEGORY_DEFAULT)

                intent.data = Uri.parse("package:$packageName")

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

                startActivity(intent)

            }

        } else {

            onPermissionsGranted(requestCode)

        }

    }

    abstract fun onPermissionsGranted(requestCode: Int)

}