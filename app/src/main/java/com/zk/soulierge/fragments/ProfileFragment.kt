package com.zk.soulierge.fragments


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.zk.soulierge.R
import com.zk.soulierge.UserChakraActivity
import com.zk.soulierge.adapter.ProfileTabAdapter
import com.zk.soulierge.support.api.ApiClient
import com.zk.soulierge.support.api.SingleCallback
import com.zk.soulierge.support.api.WebserviceBuilder
import com.zk.soulierge.support.api.model.GeneralResponse
import com.zk.soulierge.support.api.model.LoginResponse
import com.zk.soulierge.support.api.model.UploadFileResponse
import com.zk.soulierge.support.api.model.UserResponse
import com.zk.soulierge.support.api.subscribeToSingle
import com.zk.soulierge.support.utilExt.*
import com.zk.soulierge.support.utils.ImageChooserUtil
import com.zk.soulierge.support.utils.loadingDialog
import com.zk.soulierge.support.utils.showAppDialog
import com.zk.soulierge.support.utils.simpleAlert
import com.zk.soulierge.utlities.FragmentUtility
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MultipartBody

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : BaseFragment() {
    val REQUEST_CODE_PROFILE_IMAGE = 1008
    var fileName = System.currentTimeMillis().toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtUserName?.text = context?.getUserName()
        Glide.with(requireActivity())
            .load(ApiClient.BASE_IMAGE_URL + context?.getUserData<LoginResponse>()?.fileName)
            .placeholder(R.drawable.event_smaple)
            .into(imgUserImage)
        val user = context?.getUserData<LoginResponse>()
        if (user?.userTypeId.equals("4")) {
            img_chakra?.visibility = View.GONE
        } else {
            img_chakra?.visibility = View.VISIBLE
            getUseAPI(null,false)
        }
        ll_setting.setOnClickListener {
            FragmentUtility.withManager(activity?.supportFragmentManager)
                .addToBackStack("")
                .replaceToFragment(SettingFragment())
        }
        imgUserImage?.setOnClickListener {
            ImageChooserUtil.openChooserDialog(
                this,
                fileName,
                REQUEST_CODE_PROFILE_IMAGE
            )
        }
        setTabs()
    }

    private fun setTabs() {
        vp_profile.adapter = ProfileTabAdapter(context!!, childFragmentManager)

    }

    override fun getTagFragment(): String {
        return "profile_frag"
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

    private fun getUseAPI(fileName: String?, isUpdateCall: Boolean?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .getUser(user_id = context?.getUserId()),
            singleCallback = object : SingleCallback<UserResponse> {
                override fun onSingleSuccess(o: UserResponse, message: String?) {
                    context?.loadingDialog(false)
                    if (o?.userTypeId.equals("4") or (o?.userTypeId.equals("3"))) {
                        img_chakra?.visibility = View.GONE
                    } else {
                        img_chakra?.visibility = View.VISIBLE
                        setChakra(o)
                    }
                    if (isUpdateCall == true) {
                        updateUserAPI(o, fileName)
                    } else {
                        val user = context?.getUserData<LoginResponse>()
                        user?.dateCreated = o.dateCreated
                        user?.fileName = fileName ?: user?.fileName
                        user?.name = o.name
                        user?.userTypeId = o.userTypeId
                        o.name?.let { context?.setUserName(it) }
                        context?.setUserData(Gson().toJson(user).toString())
                        user?.userId?.let { context?.setUserId(it) }
                        Glide.with(requireActivity())
                            .load(ApiClient.BASE_IMAGE_URL + fileName)
                            .placeholder(R.drawable.event_smaple)
                            .into(imgUserImage)
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

    private fun setChakra(o: UserResponse?) {
        when(o?.badge){
            1->{img_chakra.setImageResource(R.drawable.chakar_2)}
            2->{img_chakra.setImageResource(R.drawable.chakar_3)}
            3->{img_chakra.setImageResource(R.drawable.chakar_4)}
            4->{img_chakra.setImageResource(R.drawable.chakar_5)}
            5->{img_chakra.setImageResource(R.drawable.chakar_6)}
            6->{img_chakra.setImageResource(R.drawable.chakar_7)}
        }
        img_chakra?.setOnClickListener { startActivity(Intent(context, UserChakraActivity::class.java)) }
    }

    private fun updateUserAPI(user: UserResponse, fileName: String?) {
        context?.loadingDialog(true)
        subscribeToSingle(
            observable = ApiClient.getHeaderClient().create(WebserviceBuilder::class.java)
                .updateUser(
                    address = user.address,
                    file_name = fileName,
                    gender = user.gender,
                    latitude = user.latitude,
                    longitude = user.longitude,
                    name = user.name,
                    phone_number = user.phoneNumber,
                    user_id = user.id
                ),
            singleCallback = object : SingleCallback<GeneralResponse> {
                override fun onSingleSuccess(o: GeneralResponse, message: String?) {
                    context?.loadingDialog(false)
                    context?.showAppDialog(o.success) { getUseAPI(fileName, false) }
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
                    getUseAPI(o.file_name, true)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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

}
