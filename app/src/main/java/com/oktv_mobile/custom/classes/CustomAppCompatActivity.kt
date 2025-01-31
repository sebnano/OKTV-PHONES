package com.oktv_mobile.custom.classes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.oktv_mobile.R
import com.oktv_mobile.utils.ConnectionLiveData
import com.oktv_mobile.utils.InitApplication
import com.oktv_mobile.utils.LoadingDialog

abstract class CustomAppCompatActivity : AppCompatActivity() {

    private lateinit var context : Context
    lateinit var loadingDialog: LoadingDialog
    private var initApplication: InitApplication? = null
    private var isInternetAvailable = false
    private var firstTime = true
    private var imageFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        ConnectionLiveData(this).observe(this) { t ->
            isInternetAvailable = t
            if (!firstTime) {

                /*                val string = if (t) "Internet back" else "Internet Lost"
                    Snackbar.make(window.decorView.rootView, string, Snackbar.LENGTH_LONG).setAction("CLOSE") { }.setActionTextColor(resources.getColor(android.R.color.holo_red_light)).show()*/

            }
            firstTime = false
        }
        loadingDialog = LoadingDialog(this)
    }

//    open fun isAppUpdateRequired(activity: AppCompatActivity?) : Boolean {
//        var installedVersion = 0.0
//        var appVersion = 0.0
//        var version: String? = null
//        try {
//            val pInfo = packageManager.getPackageInfo(packageName, 0)
//            version = pInfo.versionName
//            installedVersion = pInfo.versionName.toDouble()
//
//            if (Constant.APPVERSION.isNotEmpty()) {
//                appVersion = MyPreferences.getFromPreferences(this, Constant.APPVERSION)!!.toDouble()
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//
//        return when {
//            MyPreferences.getFromPreferences(this, Constant.APPVERSION)!!.isNotEmpty() -> {
//                Constant.alreadyShowUpdate = 1
//                installedVersion < appVersion
//            }
//            else -> {
//                false
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        firstTime = true
    }

    fun openUrlInBrowser(view: View ,url : String ){
        view.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }

    override fun setContentView(layoutResID: Int) {
        initApplication = InitApplication(this)
        if (initApplication!!.state) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        super.setContentView(layoutResID)


    }

    abstract fun initData()

    fun showToast(message: String = getString(R.string.something_went_wrong_)) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    fun startActivity(cls: Class<*>?) {
        val intent = Intent(this, cls)
        super.startActivity(intent)
    }

    fun showLoadingDialog() {
        if (this::loadingDialog.isInitialized && !loadingDialog.isShowing()) {
            loadingDialog.show()
        }
    }

    fun changeLoadingStatus(show: Boolean) {
        if (show) showLoadingDialog()
        else hideLoadingDialog()
    }

    fun showLoadingDialog(title: String) {
        showLoadingDialog()
    }

    fun showHidePass(editText: EditText, imageView: ImageView) {
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            imageView.setImageResource(R.drawable.ic_eye_open)
            //Show Password
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            imageView.setImageResource(R.drawable.ic_eye_close)
            //Hide Password
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        editText.setSelection( editText.text.toString().length )
    }

    fun hideLoadingDialog() {
        if (this::loadingDialog.isInitialized && loadingDialog.isShowing()) {
            loadingDialog.dismiss()
        }
    }

    fun showInternetErrorDialog() {
        showToast("Internet not available")
    }

    fun showAuthErrorDialog() {
        showToast("Auth Error")
    }

}