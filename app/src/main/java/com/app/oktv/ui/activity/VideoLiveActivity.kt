package com.app.oktv.ui.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.oktv.R
import com.app.oktv.custom.classes.CustomAppCompatActivity
import com.app.oktv.ui.adapter.UpComingShowAdapter
import com.app.oktv.ui.adapter.UpComingShowDetailAdapter
import com.app.oktv.ui.model.UpComingShowModel
import com.app.oktv.utils.hide
import com.app.oktv.utils.show
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import kotlinx.android.synthetic.main.activity_video_live.*

class VideoLiveActivity : CustomAppCompatActivity() {

    /** This Variable Used for exoplayer **/
    private lateinit var exoPlayer: SimpleExoPlayer

    /** This Variable Used for Adapter **/
    private lateinit var upComingShowAdapter : UpComingShowAdapter
    private lateinit var upComingShowDetailAdapter: UpComingShowDetailAdapter

    /** This Variable Used for Store Api Data **/
    private var upComingShowList = ArrayList<String>()
    private var upComingShowDetailList = ArrayList<UpComingShowModel>()

    private val hideControlsDelayMillis: Long = 3000 // 3 seconds
    private val hideControlsRunnable = Runnable { clHeader.hide()
    clFooter.hide() }
    private val controlsHandler = Handler()
    private val showControlsRunnable = Runnable { clHeader.show()
    clFooter.hide() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_live)

        initData()
        setonClickView()
        changeText()
    }

    /** This Function For Initialize **/
    override fun initData() {

        upComingShowList.clear()
        upComingShowList.add("1001  Multimedia")
        upComingShowList.add("1001  Multimedia")
        upComingShowList.add("1001  Multimedia")
        upComingShowList.add("1001  Multimedia")

        upComingShowAdapter = UpComingShowAdapter(upComingShowList)
        rvShowList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvShowList.adapter = upComingShowAdapter

        upComingShowDetailList.clear()
        upComingShowDetailList.add(UpComingShowModel("Stranger Things","12:00 to 01:00 "))
        upComingShowDetailList.add(UpComingShowModel("Stranger Things","01:00 to 02:00 "))
        upComingShowDetailList.add(UpComingShowModel("Stranger Things","02:00 to 03:00 "))
        upComingShowDetailList.add(UpComingShowModel("Stranger Things","03:00 to 04:00 "))
        upComingShowDetailList.add(UpComingShowModel("Stranger Things","04:00 to 05:00 "))
        upComingShowDetailList.add(UpComingShowModel("Stranger Things","06:00 to 07:00 "))

        upComingShowDetailAdapter = UpComingShowDetailAdapter(upComingShowDetailList)
        rvUpComingList.layoutManager = LinearLayoutManager(this)
        rvUpComingList.adapter = upComingShowDetailAdapter

        exoPlayer = SimpleExoPlayer.Builder(this).build()

        pvVideoLive.player = exoPlayer
        pvVideoLive.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        pvVideoLive.keepScreenOn = true
        pvVideoLive.useController = false

        val video = Uri.parse("https://live-par-2-cdn-alt.livepush.io/live/bigbuckbunnyclip/index.m3u8")
        val mediaItem = MediaItem.fromUri(video)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        controlsHandler.postDelayed(hideControlsRunnable, hideControlsDelayMillis)

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            clFooter.show()
            nsBottom.hide()
            ivLogo.hide()
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            view.hide()
            rvShowList.hide()
            ivLogo.show()
            nsBottom.show()
            tvVideoTitle.hide()
            pvVideoLive.minimumHeight = 500
            pvVideoLive.layoutParams.height = 700
            ivBack.layoutParams.height = 100
            ivVolumeUp.layoutParams.height = 100
            ivVolumeDown.layoutParams.height = 100
            ivFillStar.layoutParams.height = 100
            ivBlackStar.layoutParams.height = 100
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    /** This Function For Click Listener **/
    private fun setonClickView() {
        ivBack.setOnClickListener { finish() }

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

        pvVideoLive.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Show the custom controls
                    clHeader.show()
                    clFooter.show()
                    // Cancel any pending hide requests
                    controlsHandler.removeCallbacks(showControlsRunnable)
                    controlsHandler.removeCallbacks(hideControlsRunnable)
                }
                MotionEvent.ACTION_UP -> {
                    // Schedule hiding of the custom controls after delay
                    controlsHandler.postDelayed(hideControlsRunnable, hideControlsDelayMillis)
                }
            }
            true
        }

        ivFillStar.setOnClickListener {
            ivFillStar.hide()
            ivBlackStar.show()
        }

        ivBlackStar.setOnClickListener {
            ivBlackStar.hide()
            ivFillStar.show()
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

    /** This Function For Set Volume Statues **/
    private fun setTwoStateVolume(mute : Boolean) {
        exoPlayer.volume = if (mute)
            0F else 1F
    }

    /** This Function For Change Text **/
    private fun changeText() {
        tvVideoTitle.text = "1001  Multimedia"
        tvChannelName.text = getString(R.string.netflix)
        tvTitle.text = getString(R.string.upcoming_shows)
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.stop()
    }
}