package com.app.oktv.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.app.oktv.R
import com.app.oktv.custom.classes.CustomAppCompatActivity
import com.app.oktv.ui.adapter.HomeBannerAdapter
import com.app.oktv.ui.fragment.*
import com.app.oktv.utils.hide
import com.app.oktv.utils.show
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.raw_manubar_list.*

class HomeActivity : CustomAppCompatActivity() {

    /** This Variable For All Tab Fragment **/
    private lateinit var homeFragment: HomeFragment
    private lateinit var favouriteFragment: FavouriteFragment
//    private lateinit var guideFragment: GuideFragment
    private lateinit var settingFragment: SettingFragment
    private lateinit var leaveFragment: LeaveFragment
    private lateinit var guideFragment: EpgFragment

    /** This Variable Used for Adapter **/
    private lateinit var  homeBannerAdapter: HomeBannerAdapter

    /** This Variable Used for Store Api Data **/
    private val getBannerList = ArrayList<Int>()

    /** This Variables For PopupView **/
    private var myPopupWindow: PopupWindow? = null
    private var popupView: View? = null

    private lateinit var tvChannel : TextView
    private lateinit var tvFavourite : TextView
    private lateinit var tvGuide : TextView
    private lateinit var tvSetting : TextView
    private lateinit var tvLeave : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initData()
        setOnClickViews()
        changeText()
    }

    /** This Function For Initialize **/
    override fun initData() {

        homeFragment = HomeFragment()
        favouriteFragment = FavouriteFragment()
        guideFragment = EpgFragment()
        settingFragment = SettingFragment()
        leaveFragment = LeaveFragment()

        getBannerList.clear()
        getBannerList.add(R.drawable.home_banner)
        getBannerList.add(R.drawable.home_banner)

        homeBannerAdapter = HomeBannerAdapter(getBannerList)
        vpBanner.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        vpBanner.offscreenPageLimit = 1
        vpBanner.adapter = homeBannerAdapter

        popupView = layoutInflater.inflate(R.layout.raw_manubar_list, null)
        myPopupWindow = PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
        myPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set cancelable to true (optional)
        myPopupWindow!!.isOutsideTouchable = false
        myPopupWindow!!.isFocusable = false

        tvChannel = popupView?.findViewById(R.id.tvChannel) as TextView
        tvFavourite = popupView?.findViewById(R.id.tvFavourite) as TextView
        tvGuide = popupView?.findViewById(R.id.tvGuide) as TextView
        tvSetting = popupView?.findViewById(R.id.tvSetting) as TextView
        tvLeave = popupView?.findViewById(R.id.tvLeave) as TextView

        changeFragment(0)
    }

    @SuppressLint("ResourceAsColor")
    /** This Function For Click Listener **/
    private fun setOnClickViews() {
        ivMenuBar.setOnClickListener {
            if (myPopupWindow == null || !myPopupWindow!!.isShowing) {
                popupView?.show()
                myPopupWindow?.showAsDropDown(it,0,0)
            } else {
                // Dismiss the popup window if it's already showing
                myPopupWindow!!.dismiss()
            }

        }

//        clMain.setOnClickListener { clMenuList.hide() }

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
    }

    @SuppressLint("ResourceAsColor")
    /** This Function For Click To Replace Fragment **/
    private fun changeFragment(arg : Int) {
        tvChannel.setTextColor(getColor(R.color.white))
        tvFavourite.setTextColor(getColor(R.color.white))
        tvGuide.setTextColor(getColor(R.color.white))
        tvSetting.setTextColor(getColor(R.color.white))
        tvLeave.setTextColor(getColor(R.color.white))

        tvChannelName.show()

        when(arg) {
            0 -> {
                tvChannel.setTextColor(getColor(R.color.yellow_59))
                makeCurrentFragment(homeFragment)
            }
            1 -> {
                tvFavourite.setTextColor(getColor(R.color.yellow_59))
                makeCurrentFragment(favouriteFragment)

            }
            2 -> {
                tvGuide.setTextColor(getColor(R.color.yellow_59))
                makeCurrentFragment(guideFragment)
            }
            3 -> {
                tvSetting.setTextColor(getColor(R.color.yellow_59))
                makeCurrentFragment(settingFragment)
            }
            4 -> {
                tvLeave.setTextColor(getColor(R.color.yellow_59))
                makeCurrentFragment(leaveFragment)
                clFooter.show()
                tvChannelName.hide()
                ivMenuBar.hide()
            }
        }

    }

    /** This Function For Change Text **/
    private fun changeText() {
        tvChannel.text = getString(R.string.channels)
        tvFavourite.text = getString(R.string.favourite)
        tvGuide.text = getString(R.string.guide)
        tvSetting.text = getString(R.string.settings)
        tvLeave.text = getString(R.string.leave)
        tvChannelName.text = getString(R.string.netflix)
    }

    /** This Function Use for Fragment Change **/
    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}