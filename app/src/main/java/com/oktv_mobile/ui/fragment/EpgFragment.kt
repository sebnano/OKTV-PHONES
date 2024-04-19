package com.oktv_mobile.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spanned
import android.text.SpannedString
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.egeniq.androidtvprogramguide.ProgramGuideFragment
import com.egeniq.androidtvprogramguide.ProgramGuideManager
import com.egeniq.androidtvprogramguide.entity.ProgramGuideChannel
import com.egeniq.androidtvprogramguide.entity.ProgramGuideSchedule
import com.egeniq.androidtvprogramguide.util.FixedLocalDateTime
import com.oktv_mobile.OkTv
import com.oktv_mobile.R
import com.oktv_mobile.ui.activity.HomeActivity
import com.oktv_mobile.ui.activity.VideoLiveActivity
import com.oktv_mobile.ui.model.homemodel.ChannelDataModel
import com.oktv_mobile.ui.model.homemodel.ChannelProgramModel
import com.oktv_mobile.ui.model.homemodel.ChannelXMLModel
import com.oktv_mobile.ui.viewmodel.HomeVM
import com.oktv_mobile.utils.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.raw_security_pin_dialog.*
import kotlinx.android.synthetic.main.raw_security_pin_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.collections.ArrayList
import kotlin.random.Random

class EpgFragment() : ProgramGuideFragment<EpgFragment.SimpleProgram>() {

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var viewDialog: View
    private var pinRequiredMessage = ""
    private var pinValidMessage = ""

    companion object {
        private val TAG = EpgFragment::class.java.name
    }

    private var NoDataText = ""
    private var index = 0

    data class SimpleChannel(
        override val id: String,
        override val name: Spanned?,
        override val imageUrl: String?,
        var programList: ArrayList<ChannelProgramModel>
    ) : ProgramGuideChannel

    // You can put your own data in the program class
    data class SimpleProgram(
        val id: String,
        val description: String,
        val metadata: String
    )

    private lateinit var homeVM : HomeVM
    private var simpleChannel = ArrayList<SimpleChannel>()
    private var simpleProgram = ArrayList<SimpleProgram>()
    var arrChannelXMLData2 = ArrayList<ChannelXMLModel>()
    var allChannelList = ArrayList<ChannelDataModel>()


    var databaseHandler: DBHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LoadingDialog(requireContext())
        databaseHandler = DBHelper(requireContext(), null)

        viewDialog = LayoutInflater.from(context).inflate(R.layout.raw_security_pin_dialog, null)

        homeVM = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[HomeVM::class.java]

        getString()
    }

    override fun onResume() {
        super.onResume()
        getChannels()
    }

    override fun onSchedulesUpdated() {
        super.onSchedulesUpdated()
        Log.e("EPGFRAGMENT", "onSchedulesUpdated")
    }

    override fun onChannelLogoClicked(channel: ProgramGuideChannel) {
        for (i in 0 until arrChannelXMLData2.size) {
            if (channel.name.toString() == arrChannelXMLData2[i].displayName) {
                for (k in 0 until allChannelList.size) {
                    if (arrChannelXMLData2[i].id == allChannelList[k].EPG_ID_Channel) {
                        if (allChannelList[k].protectedChannel == "1") {
                            if (MyPreferences.getFromPreferences(requireContext(),Constant.PinSet).isNotEmpty()) {
                                val dialog = Dialog(requireContext())
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setContentView(viewDialog)
                                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.show()
                                dialog.ivEye.setOnClickListener { (context as HomeActivity).showHidePass(dialog.etPinPassword , dialog.ivEye) }
                                if (dialog.etPinPassword.text!!.length >= 5) {
                                    dialog.etPinPassword.isEnabled = false
                                }
                                dialog.clToolTip.hide()
                                dialog.tvSetPin.setOnClickListener {
                                    if (MyPreferences.getFromPreferences(requireContext(), Constant.PinSet) != dialog.etPinPassword.text.toString()) {
                                        (context as HomeActivity).showToast(pinValidMessage)
                                    } else {
                                        MyPreferences.saveStringInPreference(requireContext(),Constant.Title,allChannelList[k].title)
                                        (context as HomeActivity).startActivity(
                                            Intent(requireContext(), VideoLiveActivity::class.java)
                                                .putExtra("videoUrl", allChannelList[k].fieldUrl)
                                                .putExtra("chanelId", allChannelList[k].nid)
                                                .putExtra("title", allChannelList[k].title)
                                                .putExtra("posicion", allChannelList[k].posicion)
                                                .putExtra("EpgId", allChannelList[k].EPG_ID_Channel)
                                                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                        )
                                        dialog.dismiss()
                                        (context as HomeActivity).hideLoadingDialog()
                                    }
                                }
                            } else {
                                (context as HomeActivity).showToast(pinRequiredMessage)
                                (context as HomeActivity).hideLoadingDialog()
                            }
                        } else {
                            MyPreferences.saveStringInPreference(requireContext(),Constant.Title,allChannelList[k].title)
                            (context as HomeActivity).startActivity(
                                Intent(requireContext(), VideoLiveActivity::class.java)
                                    .putExtra("videoUrl", allChannelList[k].fieldUrl)
                                    .putExtra("chanelId", allChannelList[k].nid)
                                    .putExtra("title", allChannelList[k].title)
                                    .putExtra("posicion", allChannelList[k].posicion)
                                    .putExtra("EpgId", allChannelList[k].EPG_ID_Channel)
                                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            )
                            (context as HomeActivity).hideLoadingDialog()
                        }
                        break
                    }
                }
            }
        }

    }

    override fun onScheduleClickedInternal(schedule: ProgramGuideSchedule<SimpleProgram>) {
        super.onScheduleClickedInternal(schedule)
        Log.e("EPGFRAGMENT", "onScheduleClickedInternal")

        if (schedule != null) {
            (context as HomeActivity).showLoadingDialog()
            for (i in 0 until arrChannelXMLData2.size) {
                for (j in 0 until arrChannelXMLData2[i].programs.size) {
                    if (schedule!!.displayTitle == arrChannelXMLData2[i].programs[j].title) {
                        for (k in 0 until allChannelList.size) {
                            if (arrChannelXMLData2[i].id == allChannelList[k].EPG_ID_Channel) {
                                if (allChannelList[k].protectedChannel == "1") {
                                    if (MyPreferences.getFromPreferences(requireContext(),Constant.PinSet).isNotEmpty()) {
                                        val dialog = Dialog(requireContext())
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                        dialog.setContentView(viewDialog)
                                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                        dialog.show()
                                        dialog.ivEye.setOnClickListener { (context as HomeActivity).showHidePass(dialog.etPinPassword , dialog.ivEye) }
                                        if (dialog.etPinPassword.text!!.length >= 5) {
                                            dialog.etPinPassword.isEnabled = false
                                        }
                                        dialog.clToolTip.hide()
                                        dialog.tvSetPin.setOnClickListener {
                                            if (MyPreferences.getFromPreferences(requireContext(), Constant.PinSet) != dialog.etPinPassword.text.toString()) {
                                                (context as HomeActivity).showToast(pinValidMessage)
                                            } else {
                                                MyPreferences.saveStringInPreference(requireContext(),Constant.Title,allChannelList[k].title)
                                                (context as HomeActivity).startActivity(
                                                    Intent(requireContext(), VideoLiveActivity::class.java)
                                                        .putExtra("videoUrl", allChannelList[k].fieldUrl)
                                                        .putExtra("chanelId", allChannelList[k].nid)
                                                        .putExtra("title", allChannelList[k].title)
                                                        .putExtra("posicion", allChannelList[k].posicion)
                                                        .putExtra("EpgId", allChannelList[k].EPG_ID_Channel)
                                                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                                )
                                                dialog.dismiss()
                                                (context as HomeActivity).hideLoadingDialog()
                                            }
                                        }
                                    } else {
                                        (context as HomeActivity).showToast(pinRequiredMessage)
                                        (context as HomeActivity).hideLoadingDialog()
                                    }
                                } else {
                                    MyPreferences.saveStringInPreference(requireContext(),Constant.Title,allChannelList[k].title)
                                    (context as HomeActivity).startActivity(
                                        Intent(requireContext(), VideoLiveActivity::class.java)
                                            .putExtra("videoUrl", allChannelList[k].fieldUrl)
                                            .putExtra("chanelId", allChannelList[k].nid)
                                            .putExtra("title", allChannelList[k].title)
                                            .putExtra("posicion", allChannelList[k].posicion)
                                            .putExtra("EpgId", allChannelList[k].EPG_ID_Channel)
                                            .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    )
                                    (context as HomeActivity).hideLoadingDialog()
                                }
                            }
                            break
                        }
                    }
                }
            }
        }
    }

    override fun onScheduleClicked(programGuideSchedule: ProgramGuideSchedule<SimpleProgram>) {
    }

    override fun onScheduleSelected(programGuideSchedule: ProgramGuideSchedule<SimpleProgram>?) {
    }

    override fun isTopMenuVisible(): Boolean {
        return false
    }

    @SuppressLint("CheckResult")
    override fun requestingProgramGuideFor(localDate: LocalDate, type: Int) {
        Log.e("Type","---->"+type)

        generateSimpleChannels(type)

        Single.fromCallable {
            val channels = simpleChannel
            val channelMap = mutableMapOf<String, List<ProgramGuideSchedule<SimpleProgram>>>()

            channels.forEach { channel ->
                val scheduleList = mutableListOf<ProgramGuideSchedule<SimpleProgram>>()
                for (i in 0 until channel.programList.size) {
                    val schedule = createSchedule(channel.programList[i].title, channel.programList[i].start.time, channel.programList[i].stop.time)
                    scheduleList.add(schedule)
                }
                Log.e("RequestProgram:-", "scheduleList:- " + scheduleList.size)
                channelMap[channel.id] = scheduleList
            }

            return@fromCallable Pair(channels, channelMap)
        }.delay(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setData(it.first, it.second, currentDate)
                if (it.first.isEmpty() || it.second.isEmpty()) {
                    setState(State.Error(NoDataText))
                } else {
                    setState(State.Content)
                }
            }, {
//                Log.e(TAG, "Unable to load example data!", it)
            })
    }

    private fun generateSimpleChannels(type: Int) {
        Log.e("Type", "2222222---->" + type)
        simpleChannel.clear()
        if (type == 0) {
            currentDate = FixedLocalDateTime.now().toLocalDate()
            for (i in 0 until arrChannelXMLData2.size) {
                simpleChannel.add(
                    SimpleChannel(
                        id = arrChannelXMLData2[i].id,
                        name = SpannedString(arrChannelXMLData2[i].displayName),
                        imageUrl = arrChannelXMLData2[i].logo,
                        programList = arrChannelXMLData2[i].programs
                    )
                )
            }
//            ProgramGuideManager.startTimeMillis = System.currentTimeMillis()
        } else {
            val now = System.currentTimeMillis()
            val instant = Instant.ofEpochMilli(now)
            val zoneId = ZoneOffset.UTC // ZoneId.ofOffset("", ZoneOffset.ofTotalSeconds(TimeZone.getDefault().getOffset(now) / 1_000))
            val localDate = LocalDateTime.ofInstant(instant, zoneId).toLocalDate()

            val nextStartDay = localDate.plusDays(1).atStartOfDay()
            currentDate = localDate.plusDays(1)
            val timestamp = Timestamp.valueOf(nextStartDay.toLocalDate().toString() + " " + nextStartDay.toLocalTime().toString() + ":00")

            var isTimeUpdated = false
            for (i in 0 until arrChannelXMLData2.size) {
//                if (!isTimeUpdated) {
//                    for (j in 0 until arrChannelXMLData2[i].programs.size) {
//                        val showTime = Timestamp(arrChannelXMLData2[i].programs[j].start.time)
//                        Log.e("RequestProgram:- Program size", "Start Timestamp ${showTime}")
//                        if (showTime >= timestamp) {
////                            ProgramGuideManager.startTimeMillis =
////                                arrChannelXMLData2[i].programs[j].start.time
//                            isTimeUpdated = true
//                        }
//                    }
//                }
                simpleChannel.add(
                    SimpleChannel(
                        id = arrChannelXMLData2[i].id,
                        name = SpannedString(arrChannelXMLData2[i].displayName),
                        imageUrl = arrChannelXMLData2[i].logo,
                        programList = arrChannelXMLData2[i].programs
                    )
                )
            }

            for (i in 0 until arrChannelXMLData2.size) {
                val programs = ArrayList<ChannelProgramModel>()
                for (j in 0 until arrChannelXMLData2[i].programs.size) {
                    val showTime = Timestamp(arrChannelXMLData2[i].programs[j].start.time)
                    Log.e("RequestProgram:- Program size", "Start Timestamp $showTime")
                    if (showTime >= timestamp) {
                        programs.add(arrChannelXMLData2[i].programs[j])
                        if (i == 0 && j == 0) {
//                            ProgramGuideManager.startTimeMillis = arrChannelXMLData2[i].programs[j].start.time
                        }
                    }
                }
                Log.e("RequestProgram:- Program size ", "" + programs.size)
                simpleChannel.add(
                    SimpleChannel(
                        id = arrChannelXMLData2[i].id,
                        name = SpannedString(arrChannelXMLData2[i].displayName),
                        imageUrl = arrChannelXMLData2[i].logo,
                        programList = programs
                    )
                )
            }

            Log.e("RequestProgram:-", "s" + simpleChannel.size)
        }
    }

    private fun createSchedule(
        scheduleName: String,
        startTime: Long,
        endTime: Long
    ): ProgramGuideSchedule<SimpleProgram> {
        val id = Random.nextLong(100_000L)
        val metadata = "start set"/*DateTimeFormatter.ofPattern("'Starts at' HH:mm").format(startTime)*/
        return ProgramGuideSchedule.createScheduleWithProgram(
            id,
            startTime,
            endTime,
            true,
            scheduleName,
            SimpleProgram(
                id.toString(),
                "This is an example description for the programme. This description is taken from the SimpleProgram class, so by using a different class, " +
                        "you could easily modify the demo to use your own class",
                metadata
            )
        )
    }

    override fun requestRefresh() {
        // You can refresh other data here as well.
        requestingProgramGuideFor(currentDate, 0)
    }

    override fun requestRefreshEPG() {
        downloadXml()
    }

    private fun getString() {
        val getLanguageString = SharedPreferencesHelper.getArrayList(requireContext())

        for (i in 0 until getLanguageString!!.size) {
            when(getLanguageString[i].key) {
                "No_channels_loaded" -> {
                    NoDataText = getLanguageString[i].value
                }
                "ok" -> {
                    viewDialog.tvSetPin.text = getLanguageString[i].value
                }
                "enter_pin" -> {
                    viewDialog.tvPinTitle.text = getLanguageString[i].value
                    viewDialog.etPinPassword.hint = getLanguageString[i].value
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

    @SuppressLint("Range")
    private fun getChannels() {
        val cursor = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.CHANNELS_TABLE}", null)
        val allChannelId = ArrayList<String>()
        if (cursor != null) {
            allChannelList.clear()
            while (cursor.moveToNext()) {
                allChannelId.add(cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_EPG_ID_Channel)))
                allChannelList.add(
                    ChannelDataModel(
                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_title)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_logo)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_url)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_nid)),
//                        "",
                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_EPG_ID_Channel)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_posicion)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_protected_channel)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_channel_category)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_videotype)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_abreviacion)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_genres)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_ad_play_back_on_start_of_v)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_type_of_censorship)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_id)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_default)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_bitrate)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_tags)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_type)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_epg_code)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_status)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_field_ad_play_back_url)),
//                        cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_tipo_transmision)),
                    )
                )
            }
        }
        cursor?.close()

        val cursor1 = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.EPG_TABLE}", null)
        if (cursor1 != null) {
            arrChannelXMLData2.clear()
            while (cursor1.moveToNext()) {
                val epgId = cursor1.getString(cursor1.getColumnIndex(DBHelper.epgColumn_id))
                val displayName = cursor1.getString(cursor1.getColumnIndex(DBHelper.epgColumn_displayName))
                if (allChannelId.contains(epgId)) {
                    val cursor2 = (context as HomeActivity).db?.rawQuery("SELECT * FROM ${DBHelper.EPG_PROGRAMS_TABLE} WHERE ${DBHelper.epgProgramsColumn_epgId} = '${epgId}'", null)
                    if (cursor2 != null) {
                        val programs = ArrayList<ChannelProgramModel>()
                        while (cursor2.moveToNext()) {
                            val startDate = Date(cursor2.getLong(cursor2.getColumnIndex(DBHelper.epgProgramsColumn_start)))
                            val stopDate = Date(cursor2.getLong(cursor2.getColumnIndex(DBHelper.epgProgramsColumn_stop)))
                            programs.add(
                                ChannelProgramModel(
                                    cursor2.getString(cursor2.getColumnIndex(DBHelper.epgProgramsColumn_title)),
                                    cursor2.getString(cursor2.getColumnIndex(DBHelper.epgProgramsColumn_descr)),
                                    startDate,
                                    stopDate
                                )
                            )
                        }
                        if (programs.isNotEmpty()) {
                            var fieldURL = ""
                            val channel = allChannelList.filter { it.EPG_ID_Channel == epgId } as ArrayList<ChannelDataModel>
                            if (channel.isNotEmpty()) {
                                fieldURL = Constant.BaseUrl + channel.first().fieldLogo
                            }
                            arrChannelXMLData2.add(ChannelXMLModel(epgId, displayName, fieldURL, programs))
                        }
                    }
                    cursor2?.close()
                }
            }
        }
        cursor1?.close()
        Log.e("RequestProgram:-", "" + arrChannelXMLData2.size)
        setState(State.Loading)
//                generateSimpleChannels(0)
        requestingProgramGuideFor(currentDate, 0)
    }

    private fun downloadXml() {
        (context as HomeActivity).showLoadingDialog()
        Log.e("ParsedXMLData", "DownloadTry")
        try {
            val file = "channel.xml"
            var fileOutputStream: FileOutputStream

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    fileOutputStream = (requireContext()).openFileOutput(file, Context.MODE_PRIVATE)
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

                    readFromXML()

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
            }

        } catch (e: Exception) {
            Log.e("FILEPATH", e.message.toString())
            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun readFromXML() {
        Log.e("ParsedXMLData", "ReadXml")

        val file = (requireContext()).getFileStreamPath("channel.xml")
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
                    databaseHandler!!.addEPGData(
                        (context as HomeActivity).db,
                        element.attributes.getNamedItem("id").nodeValue.toString(),
                        getNodeValue("display-name", element), "", "", "", ""
                    )
                    Log.e("EPGFragment", "program main node inserted")
                    arrChannelXMLData.add(ChannelXMLModel(element.attributes.getNamedItem("id").nodeValue.toString(), getNodeValue("display-name", element), "",
                        java.util.ArrayList<ChannelProgramModel>()
                    ))
                }

                val inputDateFormat = SimpleDateFormat("yyyyMMddHHmmss Z")
                val nListPrograms = doc.getElementsByTagName("programme")

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MINUTE, -30)
                val oldTime = calendar.time

                for (i in 0 until nListPrograms.length) {
                    val element = nListPrograms.item(i) as Element
                    val parsedDate = inputDateFormat.parse(element.attributes.getNamedItem("start").nodeValue.toString())
                    val parsedDate2 = inputDateFormat.parse(element.attributes.getNamedItem("stop").nodeValue.toString())
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
                                databaseHandler!!.addEPGProgramData(
                                    (context as HomeActivity).db,
                                    data.id,
                                    program.title,
                                    program.desc,
                                    program.start.time.toDouble(),
                                    program.stop.time.toDouble()
                                )
                                Log.e("EPGFragment", "program inserted")
                            }
                            break
                        }
                    }
                }

                requestingProgramGuideFor(currentDate, 0)
                MyPreferences.saveLongPreference(requireContext(), Constant.LAST_EPG_DOWNLOAD_DATE, Date().time)
                Log.e("ParsedXMLData", arrChannelXMLData.size.toString())
                (context as HomeActivity).hideLoadingDialog()

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
    }

    private fun getNodeValue(tag: String, element: Element): String {
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
}
