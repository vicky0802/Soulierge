package com.zk.soulierge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.UserResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserId
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_user_chakra.*
import kotlinx.android.synthetic.main.toola_bar.*

class UserChakraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_chakra)
        initToolbar(tool_bar, true, getString(R.string.user_chakra))
        getUseAPI()
    }

    private fun setChakra(o: UserResponse?) {
        txtCurrentBadge?.text = getString(R.string.current_badge) + " ${o?.badge}"
        txtCurrentPoints?.text = getString(R.string.current_points) + " ${o?.currentPoints}"
        txtNextPoints?.text = getString(R.string.next_milestone_points) + " ${o?.nextMilestonePoints}"
        when (o?.badge) {
            1 -> {
                chakraImg.setImageResource(R.drawable.chakar_2)
            }
            2 -> {
                chakraImg.setImageResource(R.drawable.chakar_3)
            }
            3 -> {
                chakraImg.setImageResource(R.drawable.chakar_4)
            }
            4 -> {
                chakraImg.setImageResource(R.drawable.chakar_5)
            }
            5 -> {
                chakraImg.setImageResource(R.drawable.chakar_6)
            }
            6 -> {
                chakraImg.setImageResource(R.drawable.chakar_7)
            }
        }
    }

    private fun getUseAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getUser(user_id = getUserId()),
            singleCallback = object : SingleCallback<UserResponse> {
                override fun onSingleSuccess(o: UserResponse, message: String?) {
                    loadingDialog(false)
                    setChakra(o)
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    message?.let {
                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}