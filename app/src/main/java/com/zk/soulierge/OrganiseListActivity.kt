package com.zk.soulierge

import android.os.Bundle
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
                Glide.with(this@OrganiseListActivity).load(ApiClient.BASE_IMAGE_URL +item.fileName)
                    .placeholder(R.drawable.event_smaple)
                    .into(view.row_event_image)
                if (item.isFavorite==true){
                    view?.img_whishlist?.setImageResource(R.drawable.ic_heart_fill)
                }else{
                    view?.img_whishlist?.setImageResource(R.drawable.ic_heart)
                }
                view?.img_whishlist?.setOnClickListener{
                    if (item.isFavorite == true){
                        callUnFavAPI(item,position)
                    }else{
                        callFavAPI(item,position)
                    }
                }
                view?.txtOrganisationName.text = item.name
                view?.txtLocation.text = item.location
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun callFavAPI(item: OrganisationModalItem, position: Int) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .favouriteOrgAPI(organization_id = item.id,user_who_favourited_id = getUserId()),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    item.isFavorite = true
                    organisationBuilder?.notifyItemChanged(position)
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
    private fun callUnFavAPI(item: OrganisationModalItem, position: Int) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .unFavouriteOrgAPI(organization_id = item.id,user_who_favourited_id = getUserId()),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    item.isFavorite = false
                    organisationBuilder?.notifyItemChanged(position)
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
    private fun callOrganisationListAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getOrganisation(),
            singleCallback = object : SingleCallback<ArrayList<OrganisationModalItem?>> {
                override fun onSingleSuccess(o: ArrayList<OrganisationModalItem?>, message: String?) {
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

}