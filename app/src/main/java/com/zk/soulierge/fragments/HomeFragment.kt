package com.zk.soulierge.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.zk.soulierge.CategoriesActivity
import com.zk.soulierge.MainActivity
import com.zk.soulierge.OrganiseListActivity
import com.zk.soulierge.R
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.ApiClient.BASE_IMAGE_URL
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.UploadFileResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.toMultipartBody
import com.zk.soulierge.support.utils.ImageChooserUtil
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.row_event.view.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject

class HomeFragment : BaseFragment() {
    val REQUEST_CODE_ORG = 1008
    val REQUEST_CODE_CAT = 1011
    val REQUEST_CODE_MAIN = 1015
    var fileName = System.currentTimeMillis().toString()
    var selectedCode = 0;
    var event_banner: String? = ""
    var org_banner: String? = ""
    var banner: String? = ""

    override fun getTagFragment(): String {
        return "home_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callHomeAPI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cv_event.txtEditOrg.text = getString(R.string.ph_find_event)
        cv_organization.txtEditOrg.text = getString(R.string.ph_find_organisation)
        cv_categories.txtEditOrg.text = getString(R.string.categories)
        cv_event?.setOnClickListener {
            if (activity is MainActivity) {
                (activity as MainActivity)?.exploreEvent()
            }
        }
        cv_organization?.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    OrganiseListActivity::class.java
                )
            )
        }
        cv_categories?.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    CategoriesActivity::class.java
                )
            )
        }
        cv_organization?.row_event_image?.setOnClickListener {
            selectedCode = REQUEST_CODE_ORG
            ImageChooserUtil.openChooserDialog(this, fileName, REQUEST_CODE_ORG)
        }
        cv_categories?.row_event_image?.setOnClickListener {
            selectedCode = REQUEST_CODE_CAT
            ImageChooserUtil.openChooserDialog(this, fileName, REQUEST_CODE_CAT)
        }
        app_bar_image?.setOnClickListener {
            selectedCode = REQUEST_CODE_MAIN
            ImageChooserUtil.openChooserDialog(this, fileName, REQUEST_CODE_MAIN)
        }
    }

    private fun callHomeAPI() {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getHomePage(),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    context?.loadingDialog(false)
                    try {
                        val temp = o.string()
                        var json = JSONObject(temp.replace(", }", "}"))
                        if (json.has("success")) {
                            if (json.has("event_banner")) {
                                event_banner = json.getString("event_banner")
                                Glide.with(requireActivity())
                                    .load(BASE_IMAGE_URL + json.getString("event_banner"))
                                    .placeholder(R.drawable.event_smaple)
                                    .into(cv_event.row_event_image)
                            }
                            if (json.has("org_banner")) {
                                org_banner = json.getString("org_banner")
                                Glide.with(requireActivity())
                                    .load(BASE_IMAGE_URL + json.getString("org_banner"))
                                    .placeholder(R.drawable.event_smaple)
                                    .into(cv_organization.row_event_image)
                            }
                            if (json.has("banner")) {
                                banner = json.getString("banner")
                                Glide.with(requireActivity())
                                    .load(BASE_IMAGE_URL + json.getString("banner"))
                                    .placeholder(R.drawable.organisation_sample)
                                    .into(app_bar_image)
                            }
                        } else if (json.has("failure")) {
                            context?.showAppDialog(json.getString("failure"))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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

    private fun uploadFileAPI(toMultipartBody: MultipartBody.Part?, requestCode: Int) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .uploadFile(toMultipartBody),
            singleCallback = object : SingleCallback<UploadFileResponse> {
                override fun onSingleSuccess(o: UploadFileResponse, message: String?) {
                    context?.loadingDialog(false)
                    when (requestCode) {
                        REQUEST_CODE_ORG, REQUEST_CODE_ORG + 1 -> {
                            callUpdateResourceAPI(banner, o.file_name, event_banner)
                        }
                        REQUEST_CODE_CAT, REQUEST_CODE_CAT + 1 -> {
                            callUpdateResourceAPI(banner, org_banner, o.file_name)
                        }
                        REQUEST_CODE_MAIN, REQUEST_CODE_MAIN + 1 -> {
                            callUpdateResourceAPI(o.file_name, org_banner, event_banner)
                        }
                    }
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

    private fun callUpdateResourceAPI(
        bannerl: String?,
        org_bannerl: String?,
        event_bannerl: String?
    ) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateResources(
                    banner = bannerl,
                    org_banner = org_bannerl,
                    event_banner = event_bannerl
                ),
            singleCallback = object : SingleCallback<ResponseBody> {
                override fun onSingleSuccess(o: ResponseBody, message: String?) {
                    context?.loadingDialog(false)
                    try {
                        val temp = o.string()
                        var json = JSONObject(temp.replace(", }", "}"))
                        if (json.has("success")) {
                            context?.showAppDialog(json.getString("success"))
                            callHomeAPI()
                            if (json.has("event_banner")) {
                                event_banner = event_bannerl
                                Glide.with(requireActivity())
                                    .load(BASE_IMAGE_URL + event_banner)
                                    .placeholder(R.drawable.event_smaple)
                                    .into(cv_event.row_event_image)
                            }
                            if (json.has("org_banner")) {
                                org_banner = org_bannerl
                                Glide.with(requireActivity())
                                    .load(BASE_IMAGE_URL + org_banner)
                                    .placeholder(R.drawable.event_smaple)
                                    .into(cv_organization.row_event_image)
                            }
                            if (json.has("banner")) {
                                banner = bannerl
                                Glide.with(requireActivity())
                                    .load(BASE_IMAGE_URL + banner)
                                    .placeholder(R.drawable.organisation_sample)
                                    .into(app_bar_image)
                            }
                        } else if (json.has("failure")) {
                            context?.showAppDialog(json.getString("failure"))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                        fileName, selectedCode

                    )
                }
            }
            ImageChooserUtil.PERMISSION_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED
                ) {
                    ImageChooserUtil.startCameraIntent(
                        this,
                        fileName, selectedCode + 1
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ORG, REQUEST_CODE_ORG + 1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    ImageChooserUtil.SaveImageTask(data,
                        requestCode,
                        fileName,
                        requestCode == REQUEST_CODE_ORG + 1,
                        ImageChooserUtil.SaveImageTask.FileSaveListener { file ->
                            uploadFileAPI(file?.toMultipartBody("file"), requestCode)
                        }).execute()
                }
            }
            REQUEST_CODE_CAT, REQUEST_CODE_CAT + 1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    ImageChooserUtil.SaveImageTask(data,
                        requestCode,
                        fileName,
                        requestCode == REQUEST_CODE_CAT + 1,
                        ImageChooserUtil.SaveImageTask.FileSaveListener { file ->
                            uploadFileAPI(file?.toMultipartBody("file"), requestCode)
                        }).execute()
                }
            }
            REQUEST_CODE_MAIN, REQUEST_CODE_MAIN + 1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    ImageChooserUtil.SaveImageTask(data,
                        requestCode,
                        fileName,
                        requestCode == REQUEST_CODE_MAIN + 1,
                        ImageChooserUtil.SaveImageTask.FileSaveListener { file ->
                            uploadFileAPI(file?.toMultipartBody("file"), requestCode)
                        }).execute()
                }
            }
        }
    }

}
