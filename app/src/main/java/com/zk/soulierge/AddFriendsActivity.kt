package com.zk.soulierge

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.FriendsResponse
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.model.UserResponse
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

class AddFriendsActivity : AppCompatActivity() {
    var organisationBuilder: RecyclerViewBuilder<UserResponse>? = null
    var user: LoginResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)
        initToolbar(tool_bar, true, getString(R.string.users_friends))
        user = getUserData<LoginResponse>()
        callOrganisationListAPI("vishal")

        setupRecycleView(ArrayList<UserResponse?>())
    }

    private fun setupRecycleView(o: ArrayList<UserResponse?>) {
        organisationBuilder = rvOrgRec?.setUp(
            R.layout.row_org_user,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                view?.txtUseName?.text = item.name
                view?.txtMail?.text = item.email
                view?.txtPhone?.text = item.phoneNumber
                view?.txtGender?.text = item.gender
                view?.imgUserDelete?.visibility = View.GONE
                view?.btnAddFriend?.visibility = View.VISIBLE
                view?.btnAddFriend?.setOnClickListener {  }
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun callOrganisationListAPI(organizationId: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getSearchFiend(organizationId),
            singleCallback = object : SingleCallback<FriendsResponse?> {
                override fun onSingleSuccess(
                    o: FriendsResponse?,
                    message: String?
                ) {
                    loadingDialog(false)
                    if (o?.friends.isNullOrEmpty()) {
                        llNoData?.visibility = View.VISIBLE
                    } else {
                        llNoData?.visibility = View.GONE
                        o?.friends?.let { setupRecycleView(it) }

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
}