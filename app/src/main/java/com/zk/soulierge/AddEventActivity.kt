package com.zk.soulierge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zk.soulierge.support.utilExt.initToolbar
import kotlinx.android.synthetic.main.toola_bar.*

class AddEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        initToolbar(tool_bar, true, getString(R.string.add_event))

    }
}