package com.oktv_mobile.ui.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.oktv_mobile.R
import com.oktv_mobile.retrofit.MyResponse
import com.oktv_mobile.ui.viewmodel.HomeVM
import com.oktv_mobile.ui.viewmodel.LoginVM
import com.oktv_mobile.utils.Constant
import com.oktv_mobile.utils.MyPreferences
import com.oktv_mobile.utils.SharedPreferencesHelper
import com.oktv_mobile.ui.activity.HomeActivity
import com.oktv_mobile.ui.activity.LoginActivity
import com.oktv_mobile.utils.DBHelper
import kotlinx.android.synthetic.main.fragment_leave.*

class LeaveFragment : Fragment() {

    private lateinit var homeVM: HomeVM
    private lateinit var loginVM: LoginVM

    private var logoutMessage = ""

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
        return inflater.inflate(R.layout.fragment_leave, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        setOnClickView()
        observerData()
        getString()
    }

    private fun initData() {
        homeVM = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeVM::class.java]
        loginVM = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginVM::class.java]
    }

    /** This Function For Click Listener **/
    private fun setOnClickView() {
        btnOk.setOnClickListener {
            homeVM.deleteUserDevice(MyPreferences.getFromPreferences(requireContext(), Constant.NODE_ID))
        }

        btnCancel.setOnClickListener {
            startActivity(Intent(requireContext(), HomeActivity::class.java))
        }
    }

    private fun getString() {
        val getLanguageString = SharedPreferencesHelper.getArrayList(requireContext())

        for (i in 0 until getLanguageString!!.size) {
            when(getLanguageString[i].key) {
                "leave" -> {
                    tvTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "press_ok_button_to_exist" -> {
                    tvDialogeTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "ok" -> {
                    btnOk.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "successfully_logout" -> {
                    logoutMessage = convertHTMLtoString(getLanguageString[i].value)
                }
                "cancel" -> {
                    btnCancel.text = convertHTMLtoString(getLanguageString[i].value)
                }
            }
        }
    }

    private fun observerData() {

        homeVM.observeLogOut().observe(viewLifecycleOwner) {
            val languageCode = MyPreferences.getFromPreferences(requireContext(), Constant.languageCode)
            MyPreferences.clearData(requireContext())
            MyPreferences.saveStringInPreference(requireContext(), Constant.languageCode, languageCode)
            val db = DBHelper(requireContext(), null)
            db.truncateAllTables()
            startActivity(Intent(requireContext(), LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            (context as HomeActivity).showToast(logoutMessage)
        }

        homeVM.observeDeleteDevice().observe(viewLifecycleOwner) {
            homeVM.logOut(MyPreferences.getFromPreferences(requireContext(), Constant.LOGOUTTOKEN))
        }
    }

    /** this function used for convert html text to common text **/
    private fun convertHTMLtoString(content : String) : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(content).toString()
        }
    }
}