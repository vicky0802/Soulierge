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
import com.zk.soulierge.support.utilExt.initToolbar
import com.zk.soulierge.support.utilExt.toMultipartBody
import com.zk.soulierge.support.utils.ImageChooserUtil
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.activity_add_organisation.*
import kotlinx.android.synthetic.main.row_organisation.view.*
import kotlinx.android.synthetic.main.toola_bar.*
import okhttp3.MultipartBody

class AddOrganisation : AppCompatActivity() {
    val REQUEST_CODE_PROFILE_IMAGE = 1008
    var fileName = System.currentTimeMillis().toString()
    var organisation: OrganisationModalItem? = null
    var uploadedImgaeFileName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_organisation)
        if (intent.hasExtra("organisation")) {
            organisation = intent.getParcelableExtra<OrganisationModalItem>("organisation")
            initToolbar(tool_bar, true, getString(R.string.update_organisation))
            btnAddOrganisation?.text = getString(R.string.update_organisation)
            Glide.with(this@AddOrganisation).load(ApiClient.BASE_IMAGE_URL + organisation?.fileName)
                .into(imgOrganisation)
            uploadedImgaeFileName = organisation?.fileName
            edtOrganizationName?.setText(organisation?.name)
            edtOrganizationDetail?.setText(organisation?.description)
            edtOrganizationLocation?.setText(organisation?.location)
            edtOrganizationCountry?.setText(organisation?.country)
        } else {
            initToolbar(tool_bar, true, getString(R.string.add_organization))
            btnAddOrganisation?.text = getString(R.string.add_organization)
        }

        btnImage?.setOnClickListener {
            uploadedImgaeFileName = ""
            ImageChooserUtil.openChooserDialog(
                this,
                fileName,
                REQUEST_CODE_PROFILE_IMAGE
            )
        }

        btnAddOrganisation?.setOnClickListener {
            if (isValid()) {
                if (organisation != null) {
                    updateOrganisationAPI()
                } else {
                    addOrganisationAPI()
                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
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
                    Glide.with(this@AddOrganisation).load(ApiClient.BASE_IMAGE_URL + o.file_name)
                        .into(imgOrganisation)
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

    private fun addOrganisationAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .addOrganisation(
                    name = edtOrganizationName?.text?.toString()?.trim(),
                    description = edtOrganizationDetail.text?.toString()?.trim(),
                    location = edtOrganizationLocation?.text.toString()?.trim(),
                    country = edtOrganizationCountry?.text.toString()?.trim(),
                    latitude = "23.156953",
                    longitude = "72.6463473",
                    file_name = uploadedImgaeFileName
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

    private fun updateOrganisationAPI() {
        loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateOrganization(
                    id = organisation?.id,
                    name = edtOrganizationName?.text?.toString()?.trim(),
                    description = edtOrganizationDetail.text?.toString()?.trim(),
                    location = edtOrganizationLocation?.text.toString()?.trim(),
                    country = edtOrganizationCountry?.text.toString()?.trim(),
                    latitude = organisation?.latitude,
                    longitude = organisation?.longitude,
                    file_name = uploadedImgaeFileName
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
        if (edtOrganizationName?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please insert Organisation name")
            return false
        }
        if (edtOrganizationDetail?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please insert Organisation description")
            return false
        }
        if (edtOrganizationLocation?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please select Organisation location")
            return false
        }
        if (edtOrganizationCountry?.text?.toString()?.trim()?.isEmpty() == true) {
            showAppDialog("Please insert Organisation country")
            return false
        }
        return true
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