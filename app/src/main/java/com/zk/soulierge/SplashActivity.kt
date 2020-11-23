package com.zk.soulierge

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.zk.soulierge.support.utilExt.isLogin
import com.zk.soulierge.utlities.ActivityNavigationUtility

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (isLogin()) {
                ActivityNavigationUtility.navigateWith(this)
                    .setClearStack().navigateTo(MainActivity::class.java)
            } else {
                ActivityNavigationUtility.navigateWith(this)
                    .navigateTo(LandingActivity::class.java)
            }
            finish()
//            viewModel.shouldShowEnterButton.set(true)
        }, 2000)
    }
}