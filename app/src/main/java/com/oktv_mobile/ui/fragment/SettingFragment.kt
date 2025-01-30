package com.oktv_mobile.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.offline.Download
import com.oktv_mobile.BuildConfig
import com.oktv_mobile.R
import com.oktv_mobile.ui.viewmodel.HomeVM
import com.oktv_mobile.ui.viewmodel.LoginVM
import com.oktv_mobile.ui.`interface`.LanguageCallBack
import com.oktv_mobile.ui.`interface`.UpdateDevice
import com.oktv_mobile.ui.activity.HomeActivity
import com.oktv_mobile.ui.adapter.DeviceListAdapter
import com.oktv_mobile.ui.adapter.LanguageAdapter
import com.oktv_mobile.ui.adapter.OperatorsAdapter
import com.oktv_mobile.ui.model.homemodel.HomeBannerModel
import com.oktv_mobile.ui.model.homemodel.UserDeviceModel
import com.oktv_mobile.ui.model.loginmodel.ConfiguracionModel
import com.oktv_mobile.ui.model.loginmodel.UserOperatorModel
import com.oktv_mobile.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.tvLanguageName
import kotlinx.android.synthetic.main.raw_security_pin_dialog.*
import kotlinx.android.synthetic.main.raw_security_pin_dialog.ivEye
import kotlinx.android.synthetic.main.raw_security_pin_dialog.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n", "NotifyDataSetChanged", "Range", "SetTextI18n")
class SettingFragment : Fragment(), LanguageCallBack, UpdateDevice {

    /** This Variables For PopupView **/
    private var myPopupWindow: PopupWindow? = null
    private var popupView: View? = null

    /** This Variable Used for Adapter **/
    private lateinit var operatorsAdapter : OperatorsAdapter
    private lateinit var devicesAdapter : DeviceListAdapter
    private lateinit var languageAdapter : LanguageAdapter

    /** This Variable Used for Store Api Data **/
    private var operatorList = ArrayList<UserOperatorModel>()
    private var deviceList = ArrayList<UserDeviceModel>()
    private var languageList = ArrayList<ConfiguracionModel>()

    private var noSpaceDevice = ""

    private var maxDevices = ""
    private var operator_Id = ""
    private var operator_name = ""
    private var enterPinMessage = ""
    private var pinLengthMessage = ""
    private var isCallBack = false
    private lateinit var viewDialog: View
    private lateinit var loadingDialog: LoadingDialog

    /** This Variable For ViewModel **/
    private lateinit var homeVM : HomeVM
    private lateinit var loginVM : LoginVM

    private lateinit var rvName : RecyclerView
    private var  setPinText = ""
    private var setPinTitle = ""
    private var setBtnText = ""
    private var enterPinHint = ""
    private var toolTipMessage = ""
    private var updatePin = ""
    private var selectedOperatorTitle = ""

    private var isDeviceAdded = false
    private var databaseHandler: DBHelper? = null
    var db: SQLiteDatabase? = null

    var isFromRefreshedDevices = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHandler = DBHelper(requireContext(), null)
        db = databaseHandler?.writableDatabase
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        setOnClickViews()
        observeData()
        getString()
    }

    override fun onResume() {
        super.onResume()
        isDeviceAdded = false
    }

    /** This Function For Initialize **/
    private fun initData() {
        tvSelectOperator.hide()
        viewDialog = LayoutInflater.from(requireContext()).inflate(R.layout.raw_security_pin_dialog, null)

        homeVM = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[HomeVM::class.java]
        loginVM = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginVM::class.java]

        popupView = layoutInflater.inflate(R.layout.raw_popup_view, null)
        myPopupWindow = PopupWindow(popupView, 320, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
        rvName = popupView!!.findViewById(R.id.rvLanguageName)

        languageAdapter = LanguageAdapter(languageList,this)
        rvName.layoutManager = LinearLayoutManager(requireContext())
        rvName.adapter = languageAdapter

        devicesAdapter = DeviceListAdapter(deviceList)
        rvDevice.layoutManager = GridLayoutManager(requireContext(),2)
        rvDevice.adapter = devicesAdapter

        operator_Id = MyPreferences.getFromPreferences(requireContext(), Constant.OPERATORID)
        operator_name = MyPreferences.getFromPreferences(requireContext(), Constant.OPERATORNAME)

        operatorsAdapter = OperatorsAdapter(operatorList,this, operator_Id, selectedOperatorTitle)
        operatorsAdapter.operaterId(operator_Id, operator_name)

        rvOperator.layoutManager = GridLayoutManager(requireContext(),2)
        rvDevice.layoutManager = GridLayoutManager(requireContext(),2)
        rvOperator.adapter = operatorsAdapter
        operatorsAdapter.notifyDataSetChanged()

        isFromRefreshedDevices = true
        getOperators()
        getRefreshDevices()
        getDevices()
        getLanguageName()

        loadingDialog = LoadingDialog(requireContext())
        if (MyPreferences.getFromPreferences(requireContext(), Constant.PinSet).isNotEmpty()) {
            btnSetPin.text = updatePin
        } else {
            btnSetPin.text = setPinText
        }
        DownloadManager.initialize(requireContext(), viewLifecycleOwner, this)
        DownloadManager.getTranslations(MyPreferences.getFromPreferences(requireContext(), Constant.languageCode))
    }

    /** This Function For Click Listener **/
    private fun setOnClickViews() {
        clLanguageSelect.setOnClickListener {
            popupView?.show()
            myPopupWindow?.showAsDropDown(it,0,0)
        }

        btnUpdate.setOnClickListener {
            refreshAllData()
        }

        btnSetPin.setOnClickListener {
            if (MyPreferences.getFromPreferences(requireContext(),Constant.PinSet).isEmpty()) {
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(LayoutInflater.from(requireContext()).inflate(R.layout.raw_security_pin_dialog, null))
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                dialog.clToolTip.show()
                dialog.tvPinTitle.text = setPinTitle
                dialog.tvSetPin.text = setBtnText
                dialog.ivEye.setOnClickListener { (context as HomeActivity).showHidePass(dialog.etPinPassword , dialog.ivEye) }
                if (dialog.etPinPassword.text!!.length >= 5) {
                    dialog.etPinPassword.isEnabled = false
                }
                dialog.tvSetPin.setOnClickListener {
                    if (dialog.etPinPassword.text!!.isEmpty()) {
                        (context as HomeActivity).showToast(enterPinMessage)
                    } else if (dialog.etPinPassword.text!!.length < 4) {
                        (context as HomeActivity).showToast(pinLengthMessage)
                    } else {
                        MyPreferences.saveStringInPreference(requireContext(),Constant.PinSet,dialog.etPinPassword.text.toString())
                        dialog.dismiss()
                        btnSetPin.text = updatePin
                    }
                }
            } else {
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(LayoutInflater.from(requireContext()).inflate(R.layout.raw_security_pin_dialog, null))
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                dialog.etPinPassword.hint = enterPinHint
                dialog.tvTip.text = toolTipMessage
                dialog.ivEye.setOnClickListener { (context as HomeActivity).showHidePass(dialog.etPinPassword , dialog.ivEye) }
                if (dialog.etPinPassword.text!!.length >= 5) {
                    dialog.etPinPassword.isEnabled = false
                }
                dialog.clToolTip.hide()
                dialog.tvPinTitle.text = "Enter Your Old Pin"
                dialog.tvSetPin.text = "Done"
                dialog.tvSetPin.setOnClickListener {
                    if (dialog.etPinPassword.text!!.isEmpty()) {
                        (context as HomeActivity).showToast(enterPinMessage)
                    } else if (dialog.etPinPassword.text!!.length < 4) {
                        (context as HomeActivity).showToast(pinLengthMessage)
                    } else if (dialog.etPinPassword.text!!.toString() != MyPreferences.getFromPreferences(requireContext(),Constant.PinSet) ) {
                        (context as HomeActivity).showToast("your pin is invalid")
                    } else {
                        dialog.etPinPassword.text!!.clear()
                        btnSetPin.text = updatePin
                        dialog.clToolTip.show()
                        dialog.tvPinTitle.text = setPinTitle
                        dialog.tvSetPin.text = setBtnText
                        dialog.ivEye.setOnClickListener { (context as HomeActivity).showHidePass(dialog.etPinPassword , dialog.ivEye) }
                        if (dialog.etPinPassword.text!!.length >= 5) {
                            dialog.etPinPassword.isEnabled = false
                        }
                        dialog.tvSetPin.setOnClickListener {
                            if (dialog.etPinPassword.text!!.isEmpty()) {
                                (context as HomeActivity).showToast(enterPinMessage)
                            } else if (dialog.etPinPassword.text!!.length < 4) {
                                (context as HomeActivity).showToast(pinLengthMessage)
                            } else {
                                MyPreferences.saveStringInPreference(requireContext(),Constant.PinSet,dialog.etPinPassword.text.toString())
                                dialog.dismiss()
                                btnSetPin.text = updatePin
                            }
                        }
                    }
                }
            }
        }
    }

    private fun refreshAllData() {
        val db = DBHelper(requireContext(), null)
        db.truncateAllTables()
        showLoadingDialog()
        DownloadManager.getOperator()
    }

    private fun getLanguageName() {
        val cursor = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.LANGUAGES_TABLE}", null)
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
        languageAdapter.notifyDataSetChanged()

        if (MyPreferences.getFromPreferences(requireContext(), Constant.languageCode).isNotEmpty() && languageList.size > 0) {
            val languageName = languageList.filter {
                it.key == MyPreferences.getFromPreferences(requireContext(), Constant.languageCode)
            }
            if (languageName.isNotEmpty()) {
                val defaultLanguageName = languageName.first()
                tvLanguageName.text = defaultLanguageName.value
            }
        } else {
            tvLanguageName.text = getString(R.string.english)
        }
    }

    private fun getOperators() {
        val cursor = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.OPERATOR_TABLE}", null)
        if (cursor != null) {
            operatorList.clear()
            while (cursor.moveToNext()) {
                operatorList.add(
                    UserOperatorModel(
                        cursor.getString(cursor.getColumnIndex(DBHelper.operatorColumn_operador)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.operatorColumn_nombre_operador)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.operatorColumn_max_devices)),
                    )
                )
            }
        }
        cursor?.close()

        operatorsAdapter.operaterId(operator_Id, operator_name)
        operatorsAdapter.notifyDataSetChanged()
    }

    private fun getDevices() {
        val cursor = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.DEVICES_TABLE}", null)
        if (cursor != null) {
            deviceList.clear()
            devicesAdapter.notifyDataSetChanged()
            while (cursor.moveToNext()) {
                Log.i("Device-Node", cursor.getColumnIndex(DBHelper.deviceColumn_node_id).toString())
                deviceList.add(
                    UserDeviceModel(
                        cursor.getString(cursor.getColumnIndex(DBHelper.deviceColumn_field_dispositivo_nombre)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.deviceColumn_field_dispositivo_ip)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.deviceColumn_node_id)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.deviceColumn_field_dispositivo_mac)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.deviceColumn_field_id_opera)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.deviceColumn_pais_dispositivos))
                    )
                )
            }
        }
        cursor?.close()
        if (MyPreferences.getFromPreferences(requireContext(), Constant.MaxDevicesConn).isNotEmpty()) {
            tvDeviceCount.text = "${deviceList.size}/${MyPreferences.getFromPreferences(requireContext(),
                Constant.MaxDevicesConn)}"
        } else {
            tvDeviceCount.text = "${deviceList.size}/${MyPreferences.getFromPreferences(requireContext(),
                Constant.MaxDevice)}"
        }
        devicesAdapter.notifyDataSetChanged()
    }

    private fun observeData() {
        DownloadManager.data.observe(viewLifecycleOwner) { it ->
            if (it == "showLoader") {
                showLoadingDialog()
            } else if (it == "hideLoader") {
                hideLoadingDialog()
                getOperators()
                getDevices()
            }
        }

        homeVM.observeDevices1().observe(viewLifecycleOwner) {
            if (it != null) {
                if (isFromRefreshedDevices) {
                    isFromRefreshedDevices = false
                    if (it != null) {
                        db?.execSQL("DELETE FROM ${DBHelper.DEVICES_TABLE}")
                        for (item in it) {
                            databaseHandler!!.addDevices(db, item.operatorId, item.pais_dispositivos, item.nodeId, item.deviceMac, item.deviceTitle, item.deviceIp)
                        }
                        getDevices()
                    }
                } else {
                    if (maxDevices.isEmpty()) {
                        deleteDevice()
                    } else if (it.size < maxDevices.toInt()) {
                        deleteDevice()
                    } else {
                        maxDevices = ""
                        operator_Id = ""
                        operator_name = ""
                        (context as HomeActivity).showToast(noSpaceDevice)
                        hideLoadingDialog()
                    }
                }
            }
        }

        homeVM.observeAddConnectedDevice1().observe(viewLifecycleOwner) {
            MyPreferences.saveStringInPreference(requireContext(), Constant.OPERATORID, operator_Id)
            MyPreferences.saveStringInPreference(requireContext(), Constant.OPERATORNAME, operator_name)
            MyPreferences.saveStringInPreference(requireContext(), Constant.MaxDevicesConn, maxDevices)
            MyPreferences.saveStringInPreference(requireContext(), Constant.NODE_ID, it.nid[0].value.toString())

            hideLoadingDialog()
            if (isDeviceAdded) {
                isDeviceAdded = false
                getRefreshDevices()
                refreshAllData()
            }
        }

        homeVM.deleteDeviceStatus.observe(viewLifecycleOwner) {
            isDeviceAdded = true
            homeVM.addNewConnectedDevice1(HashMap<String, Any>().apply {
                put("type", listOf(mapOf("target_id" to "dispositivos_por_usuarios")))
                put("title", listOf(mapOf("value" to "Dispositivo del usuario")))
                put("field_id_opera", listOf(mapOf("value" to operator_Id)))
                put("field_dispositivo_ip", listOf(mapOf("value" to MyPreferences.getFromPreferences(requireContext(),
                    Constant.IPADDRESS))))
                put("field_dispositivo_mac", listOf(mapOf("value" to MyPreferences.getFromPreferences(requireContext(),
                    Constant.MACADD))))
                put("field_dispositivo_nombre", listOf(mapOf("value" to Build.MANUFACTURER + " " + Build.MODEL)))
            })
        }

        DownloadManager.langKeywordsData.observe(viewLifecycleOwner) { data ->
            if (data == "showLoader") {
                showLoadingDialog()
            } else if (data == "hideLoader") {
                hideLoadingDialog()
                getString()
                if (isCallBack) {
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                }
            }
        }
    }

    private fun showLoadingDialog() {
        if (this::loadingDialog.isInitialized && !loadingDialog.isShowing()) {
            loadingDialog.show()
        }
    }

    private fun hideLoadingDialog() {
        if (this::loadingDialog.isInitialized && loadingDialog.isShowing()) {
            loadingDialog.dismiss()
        }
    }

    private fun getString() {
        val getLanguageString = SharedPreferencesHelper.getArrayList(requireContext())
        Log.e("languageList", "--->$getLanguageString")

        for (i in 0 until getLanguageString!!.size) {
            when(getLanguageString[i].key) {
                "settings" -> {
                    tvSettingTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "operators" -> {
                    tvOperatorTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "select_the_operators" -> {
                    tvOperatorTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "devices" -> {
                    tvDeviceTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "language" -> {
                    tvLanguageTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "version_of_application" -> {
                    tvVersionTitle.text = convertHTMLtoString(getLanguageString[i].value) + "Version" + "\u0020\u0020" + BuildConfig.VERSION_NAME
                }
                "update" -> {
                    btnUpdate.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "not_space_in_device" -> {
                    noSpaceDevice = convertHTMLtoString(getLanguageString[i].value)
                }
                "set_pin_for_see_a_protected_channel" -> {
                    viewDialog.tvPinTitle.text = convertHTMLtoString(getLanguageString[i].value)
                    setPinTitle = convertHTMLtoString(getLanguageString[i].value)
                }
                "enter_pin" -> {
                    viewDialog.etPinPassword.hint = convertHTMLtoString(getLanguageString[i].value)
                    enterPinHint = convertHTMLtoString(getLanguageString[i].value)
                }
                "pin_requirement_message" -> {
                    viewDialog.tvTip.text = convertHTMLtoString(getLanguageString[i].value)
                    toolTipMessage = convertHTMLtoString(getLanguageString[i].value)
                }
                "set" -> {
                    viewDialog.tvSetPin.text = convertHTMLtoString(getLanguageString[i].value)
                    setBtnText = convertHTMLtoString(getLanguageString[i].value)
                }
                "please_enter_pin" -> {
                    enterPinMessage = convertHTMLtoString(getLanguageString[i].value)
                }
                "pin_should_be_at_least_of_4_character" -> {
                    pinLengthMessage = convertHTMLtoString(getLanguageString[i].value)
                }
                "set_pin" -> {
                    btnSetPin.text = convertHTMLtoString(getLanguageString[i].value)
                    setPinText = convertHTMLtoString(getLanguageString[i].value)
                }
                "update_pin" -> {
                    updatePin = convertHTMLtoString(getLanguageString[i].value)
                }
                "selected" -> {
                    selectedOperatorTitle = getLanguageString[i].value
                }
            }
        }

        operatorsAdapter.changeSelected(selectedOperatorTitle)
    }

    override fun onDestroyView() {
        homeVM.observeAddConnectedDevice1().removeObservers(viewLifecycleOwner)
        homeVM.deleteDeviceStatus.removeObservers(viewLifecycleOwner)
        homeVM.observeDevices1().removeObservers(viewLifecycleOwner)
        loginVM.observeLanguageName().removeObservers(viewLifecycleOwner)
        loginVM.observeLanguageString().removeObservers(viewLifecycleOwner)
        DownloadManager.data.removeObservers(viewLifecycleOwner)
        super.onDestroyView()

    }

    /** this function used for convert html text to common text **/
    private fun convertHTMLtoString(content : String) : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(content).toString()
        }
    }

    override fun language(position: Int) {
        tvLanguageName.text = languageList[position].value
        MyPreferences.saveStringInPreference(requireContext(), Constant.languageCode,languageList[position].key)
        (context as HomeActivity).db?.execSQL("DELETE FROM ${DBHelper.TRANSLATIONS_TABLE}")
        DownloadManager.getTranslations(languageList[position].key)
        myPopupWindow?.dismiss()
        isCallBack = true
    }

    private fun deleteDevice() {
        val nodeId = MyPreferences.getFromPreferences(requireContext(), Constant.NODE_ID)
        if (nodeId != "0") {
            homeVM.deleteUserDevice1(nodeId)
        }
    }

    override fun deviceData(operatorId: String, maxDevice: String, operatorName: String) {
        if (operatorId != MyPreferences.getFromPreferences(requireContext(),Constant.OPERATORID)) {
            operator_Id = operatorId
            operator_name = operatorName
            maxDevices = maxDevice
            homeVM.userDeviceList1(MyPreferences.getFromPreferences(requireContext(), Constant.USERID), operatorId)
            showLoadingDialog()
        }
    }

    private fun getRefreshDevices() {
        homeVM.userDeviceList1(
            MyPreferences.getFromPreferences(requireContext(), Constant.USERID),
            MyPreferences.getFromPreferences(requireContext(), Constant.OPERATORID))
        isFromRefreshedDevices = true
    }
}
