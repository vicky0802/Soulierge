package com.zk.soulierge

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.OrganisationModalItem
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.*
import com.zk.soulierge.support.utils.confirmationDialog
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_organise_list.*
import kotlinx.android.synthetic.main.row_organisation.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.ResponseBody

class OrganiseListActivity : AppCompatActivity() {
    var organisationBuilder: RecyclerViewBuilder<OrganisationModalItem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organise_list)
        initToolbar(tool_bar, true, getString(R.string.ph_organisation))

        callOrganisationListAPI()
        setupRecycleView(ArrayList<OrganisationModalItem?>())
        addOrg?.setOnClickListener {
            startActivityForResult(
                Intent(
                    this,
                    AddOrganisation::class.java
                ), 1002
            )
        }
//        http://soulierge-test-env.us-east-1.elasticbeanstalk.com/rest/OrganizationService/addFavouriteOrganization?organization_id=34&user_who_favourited_id=61
//        http://soulierge-test-env.us-east-1.elasticbeanstalk.com/rest/OrganizationService/deleteFavouriteOrganization?organization_id=34&user_who_favourited_id=61
    }

    private fun setupRecycleView(o: ArrayList<OrganisationModalItem?>) {
        organisationBuilder = rvOrgRec?.setUp(
            R.layout.row_organisation,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                view?.img_delete?.visibility = View.VISIBLE
                Glide.with(this@OrganiseListActivity).load(ApiClient.BASE_IMAGE_URL + item.fileName)
                    .placeholder(R.drawable.event_smaple)
                    .into(view.row_event_image)
                if (item.isFavorite == true) {
                    view?.img_whishlist?.setImageResource(R.drawable.ic_heart_fill)
                } else {
                    view?.img_whishlist?.setImageResource(R.drawable.ic_heart)
                }
                view?.img_whishlist?.setOnClickListener {
                    if (item.isFavorite == true) {
                        callUnFavAPI(item, position)
                    } else {
                        callFavAPI(item, position)
                    }
                }
                view?.img_delete?.setOnClickListener {
                    confirmationDialog(getString(R.string.app_name).toUpperCase(),
                        getString(R.string.del_message), { callDeleteOrginationAPI(item) })
                }
                view?.txtOrganisationName.text = item.name
                view?.txtLocation.text = item.location
                view?.setOnClickListener {
                    val intent = Intent(this@OrganiseListActivity, OrgDetailActivity::class.java)
                    intent.putExtra("organisation", item)
                    startActivityForResult(intent, 1002) }
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
                    callOrganisationListAPI()
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

    private fun callOrganisationListAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getOrganisation(),
            singleCallback = object : SingleCallback<ArrayList<OrganisationModalItem?>> {
                override fun onSingleSuccess(
                    o: ArrayList<OrganisationModalItem?>,
                    message: String?
                ) {
                    loadingDialog(false)
                    if (o.size > 0) {
                        llNoData?.visibility = GONE
                    } else {
                        llNoData?.visibility = VISIBLE
                    }
                    setupRecycleView(o)
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    llNoData?.visibility = VISIBLE
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    llNoData?.visibility = VISIBLE
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
                callOrganisationListAPI()
            }
        }
    }

}