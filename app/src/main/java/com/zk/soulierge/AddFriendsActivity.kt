package com.zk.soulierge

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.google.gson.JsonObject
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.model.UserResponse
import com.zk.soulierge.support.api.model.toJson
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserData
import com.zk.soulierge.support.utilExt.hideSoftKeyboard
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.row_org_user.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.MediaType
import okhttp3.RequestBody

class AddFriendsActivity : AppCompatActivity() {
    var organisationBuilder: RecyclerViewBuilder<UserResponse>? = null
    var user: LoginResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)
        initToolbar(tool_bar, true, getString(R.string.users_friends))
        user = getUserData<LoginResponse>()

        setupRecycleView(ArrayList<UserResponse?>())

        serch_event?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                return if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (serch_event?.text?.toString()?.trim()?.isEmpty() == true) {
                        showAppDialog(getString(R.string.warning_search))
                    } else {
                        img_clear?.visibility = View.VISIBLE
                        callOrganisationListAPI(serch_event?.text.toString().trim())
                        serch_event?.clearFocus()
                        hideSoftKeyboard()
                    }
                    true
                } else false
            }
        })

        img_clear?.setOnClickListener {
            clearSearch()
        }
    }

    private fun clearSearch() {
        serch_event.setText("")
        img_clear?.visibility = View.INVISIBLE
//        serch_event?.clearFocus()
        hideSoftKeyboard()
    }

    private fun setupRecycleView(o: ArrayList<UserResponse?>) {
        if (o?.isNullOrEmpty()) {
            llNoData?.visibility = View.VISIBLE
        } else {
            llNoData?.visibility = View.GONE
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
                    view?.btnAddFriend?.setOnClickListener { addFriendAPI(item) }
                }
                isNestedScrollingEnabled = false
            }
        }
    }

    private fun addFriendAPI(item: UserResponse?) {
        loadingDialog(true)
        val json = JsonObject()
//        json.addProperty("email", serch_event?.text?.toString()?.trim())
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .addFriend(user_id = user?.userId,friend_user_id = item?.id),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    showAppDialog((if(o.success.isNullOrEmpty()) o.failure else o.success)){callOrganisationListAPI(serch_event?.text?.toString()?.trim())}
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
        val json = JsonObject()
        json.addProperty("email", organizationId)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getSearchFiend(body),
            singleCallback = object : SingleCallback<ArrayList<UserResponse?>> {
                override fun onSingleSuccess(
                    o: ArrayList<UserResponse?>,
                    message: String?
                ) {
                    loadingDialog(false)
                    o?.let { setupRecycleView(it) }
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