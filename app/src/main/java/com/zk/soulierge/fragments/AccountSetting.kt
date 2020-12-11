package com.zk.soulierge.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.model.UserResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserData
import com.zk.soulierge.support.utilExt.getUserId
import com.zk.soulierge.support.utilExt.setUserData
import com.zk.soulierge.support.utilExt.setUserName
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.fragment_account_setting.*

/**
 * A simple [Fragment] subclass.
 */
class AccountSetting : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUseAPI()

    }

    override fun getTagFragment(): String {
        return "account_setting_frag"
    }

    private fun getUseAPI() {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getUser(user_id = context?.getUserId()),
            singleCallback = object : SingleCallback<UserResponse> {
                override fun onSingleSuccess(o: UserResponse, message: String?) {
                    context?.loadingDialog(false)
                    txtUserName?.setText(o?.name)
                    txtEmail?.setText(o?.email)
                    txtPhone?.setText(o?.phoneNumber)
                    txtGender?.setText(o?.gender)
                    val user = context?.getUserData<LoginResponse>()
                    user?.dateCreated = o.dateCreated
                    user?.name = o.name
                    user?.userTypeId = o.userTypeId
                    o.name?.let { context?.setUserName(it) }
                    context?.setUserData(Gson().toJson(user).toString())
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    context?.loadingDialog(false)
                    context?.simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    context?.loadingDialog(false)
                    message?.let {
                        context?.simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

}
