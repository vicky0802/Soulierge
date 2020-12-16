package com.zk.soulierge.utlities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zk.soulierge.AddEventActivity
import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.OrganisationModalItem
import com.zk.soulierge.support.api.model.UpEventResponseItem
import com.zk.soulierge.support.api.model.toJson
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserId
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utilExt.toDisplayDateFormat
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_org_detail.*
import kotlinx.android.synthetic.main.row_upcoming_event.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody

class OrgDetailActivity : AppCompatActivity() {
    var organisation: OrganisationModalItem? = null
    var upEventBuilder: RecyclerViewBuilder<UpEventResponseItem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_detail)
        if (intent.hasExtra("organisation")) {
            organisation = intent.getParcelableExtra<OrganisationModalItem>("organisation")
        }
        initToolbar(tool_bar, true, organisation?.name)
        Glide.with(this).load(ApiClient.BASE_IMAGE_URL + organisation?.fileName)
            .placeholder(R.drawable.event_smaple)
            .into(imgOrganisation)
        txtOrgName?.setText(organisation?.name)
        txtOrgDes?.setText(organisation?.description)
        txtOrgLocation?.setText(organisation?.location)
        setupRecycleView(ArrayList());
        callUpEventListAPI();

        fbAddEvent?.setOnClickListener {
            val intent = Intent(this,AddEventActivity::class.java)
            this.intent.extras?.let { it1 -> intent.putExtras(it1) }
            startActivityForResult(intent,1004)
        }
    }

    private fun setupRecycleView(o: ArrayList<UpEventResponseItem?>) {
        upEventBuilder = rvUpComingEvent?.setUp(
            R.layout.row_upcoming_event,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->

                Glide.with(this@OrgDetailActivity).load(ApiClient.BASE_IMAGE_URL + item.fileName)
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

                view?.btnDelete.setOnClickListener { deleteEvent(item.id) }
                view?.txtOrganisationName.text = item.name
                view?.txtLocation.text = item.location
                view?.txtEventDate.text = item.date.toDisplayDateFormat() + " | " + item.time
                view?.txtEventUpDate.text =
                    item.endDate.toDisplayDateFormat() + " | " + item.endTime
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun deleteEvent(eventId:String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .delEvent(eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ){callUpEventListAPI()}
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

    private fun callUpEventListAPI() {
        loadingDialog(true)
        var categories = JsonArray();
        val json = JsonObject()
        json.add("category", categories)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getEventForOrg(organization_id = organisation?.id,body = body),
            singleCallback = object : SingleCallback<ArrayList<UpEventResponseItem?>> {
                override fun onSingleSuccess(o: ArrayList<UpEventResponseItem?>, message: String?) {
                    loadingDialog(false)
                    if (o.size > 0) {
                        llNoData?.visibility = View.GONE
                        rvUpComingEvent?.visibility = View.VISIBLE
                    } else {
                        rvUpComingEvent?.visibility = View.GONE
                        llNoData?.visibility = View.VISIBLE
                    }
                    setupRecycleView(o)
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
                    rvUpComingEvent?.visibility = View.GONE
                    llNoData?.visibility = View.VISIBLE
                    simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
                    rvUpComingEvent?.visibility = View.GONE
                    llNoData?.visibility = View.VISIBLE
                    message?.let {
                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

    private fun callFavAPI(item: UpEventResponseItem, position: Int) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .favouriteEventAPI(event_id = item.id, user_who_favourited_id = getUserId()),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    item.isFavorite = true
                    upEventBuilder?.notifyItemChanged(position)
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

    private fun callUnFavAPI(item: UpEventResponseItem, position: Int) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .unFavouriteEventAPI(event_id = item.id, user_who_favourited_id = getUserId()),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    item.isFavorite = false
                    upEventBuilder?.notifyItemChanged(position)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==1004){
            if (resultCode == RESULT_OK){
                callUpEventListAPI()
            }
        }
    }
}