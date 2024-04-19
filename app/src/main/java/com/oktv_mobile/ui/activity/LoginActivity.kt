package com.oktv_mobile.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oktv_mobile.R
import com.oktv_mobile.custom.classes.CustomAppCompatActivity
import com.oktv_mobile.ui.viewmodel.HomeVM
import com.oktv_mobile.ui.viewmodel.LoginVM
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.offline.Download
import com.oktv_mobile.ui.`interface`.LanguageCallBack
import com.oktv_mobile.ui.adapter.LanguageAdapter
import com.oktv_mobile.ui.model.homemodel.CategoryChannelModel
import com.oktv_mobile.ui.model.homemodel.UserDeviceModel
import com.oktv_mobile.ui.model.loginmodel.ConfiguracionModel
import com.oktv_mobile.ui.model.loginmodel.LanguageStringModel
import com.oktv_mobile.ui.model.loginmodel.UserOperatorModel
import com.oktv_mobile.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import java.net.NetworkInterface

class LoginActivity : CustomAppCompatActivity(), LanguageCallBack {

    /** This Variables For PopupView **/
    private var myPopupWindow: PopupWindow? = null
    private var popupView: View? = null

    /** This Variable For ViewModel **/
    private lateinit var loginVM : LoginVM
    private lateinit var homeVM : HomeVM

    /** This Variable Used for Store Api Data **/
    private var languageList = ArrayList<ConfiguracionModel>()
    private var operatorList = ArrayList<UserOperatorModel>()
    private var emptyMaxDeviceOperator : UserOperatorModel? = null

    /** This Variable Used for Adapter **/
    private lateinit var languageAdapter : LanguageAdapter

    private lateinit var rvName : RecyclerView

    private var passwordCharacter = ""
    private var enterPassword = ""
    private var enterUserName = ""
    private var notValidMessage = ""


    private var databaseHandler: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initData()
        setOnViewClicks()
        observeData()
    }

    /** This Function For Initialize **/
    override fun initData() {
        showLoadingDialog()
        loginVM = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginVM::class.java]
        homeVM = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeVM::class.java]
        databaseHandler = DBHelper(this, null)

        popupView = layoutInflater.inflate(R.layout.raw_popup_view, null)
        myPopupWindow = PopupWindow(popupView,ConstraintLayout.LayoutParams.WRAP_CONTENT , ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        rvName = popupView!!.findViewById(R.id.rvLanguageName)

        languageAdapter = LanguageAdapter(languageList,this)
        rvName.layoutManager = LinearLayoutManager(this)
        rvName.adapter = languageAdapter

        loginVM.configuration()

        DownloadManager.initialize(this, this, this)
        DownloadManager.getLanguages()
    }

    /** This Function For Click Listener **/
    private fun setOnViewClicks() {
        ivEye.setOnClickListener { showHidePass(etPassword , ivEye) }

        clLanguage.setOnClickListener {
            popupView?.show()
            myPopupWindow?.showAsDropDown(it,0,0)
        }

        btnLogin.setOnClickListener {
            if (etUserName.text.toString().trim().isEmpty()) {
                showToast(enterUserName)
            } else if (etPassword.text.toString().trim().isEmpty()) {
                showToast(enterPassword)
            } else if (etPassword.text.toString().trim().length < 6) {
                showToast(passwordCharacter)
            } else {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                showLoadingDialog()
                loginUser()
            }
        }

        btnLeave.setOnClickListener { finish() }
    }

    /**This function used to get response from API**/
    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun observeData() {
        loginVM.observeConfiguration().observe(this) {
            if (it != null) {
                Glide.with(this).load(it[2].value).into(ivMainBackground)
                MyPreferences.saveStringInPreference(this, Constant.MaxDevice, it[3].value)
                MyPreferences.saveStringInPreference(this, Constant.languageCode,it[5].value)
            } else {
                hideLoadingDialog()
            }
        }

        homeVM.observeDevices().observe(this) {
            if (it.size < operatorList[0].maxDevices.toInt()) {
                homeVM.addNewConnectedDevice(java.util.HashMap<String, Any>().apply {
                    put("type", listOf(mapOf("target_id" to "dispositivos_por_usuarios")))
                    put("title", listOf(mapOf("value" to "Dispositivo del usuario")))
                    put("field_id_opera", listOf(mapOf("value" to operatorList[0].operador)))
                    put("field_dispositivo_ip", listOf(mapOf("value" to MyPreferences.getFromPreferences(this@LoginActivity,
                        Constant.IPADDRESS))))
                    put("field_dispositivo_mac", listOf(mapOf("value" to MyPreferences.getFromPreferences(this@LoginActivity,
                        Constant.MACADD))))
                    put("field_dispositivo_nombre", listOf(mapOf("value" to Build.MANUFACTURER + " " + Build.MODEL)))
                })
            } else {
                operatorList.removeAt(0)
                operatorData()
            }
        }

        loginVM.observeLogin().observe(this) {
            if (it != null) {
                MyPreferences.saveStringInPreference(this, Constant.USERID, it.currentUser.uId)
                MyPreferences.saveStringInPreference(this, Constant.LOGOUTTOKEN, it.logoutToken)
                authentication()
            } else {
                hideLoadingDialog()
               showToast(notValidMessage)
            }
        }

        homeVM.observeAddConnectedDevice().observe(this) {
            MyPreferences.saveStringInPreference(this, Constant.OPERATORID, operatorList[0].operador)
            MyPreferences.saveStringInPreference(this, Constant.OPERATORNAME, operatorList[0].operatorName)
            MyPreferences.saveStringInPreference(this, Constant.MaxDevicesConn, operatorList[0].maxDevices)
            MyPreferences.saveStringInPreference(this, Constant.NODE_ID, it.nid[0].value.toString())

            hideLoadingDialog()
            startDownloadData()
        }

        loginVM.observeAuth().observe(this) { it ->
            if (it != null) {
                Constant.USERTOKEN = it.accessToken
                MyPreferences.saveStringInPreference(this, Constant.ACCESSTOKEN, it.accessToken)
                MyPreferences.saveStringInPreference(this, Constant.REFRESHTOKEN, it.refreshToken)
                loginVM.operator(MyPreferences.getFromPreferences(this, Constant.USERID))
                getDeviceIpAddress()?.let {
                    MyPreferences.saveStringInPreference(this@LoginActivity, Constant.IPADDRESS, it)
                }
                MyPreferences.saveStringInPreference(this@LoginActivity,
                    Constant.MACADD,getMacAddress(this))
            } else {
                hideLoadingDialog()
            }
        }

        loginVM.observeOperator().observe(this) {
            if (it != null) {
                operatorList.clear()
                operatorList.addAll(it)
                operatorData()
            } else {
                hideLoadingDialog()
            }
        }

        DownloadManager.data.observe(this) { data ->
            if (data == "showLoader") {
                showLoadingDialog()
            } else if (data == "hideLoader") {
                hideLoadingDialog()
                if (MyPreferences.getFromPreferences(this, Constant.ACCESSTOKEN).isNotEmpty()) {
                    startActivity(Intent(this, HomeActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                    finish()
                }
            }
        }

        DownloadManager.langData.observe(this) { data ->
            if (data == "showLoader") {
                showLoadingDialog()
            } else if (data == "hideLoader") {
                hideLoadingDialog()

                val dbHelper = DBHelper(this, null)
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT * FROM ${DBHelper.LANGUAGES_TABLE}", null)
                if (cursor != null) {
                    languageList.clear()
                    while (cursor.moveToNext()) {
                        languageList.add(
                            ConfiguracionModel(
                                cursor.getString(cursor.getColumnIndex(DBHelper.languagesColumn_field_key)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.languagesColumn_value)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.languagesColumn_image)),
                            )
                        )
                    }
                }
                cursor?.close()
                db.close()
                languageAdapter.notifyDataSetChanged()

                if (MyPreferences.getFromPreferences(this, Constant.languageCode).isNotEmpty() && languageList.size > 0) {
                    val languageName = languageList.filter {
                        it.key == MyPreferences.getFromPreferences(this, Constant.languageCode)
                    }

                    if (languageName.isNotEmpty()) {
                        val defaultLanguageName = languageName.first()
                        tvLanguageName.text = defaultLanguageName.value
                        Log.e("languageCode","--->"+defaultLanguageName.value)
                        Log.e("languageCode","--->"+defaultLanguageName.key)
                        getLanguageKeywords(defaultLanguageName.key)
//                        DownloadManager.getTranslations(defaultLanguageName.key)
                    } else {
                        tvLanguageName.text = getString(R.string.english)
                        getLanguageKeywords("en-gb")
//                        DownloadManager.getTranslations("en-gb")
                    }
                } else {
                    tvLanguageName.text = getString(R.string.english)
                    getLanguageKeywords("en-gb")
//                    DownloadManager.getTranslations("en-gb")
                }
                DownloadManager.langData.removeObservers(this)
            }
        }

        DownloadManager.langKeywordsData.observe(this) { data ->
            if (data == "showLoader") {
                showLoadingDialog()
            } else if (data == "hideLoader") {
                hideLoadingDialog()
                getAllStrings()
            }
        }

        loginVM.observeLanguageString().observe(this) {
            Log.i("LoginActivity", "observeLanguageString called")
            if (it != null) {
                val db = databaseHandler!!.writableDatabase
                val languageList = ArrayList<LanguageStringModel>()
                db.execSQL("DELETE FROM ${DBHelper.TRANSLATIONS_TABLE}")
                for (item in it) {
                    databaseHandler!!.addTranslation(db, item.key, item.value)
                    languageList.add(LanguageStringModel(item.key, item.value))
                }
                SharedPreferencesHelper.saveArrayList(this@LoginActivity, languageList)
                db.close()
                hideLoadingDialog()
                getAllStrings()
            }
        }
    }

    private fun getAllStrings() {
        val languageName = languageList.filter {
            it.key == MyPreferences.getFromPreferences(this, Constant.languageCode)
        }
        val defaultLanguageName = languageName.first()
        tvLanguageName.text = defaultLanguageName.value
        val getLanguageString = SharedPreferencesHelper.getArrayList(this)
        Log.e("languageList", "--->$getLanguageString")
        for (i in 0 until getLanguageString!!.size) {
            when(getLanguageString[i].key) {
                "login" -> {
                    btnLogin.text = convertHTMLtoString(getLanguageString[i].value)
                    tvLogin.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "leave" -> {
                    btnLeave.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "enter_user_name" -> {
                    etUserName.hint = convertHTMLtoString(getLanguageString[i].value)
                }
                "password" -> {
                    etPassword.hint = convertHTMLtoString(getLanguageString[i].value)
                }
                "password_should_be_at_least_of_6_character" -> {
                    passwordCharacter = convertHTMLtoString(getLanguageString[i].value)
                }
                "enter_r_userName" ->{
                    enterUserName = convertHTMLtoString(getLanguageString[i].value)
                }
                "enter_r_password" ->{
                    enterPassword = convertHTMLtoString(getLanguageString[i].value)
                }
                "internet_not_available" ->{
                    MyPreferences.saveStringInPreference(this, Constant.InternetMessage,getLanguageString[i].value)
                }
                "unrecognized_username_or_password" -> {
                    notValidMessage = convertHTMLtoString(getLanguageString[i].value)
                }
            }
        }
    }

    private fun getMacAddress(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo
        return if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return wifiInfo?.macAddress ?: "MAC address not available"
        } else {
            return wifiInfo?.macAddress ?: "MAC address not available"
        }
    }

    private fun operatorData() {
        if (operatorList.isNotEmpty()) {
            if (operatorList[0].maxDevices.isNotEmpty()) {
                homeVM.userDeviceList(
                    MyPreferences.getFromPreferences(this, Constant.USERID),
                    operatorList[0].operador
                )
            } else {
                emptyMaxDeviceOperator = operatorList[0]
                operatorList.removeAt(0)
                operatorData()
            }
        } else {
            if (emptyMaxDeviceOperator != null) {
                operatorList.add(emptyMaxDeviceOperator!!)
                homeVM.addNewConnectedDevice(java.util.HashMap<String, Any>().apply {
                    put("type", listOf(mapOf("target_id" to "dispositivos_por_usuarios")))
                    put("title", listOf(mapOf("value" to "Dispositivo del usuario")))
                    put("field_id_opera", listOf(mapOf("value" to operatorList[0].operador)))
                    put("field_dispositivo_ip", listOf(mapOf("value" to MyPreferences.getFromPreferences(this@LoginActivity,
                        Constant.IPADDRESS))))
                    put("field_dispositivo_mac", listOf(mapOf("value" to MyPreferences.getFromPreferences(this@LoginActivity,
                        Constant.MACADD))))
                    put("field_dispositivo_nombre", listOf(mapOf("value" to Build.MANUFACTURER + " " + Build.MODEL)))
                })
            } else {
                hideLoadingDialog()
                startDownloadData()
            }
        }
    }

    /**This function used to call Login API**/
    private fun loginUser() {
        loginVM.login(HashMap<String, Any>().apply {
            put("name",etUserName.text.toString().trim())
            put("pass",etPassword.text.toString().trim())
        })
    }

    private fun startDownloadData() {
        DownloadManager.getOperator()
    }

    private fun getDeviceIpAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses

                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()

                    if (!inetAddress.isLoopbackAddress && inetAddress?.hostAddress?.indexOf(':') == -1) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun authentication() {
        loginVM.authentication(etUserName.text.toString().trim(),etPassword.text.toString().trim())
    }

    /** this function used for convert html text to common text **/
    private fun convertHTMLtoString(content : String) : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(content).toString()
        }
    }

    private fun getLanguageKeywords(languageKey: String) {
        showLoadingDialog()
        loginVM.getLanguageStringList(languageKey)
    }

    override fun language(position: Int) {
        MyPreferences.saveStringInPreference(this, Constant.languageCode, languageList[position].key)

        val db = databaseHandler!!.writableDatabase
        db.execSQL("DELETE FROM ${DBHelper.TRANSLATIONS_TABLE}")
        db.close()

        SharedPreferencesHelper.removeArrayList(this)
//        DownloadManager.getTranslations(languageList[position].key)

        getLanguageKeywords(languageList[position].key)
        myPopupWindow?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginVM.observeConfiguration().removeObservers(this)
        loginVM.observeLanguageString().removeObservers(this)
        homeVM.observeDevices().removeObservers(this)
        loginVM.observeLogin().removeObservers(this)
        homeVM.observeAddConnectedDevice().removeObservers(this)
        loginVM.observeAuth().removeObservers(this)
        loginVM.observeOperator().removeObservers(this)
        loginVM.observeLanguageName().removeObservers(this)
        DownloadManager.data.removeObservers(this)
    }
}