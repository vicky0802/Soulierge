package com.zk.soulierge

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.model.OrgUserModel
import com.zk.soulierge.support.api.model.OrganisationModalItem
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserData
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utils.confirmationDialog
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_org_detail.*
import kotlinx.android.synthetic.main.activity_org_users.*
import kotlinx.android.synthetic.main.activity_org_users.llNoData
import kotlinx.android.synthetic.main.row_org_user.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.ResponseBody

class OrgUsersActivity : AppCompatActivity() {
    var organisationBuilder: RecyclerViewBuilder<OrgUserModel>? = null
    var organisation: OrganisationModalItem? = null

    var user: LoginResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_users)
        initToolbar(tool_bar, true, getString(R.string.lbl_organisation_users))
        if (intent.hasExtra("organisation")) {
            organisation = intent.getParcelableExtra<OrganisationModalItem>("organisation")
            callOrganisationListAPI(organisation?.id)
        } else {
            llNoData?.visibility = View.VISIBLE
        }

        user = getUserData<LoginResponse>()
        if (user?.userTypeId.equals("4")) {
            addOrg?.visibility = View.VISIBLE
        } else {
            addOrg?.visibility = View.GONE
        }

        setupRecycleView(ArrayList<OrgUserModel?>())
        addOrg?.setOnClickListener {
            val intent = Intent(this, AddOrgUserActivity::class.java)
            this.intent.extras?.let { it1 -> intent.putExtras(it1) }
            startActivityForResult(intent, 1002)
        }
    }

    private fun setupRecycleView(o: ArrayList<OrgUserModel?>) {
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
                view?.imgUserDelete?.setOnClickListener {
                    confirmationDialog(getString(R.string.app_name).toUpperCase(),
                        getString(R.string.del_message), {
                            callDeleteOrginationUserAPI(item)
                        })
                }
                view?.setOnClickListener {
                    val intent = Intent(this@OrgUsersActivity, UpdateOrgUserActivity::class.java)
                    intent.putExtra("user", item)
                    intent.extras?.let { it1 -> intent.putExtras(it1) }
                    startActivityForResult(intent, 1002)
                }
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun callDeleteOrginationUserAPI(item: OrgUserModel) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .deleteOrgUserAPI(id = item.id.toString()),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    callOrganisationListAPI(organizationId = organisation?.id)
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
                .getOrganisationUser(organizationId),
            singleCallback = object : SingleCallback<ArrayList<OrgUserModel?>> {
                override fun onSingleSuccess(
                    o: ArrayList<OrgUserModel?>,
                    message: String?
                ) {
                    loadingDialog(false)
                    if (o.size > 0) {
                        llNoData?.visibility = View.GONE
                    } else {
                        llNoData?.visibility = View.VISIBLE
                    }
                    setupRecycleView(o)
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
                callOrganisationListAPI(organisation?.id)
            }
        }
    }

}