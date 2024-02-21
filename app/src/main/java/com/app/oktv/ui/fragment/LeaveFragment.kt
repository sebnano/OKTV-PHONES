package com.app.oktv.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.oktv.R
import com.app.oktv.ui.activity.HomeActivity
import com.app.oktv.ui.activity.LoginActivity
import kotlinx.android.synthetic.main.fragment_leave.*

class LeaveFragment : Fragment() {

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

        setOnClickView()
        changeText()
    }

    /** This Function For Click Listener **/
    private fun setOnClickView() {
        btnOk.setOnClickListener {
            startActivity(Intent(requireContext(),LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            (context as HomeActivity).finish()
        }
    }

    /** This Function For Change Text **/
    private fun changeText() {
        tvTitle.text = getString(R.string.leave)
        tvDialogeTitle.text = getString(R.string.press_ok_button_to_exist)
        btnOk.text = getString(R.string.ok)
    }
}