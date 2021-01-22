package com.zk.soulierge.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.zk.soulierge.EventDetailActivity
import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.*
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.BottomSheetDialogBuilder
import com.zk.soulierge.support.utilExt.getUserData
import com.zk.soulierge.support.utilExt.getUserId
import com.zk.soulierge.support.utilExt.toDisplayDateFormat
import com.zk.soulierge.support.utils.confirmationDialog
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.dialog_category.view.*
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.fragment_favourites_event.*
import kotlinx.android.synthetic.main.row_dialog_category.view.*
import kotlinx.android.synthetic.main.row_upcoming_event.view.*
import okhttp3.ResponseBody

/**
 * A simple [Fragment] subclass.
 */
class FavouriteEventFragment : BaseFragment() {
    var categoryBuilder: RecyclerViewBuilder<CategoryItem>? = null
    var categoryList = ArrayList<CategoryItem?>()

    var user = context?.getUserData<LoginResponse>()

    //    var selectedCategory = ArrayList<CategoryItem?>()
    var upEventBuilder: RecyclerViewBuilder<FavEventResponseItem>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites_event, container, false)
    }

    override fun getTagFragment(): String {
        return "favourit_event_fragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView(ArrayList());
        callUpEventListAPI()
        fabFilter?.setOnClickListener {
            openBottomSheetDialog()
        }
        callCategoriesListAPI(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = context?.getUserData<LoginResponse>()
    }

    private fun openBottomSheetDialog() {
        val view = View.inflate(context, R.layout.dialog_filter, null)
        val builder = context?.let { BottomSheetDialogBuilder(it) }
        builder?.customView(view)
        view?.btn_1_week?.setOnClickListener { callUpEventListAPI(filtersDay = "7",);builder?.dismiss(); }
        view?.btn_2_week?.setOnClickListener { callUpEventListAPI(filtersDay = "14",);builder?.dismiss(); }
        view?.btn_3_week?.setOnClickListener { callUpEventListAPI(filtersDay = "21",);builder?.dismiss(); }
        view?.btn_near_me?.setOnClickListener { }
        view?.btn_category?.setOnClickListener {
            builder?.dismiss();
            if (categoryList.size > 0) {
                openCategoryBottomSheet()
            } else {
                callCategoriesListAPI(true)
            }
        }
        view?.btn_all?.setOnClickListener { callUpEventListAPI(); builder?.dismiss(); }
        view?.btnCancel?.setOnClickListener { builder?.dismiss(); }
        builder?.dialog?.show()
    }

    private fun openCategoryBottomSheet() {
        var selectedCategory = ArrayList<CategoryItem?>()
        val view = View.inflate(context, R.layout.dialog_category, null)
        val builder = context?.let { BottomSheetDialogBuilder(it) }
        builder?.customView(view)
        view?.btnCategoryDone?.setOnClickListener {
            if (selectedCategory.size > 0) callUpEventListAPI(
//                selectedCategory = selectedCategory
            ); builder?.dismiss()
        }
        categoryBuilder = view?.rvCategory?.setUp(
            R.layout.row_dialog_category,
            categoryList,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                if (item.isSelected) {
                    view?.imgSelected.visibility = View.VISIBLE
                } else {
                    view?.imgSelected.visibility = View.GONE
                }
                view?.txtCategoryTitle.text = item.name
                view?.setOnClickListener {
                    if (item.isSelected) {
                        item.isSelected = !item.isSelected
                        view?.imgSelected.visibility = View.GONE
                        selectedCategory.remove(item)
                    } else {
                        item.isSelected = !item.isSelected
                        view?.imgSelected.visibility = View.VISIBLE
                        selectedCategory.add(item)
                    }
                }
            }
            isNestedScrollingEnabled = false
        }
        builder?.show()
    }

    private fun callCategoriesListAPI(callAgain: Boolean?) {
//        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getCategories(),
            singleCallback = object : SingleCallback<ArrayList<CategoryItem?>> {
                override fun onSingleSuccess(o: ArrayList<CategoryItem?>, message: String?) {
//                    loadingDialog(false)
                    categoryList = o
                    if (callAgain == true) {
                        if (o.size > 0) {
                            context?.showAppDialog("No Category Available")
                        } else {
                            openCategoryBottomSheet()
                        }
                    }
//                    if (o.size > 0) {
//                        llNoData?.visibility = View.GONE
//                    } else {
//                        llNoData?.visibility = View.VISIBLE
//                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
//                    loadingDialog(false)
//                    simpleAlert(
//                        getString(R.string.app_name).toUpperCase(),
//                        throwable.message
//                    )
                }

                override fun onError(message: String?) {
//                    loadingDialog(false)
//                    message?.let {
//                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
//                    }
                }
            }
        )
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

    private fun setupRecycleView(o: ArrayList<FavEventResponseItem?>) {
        upEventBuilder = rvUpComingEvent?.setUp(
            R.layout.row_upcoming_event,
            o,
            RecyclerViewLayoutManager.LINEAR,
            RecyclerViewLinearLayout.VERTICAL
        ) {
            contentBinder { item, view, position ->
                context?.let {
                    Glide.with(it).load(ApiClient.BASE_IMAGE_URL + item.event.fileName)
                        .placeholder(R.drawable.event_smaple)
                        .into(view.row_event_image)
                }
//                if (item.isFavorite == true) {
                view?.img_whishlist?.setImageResource(R.drawable.ic_heart_fill)
//                } else {
//                    view?.img_whishlist?.setImageResource(R.drawable.ic_heart)
//                }
                view?.img_whishlist?.setOnClickListener {
//                    if (item.isFavorite == true) {
                    callUnFavAPI(item, position)
//                    } else {
//                        callFavAPI(item, position)
//                    }
                }
                view?.setOnClickListener {
                    var eventIntent = Intent(context, EventDetailActivity::class.java)
                    eventIntent.putExtra("eventId", item.id)
                    startActivityForResult(eventIntent, 1005)
                }
                view?.txtOrganisationName.text = item.event.name
                view?.txtLocation.text = item.event.location
                view?.txtEventDesc?.text = item.event.description
                view?.txtEventDate.text =
                    item.event.date.toDisplayDateFormat("dd/MM/yyyy") + " | " + item.event.time
                view?.txtEventUpDate.text =
                    item.event.endDate.toDisplayDateFormat("dd/MM/yyyy") + " | " + item.event.endTime

                if (user?.userTypeId.equals("4") or (user?.userTypeId.equals("3"))) {
                    view?.btnDelete?.text = getString(R.string.delete)
                    view?.btnDelete?.setOnClickListener {
                        context?.confirmationDialog(getString(R.string.app_name).toUpperCase(),
                            getString(R.string.del_message), {
                                deleteEvent(item?.event?.id.toString())
                            })
                    }
                } else {
                    if (item?.event?.isParticipated == true) {
                        view?.btnDelete?.text = getString(R.string.cancel_participate)
                        view?.btnDelete?.setOnClickListener {
                            context?.confirmationDialog(getString(R.string.app_name).toUpperCase(),
                                getString(R.string.warning_cancel_participate), {
                                    cancelParticipateEvent(item?.event?.id.toString())
                                })
                        }
                    } else {
                        view?.btnDelete?.text = getString(R.string.participate)
                        view?.btnDelete?.setOnClickListener {
                            if (item.event?.ageRestriction != null) {
                                context?.confirmationDialog(getString(R.string.app_name).toUpperCase(),
                                    "Are you ${item.event?.ageRestriction} years old or above?", {
                                        participateEvent(item?.event?.id.toString())
                                    })
                            } else {
                                participateEvent(item?.event?.id.toString())
                            }
                        }
                    }
                }
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun participateEvent(eventId: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .partEvent(user_id = user?.userId, event_id = eventId),
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

    private fun cancelParticipateEvent(eventId: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .cancelPartEvent(user_id = user?.userId, event_id = eventId),
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

    private fun callUnFavAPI(item: FavEventResponseItem, position: Int) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .unFavouriteEventAPI(
                    event_id = item.eventId.toString(),
                    user_who_favourited_id = context?.getUserId()
                ),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    context?.loadingDialog(false)
//                    item.isFavorite = false
//                    upEventBuilder?.notifyItemChanged(position)
                    callUpEventListAPI()
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

    private fun callUpEventListAPI(
        filtersDay: String? = null,
    ) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getFavEvent(user_id = context?.getUserId(), filter_days = filtersDay),
            singleCallback = object : SingleCallback<ArrayList<FavEventResponseItem?>> {
                override fun onSingleSuccess(
                    o: ArrayList<FavEventResponseItem?>,
                    message: String?
                ) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1005) {
            if (resultCode == RESULT_OK) {
                callUpEventListAPI()
            }
        }
    }

}
