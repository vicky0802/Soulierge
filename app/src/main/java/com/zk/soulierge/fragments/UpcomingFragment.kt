package com.zk.soulierge.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.UpEventResponseItem
import com.zk.soulierge.support.api.model.toJson
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserId
import com.zk.soulierge.support.utilExt.toDisplayDateFormat
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.fragment_upcoming.*
import kotlinx.android.synthetic.main.row_upcoming_event.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * A simple [Fragment] subclass.
 */
class UpcomingFragment : BaseFragment() {
    var upEventBuilder: RecyclerViewBuilder<UpEventResponseItem>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun getTagFragment(): String {
        return "upcoming_fragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView(ArrayList());
        callUpEventListAPI()
    }

    private fun deleteEvent(eventId: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .delEvent(eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    context?.loadingDialog(false)
                    context?.showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ) { callUpEventListAPI() }
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

    private fun setupRecycleView(o: ArrayList<UpEventResponseItem?>) {
        upEventBuilder = rvUpComingEvent?.setUp(
            R.layout.row_upcoming_event,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                context?.let {
                    Glide.with(it).load(ApiClient.BASE_IMAGE_URL + item.fileName)
                        .placeholder(R.drawable.event_smaple)
                        .into(view.row_event_image)
                }
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
                view?.txtOrganisationName.text = item.name
                view?.txtLocation.text = item.location
                view?.txtEventDate.text = item.date.toDisplayDateFormat() + " | " + item.time
                view?.txtEventUpDate.text =
                    item.endDate.toDisplayDateFormat() + " | " + item.endTime

                view?.btnDelete?.setOnClickListener { deleteEvent(item.id) }
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun callFavAPI(item: UpEventResponseItem, position: Int) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .favouriteEventAPI(
                    event_id = item.id,
                    user_who_favourited_id = context?.getUserId()
                ),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    context?.loadingDialog(false)
                    item.isFavorite = true
                    upEventBuilder?.notifyItemChanged(position)
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

    private fun callUnFavAPI(item: UpEventResponseItem, position: Int) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .unFavouriteEventAPI(
                    event_id = item.id,
                    user_who_favourited_id = context?.getUserId()
                ),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    context?.loadingDialog(false)
                    item.isFavorite = false
                    upEventBuilder?.notifyItemChanged(position)
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

    private fun callUpEventListAPI() {
        context?.loadingDialog(true)
        var categories = JsonArray();
        val json = JsonObject()
        json.add("category", categories)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getEvents(body),
            singleCallback = object : SingleCallback<ArrayList<UpEventResponseItem?>> {
                override fun onSingleSuccess(o: ArrayList<UpEventResponseItem?>, message: String?) {
                    context?.loadingDialog(false)
                    if (o.size > 0) {
                        llNoData?.visibility = View.GONE
                    } else {
                        llNoData?.visibility = View.VISIBLE
                    }
                    setupRecycleView(o)
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    context?.loadingDialog(false)
                    llNoData?.visibility = View.VISIBLE
                    context?.simpleAlert(
                        getString(R.string.app_name).toUpperCase(),
                        throwable.message
                    )
                }

                override fun onError(message: String?) {
                    context?.loadingDialog(false)
                    llNoData?.visibility = View.VISIBLE
                    message?.let {
                        context?.simpleAlert(getString(R.string.app_name).toUpperCase(), it)
                    }
                }
            }
        )
    }

}
