package com.zk.soulierge.fragments


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.zk.soulierge.FriendRequestActivity
import com.zk.soulierge.R
import com.zk.soulierge.UserFriendActivity
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.model.UserResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.*
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.PendingFriendsActivity
import kotlinx.android.synthetic.main.dialog_gender.view.*
import kotlinx.android.synthetic.main.dialog_gender.view.btnCancel
import kotlinx.android.synthetic.main.fragment_account_setting.*
import kotlinx.android.synthetic.main.fragment_account_setting.txtEmail
import kotlinx.android.synthetic.main.fragment_account_setting.txtGender
import kotlinx.android.synthetic.main.fragment_account_setting.txtPhone

/**
 * A simple [Fragment] subclass.
 */
class AccountSetting : BaseFragment() {
    var userResponse: UserResponse? = null

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
        btn_back?.setOnClickListener { activity?.onBackPressed() }
        txtGender?.setOnClickListener { openBottomSheetDialog() }
        btnShowFriends?.setOnClickListener { startActivity(Intent(context, UserFriendActivity::class.java)) }
        btnShowPendingFriends?.setOnClickListener { startActivity(Intent(context, PendingFriendsActivity::class.java)) }
        btnPendingFriendsRequest?.setOnClickListener { startActivity(Intent(context, FriendRequestActivity::class.java)) }
    }

    override fun getTagFragment(): String {
        return "account_setting_frag"
    }

    private fun openBottomSheetDialog() {
        val view = View.inflate(context, R.layout.dialog_gender, null)
        val builder = context?.let { BottomSheetDialogBuilder(it) }
        builder?.customView(view)
        view?.btnMale?.setOnClickListener { txtGender.setText(view?.btnMale.text.toString());builder?.dismiss(); }
        view?.btnFeMale?.setOnClickListener { txtGender.setText(view?.btnFeMale?.text.toString());builder?.dismiss(); }
        view?.btnCancel?.setOnClickListener { builder?.dismiss(); }
        builder?.show()
    }

    private fun getUseAPI() {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getUser(user_id = context?.getUserId()),
            singleCallback = object : SingleCallback<UserResponse> {
                override fun onSingleSuccess(o: UserResponse, message: String?) {
                    context?.loadingDialog(false)
                    userResponse = o
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
                    user?.userId?.let { context?.setUserId(it) }
                    btn_update?.setOnClickListener{ val user = context?.getUserData<LoginResponse>()
                        updateUserAPI(userResponse, user?.fileName)}
                    if (user?.userTypeId.equals("1")or(user?.userTypeId.equals("2"))){
                        btnShowFriends?.visibility = View.VISIBLE
                        btnShowPendingFriends?.visibility = View.VISIBLE
                        btnPendingFriendsRequest?.visibility = View.VISIBLE
                    }else{
                        btnShowFriends?.visibility = View.GONE
                        btnShowPendingFriends?.visibility = View.GONE
                        btnPendingFriendsRequest?.visibility = View.GONE
                    }
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

    private fun updateUserAPI(user: UserResponse?, fileName: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateUser(
                    address = user?.address,
                    file_name = fileName,
                    gender = txtGender?.text.toString().trim(),
                    latitude = user?.latitude,
                    longitude = user?.longitude,
                    name = txtUserName?.text.toString().trim(),
                    phone_number = txtPhone?.text.toString().trim(),
                    user_id = user?.id
                ),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    context?.loadingDialog(false)
                    context?.showAppDialog(o.success) { getUseAPI() }
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
