package com.app.oktv.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.oktv.R
import com.app.oktv.ui.adapter.DeviceListAdapter
import com.app.oktv.ui.adapter.OperatorsAdapter
import com.app.oktv.ui.model.DeviceDataModel
import com.app.oktv.ui.model.OperatorModel
import com.app.oktv.utils.hide
import com.app.oktv.utils.show
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {

    /** This Variables For PopupView **/
    private var myPopupWindow: PopupWindow? = null
    private var popupView: View? = null

    /** This Variable Used for Adapter **/
    private lateinit var operatorsAdapter : OperatorsAdapter
    private lateinit var devicesAdapter : DeviceListAdapter

    /** This Variable Used for Store Api Data **/
    private var operatorList = ArrayList<OperatorModel>()
    private var deviceList = ArrayList<DeviceDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        changeText()
    }

    /** This Function For Initialize **/
    private fun initData() {
        popupView = layoutInflater.inflate(R.layout.raw_language_list, null)
        myPopupWindow = PopupWindow(popupView, 500, RelativeLayout.LayoutParams.WRAP_CONTENT, true)

        operatorList.clear()
        operatorList.add(OperatorModel("Demo"))
        operatorList.add(OperatorModel("STARTV-Mon"))
        operatorList.add(OperatorModel("Television"))
        operatorList.add(OperatorModel("MBA"))

        deviceList.clear()
        deviceList.add(DeviceDataModel("LG/SmartTV","109.147.47.94","e7227e32-777a-4b1e-a19a-259963159a"))
        deviceList.add(DeviceDataModel("Android","190.103.43.11","1a50fac6e8b197a9"))
        deviceList.add(DeviceDataModel("Android","109.147.47.94","c0982ac3021a1f3f"))

        operatorsAdapter = OperatorsAdapter(operatorList)
        rvOperator.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        rvOperator.adapter = operatorsAdapter

        devicesAdapter = DeviceListAdapter(deviceList)
        rvDevice.layoutManager = GridLayoutManager(requireContext(),2)
        rvDevice.adapter = devicesAdapter

    }

    /** This Function For Click Listener **/
    private fun setOnClickViews() {
        clLanguageSelect.setOnClickListener {
            popupView?.show()
            myPopupWindow?.showAsDropDown(it,0,0)

            val tvEnglish = popupView?.findViewById(R.id.tvEnglish) as TextView
            val tvSpanish = popupView?.findViewById(R.id.tvSpanish) as TextView

            tvEnglish.text = getString(R.string.english)
            tvSpanish.text = getString(R.string.spanish)

            tvEnglish.setOnClickListener {
                tvLanguageName.text = getString(R.string.english)
                myPopupWindow?.dismiss()
            }

            tvSpanish.setOnClickListener {
                tvLanguageName.text = getString(R.string.spanish)
                myPopupWindow?.dismiss()
            }
        }
    }

    /** This Function For Change Text **/
    private fun changeText() {
        tvSettingTitle.text = getString(R.string.settings)
        tvOperatorTitle.text = getString(R.string.operators)
        tvSelectOperator.text = getString(R.string.select_the_operators)
        tvDeviceTitle.text = getString(R.string.devices)
        tvLanguageTitle.text = getString(R.string.language)
        tvLanguageName.text = getString(R.string.english)
        tvVersionTitle.text = getString(R.string.version_of_application)
        btnUpdate.text = getString(R.string.update)
    }
}