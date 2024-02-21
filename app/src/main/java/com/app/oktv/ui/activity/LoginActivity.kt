package com.app.oktv.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.app.oktv.R
import com.app.oktv.custom.classes.CustomAppCompatActivity
import com.app.oktv.utils.isValidEmail
import com.app.oktv.utils.show
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : CustomAppCompatActivity() {

    /** This Variables For PopupView **/
    private var myPopupWindow: PopupWindow? = null
    private var popupView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initData()
        setOnViewClicks()
    }

    /** This Function For Initialize **/
    override fun initData() {
        popupView = layoutInflater.inflate(R.layout.raw_language_list, null)
        myPopupWindow = PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true)
    }

    /** This Function For Click Listener **/
    private fun setOnViewClicks() {
        ivEye.setOnClickListener { showHidePass(etPassword , ivEye) }

        clLanguage.setOnClickListener {
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

        btnLogin.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
                finish()
//            if (etEmail.text.toString().trim().isEmpty()) { showToast(getString(R.string.enter_r_email))
//            } else if (!etEmail.text.toString().trim().isValidEmail()) {
//                showToast((getString(R.string.enter_r_valid_email)))
//            } else if (etPassword.text.toString().trim().isEmpty()) {
//                showToast(getString(R.string.enter_r_password))
//            } else if (etPassword.text.toString().trim().length < 6) {
//                showToast(getString(R.string.password_should_be_at_least_of_6_character))
//            } else {
//                startActivity(Intent(this,HomeActivity::class.java))
//                finish()
//            }
        }

        btnLeave.setOnClickListener { finish() }
    }
}