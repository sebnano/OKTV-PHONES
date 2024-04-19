package com.oktv_mobile.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.oktv_mobile.R
import com.oktv_mobile.ui.`interface`.ChannelItemCallback
import com.oktv_mobile.ui.activity.HomeActivity
import com.oktv_mobile.ui.activity.VideoLiveActivity
import com.oktv_mobile.ui.adapter.ChannelCategorySecondAdapter
import com.oktv_mobile.ui.model.homemodel.CategoryChannelModel
import com.oktv_mobile.ui.model.homemodel.ChannelDataModel
import com.oktv_mobile.ui.viewmodel.LoginVM
import com.oktv_mobile.utils.*
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_favourite.rvAllChannel
import kotlinx.android.synthetic.main.fragment_favourite.tvTitle
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.raw_security_pin_dialog.*
import kotlinx.android.synthetic.main.raw_security_pin_dialog.view.*

@SuppressLint("NotifyDataSetChanged", "Range")
class FavouriteFragment : Fragment(), ChannelItemCallback {

    /** This Variable Used for Adapter **/
    private lateinit var channelCategoryAdapter : ChannelCategorySecondAdapter

    /** This Variable Used for Store Api Data **/
    private var channelCategoryListMain = ArrayList<CategoryChannelModel>()

    /** This Variable For ViewModel **/
    private lateinit var loginVM: LoginVM

    private var pinRequiredMessage = ""
    private var pinValidMessage = ""
    private var pinEnterMessage = ""
    private var pinOk = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        getString()
    }

    /** This Function For Initialize **/
    private fun initData() {
        loginVM = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[LoginVM::class.java]

        channelCategoryAdapter = ChannelCategorySecondAdapter(channelCategoryListMain, this, -1)
        rvAllChannel.layoutManager = LinearLayoutManager(requireContext())
        rvAllChannel.adapter = channelCategoryAdapter
        getFavChannels()
    }

    private fun getString() {
        val getLanguageString = SharedPreferencesHelper.getArrayList(requireContext())
        for (i in 0 until getLanguageString!!.size) {
            when(getLanguageString[i].key) {
                "favourites" -> {
                    tvTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "ok" -> {
                    pinOk = getLanguageString[i].value
                }
                "enter_pin" -> {
                    pinEnterMessage = getLanguageString[i].value
                }
                "please_enter_a_valid_pin" -> {
                    pinValidMessage = getLanguageString[i].value
                }
                "please_set_a_secure_pin_to_show_live_telecast_of_this_channel" -> {
                    pinRequiredMessage = getLanguageString[i].value
                }
            }
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

    override fun onResume() {
        super.onResume()
        getFavChannels()
    }

    private fun getFavChannels() {
        val channelCategoryData = ArrayList<CategoryChannelModel>()
        val cursor = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.CATEGORIES_TABLE}", null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val categoryName =
                    cursor.getString(cursor.getColumnIndex(DBHelper.categoriesColumn_category))

                val allChannelList = ArrayList<ChannelDataModel>()
                val cursor1 = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.CHANNELS_TABLE} where ${DBHelper.channelColumn_field_channel_category} = '$categoryName' AND ${DBHelper.channelColumn_isLiked} = 1", null)
                if (cursor1 != null) {
                    while (cursor1.moveToNext()) {
                        allChannelList.add(
                            ChannelDataModel(
                                cursor1.getString(cursor1.getColumnIndex(DBHelper.channelColumn_title)),
                                cursor1.getString(cursor1.getColumnIndex(DBHelper.channelColumn_field_logo)),
                                cursor1.getString(cursor1.getColumnIndex(DBHelper.channelColumn_field_url)),
                                cursor1.getString(cursor1.getColumnIndex(DBHelper.channelColumn_nid)),
                                cursor1.getString(cursor1.getColumnIndex(DBHelper.channelColumn_EPG_ID_Channel)),
                                cursor1.getString(cursor1.getColumnIndex(DBHelper.channelColumn_posicion)),
                                cursor1.getString(cursor1.getColumnIndex(DBHelper.channelColumn_protected_channel)),
                                cursor1.getString(cursor1.getColumnIndex(DBHelper.channelColumn_field_channel_category)),
                            )
                        )
                    }
                }
                cursor1?.close()

                if (allChannelList.isNotEmpty()) {
                    channelCategoryData.add(
                        CategoryChannelModel(
                            categoryName,
                            cursor.getString(cursor.getColumnIndex(DBHelper.categoriesColumn_id_category)),
                            allChannelList
                        )
                    )
                }
            }
        }
        channelCategoryListMain.clear()
        channelCategoryListMain.addAll(channelCategoryData)
        cursor?.close()
        channelCategoryAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(parentPosition: Int, position: Int) {
        val data = channelCategoryListMain[parentPosition].categoryList[position]
        SharedPreferencesHelper.saveVideoList(requireContext(), channelCategoryListMain[parentPosition].categoryList)
        if (data.protectedChannel == "1") {
            if (MyPreferences.getFromPreferences(requireContext(), Constant.PinSet).isNotEmpty()) {
                val viewDialog = LayoutInflater.from(context).inflate(R.layout.raw_security_pin_dialog, null)
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(viewDialog)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                dialog.ivEye.setOnClickListener { (context as HomeActivity).showHidePass(dialog.etPinPassword , dialog.ivEye) }
                if (dialog.etPinPassword.text!!.length >= 5) {
                    dialog.etPinPassword.isEnabled = false
                }
                viewDialog.tvSetPin.text = pinOk
                viewDialog.tvPinTitle.text = pinEnterMessage
                viewDialog.etPinPassword.hint = pinEnterMessage

                dialog.clToolTip.hide()
                dialog.tvSetPin.setOnClickListener {
                    if (MyPreferences.getFromPreferences(requireContext(), Constant.PinSet) != dialog.etPinPassword.text.toString()) {
                        (context as HomeActivity).showToast(pinValidMessage)
                    } else {
                        if (data.fieldUrl.isNotEmpty()) {
                            MyPreferences.saveStringInPreference(requireContext(), Constant.Title, data.title)
                            requireContext().startActivity(
                                Intent(context, VideoLiveActivity::class.java)
                                .putExtra("videoUrl", data.fieldUrl)
                                .putExtra("chanelId", data.nid)
                                .putExtra("title", data.title)
                                .putExtra("posicion", data.posicion)
                                .putExtra("EpgId", data.EPG_ID_Channel)
                            )
                        }
                        dialog.dismiss()
                    }
                }
            } else {
                (context as HomeActivity).showToast(pinRequiredMessage)
            }
        } else {
            if (data.fieldUrl.isNotEmpty()) {
                MyPreferences.saveStringInPreference(requireContext(),Constant.Title, data.title)
                requireContext().startActivity(
                    Intent(context, VideoLiveActivity::class.java)
                    .putExtra("videoUrl", data.fieldUrl)
                    .putExtra("chanelId", data.nid)
                    .putExtra("title", data.title)
                    .putExtra("posicion", data.posicion)
                    .putExtra("EpgId", data.EPG_ID_Channel)
                )
            }
        }
    }
}