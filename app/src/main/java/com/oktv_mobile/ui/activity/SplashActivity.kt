package com.oktv_mobile.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.oktv_mobile.R
import com.oktv_mobile.custom.classes.CustomAppCompatActivity
import com.oktv_mobile.utils.Constant
import com.oktv_mobile.utils.DownloadManager
import com.oktv_mobile.utils.MyPreferences

@SuppressLint("CustomSplashScreen")
class SplashActivity : CustomAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initData()
    }

    /** This Function For Initialize **/
    override fun initData() {
        Handler(Looper.getMainLooper()).postDelayed({
            Log.e("ACCESSTOKEN","1-->"+ MyPreferences.getFromPreferences(this, Constant.ACCESSTOKEN))
            if (MyPreferences.getFromPreferences(this, Constant.ACCESSTOKEN).isNotEmpty()) {
                startActivity(Intent(this, HomeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                finish()
            }
        }, 3000)
    }


//    private fun requestStoragePermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 100)
//    }
//
//    private fun isStoragePermissionGranted(): Boolean {
//        val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
//        val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
//        return (ContextCompat.checkSelfPermission(this, writePermission) == PackageManager.PERMISSION_GRANTED
//                && ContextCompat.checkSelfPermission(this, readPermission) == PackageManager.PERMISSION_GRANTED)
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 100) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//               initData()
//            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
//              initData()
//            }
//        }
//    }
}