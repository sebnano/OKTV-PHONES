package com.oktv_mobile.ui.activity

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.oktv_mobile.R
import com.oktv_mobile.custom.classes.CustomAppCompatActivity
import com.oktv_mobile.ui.viewmodel.HomeVM
import com.oktv_mobile.ui.viewmodel.LoginVM
import com.egeniq.androidtvprogramguide.entity.ProgramGuideSchedule
import com.oktv_mobile.ui.adapter.HomeBannerAdapter
import com.oktv_mobile.ui.fragment.*
import com.oktv_mobile.ui.model.homemodel.HomeBannerModel
import com.oktv_mobile.utils.*
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : CustomAppCompatActivity() {

    /** This Variable For All Tab Fragment **/
    lateinit var homeFragment: HomeFragment
    private lateinit var favouriteFragment: FavouriteFragment
    private lateinit var guideFragment: EpgFragment
    private lateinit var settingFragment: SettingFragment
    private lateinit var leaveFragment: LeaveFragment

    /** This Variable Used for Adapter **/
    private lateinit var homeBannerAdapter: HomeBannerAdapter

    /** This Variable Used for Store Api Data **/
    private val getBannerList = ArrayList<HomeBannerModel>()

    /** This Variables For PopupView **/
    private var myPopupWindow: PopupWindow? = null
    private var popupView: View? = null
    private lateinit var tvChannel: TextView
    private lateinit var tvFavourite: TextView
    private lateinit var tvGuide: TextView
    private lateinit var tvSetting: TextView
    private lateinit var tvLeave: TextView
    private lateinit var tvExit: TextView

    private lateinit var handler: Handler
    private var bannerTime = "10000"
    private var somethingWrongMessage = ""

    /** This Variable For ViewModel **/
    private lateinit var loginVM: LoginVM

    var databaseHandler: DBHelper? = null
    var db: SQLiteDatabase? = null

    private val runnable = Runnable {
        vpBanner.currentItem = vpBanner.currentItem + 1
    }

    private lateinit var homeVM : HomeVM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initData()
        setOnClickViews()
        observeData()
        getString()
    }

    /** This Function For Initialize **/
    override fun initData() {
        databaseHandler = DBHelper(this, null)
        db = databaseHandler?.writableDatabase
        handler = Handler()
        loginVM = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginVM::class.java]
        homeVM = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeVM::class.java]

        homeFragment = HomeFragment()
        favouriteFragment = FavouriteFragment()
        guideFragment = EpgFragment()
        settingFragment = SettingFragment()
        leaveFragment = LeaveFragment()

        popupView = layoutInflater.inflate(R.layout.raw_manubar_list, null)
        myPopupWindow = PopupWindow(
            popupView,
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        myPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        loginVM.configuration()

        // Set cancelable to true (optional)
        myPopupWindow!!.isOutsideTouchable = true
        myPopupWindow!!.isFocusable = true

        tvChannel = popupView?.findViewById(R.id.tvChannel) as TextView
        tvFavourite = popupView?.findViewById(R.id.tvFavourite) as TextView
        tvGuide = popupView?.findViewById(R.id.tvGuide) as TextView
        tvSetting = popupView?.findViewById(R.id.tvSetting) as TextView
        tvLeave = popupView?.findViewById(R.id.tvLeave) as TextView
        tvExit = popupView?.findViewById(R.id.tvExit) as TextView

        tvFavourite.text = getString(R.string.favourite)

        homeBannerAdapter = HomeBannerAdapter(getBannerList)
        vpBanner.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vpBanner.offscreenPageLimit = 1
        vpBanner.adapter = homeBannerAdapter
        viewpagerAdapter()
//        startAutoScroll()
        getBanners()

        if (intent.getStringExtra("changeFragment") == "sendSetting") {
            changeFragment(3)
        } else {
            changeFragment(0)
        }
        tvChannelName.text = MyPreferences.getFromPreferences(this, Constant.OPERATORNAME)
        ProgramGuideSchedule.setLanguageCode(MyPreferences.getFromPreferences(this, Constant.languageCode))
    }

    private fun getString() {
        val getLanguageString = SharedPreferencesHelper.getArrayList(this)
        Log.e("languageList","--->"+getLanguageString)
        for (i in 0 until getLanguageString!!.size) {
            when(getLanguageString[i].key) {
                "channels" -> {
                    tvChannel.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "favourites" -> {
                    tvFavourite.text = getLanguageString[i].value
                }
                "guide" -> {
                    tvGuide.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "settings" -> {
                    tvSetting.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "leave" -> {
                    tvLeave.text = convertHTMLtoString(getLanguageString[i].value)
                }
                "something_went_wrong_" -> {
                    somethingWrongMessage = getLanguageString[i].value
                }
                "exit" -> {
                    tvExit.text = convertHTMLtoString(getLanguageString[i].value)
                }
            }
        }
    }

    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun getBanners() {
        val cursor = db?.rawQuery("SELECT * FROM ${DBHelper.BANNERS_TABLE}", null)
        if (cursor != null) {
            getBannerList.clear()
            while (cursor.moveToNext()) {
                getBannerList.add(
                    HomeBannerModel(
                        "",
                        cursor.getString(cursor.getColumnIndex(DBHelper.bannerColumn_type)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.bannerColumn_status)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.bannerColumn_field_active_banner)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.bannerColumn_field_imagen)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.bannerColumn_field_position)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.bannerColumn_field_automatic_slide)),
                    )
                )
            }
        }
        cursor?.close()
        homeBannerAdapter.notifyDataSetChanged()
    }

    private fun startAutoScroll() {
        try {
            var currentPage = 0
            val handler = Handler()
            val update = Runnable {
                if (currentPage == homeBannerAdapter.itemCount) {
                    currentPage = 0
                }
                vpBanner.setCurrentItem(currentPage++, true)
            }
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(update)
                }
            }, bannerTime.toLong(), bannerTime.toLong())
        } catch (e: java.lang.ArithmeticException) {
            showToast(somethingWrongMessage)
        } catch (e: java.lang.Exception) {
            showToast(somethingWrongMessage)
        }
    }

    @SuppressLint("ResourceAsColor")
    /** This Function For Click Listener **/
    private fun setOnClickViews() {
        clFooter.setOnClickListener { }

        ivMenuBar.setOnClickListener {
            if (myPopupWindow == null || !myPopupWindow!!.isShowing) {
                popupView?.show()
                myPopupWindow?.showAsDropDown(it, 0, 0)
            } else {
                // Dismiss the popup window if it's already showing
                myPopupWindow!!.dismiss()
            }
        }

        tvChannel.setOnClickListener {
            changeFragment(0)
            myPopupWindow?.dismiss()
        }

        tvFavourite.setOnClickListener {
            changeFragment(1)
            myPopupWindow?.dismiss()
        }

        tvGuide.setOnClickListener {
            changeFragment(2)
            myPopupWindow?.dismiss()
        }

        tvSetting.setOnClickListener {
            changeFragment(3)
            myPopupWindow?.dismiss()
        }

        tvLeave.setOnClickListener {
            changeFragment(4)
            myPopupWindow?.dismiss()
        }

        tvExit.setOnClickListener { finish() }

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    @SuppressLint("ResourceAsColor")
    /** This Function For Click To Replace Fragment **/
    fun changeFragment(arg: Int) {
        tvChannel.setTextColor(getColor(R.color.white))
        tvFavourite.setTextColor(getColor(R.color.white))
        tvGuide.setTextColor(getColor(R.color.white))
        tvSetting.setTextColor(getColor(R.color.white))
        tvLeave.setTextColor(getColor(R.color.white))
          tvChannelName.show()

        when (arg) {
            0 -> {
                tvChannel.setTextColor(getColor(R.color.yellow_59))
                removeFragment(leaveFragment)
                removeFragment(favouriteFragment)
                removeFragment(guideFragment)
                removeFragment(settingFragment)
                if (!homeFragment.isAdded) {
                    addFragment(homeFragment)
                }
            }

            1 -> {
                tvFavourite.setTextColor(getColor(R.color.yellow_59))
                removeFragment(leaveFragment)
                removeFragment(homeFragment)
                removeFragment(guideFragment)
                removeFragment(settingFragment)
                if (!favouriteFragment.isAdded) {
                    addFragment(favouriteFragment)
                }
            }

            2 -> {
                tvGuide.setTextColor(getColor(R.color.yellow_59))
                removeFragment(leaveFragment)
                removeFragment(homeFragment)
                removeFragment(favouriteFragment)
                removeFragment(settingFragment)
                if (!guideFragment.isAdded) {
                    addFragment(guideFragment)
                }
            }

            3 -> {
                tvSetting.setTextColor(getColor(R.color.yellow_59))
                removeFragment(homeFragment)
                removeFragment(favouriteFragment)
                removeFragment(guideFragment)
                removeFragment(leaveFragment)
                if (!settingFragment.isAdded) {
                    addFragment(settingFragment)
                }
            }

            4 -> {
                tvLeave.setTextColor(getColor(R.color.yellow_59))
                removeFragment(homeFragment)
                removeFragment(favouriteFragment)
                removeFragment(guideFragment)
                removeFragment(settingFragment)
                if (!leaveFragment.isAdded) {
                    addFragment(leaveFragment)
                }
                clFooter.show()
                tvChannelName.hide()
                ivMenuBar.hide()
            }
        }

    }

    /** this function used for add a fragment **/
    private fun removeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
    }

    /** this function used for add a fragment **/
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.flFragment, fragment).commit()
    }

    private fun observeData() {
        loginVM.observeConfiguration().observe(this) { list ->
            if (list != null) {
                bannerTime = list[1].value

                val epgConfiguration = list.filter {
                    it.key == "url_epg"
                }
                if (epgConfiguration != null && epgConfiguration.isNotEmpty()) {
                    Constant.EPG_URL = epgConfiguration.first().value
                    Log.e("EPG_URL", epgConfiguration.first().value)
                } else {
                    Constant.EPG_URL = "https://portal.ok-television.com/sites/default/files/android-tv-cdn/perfectfile.xml"
                }

            } else {
                hideLoadingDialog()
            }
        }
        Log.e("ParsedXMLData", "Start")
        Constant.EPG_URL = "https://portal.ok-television.com/sites/default/files/android-tv-cdn/perfectfile.xml"
//        downloadXml()

        getDevices()
    }

    /** this function used for convert html text to common text **/
    private fun convertHTMLtoString(content: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(content).toString()
        }
    }

    /**This Function Product Image ViewPager **/
    private fun viewpagerAdapter() {
        val nextItemVisiblePx = 0
        val currentItemHorizontalMarginPx = 0
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.00f * kotlin.math.abs(position))
        }
        vpBanner.setPageTransformer(pageTransformer)
    }

    override fun onDestroy() {
//        super.onDestroy()
        loginVM.observeConfiguration().removeObservers(this)
        loginVM.observeLanguageString().removeObservers(this)
//        db?.close()
        val window = window ?: return
        val decor = window.decorView
        if (decor?.parent != null) {
            super.onDestroy()
        }
    }

    private fun getDevices() {
        Log.i("ADDED_OPERATOR_DEVICE_ID_NAME", "${MyPreferences.getFromPreferences(
            this, Constant.OPERATORID)}-${MyPreferences.getFromPreferences(
            this, Constant.OPERATORNAME)}")
        homeVM.userDeviceList(
            MyPreferences.getFromPreferences(this, Constant.USERID),
            MyPreferences.getFromPreferences(this, Constant.OPERATORID))
        homeVM.observeDevices().observe(this) {
            Log.i("DownloadManager", "observeDevices called")
            if (it != null) {
                Log.i("ADDED_OPERATOR_DEVICE", it.toString())
                db?.execSQL("DELETE FROM ${DBHelper.DEVICES_TABLE}")
                for (item in it) {
                    databaseHandler!!.addDevices(db, item.operatorId, item.pais_dispositivos, item.nodeId, item.deviceMac, item.deviceTitle, item.deviceIp)
                }
                homeVM.observeDevices().removeObservers(this)
            }
        }
    }
}
