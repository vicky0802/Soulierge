package com.zk.soulierge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zk.soulierge.utlities.ActivityNavigationUtility
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        btn_sign_in.setOnClickListener {
            ActivityNavigationUtility.navigateWith(this)
                .navigateTo(SigninActivity::class.java)
        }

        btn_sign_up.setOnClickListener {
            ActivityNavigationUtility.navigateWith(this)
                .navigateTo(SignupActivity::class.java)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
