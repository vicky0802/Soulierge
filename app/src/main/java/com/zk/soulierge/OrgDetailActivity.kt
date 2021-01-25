package com.zk.soulierge

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.RecyclerViewBuilder
import com.example.parth.worldz_code.utils.RecyckerViewBuilder.setUp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.*
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.*
import com.zk.soulierge.support.utils.confirmationDialog
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.activity_org_detail.*
import kotlinx.android.synthetic.main.dialog_category.view.*
import kotlinx.android.synthetic.main.row_dialog_category.view.*
import kotlinx.android.synthetic.main.row_upcoming_event.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.util.*
import kotlin.collections.ArrayList


class OrgDetailActivity : AppCompatActivity() {
    var categoryBuilder: RecyclerViewBuilder<CategoryItem>? = null
    var categoryList = ArrayList<CategoryItem?>()
    var selectedCategory = ArrayList<CategoryItem?>()
    var organisationId: String? = null
    var upEventBuilder: RecyclerViewBuilder<UpEventResponseItem>? = null

    var organisationModalItem: OrganisationModalItem? = null

    var user: LoginResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_detail)
        if (intent.hasExtra("organisationId")) {
            organisationId = intent.getStringExtra("organisationId")
        }
        initToolbar(tool_bar, true, getString(R.string.organization_details))
        user = getUserData<LoginResponse>()
        if (user?.userTypeId.equals("4")) {
            txtEditOrg?.visibility = View.VISIBLE
            fbAddEvent?.visibility = View.VISIBLE
            cvOrgUsers?.visibility = View.VISIBLE
        } else if ((user?.userTypeId.equals("3"))) {
            txtEditOrg?.visibility = View.VISIBLE
            fbAddEvent?.visibility = View.VISIBLE
            cvOrgUsers?.visibility = View.GONE
        } else {
            txtEditOrg?.visibility = View.GONE
            fbAddEvent?.visibility = View.GONE
            cvOrgUsers?.visibility = View.GONE
        }
        callOrgDetailAPI(organisationId)
        setupRecycleView(ArrayList());
        callUpEventListAPI();
        callCategoriesListAPI(false)

        fbAddEvent?.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            this.intent.extras?.let { it1 -> intent.putExtras(it1) }
            startActivityForResult(intent, 1004)
        }

        fabFilter?.setOnClickListener {
            if (categoryList.size > 0) {
                openBottomSheet()
            } else {
                callCategoriesListAPI(true)
            }
        }
        txtEditOrg?.setOnClickListener {
            val intent = Intent(this, AddOrganisation::class.java)
            this.intent.extras?.let { it1 -> intent.putExtras(it1) }
            organisationModalItem?.let { intent.putExtra("organisation", it) }
            startActivityForResult(intent, 1006)
        }

        cvOrgUsers?.setOnClickListener {
            val intent = Intent(this, OrgUsersActivity::class.java)
            this.intent.extras?.let { it1 -> intent.putExtras(it1) }
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun openBottomSheet() {
        val view = View.inflate(this, R.layout.dialog_category, null)
        val builder = BottomSheetDialogBuilder(this)
        builder.customView(view)
        view?.btnCategoryDone?.setOnClickListener { /*if (selectedCategory.size > 0)*/ callUpEventListAPI(); builder.dismiss() }
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
        builder.show()
    }

    private fun callCategoriesListAPI(callAgain: Boolean?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getCategories(),
            singleCallback = object : SingleCallback<ArrayList<CategoryItem?>> {
                override fun onSingleSuccess(o: ArrayList<CategoryItem?>, message: String?) {
                    loadingDialog(false)
                    categoryList = o
                    if (callAgain == true) {
                        if (o.size > 0) {
                            showAppDialog("No Category Available")
                        } else {
                            openBottomSheet()
                        }
                    }
//                    if (o.size > 0) {
//                        llNoData?.visibility = View.GONE
//                    } else {
//                        llNoData?.visibility = View.VISIBLE
//                    }
                }

                override fun onFailure(throwable: Throwable, isDisplay: Boolean) {
                    loadingDialog(false)
//                    simpleAlert(
//                        getString(R.string.app_name).toUpperCase(),
//                        throwable.message
//                    )
                }

                override fun onError(message: String?) {
                    loadingDialog(false)
//                    message?.let {
//                        simpleAlert(getString(R.string.app_name).toUpperCase(), it)
//                    }
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

                view?.setOnClickListener {
                    var eventIntent = Intent(
                        this@OrgDetailActivity,
                        EventDetailActivity::class.java
                    )
                    eventIntent.putExtra("eventId", item.id)
                    organisationModalItem?.let { intent.putExtra("organisation", it) }
                    startActivityForResult(eventIntent, 1004)
                }

                if (user?.userTypeId.equals("4") or (user?.userTypeId.equals("3"))) {
                    view?.btnDelete?.text = getString(R.string.delete)
                    view?.btnDelete?.setOnClickListener {
                        confirmationDialog(getString(R.string.app_name).toUpperCase(),
                            getString(R.string.del_message), {
                                deleteEvent(item.id)
                            })
                    }
                } else {
                    if (item?.isParticipated == true) {
                        view?.btnDelete?.text = getString(R.string.cancel_participate)
                        view?.btnDelete?.setOnClickListener {
                            confirmationDialog(getString(R.string.app_name).toUpperCase(),
                                getString(R.string.warning_cancel_participate), {
                                    cancelParticipateEvent(item.id)
                                })
                        }
                    } else {
                        view?.btnDelete?.text = getString(R.string.participate)
                        view?.btnDelete?.setOnClickListener {
                            if (item.ageRestriction != null) {
                                confirmationDialog(getString(R.string.app_name).toUpperCase(),
                                    "Are you ${item.ageRestriction} years old or above?", {
                                        participateEvent(item.id)
                                    })
                            } else {
                                participateEvent(item.id)
                            }
                        }
                    }
                }
                view?.txtOrganisationName.text = item.name
                view?.txtLocation.text = item.location
                view?.txtEventDesc?.text = item.description
                view?.txtEventDate.text =
                    item.date.toDisplayDateFormat("dd/MM/yyyy") + " | " + item.time
                view?.txtEventUpDate.text =
                    item.endDate.toDisplayDateFormat("dd/MM/yyyy") + " | " + item.endTime
                view?.txtAddToCalendar?.setOnClickListener {
                    val beginTime: Calendar = Calendar.getInstance()
                    beginTime.time = (item.date + " " + item.time).toDate("dd/MM/yyyy HH:mm")
                    val endTime: Calendar = Calendar.getInstance()
                    endTime.time = (item.endDate + " " + item.endTime).toDate("dd/MM/yyyy HH:mm")
                    val intent: Intent = Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(
                            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            beginTime.getTimeInMillis()
                        )
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, item.name)
                        .putExtra(CalendarContract.Events.DESCRIPTION, item.description)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, item.location)
                        .putExtra(
                            CalendarContract.Events.AVAILABILITY,
                            CalendarContract.Events.AVAILABILITY_BUSY
                        )
//                        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
                    startActivity(intent)
                }
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun participateEvent(eventId: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .partEvent(user_id = user?.userId, event_id = eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ) { callUpEventListAPI() }
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

    private fun cancelParticipateEvent(eventId: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .cancelPartEvent(user_id = user?.userId, event_id = eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ) { callUpEventListAPI() }
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


    private fun deleteEvent(eventId: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .delEvent(eventId),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    loadingDialog(false)
                    showAppDialog(
                        if (o.success?.isNotEmpty() == true) o.success else o.failure,
                    ) { callUpEventListAPI() }
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
        val categories = JsonArray();
        selectedCategory.forEach { categories.add(it?.id) }
        val json = JsonObject()
        json.add("category", categories)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getEventForOrg(organization_id = organisationId, body = body),
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

    private fun callDeleteOrginationAPI(item: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .deleteOrgAPI(id = item),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    loadingDialog(false)
                    setResult(RESULT_OK)
                    onBackPressed()
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

    private fun callOrgDetailAPI(id: String?) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .orgDetail(orgId = id),
            singleCallback = object : SingleCallback<OrganisationModalItem> {
                override fun onSingleSuccess(o: OrganisationModalItem, message: String?) {
                    loadingDialog(false)
                    changeTitle(o?.name)
                    organisationModalItem = o
                    Glide.with(this@OrgDetailActivity)
                        .load(ApiClient.BASE_IMAGE_URL + o?.fileName)
                        .placeholder(R.drawable.event_smaple)
                        .into(imgOrganisation)
                    txtOrgName?.setText(o?.name)
                    txtOrgDes?.setText(o?.description)
                    txtOrgLocation?.setText(o?.location)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            confirmationDialog(getString(R.string.app_name).toUpperCase(),
                getString(R.string.del_message),
                { organisationId?.let { callDeleteOrginationAPI(it) } })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (user?.userTypeId.equals("4")) {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_organisation, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1004) {
            if (resultCode == RESULT_OK) {
                callUpEventListAPI()
            }
        }
        if (requestCode == 1006) {
            if (resultCode == RESULT_OK) {
                if (intent.hasExtra("organisationId")) {
                    organisationId = intent.getStringExtra("organisationId")
                    callOrgDetailAPI(organisationId)
                }
            }
        }
    }
}