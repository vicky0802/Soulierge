package com.zk.soulierge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.FriendRequestResponse
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserData
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_user_friend.*
import kotlinx.android.synthetic.main.row_org_user.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.ResponseBody

class FriendRequestActivity : AppCompatActivity() {
    var organisationBuilder: RecyclerViewBuilder<FriendRequestResponse>? = null

    var user: LoginResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)
        initToolbar(tool_bar, true, getString(R.string.pending_request))
        user = getUserData<LoginResponse>()
        callOrganisationListAPI(user?.userId)

        setupRecycleView(ArrayList<FriendRequestResponse?>())
        addOrg?.setOnClickListener {
            val intent = Intent(this, AddOrgUserActivity::class.java)
            this.intent.extras?.let { it1 -> intent.putExtras(it1) }
            startActivityForResult(intent, 1002)
        }
    }

    private fun setupRecycleView(o: ArrayList<FriendRequestResponse?>) {
        organisationBuilder = rvOrgRec?.setUp(
            R.layout.row_org_user,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                view?.btnAccept?.visibility = View.VISIBLE
                view?.btnReject?.visibility = View.VISIBLE
                view?.txtUseName?.text = item.friendUser?.name
                view?.txtMail?.text = item.friendUser?.email
                view?.txtPhone?.text = item.friendUser?.phoneNumber
                view?.txtGender?.text = item.friendUser?.gender
                view?.imgUserDelete?.visibility = View.GONE
                view?.btnAccept?.setOnClickListener { callAcceptAPI(item) }
                view?.btnReject?.setOnClickListener { callRejectAPI(item) }
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun callAcceptAPI(item: FriendRequestResponse) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateRequest(id = item.id.toString(),status = "1"),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    callOrganisationListAPI(user?.userId)
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                    Test@1234
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

    private fun callRejectAPI(item: FriendRequestResponse) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateRequest(id = item.id.toString(),status = "2"),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    callOrganisationListAPI(user?.userId)
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

    private fun callOrganisationListAPI(organizationId: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getPendingFriends(organizationId),
            singleCallback = object : SingleCallback<ArrayList<FriendRequestResponse?>> {
                override fun onSingleSuccess(
                    o: ArrayList<FriendRequestResponse?>,
                    message: String?
                ) {
                    loadingDialog(false)
                    if (o?.isNullOrEmpty()) {
                        llNoData?.visibility = View.VISIBLE
                    } else {
                        llNoData?.visibility = View.GONE
                        o?.let { setupRecycleView(it) }

                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    llNoData?.visibility = View.VISIBLE
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    llNoData?.visibility = View.VISIBLE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002) {
            if (resultCode == RESULT_OK) {
                callOrganisationListAPI(user?.userId)
            }
        }
    }
}