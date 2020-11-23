package com.zk.soulierge

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.widget.ImageViewCompat
import com.zk.soulierge.fragments.*
import com.zk.soulierge.utlities.FragmentUtility
import kotlinx.android.synthetic.main.layout_bottom_menu.*

class MainActivity : AppCompatActivity() {
    private var mSelectedMenuItem: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            ll_home.performClick()
        },400)

        ll_home.setOnClickListener {
            updateMenu(it)
            FragmentUtility.withManager(supportFragmentManager)
                .replaceToFragment(HomeFragment())
        }

        ll_events.setOnClickListener {
            updateMenu(it)
            FragmentUtility.withManager(supportFragmentManager)
                .replaceToFragment(EventsFragment())
        }

        ll_add_credits.setOnClickListener {
            updateMenu(it)
            FragmentUtility.withManager(supportFragmentManager)
                .replaceToFragment(AddCreditFragment())
        }

        ll_upcoming.setOnClickListener {
            updateMenu(it)
            FragmentUtility.withManager(supportFragmentManager)
                .replaceToFragment(UpcomingFragment())
        }

        ll_profile.setOnClickListener {
            updateMenu(it)
            FragmentUtility.withManager(supportFragmentManager)
                .replaceToFragment(ProfileFragment())
        }
    }

    private fun updateMenu(selectedView: View) {
        if (mSelectedMenuItem != null) {
            setMenuItemColor(mSelectedMenuItem, R.color.dark_grey)
        }
        mSelectedMenuItem = selectedView as? LinearLayout
        setMenuItemColor(mSelectedMenuItem, R.color.app_blue)
    }

    private fun setMenuItemColor(optionItem: LinearLayout?, colorId: Int) {
        optionItem?.forEachIndexed { index, view ->
            (view as? TextView)?.setTextColor(ContextCompat.getColor(this, colorId))
            (view as? ImageView)?.let {
                ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(ContextCompat.getColor(this, colorId)))
            }
        }
    }


}
