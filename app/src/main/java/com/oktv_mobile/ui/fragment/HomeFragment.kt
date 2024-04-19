package com.oktv_mobile.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.Html
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.oktv_mobile.OkTv
import com.oktv_mobile.R
import com.oktv_mobile.ui.`interface`.ChannelItemCallback
import com.oktv_mobile.ui.activity.HomeActivity
import com.oktv_mobile.ui.activity.VideoLiveActivity
import com.oktv_mobile.ui.adapter.ChannelCategorySecondAdapter
import com.oktv_mobile.ui.model.homemodel.CategoryChannelModel
import com.oktv_mobile.ui.model.homemodel.ChannelDataModel
import com.oktv_mobile.ui.model.homemodel.ChannelProgramModel
import com.oktv_mobile.ui.model.homemodel.ChannelXMLModel
import com.oktv_mobile.utils.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.raw_security_pin_dialog.*
import kotlinx.android.synthetic.main.raw_security_pin_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.collections.ArrayList


@SuppressLint("NotifyDataSetChanged", "Range")
class HomeFragment : Fragment(), ChannelItemCallback {

    /** This Variable Used for Adapter **/
    private lateinit var channelCategoryAdapter : ChannelCategorySecondAdapter
    private var channelCategoryListMain = ArrayList<CategoryChannelModel>()

    private var pinRequiredMessage = ""
    private var pinValidMessage = ""
    private var pinEnterMessage = ""
    private var pinOk = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()

        initData()
        getString()

        Constant.EPG_URL = "https://portal.ok-television.com/sites/default/files/android-tv-cdn/perfectfile.xml"
        lifecycleScope.launch {
            try {
                (context as HomeActivity).showLoadingDialog()
                val data = downloadXml()
                if (data) {
                    lifecycleScope.launch {
                        try {
                            (context as HomeActivity).showLoadingDialog()
                            val data = readFromXML()
                            if (data) {
                                (context as HomeActivity).hideLoadingDialog()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** This Function For Initialize **/
    private fun initData() {
        getCategories()
    }

    private fun getString() {
        val getLanguageString = SharedPreferencesHelper.getArrayList(requireContext())
        for (i in 0 until getLanguageString!!.size) {
            when(getLanguageString[i].key) {
                "channels" -> {
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

    private fun getCategories() {
        lifecycleScope.launch {
            try {
                (context as HomeActivity).showLoadingDialog()
                val data = fetchDataFromDatabase()
                updateUI(data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchDataFromDatabase(): ArrayList<CategoryChannelModel> {
        return withContext(Dispatchers.IO) {

            val channelCategoryData = ArrayList<CategoryChannelModel>()
            val cursor = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.CATEGORIES_TABLE}", null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val categoryName =
                        cursor.getString(cursor.getColumnIndex(DBHelper.categoriesColumn_category))

                    val allChannelList = ArrayList<ChannelDataModel>()
                    val cursor1 = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.CHANNELS_TABLE} where ${DBHelper.channelColumn_field_channel_category} = '$categoryName'", null)
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

                    channelCategoryData.add(
                        CategoryChannelModel(
                            categoryName,
                            cursor.getString(cursor.getColumnIndex(DBHelper.categoriesColumn_id_category)),
                            allChannelList
                        )
                    )
                }
            }
            cursor?.close()

            channelCategoryData
        }
    }

    private fun updateUI(data: ArrayList<CategoryChannelModel>) {
        channelCategoryListMain.clear()
        channelCategoryListMain.addAll(data)
        channelCategoryAdapter = ChannelCategorySecondAdapter(channelCategoryListMain, this, -1)
        rvChannel.layoutManager = LinearLayoutManager(requireContext())
        rvChannel.adapter = channelCategoryAdapter
        (context as HomeActivity).hideLoadingDialog()
    }

    /** this function used for convert html text to common text **/
    private fun convertHTMLtoString(content : String) : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(content).toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        homeVM.observeAllChannels().removeObservers(viewLifecycleOwner)
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
                            requireContext().startActivity(Intent(context, VideoLiveActivity::class.java)
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
                requireContext().startActivity(Intent(context, VideoLiveActivity::class.java)
                    .putExtra("videoUrl", data.fieldUrl)
                    .putExtra("chanelId", data.nid)
                    .putExtra("title", data.title)
                    .putExtra("posicion", data.posicion)
                    .putExtra("EpgId", data.EPG_ID_Channel)
                )
            }
        }
    }


    private suspend fun downloadXml() : Boolean {
        return withContext(Dispatchers.IO) {
            var status = false

            val lastEPGDate =
                MyPreferences.getLongFromPreferences(requireContext(), Constant.LAST_EPG_DOWNLOAD_DATE)
            var isEPGDownload = true
            if (lastEPGDate != 0L) {
                isEPGDownload = !DateUtils.isToday(lastEPGDate)
            }
            if (isEPGDownload) {
                Log.e("ParsedXMLData", "DownloadTry")
                try {
                    val file = "channel.xml"
                    var fileOutputStream: FileOutputStream

//                    GlobalScope.launch(Dispatchers.IO) {
                    try {
                        fileOutputStream = requireContext().openFileOutput(file, AppCompatActivity.MODE_PRIVATE)
                        val url = URL(Constant.EPG_URL)
                        val connection = url.openConnection()
                        val inputStream = connection.getInputStream()

                        val inputBuffer = ByteArray(1024 * 1024)
                        var bytesRead: Int

                        while (inputStream.read(inputBuffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(inputBuffer, 0, bytesRead)
                        }

                        inputStream.close()
                        fileOutputStream.close()

                        status = true

//                            readFromXML()

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        Log.e("FILEPATH", e.message.toString())
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        Log.e("FILEPATH", e.message.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.e("FILEPATH", e.message.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("FILEPATH", e.message.toString())
                    }
//                    }

                } catch (e: Exception) {
                    Log.e("FILEPATH", e.message.toString())
                    e.printStackTrace()
                }
            }

            status
        }
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun readFromXML() : Boolean {
        return withContext(Dispatchers.IO) {
            var status = false
            Log.e("ParsedXMLData", "ReadXml")

            val file = requireContext().getFileStreamPath("channel.xml")
            if (file.exists()) {
                try {
                    val builderFactory = DocumentBuilderFactory.newInstance()
                    val docBuilder = builderFactory.newDocumentBuilder()
                    val doc = docBuilder.parse(file)

                    val arrChannelXMLData = java.util.ArrayList<ChannelXMLModel>()

                    (context as HomeActivity).db?.execSQL("DELETE FROM ${DBHelper.EPG_TABLE}")
                    (context as HomeActivity).db?.execSQL("DELETE FROM ${DBHelper.EPG_PROGRAMS_TABLE}")

                    val nList = doc.getElementsByTagName("channel")
                    for (i in 0 until nList.length) {
                        val element = nList.item(i) as Element
                        (context as HomeActivity).databaseHandler!!.addEPGData(
                            (context as HomeActivity).db,
                            element.attributes.getNamedItem("id").nodeValue.toString(),
                            getNodeValue("display-name", element), "", "", "", ""
                        )
                        arrChannelXMLData.add(
                            ChannelXMLModel(
                                element.attributes.getNamedItem("id").nodeValue.toString(),
                                getNodeValue("display-name", element),
                                "",
                                java.util.ArrayList<ChannelProgramModel>()
                            )
                        )
                    }

                    val inputDateFormat = SimpleDateFormat("yyyyMMddHHmmss Z")
                    val nListPrograms = doc.getElementsByTagName("programme")

                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.MINUTE, -30)
                    val oldTime = calendar.time

                    for (i in 0 until nListPrograms.length) {
                        val element = nListPrograms.item(i) as Element
                        val parsedDate =
                            inputDateFormat.parse(element.attributes.getNamedItem("start").nodeValue.toString())
                        val parsedDate2 =
                            inputDateFormat.parse(element.attributes.getNamedItem("stop").nodeValue.toString())
                        val program = ChannelProgramModel(
                            getNodeValue("title", element),
                            getNodeValue("desc", element),
                            parsedDate!!,
                            parsedDate2!!
                            //                        getNodeArrayValue("category", element)
                        )

                        for (i in 0 until arrChannelXMLData.size) {
                            val data = arrChannelXMLData[i]
                            if (data.id == element.attributes.getNamedItem("channel").nodeValue.toString()) {
                                if (parsedDate.time >= oldTime.time) {
                                    (context as HomeActivity).databaseHandler!!.addEPGProgramData(
                                        (context as HomeActivity).db,
                                        data.id,
                                        program.title,
                                        program.desc,
                                        program.start.time.toDouble(),
                                        program.stop.time.toDouble()
                                    )
                                }
                                break
                            }
                        }
                    }

                    MyPreferences.saveLongPreference(
                        requireContext(),
                        Constant.LAST_EPG_DOWNLOAD_DATE,
                        Date().time
                    )
                    Log.e("ParsedXMLData", arrChannelXMLData.size.toString())
                    status = true

                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ParserConfigurationException) {
                    e.printStackTrace()
                } catch (e: SAXException) {
                    e.printStackTrace()
                }
            } else {
                println("File not found.")
            }
            status
        }
    }

    protected fun getNodeValue(tag: String, element: Element): String {
        val nodeList = element.getElementsByTagName(tag)
        val node = nodeList.item(0)
        if (node != null) {
            if (node.hasChildNodes()) {
                val child = node.firstChild
                if (child != null) {
                    if (child.getNodeType() === Node.TEXT_NODE) {
                        return child.getNodeValue()
                    }
                }
            }
        }
        return ""
    }

    protected fun getNodeArrayValue(tag: String, element: Element): java.util.ArrayList<String> {
        val nodeList = element.getElementsByTagName(tag)
        val arrCategory = java.util.ArrayList<String>()
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            if (node != null) {
                if (node.hasChildNodes()) {
                    val child = node.firstChild
                    if (child.nodeType === Node.TEXT_NODE) {
                        arrCategory.add(child.nodeValue)
                    }
                }
            }
        }

        return arrCategory
    }
}