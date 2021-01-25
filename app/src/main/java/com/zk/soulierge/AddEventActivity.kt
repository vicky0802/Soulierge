package com.zk.soulierge

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
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
import com.zk.soulierge.support.utils.ImageChooserUtil
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.RecyclerViewLayoutManager
import com.zk.soulierge.utlities.RecyclerViewLinearLayout
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.dialog_category.view.*
import kotlinx.android.synthetic.main.row_dialog_category.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class AddEventActivity : AppCompatActivity() {
    var organisationId: String? = null
    val REQUEST_CODE_PROFILE_IMAGE = 1008
    var fileName = System.currentTimeMillis().toString()

    var categoryBuilder: RecyclerViewBuilder<CategoryItem>? = null
    var categoryList = ArrayList<CategoryItem?>()
    var selectedCategory = ArrayList<CategoryItem?>()

    var event: UpEventResponseItem? = null

    var selectedStartTime: Date? = null
    var selectedEndime: Date? = null

    var uploadedImgaeFileName: String? = ""

    var selectedLocation: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        if (intent.hasExtra("event")) {
            event = intent.getParcelableExtra<UpEventResponseItem>("event")
            initToolbar(tool_bar, true, getString(R.string.update_event))
            btnCreateEvent?.text = getString(R.string.update)
            Glide.with(this).load(ApiClient.BASE_IMAGE_URL + event?.fileName)
                .into(imgEvent)
            uploadedImgaeFileName = event?.fileName
            edtEventName?.setText(event?.name)
            edtEventDetail?.setText(event?.description)
            edtEventLocation?.setText(event?.location)
            edtEventCapacity?.setText(event?.capacity?.toString())
            edtEventAgeRestriction?.setText(event?.ageRestriction?.toString())
            eventStartDate?.setText(event?.date.toDisplayDateFormat("dd/MM/yyyy") + " | " + event?.time)
            eventEndDate?.setText(event?.endDate.toDisplayDateFormat("dd/MM/yyyy") + " | " + event?.endTime)
            selectedLocation = Address(Locale.getDefault())
            selectedLocation?.latitude = event?.latitude?.toDouble() ?: 0.0
            selectedLocation?.longitude = event?.longitude?.toDouble() ?: 0.0
            organisationId = event?.organizationId.toString()
        } else {
            initToolbar(tool_bar, true, getString(R.string.add_event))
            btnCreateEvent?.text = getString(R.string.create)
        }
        if (intent.hasExtra("organisationId")) {
            organisationId = intent.getStringExtra("organisationId")
        }
        callCategoriesListAPI(false)
        btnImage?.setOnClickListener {
            uploadedImgaeFileName = ""
            ImageChooserUtil.openChooserDialog(
                this,
                fileName,
                REQUEST_CODE_PROFILE_IMAGE
            )
        }

        btnCreateEvent?.setOnClickListener {
            if (isValid()) {
                addEventAPI()
            }
        }

        eventCategory.setOnClickListener {
            if (categoryList.size > 0) {
                openBottomSheet()
            } else {
                callCategoriesListAPI(true)
            }
        }

        eventStartDate?.setOnClickListener { startEventDateTime() }
        eventEndDate?.setOnClickListener { endEventDateTime() }

        edtEventLocation?.setOnClickListener {
            val intent = Intent(this, LocationSearchActivity::class.java)
            this.intent.extras?.let { it1 -> intent.putExtras(it1) }
            startActivityForResult(intent, 1004)
        }
    }


    private fun openBottomSheet() {
        val view = View.inflate(this, R.layout.dialog_category, null)
        val builder = BottomSheetDialogBuilder(this)
        builder.customView(view)
        view?.btnCategoryDone?.setOnClickListener { builder.dismiss() }
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    private fun endEventDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)
        var timePickerDialog: TimePickerDialog? = null
        val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
                if (selectedStartTime != null) {
                    val pickedDateTime = Calendar.getInstance()
                    pickedDateTime.set(year, month, day, hour, minute)
                    if (pickedDateTime.time?.compareTo(selectedStartTime) == 1) {
                        eventEndDate?.tag = pickedDateTime.time.toAPIDateFormat()
                        eventEndDate?.text =
                            pickedDateTime.time.toDisplayDateFormat("dd MMMM yyyy | hh:mm aa")
                        selectedEndime = pickedDateTime.time
                    } else {
                        showAppDialog(getString(R.string.warning_time)) { timePickerDialog?.show() }
                    }
                } else {
                    showAppDialog(getString(R.string.warning_select_start_time)) { timePickerDialog?.show() }
                }
            }, startHour, startMinute, false)
            timePickerDialog?.show()
            timePickerDialog?.setCancelable(false)
        }, startYear, startMonth, startDay)
        if (selectedStartTime != null) {
            selectedStartTime?.time?.let { datePickerDialog.datePicker.minDate = it }
        }
        if (selectedStartTime != null)
            datePickerDialog.show()
        else {
            showAppDialog(getString(R.string.warning_select_start_time)) { timePickerDialog?.show() }
        }
    }

    private fun startEventDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                eventStartDate?.tag = pickedDateTime.time.toAPIDateFormat()
                eventStartDate?.text =
                    pickedDateTime.time.toDisplayDateFormat("dd MMMM yyyy | hh:mm aa")
                selectedStartTime = pickedDateTime.time
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay)
        if (selectedEndime != null) {
            selectedEndime?.time?.let { datePickerDialog.datePicker.maxDate = it }
        }
        datePickerDialog.show()
    }

    private fun addEventAPI() {
        loadingDialog(true)
        val categories = JsonArray();
        selectedCategory.forEach { categories.add(it?.id) }
        val json = JsonObject()
        json.add("category", categories)
        val mediaType: MediaType? = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, json.toJson())
        var observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
            .addEvent(
                name = edtEventName?.text?.toString()?.trim(),
                description = edtEventDetail.text?.toString()?.trim(),
                location = edtEventLocation?.text.toString()?.trim(),
                latitude = selectedLocation?.latitude.toString(),
                longitude = selectedLocation?.longitude?.toString(),
                file_name = uploadedImgaeFileName,
                capacity = edtEventCapacity?.text.toString()?.trim(),
                age_restriction = edtEventAgeRestriction?.text.toString()?.trim(),
                date = eventStartDate?.tag.toString().toDate().toAPIDateFormat("dd/MM/yyyy"),
                time = eventStartDate?.tag.toString().toDate().toAPIDateFormat("HH:mm"),
                end_date = eventEndDate?.tag.toString().toDate().toAPIDateFormat("dd/MM/yyyy"),
                end_time = eventEndDate?.tag.toString().toDate().toAPIDateFormat("HH:mm"),
                organization_id = organisationId,
                user_id = getUserId(), body = body
            )
        if (intent.hasExtra("event")){
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateEvent(
                    id = event?.id,
                    name = edtEventName?.text?.toString()?.trim(),
                    description = edtEventDetail.text?.toString()?.trim(),
                    location = edtEventLocation?.text.toString()?.trim(),
                    latitude = selectedLocation?.latitude.toString(),
                    longitude = selectedLocation?.longitude?.toString(),
                    file_name = uploadedImgaeFileName,
                    capacity = edtEventCapacity?.text.toString()?.trim(),
                    age_restriction = edtEventAgeRestriction?.text.toString()?.trim(),
                    date = eventStartDate?.tag.toString().toDate().toAPIDateFormat("dd/MM/yyyy"),
                    time = eventStartDate?.tag.toString().toDate().toAPIDateFormat("HH:mm"),
                    end_date = eventEndDate?.tag.toString().toDate().toAPIDateFormat("dd/MM/yyyy"),
                    end_time = eventEndDate?.tag.toString().toDate().toAPIDateFormat("HH:mm"),
                    organization_id = organisationId,
                    user_id = getUserId(), body = body
                )
        }
        subscribeToSingle(
            observable = observable,
            singleCallback = object : SingleCallback<AddOrgResponse> {
                override fun onSingleSuccess(o: AddOrgResponse, message: String?) {
                    loadingDialog(false)
                    if (o.success.isNullOrEmpty()) {
                        showAppDialog(o.failure)
                    } else {
                        showAppDialog(o.success) {
                            setResult(RESULT_OK)
                            onBackPressed()
                        }
                    }

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

    fun isValid(): Boolean {
        if (edtEventName?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please insert Event name")
            return false
        }
        if (edtEventDetail?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please insert Event description")
            return false
        }
        if (edtEventLocation?.text?.toString()?.trim()
                ?.isEmpty() == true or (selectedLocation == null)
        ) {
            showAppDialog("Please select Event location")
            return false
        }
        if (edtEventCapacity?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please enter Event capacity")
            return false
        }
        if (eventStartDate?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please enter Event start date")
            return false
        }
        if (eventEndDate?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please enter Event end date")
            return false
        }
        return true
    }

    private fun uploadFileAPI(toMultipartBody: MultipartBody.Part?, requestCode: Int) {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .uploadFile(toMultipartBody),
            singleCallback = object : SingleCallback<UploadFileResponse> {
                override fun onSingleSuccess(o: UploadFileResponse, message: String?) {
                    loadingDialog(false)
                    Glide.with(this@AddEventActivity).load(ApiClient.BASE_IMAGE_URL + o.file_name)
                        .into(imgEvent)
                    uploadedImgaeFileName = o.file_name
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
        when (requestCode) {
            REQUEST_CODE_PROFILE_IMAGE, REQUEST_CODE_PROFILE_IMAGE + 1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    ImageChooserUtil.SaveImageTask(data,
                        requestCode,
                        fileName,
                        requestCode == REQUEST_CODE_PROFILE_IMAGE + 1,
                        ImageChooserUtil.SaveImageTask.FileSaveListener { file ->
                            uploadFileAPI(file?.toMultipartBody("file"), requestCode)
                        }).execute()
                }
            }
            1004 -> {
                selectedLocation = null
                if (resultCode == RESULT_OK) {
                    if (data?.hasExtra("address") == true) run {
                        selectedLocation = data?.getParcelableExtra("address")
                        edtEventLocation?.setText(selectedLocation?.featureName)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ImageChooserUtil.PERMISSION_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED
                ) {
                    ImageChooserUtil.openChooserDialog(
                        this,
                        fileName, REQUEST_CODE_PROFILE_IMAGE

                    )
                }
            }
            ImageChooserUtil.PERMISSION_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED
                ) {
                    ImageChooserUtil.startCameraIntent(
                        this,
                        fileName, REQUEST_CODE_PROFILE_IMAGE + 1
                    )
                }
            }
        }
    }
}