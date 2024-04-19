package com.oktv_mobile.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.oktv_mobile.R
import com.oktv_mobile.custom.classes.CustomAppCompatActivity
import com.oktv_mobile.ui.adapter.UpComingShowAdapter
import com.oktv_mobile.ui.adapter.UpComingShowDetailAdapter
import com.oktv_mobile.ui.model.homemodel.ChannelProgramModel
import com.oktv_mobile.ui.viewmodel.HomeVM
import com.oktv_mobile.ui.viewmodel.LoginVM
import com.oktv_mobile.utils.*
import kotlinx.android.synthetic.main.activity_video_live.*
import kotlinx.android.synthetic.main.raw_security_pin_dialog.*
import java.util.*
import kotlin.concurrent.schedule

class VideoLiveActivity : CustomAppCompatActivity() {

    /** This Variable Used for exoplayer **/
    private lateinit var exoPlayer: SimpleExoPlayer

    /** This Variable Used for Adapter **/
    private lateinit var upComingShowAdapter : UpComingShowAdapter
    private lateinit var upComingShowDetailAdapter: UpComingShowDetailAdapter

    /** This Variable Used for Store Api Data **/
    private val hideControlsDelayMillis: Long = 3000 // 3 seconds
    private val hideControlsRunnable = Runnable { clHeader.hide()
    clFooter.hide() }
    private val controlsHandler = Handler()
    private val showControlsRunnable = Runnable { clHeader.show()
    clFooter.hide() }

    private lateinit var homeVM: HomeVM
    private lateinit var loginVM: LoginVM
    private var videoUrl = ""
    private var channelId = ""
    private var posicion = ""
    private var epgId = ""
    private var title = ""
    private var favNodeId = ""

    private var isLoader = false
    private var isLandscape = false

    private var pinValidMessage = ""
    private var pinRequiredMessage = ""
    private var pinText = ""
    private var pinTitle = ""
    private var pinPasswordHint = ""
    private var savePosition = 0
    private var addToFavourite = ""
    private var removeFromFavourite = ""


    var databaseHandler: DBHelper? = null
    var db: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_live)

        initData()
        setonClickView()
        observeData()
        getString()
    }

    /** This Function For Initialize **/
    override fun initData() {
        databaseHandler = DBHelper(this, null)
        db = databaseHandler?.readableDatabase

        homeVM = ViewModelProvider(this,ViewModelProvider.NewInstanceFactory())[HomeVM::class.java]
        loginVM = ViewModelProvider(this,ViewModelProvider.NewInstanceFactory())[LoginVM::class.java]

        tvChannelName.text = MyPreferences.getFromPreferences(this, Constant.OPERATORNAME)

        videoUrl = intent.getStringExtra("videoUrl").toString()
        Log.e("VIDEOURL", videoUrl.toString())
        title = intent.getStringExtra("title").toString()
        channelId = intent.getStringExtra("chanelId").toString()
        epgId = intent.getStringExtra("EpgId").toString()
        posicion = intent.getStringExtra("posicion").toString()
        getFavChannels()
        tvVideoTitle.text = title
        exoPlayer = SimpleExoPlayer.Builder(this).build()

        pvVideoLive.player = exoPlayer
        pvVideoLive.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        pvVideoLive.keepScreenOn = true
        pvVideoLive.useController = false

//        val video = if (videoUrl.isNotEmpty()) {
//            Uri.parse(videoUrl)
//        } else {
//            Uri.parse("https://live-par-2-cdn-alt.livepush.io/live/bigbuckbunnyclip/index.m3u8")
//        }

        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        controlsHandler.postDelayed(hideControlsRunnable, hideControlsDelayMillis)

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height: Int = displayMetrics.heightPixels

            pvVideoLive.minimumHeight = 650
            pvVideoLive.layoutParams.height = height

            clFooter.show()
            nsBottom.hide()
            ivLogo.hide()

//            ivBack.layoutParams.height = 100
//            ivVolumeUp.layoutParams.height = 100
//            ivVolumeDown.layoutParams.height = 100
//            ivFillStar.layoutParams.height = 100
//            ivFillStar.layoutParams.width = 100

            getVideoList(MyPreferences.getFromPreferences(this,Constant.Title))
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            isLandscape = true
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            view.hide()
            rvShowList.hide()
            ivLogo.show()
            nsBottom.show()
            clFooter.hide()
            pvVideoLive.minimumHeight = 650
            pvVideoLive.layoutParams.height = 650


//            ivBack.layoutParams.height = 60
//            ivVolumeUp.layoutParams.height = 60
//            ivVolumeDown.layoutParams.height = 60
//            ivFillStar.layoutParams.height = 60
//            ivFillStar.layoutParams.width = 60

            getVideoList(MyPreferences.getFromPreferences(this,Constant.Title))
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        upcomingProgramList()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height: Int = displayMetrics.heightPixels

            pvVideoLive.minimumHeight = 650
            pvVideoLive.layoutParams.height = height

            clFooter.show()
            nsBottom.hide()
            ivLogo.hide()

//            ivBack.layoutParams.height = 100
//            ivVolumeUp.layoutParams.height = 100
//            ivVolumeDown.layoutParams.height = 100
//            ivFillStar.layoutParams.height = 100
//            ivFillStar.layoutParams.width = 100

            getVideoList(MyPreferences.getFromPreferences(this,Constant.Title))
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            isLandscape = true

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            view.hide()
            rvShowList.hide()
            ivLogo.show()
            nsBottom.show()
            clFooter.hide()
            pvVideoLive.minimumHeight = 650
            pvVideoLive.layoutParams.height = 650

//            ivBack.layoutParams.height = 60
//            ivVolumeUp.layoutParams.height = 60
//            ivVolumeDown.layoutParams.height = 60
//            ivFillStar.layoutParams.height = 60
//            ivFillStar.layoutParams.width = 60

            getVideoList(MyPreferences.getFromPreferences(this,Constant.Title))
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    private fun getVideoList(title : String) {
        Log.e("videoTitle", "--->$title")
        val getVideo = SharedPreferencesHelper.getVideoList(this)
        if (getVideo != null) {
            for (i in 0 until getVideo.size) {
                if (title == getVideo[i].title) {
                    videoUrl = getVideo[i].fieldUrl
                    posicion = getVideo[i].posicion
                    epgId = getVideo[i].EPG_ID_Channel
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()
                    savePosition = i
                    upcomingProgramList()

                }
            }
        }
    }

    @SuppressLint("Range")
    private fun upcomingProgramList() {
        tvVideoTitle.text = MyPreferences.getFromPreferences(this,Constant.Title)
        if (epgId.isNotEmpty()) {
            val cursor1 = db?.rawQuery("SELECT * FROM ${DBHelper.EPG_PROGRAMS_TABLE} WHERE ${DBHelper.epgProgramsColumn_epgId} = '${epgId}'", null)
            if (cursor1 != null) {
                val programs = ArrayList<ChannelProgramModel>()
                while (cursor1.moveToNext()) {
                    val startDate = Date(cursor1.getLong(cursor1.getColumnIndex(DBHelper.epgProgramsColumn_start)))
                    val stopDate = Date(cursor1.getLong(cursor1.getColumnIndex(DBHelper.epgProgramsColumn_stop)))
                    programs.add(
                        ChannelProgramModel(
                            cursor1.getString(cursor1.getColumnIndex(DBHelper.epgProgramsColumn_title)),
                            cursor1.getString(cursor1.getColumnIndex(DBHelper.epgProgramsColumn_descr)),
                            startDate,
                            stopDate
                        )
                    )
                }
                if (programs.isNotEmpty()) {
                    rvUpComingList.show()
                    rvShowList.show()
                    upComingShowDetailAdapter = UpComingShowDetailAdapter(programs)
                    rvUpComingList.layoutManager = LinearLayoutManager(this)
                    rvUpComingList.adapter = upComingShowDetailAdapter

                    upComingShowAdapter = UpComingShowAdapter(programs, posicion)
                    rvShowList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                    rvShowList.adapter = upComingShowAdapter

                    Log.e("programList","-->"+ programs)

                    Timer("SettingUp", false).schedule(5000) {  hideLoadingDialog() }
                }
            }
            cursor1?.close()
        } else {
            rvUpComingList.hide()
            rvShowList.hide()
//            showToast("not found upcoming show")
            Timer("SettingUp", false).schedule(5000) {  hideLoadingDialog() }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    /** This Function For Click Listener **/
    private fun setonClickView() {
        ivBack.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

        ivVolumeUp.setOnClickListener {
            ivVolumeUp.hide()
            ivVolumeDown.show()
            setTwoStateVolume(true)
        }

        ivVolumeDown.setOnClickListener {
            ivVolumeUp.show()
            ivVolumeDown.hide()
            setTwoStateVolume(false)
        }

        pvVideoLive.setOnTouchListener(object : OnSwipeTouchListener(this@VideoLiveActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                var newPositionAdd = savePosition
                newPositionAdd++
                swipeCall(newPositionAdd)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()

                var newPosition = savePosition
                newPosition--
                swipeCall(newPosition)
            }

            override fun onClick() {
                // Show the custom controls
                if (clHeader.visibility == View.VISIBLE) {
                    clHeader.hide()
                    clFooter.hide()
                } else {
                    if (isLandscape) {
                        clHeader.show()
                        clFooter.show()
                    } else {
                        clHeader.show()
                    }
                }
//                    // Cancel any pending hide requests
                controlsHandler.removeCallbacks(showControlsRunnable)
                controlsHandler.removeCallbacks(hideControlsRunnable)

                // Schedule hiding of the custom controls after delay
                controlsHandler.postDelayed(hideControlsRunnable, hideControlsDelayMillis)
            }
        })

        clStar.setOnClickListener {
            isLoader = true
            if (favNodeId == "") {
                homeVM.markFavouriteChanel(HashMap<String,Any>().apply {
                    put("type", listOf(mapOf("target_id" to "canales_favoritos_por_usuario")))
                    put("title", listOf(mapOf("value" to title)))
                    put("field_operador", listOf(mapOf("value" to MyPreferences.getFromPreferences(this@VideoLiveActivity,
                        Constant.OPERATORID))))
                    put("field_canal", listOf(mapOf("target_id" to channelId)))
                })
            } else {
                homeVM.deleteUserDevice(favNodeId)
            }
        }

        pvVideoLive.setOnClickListener {
            if (clHeader.visibility == View.VISIBLE) {
                clHeader.hide()
                clFooter.hide()
            } else {
                clHeader.show()
                clHeader.show()
                controlsHandler.postDelayed(hideControlsRunnable, hideControlsDelayMillis)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun swipeCall(newPosition : Int) {
        val getVideo = SharedPreferencesHelper.getVideoList(this@VideoLiveActivity)
        if (getVideo != null) {
            for (i in 0 until getVideo.size) {
            if (newPosition == i) {
                savePosition = newPosition
                Log.e("addPosition","--->"+newPosition)
                if (getVideo[i].protectedChannel == "1") {
                    exoPlayer.pause()
                    Log.e("setPinNot","--->"+ MyPreferences.getFromPreferences(this,Constant.PinSet))
                    if (MyPreferences.getFromPreferences(this,Constant.PinSet).isNotEmpty()) {
                        val dialog = Dialog(this@VideoLiveActivity)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(LayoutInflater.from(this@VideoLiveActivity).inflate(R.layout.raw_security_pin_dialog, null))
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.show()
                        dialog.setCancelable(false)
                        dialog.tvSetPin.text = pinText
                        dialog.tvPinTitle.text = pinTitle
                        dialog.etPinPassword.hint = pinPasswordHint
                        dialog.ivEye.setOnClickListener { showHidePass(dialog.etPinPassword, dialog.ivEye) }
                        if (dialog.etPinPassword.text!!.length > 5) {
                            dialog.etPinPassword.isEnabled = false
                        }
                        dialog.clToolTip.hide()
                        dialog.tvSetPin.setOnClickListener {
                            if (MyPreferences.getFromPreferences(
                                    this@VideoLiveActivity,
                                    Constant.PinSet
                                ) != dialog.etPinPassword.text.toString()
                            ) {
                                showToast(pinValidMessage)
                            } else {
                                if (getVideo[i].fieldUrl.isNotEmpty()) {
                                    videoUrl = getVideo[i].fieldUrl
                                    posicion = getVideo[i].posicion
                                    epgId = getVideo[i].EPG_ID_Channel
                                    title = getVideo[i].title
                                    MyPreferences.saveStringInPreference(
                                        this@VideoLiveActivity,
                                        Constant.Title,
                                        getVideo[i].title
                                    )
                                    val mediaItem = MediaItem.fromUri(videoUrl)
                                    exoPlayer.setMediaItem(mediaItem)
                                    exoPlayer.prepare()
                                    exoPlayer.play()
                                    Log.e(
                                        "epgData",
                                        "--->" + getVideo[i].EPG_ID_Channel
                                    )
                                    upcomingProgramList()
                                }
                                dialog.dismiss()
                            }
                        }
                        break
                    } else {
                        hideLoadingDialog()
                        showToast(pinRequiredMessage)
                                    startActivity(Intent(this@VideoLiveActivity, HomeActivity::class.java).putExtra("changeFragment","sendSetting"))
                                    finish()
                        break
                    }

                } else {
                    if (getVideo[i].fieldUrl.isNotEmpty()) {
                        videoUrl = getVideo[i].fieldUrl
                        posicion = getVideo[i].posicion
                        epgId = getVideo[i].EPG_ID_Channel
                        title = getVideo[i].title
                        MyPreferences.saveStringInPreference(
                            this@VideoLiveActivity,
                            Constant.Title,
                            getVideo[i].title
                        )
                        val mediaItem = MediaItem.fromUri(videoUrl)
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.prepare()
                        exoPlayer.play()
                        Log.e("epgData", "--->" + getVideo[i].EPG_ID_Channel)
                        upcomingProgramList()
                        break
                    }
                }
            }
        }
        }
    }

    /** This Function For Set Volume Statues **/
    private fun setTwoStateVolume(mute : Boolean) {
        val audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
         if (mute) {
             exoPlayer.volume =  0F
             audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND)
         } else  {
             exoPlayer.volume = 1F
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)
        }
    }


    @SuppressLint("Range")
    private fun getFavChannels() {
        val cursor = db?.rawQuery("SELECT * FROM ${DBHelper.CHANNELS_TABLE} WHERE ${DBHelper.channelColumn_isLiked} = 1 " +
                "AND ${DBHelper.channelColumn_posicion} = '${posicion}'", null)
        var counter = 0
        if (cursor != null) {
            while (cursor.moveToNext()) {
                counter = 1
                favNodeId = cursor.getString(cursor.getColumnIndex(DBHelper.channelColumn_favNodeId))
                break
            }
        }
        cursor?.close()

        if (counter == 0) {
            ivFillStar.setBackgroundResource(R.drawable.ic_blank_star_blue)
        } else {
            ivFillStar.setBackgroundResource(R.drawable.ic_fill_star_blue)
        }
    }

    private fun observeData() {
        homeVM.observeLoading().observe(this) {
            if (!isLoader) {
                changeLoadingStatus(it)
            }
        }

        homeVM.observeMarkFavouriteChanel().observe(this) {
            if (it != null) {
                if (it.nodeId[0].value.toString().isNotEmpty()) {
                    showToast(addToFavourite)
                    favNodeId = it.nodeId[0].value.toString()
                    databaseHandler!!.updateChannelByPosicion(db, posicion, 1, favNodeId)
                    ivFillStar.setBackgroundResource(R.drawable.ic_fill_star_blue)
                }
            }
        }

        homeVM.observeDeleteDevice().observe(this) {
            showToast(removeFromFavourite)
            favNodeId = ""
            ivFillStar.setBackgroundResource(R.drawable.ic_blank_star_blue)
            databaseHandler!!.updateChannelByPosicion(db, posicion, 0, "")
        }
    }

    private fun getString() {
        val getLanguageString = SharedPreferencesHelper.getArrayList(this)

        for (i in 0 until getLanguageString!!.size) {
            when(getLanguageString[i].key) {
                "upcoming_show" -> {
                    tvTitle.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "please_enter_a_valid_pin" -> {
                    pinValidMessage = getLanguageString[i].value
                }
                "please_set_a_secure_pin_to_show_live_telecast_of_this_channel" -> {
                    pinRequiredMessage = getLanguageString[i].value
                }
                "ok" -> {
                    pinText = getLanguageString[i].value
                }
                "enter_pin" -> {
                    pinTitle = getLanguageString[i].value
                    pinPasswordHint= getLanguageString[i].value
                }
                "add_favorite" -> {
                    addToFavourite = getLanguageString[i].value
                }
                "remove_favorite" -> {
                    removeFromFavourite = getLanguageString[i].value
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


    override fun onStop() {
        super.onStop()
        exoPlayer.stop()
    }

    override fun onResume() {
        super.onResume()
        val audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (currentVolume == 0) {
            ivVolumeDown.show()
            ivVolumeUp.hide()
        } else {
            ivVolumeDown.hide()
            ivVolumeUp.show()
        }

    }

    override fun onDestroy() {
        exoPlayer.stop()
        db?.close()
        databaseHandler?.close()
        val window = window ?: return
        val decor = window.decorView
        if (decor?.parent != null) {
            super.onDestroy()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }
}