package com.zk.soulierge

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.AddOrgResponse
import com.zk.soulierge.support.api.model.OrganisationModalItem
import com.zk.soulierge.support.api.model.UploadFileResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.getUserId
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utilExt.toMultipartBody
import com.zk.soulierge.support.utils.ImageChooserUtil
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.MultipartBody

class AddEventActivity : AppCompatActivity() {
    var organisation: OrganisationModalItem? = null
    val REQUEST_CODE_PROFILE_IMAGE = 1008
    var fileName = System.currentTimeMillis().toString()

    var uploadedImgaeFileName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        initToolbar(tool_bar, true, getString(R.string.add_event))
        if (intent.hasExtra("organisation")) {
            organisation = intent.getParcelableExtra<OrganisationModalItem>("organisation")
        }
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
    }

    private fun addEventAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .addEvent(
                    name = edtEventName?.text?.toString()?.trim(),
                    description = edtEventDetail.text?.toString()?.trim(),
                    location = edtEventLocation?.text.toString()?.trim(),
                    latitude = "23.156953",
                    longitude = "72.6463473",
                    file_name = uploadedImgaeFileName,
                    capacity = edtEventCapacity?.text.toString()?.trim(),
                    age_restriction = edtEventAgeRestriction?.text.toString()?.trim(),
                    date = "01/09/2020",
                    time = "11:38",
                    end_date = "01/09/2020",
                    end_time = "12:00",
                    organization_id = organisation?.id,
                    user_id = getUserId()
                ),
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
        if (edtEventLocation?.text?.toString()?.trim()?.isEmpty() == true) {
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