package com.zk.soulierge

import android.content.Intent
import android.os.Bundle
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
import com.zk.soulierge.support.utilExt.BottomSheetDialogBuilder
import com.zk.soulierge.support.utilExt.getUserId
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utilExt.toDisplayDateFormat
import com.zk.soulierge.support.utils.confirmationDialog
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.activity_org_detail.*
import kotlinx.android.synthetic.main.activity_org_detail.llNoData
import kotlinx.android.synthetic.main.dialog_category.view.*
import kotlinx.android.synthetic.main.row_dialog_category.view.*
import kotlinx.android.synthetic.main.row_upcoming_event.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody


class OrgDetailActivity : AppCompatActivity() {
    var categoryBuilder: RecyclerViewBuilder<CategoryItem>? = null
    var categoryList = ArrayList<CategoryItem?>()
    var selectedCategory = ArrayList<CategoryItem?>()
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
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
    }

    private fun openBottomSheet() {
        val view = View.inflate(this, R.layout.dialog_category, null)
        val builder = BottomSheetDialogBuilder(this)
        builder.customView(view)
        view?.btnCategoryDone?.setOnClickListener { if (selectedCategory.size > 0) callUpEventListAPI(); builder.dismiss() }
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
                    startActivityForResult(eventIntent, 1004)
                }

                view?.btnDelete.setOnClickListener { deleteEvent(item.id) }
                view?.txtOrganisationName.text = item.name
                view?.txtLocation.text = item.location
                view?.txtEventDate.text =
                    item.date.toDisplayDateFormat("dd/MM/yyyy") + " | " + item.time
                view?.txtEventUpDate.text =
                    item.endDate.toDisplayDateFormat("dd/MM/yyyy") + " | " + item.endTime
            }
            isNestedScrollingEnabled = false
        }
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
                .getEventForOrg(organization_id = organisation?.id, body = body),
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

    private fun callDeleteOrginationAPI(item: OrganisationModalItem) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .deleteOrgAPI(id = item.id),
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            confirmationDialog(getString(R.string.app_name).toUpperCase(),
                getString(R.string.del_message),
                { organisation?.let { callDeleteOrginationAPI(it) } })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_organisation, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1004) {
            if (resultCode == RESULT_OK) {
                callUpEventListAPI()
            }
        }
    }
}