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
import com.zk.soulierge.support.api.model.OrgUserModel
import com.zk.soulierge.support.api.model.OrganisationModalItem
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserId
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_org_users.*
import kotlinx.android.synthetic.main.row_org_user.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.ResponseBody

class OrgUsersActivity : AppCompatActivity() {
    var organisationBuilder: RecyclerViewBuilder<OrgUserModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_users)
        initToolbar(tool_bar, true, getString(R.string.lbl_organisation_users))

        if (intent.hasExtra("organisation")) {
            val organisation = intent.getParcelableExtra<OrganisationModalItem>("organisation")
            callOrganisationListAPI(organisation?.id)
        }else{
            llNoData?.visibility = View.VISIBLE
        }
        setupRecycleView(ArrayList<OrgUserModel?>())
        addOrg?.setOnClickListener {
            startActivityForResult(
                Intent(
                    this,
                    AddOrganisation::class.java
                ), 1002
            )
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

            }
            isNestedScrollingEnabled = false
        }
    }

    private fun callDeleteOrginationAPI(item: OrganisationModalItem) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .deleteOrgAPI(id = item.id),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
//                    callOrganisationListAPI()
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

    private fun callFavAPI(item: OrganisationModalItem, position: Int) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .favouriteOrgAPI(organization_id = item.id, user_who_favourited_id = getUserId()),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    item.isFavorite = true
                    organisationBuilder?.notifyItemChanged(position)
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

    private fun callUnFavAPI(item: OrganisationModalItem, position: Int) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .unFavouriteOrgAPI(organization_id = item.id, user_who_favourited_id = getUserId()),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    item.isFavorite = false
                    organisationBuilder?.notifyItemChanged(position)
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

    private fun callOrganisationListAPI(organizationId:String?) {
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
//                callOrganisationListAPI()
            }
        }
    }

}